package models

import play.api.libs.functional.syntax._
import play.api.libs.json.{JsPath, Reads}

case class GeoNames(lat: String, lng: String, population: Int)

object GeoNames {
  implicit val geoNamesReads: Reads[GeoNames] = (
    (JsPath \\ "lat").read[String] and
      (JsPath \\ "lng").read[String] and
      (JsPath \\ "population").read[Int]

    ) (GeoNames.apply _)

}
