package services

import java.time.Instant

import akka.Done
import com.datastax.driver.core.{Cluster, QueryLogger, Row, Session}
import domain.TicketState.TicketState
import domain.{Project, Ticket, TicketState}
import msvaljek.cql.CassandraCql._

import scala.collection.JavaConversions._
import scala.collection.JavaConverters._
import scala.concurrent.Future

class TicketsCassandraClient {

  import scala.concurrent.ExecutionContext.Implicits.global

  private lazy val omniBucket = "all"

  val queryLogger = QueryLogger.builder().withConstantThreshold(1).withMaxQueryStringLength(1).build()

  val cluster = Cluster.builder()
    .addContactPoint("172.17.0.3")
    .withPort(9042)
    .build()

  cluster.register(queryLogger)

  implicit val session: Session = cluster.connect()

  def tickets(projectId: String): Future[List[Ticket]] =
    execute(cql"SELECT id, name, description, state, changed_at FROM tickets.ticket WHERE project = ?", projectId).map(
      _.asScala.view.map(parseRowToTicket).toList)

  def parseRowToTicket(row: Row): Ticket = Ticket(
    row.getString("id"),
    row.getString("name"),
    row.getString("description"),
    Option(row.getString("state")) match {
      case Some(state) if TicketState.isTicketState(state) => TicketState.withName(state)
      case _ => TicketState.waiting
    },
    Option(row.getTimestamp("changed_at")) match {
      case Some(timestamp) => timestamp.toInstant
      case _ => Instant.now()
    }
  )

  def projects(): Future[List[Project]] =
    execute(cql"SELECT project, description FROM tickets.projects WHERE bucket = ?", omniBucket).map(_.asScala.view.map(parseRowToProject).toList)

  def parseRowToProject(row: Row): Project = Project(
    row.getString("project"),
    row.getString("description")
  )

  def addTicket(projectId: String, ticketName: String, ticketDescription: String): Ticket = {
    val id = session.execute(s"SELECT id from tickets.ticket WHERE project = '$projectId' limit 1;")
      .all().collectFirst { case row => row.getString("id").split('-')(1).toInt + 1 }.getOrElse(0)

    val ticketId = s"$projectId-$id"

    val changedAt = Instant.now()
    val ticketState = TicketState.waiting

    execute(cql"INSERT INTO tickets.ticket(project, id, name, description, state, changed_at) VALUES (?, ?, ?, ?, ?, ?)", projectId, ticketId, ticketName, ticketDescription, ticketState, changedAt)

    Ticket(ticketId, ticketName, ticketDescription, ticketState, changedAt)
  }

  def updateTicket(projectId: String, ticketId: String, ticketName: String, ticketDescription: String, ticketState: TicketState): Ticket = {

    val changedAt = Instant.now()

    execute(cql"INSERT INTO tickets.ticket(project, id, name, description, state, changed_at) VALUES (?, ?, ?, ?, ?, ?)", projectId, ticketId, ticketName, ticketDescription, ticketState, changedAt)

    Ticket(ticketId, ticketName, ticketDescription, ticketState, changedAt)
  }

  def delete(projectId: String, ticketId: String): Done = {
    execute(cql"DELETE FROM tickets.ticket WHERE project = ? AND id = ?", projectId, ticketId)

    Done
  }
}
