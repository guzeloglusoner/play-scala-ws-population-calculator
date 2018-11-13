/*@author: Soner Guzeloglu
*
* GeoRouter
*
* Routes the coming url
* */
package router

import javax.inject.Inject
import play.api.mvc._
import play.api.routing.SimpleRouter
import play.api.routing.sird._

class GeoRouter @Inject()(controller: controllers.GeoController)
  extends SimpleRouter {
  override def routes: PartialFunction[RequestHeader, Action[AnyContent]] = {
    case GET(p"/geo" ? q"city=$city" & q"radius=$radius") => controller.get(city, radius)
  }
}
