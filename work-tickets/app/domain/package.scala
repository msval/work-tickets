import java.time.Instant

package object domain {
  object TicketState extends Enumeration {
    type TicketState = Value
    val waiting, in_progress, done, canceled = Value

    def isTicketState(s: String): Boolean = values.exists(_.toString == s)
  }

  import TicketState._

  case class Ticket(id: String, name: String, description: String, state: TicketState = waiting, changedAt: Instant)
  case class Project(project: String, description: String)
}