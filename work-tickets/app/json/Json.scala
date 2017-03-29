package json

import java.time.Instant

import domain.TicketState.TicketState
import domain.{Project, Ticket}
import play.api.libs.json._
import play.api.libs.functional.syntax._

object Json {

  implicit val projectWrites: Writes[Project] = (
    (JsPath \ "project").write[String] and
      (JsPath \ "description").write[String]
    )(unlift(Project.unapply))

  implicit val ticketsWrites: Writes[Ticket] = (
    (JsPath \ "id").write[String] and
      (JsPath \ "name").write[String] and
      (JsPath \ "description").write[String] and
      (JsPath \ "state").write[TicketState] and
      (JsPath \ "changed_at").write[Instant]
    )(unlift(Ticket.unapply))

}
