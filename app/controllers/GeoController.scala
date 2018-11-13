/*@author: Soner Guzeloglu
*
* Controller
* 1. SearchJSON to get lat and lng
* 2. findNearbyPlaceNameJSON to get near by populated places
* 3. SearchJSONwithCC to get each population of returned places
* */

package controllers

import javax.inject._
import models.{GeoNames, PopulatedPlaces}
import play.api.Configuration
import play.api.libs.json._
import play.api.libs.ws.{WSClient, WSResponse}
import play.api.mvc._

import scala.concurrent.{ExecutionContext, Future}

@Singleton
class GeoController @Inject()(ws: WSClient,
                              configuration: Configuration,
                              cc: ControllerComponents)
                             (implicit ec: ExecutionContext)
  extends AbstractController(cc) {

  private val searchJSONUrl = configuration.get[String]("searchJSON.url")
  private val findNearbyPlaceName = configuration.get[String]("findNearbyPlaceName.url")
  private val searchJSONUrlWithCC = configuration.get[String]("searchJSONwithCC.url")

  def get(city: String, radius: String): Action[AnyContent] = Action.async {

    val futureSearchResult: Future[Result] = for {
      latLngResult <- getLatLng(city) // get the search data
      populatedPlaces <- getPopulatedPlaces(Json.parse(latLngResult.body), radius) //get populated places to sum population
      futurePopulations = getPopulation(Json.parse(populatedPlaces.body)) //get population by name and country code
      populations <- Future.sequence(futurePopulations) // gather all Future[WSResponse]s
    } yield Ok(getTotalNumberOfPeople(populations)) // get sum of people on responses

    futureSearchResult.recover {
      case e: NoSuchElementException =>
        InternalServerError(Json.obj("error" -> JsString("Could not search the data")))
    }
  }

  /*Get latitude and longitude for given city*/
  private def getLatLng(city: String): Future[WSResponse] = {
    ws.url(searchJSONUrl.format(city)).get.withFilter { response =>
      response.status == OK
    }
  }

  /*Get populated places around within given radius*/
  private def getPopulatedPlaces(json: JsValue, radius: String): Future[WSResponse] = {
    val a = (json \ "geonames").as[Seq[GeoNames]]
    ws.url(findNearbyPlaceName.format(a.head.lat,
      a.head.lng, radius))
      .get
      .withFilter { response =>
        response.status == OK
      }
  }

  /*Call search method again to get information of detected nearby places*/
  private def getPopulation(json: JsValue): Seq[Future[WSResponse]] = {
    (json \ "geonames").as[Seq[PopulatedPlaces]] map (populatedPlace => getNumberOfPeople(populatedPlace.name, populatedPlace.countryCode))
  }

  /*Get information of detected nearby places with geonameId*/
  private def getNumberOfPeople(name: String, cc: String): Future[WSResponse] = {
    ws.url(searchJSONUrlWithCC.format(name, cc)).get.withFilter { response =>
      response.status == OK
    }
  }

  /*Get sum of people on detected places*/
  private def getTotalNumberOfPeople(responses: Seq[WSResponse]): JsValue = {
    //Total Number of People
    val list: List[Int] = responses.toList.map(res => (res.json \ "geonames").as[Seq[GeoNames]].head.population)
    val total = list.sum
    val str = total.toString
    Json.parse(s"""{"totalNumberOfPeople":$str}""")
  }
}