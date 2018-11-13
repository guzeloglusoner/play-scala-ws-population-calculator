package models

import play.api.libs.json.{JsPath, Reads}
import play.api.libs.functional.syntax._

case class PopulatedPlaces(name: String, countryCode: String)

object PopulatedPlaces {
  implicit val populatedPlacesReads: Reads[PopulatedPlaces] = (
    (JsPath \\ "name").read[String] and
      (JsPath \\ "countryCode").read[String]
    ) (PopulatedPlaces.apply _)
}
