package object domain {
  case class Ticket(id: String, name: String, description: String)
  case class Project(project: String, description: String)
}
