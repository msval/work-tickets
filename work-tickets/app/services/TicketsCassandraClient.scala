package services

import java.time.Instant

import akka.Done
import com.datastax.driver.core.{Cluster, Row, Session}
import domain.TicketState.TicketState
import domain.{Project, Ticket, TicketState}

import scala.collection.JavaConversions._
import msvaljek.cql.CassandraCql._

import scala.collection.IterableView
import scala.collection.JavaConverters._
import scala.concurrent.Future

class TicketsCassandraClient {

  import scala.concurrent.ExecutionContext.Implicits.global

  val keyspace = "tickets"
  private lazy val omniBucket = "all"

  implicit val session: Session = Cluster.builder()
    .addContactPoint("172.17.0.3")
    .withPort(9042)
    .build()
    .connect()

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

  def tickets(projectId: String): Future[List[Ticket]] =
    execute(cql"SELECT id, name, description, state, changed_at FROM tickets.ticket where project = ?", projectId).map(_.asScala.view.map(parseRowToTicket).toList)

  def projects(): List[Project] =
    session.execute(s"SELECT project, description FROM $keyspace.projects WHERE bucket = '$omniBucket'")
      .all()
      .map { row => Project(row.getString("project"), row.getString("description")) }.toList

  def addTicket(projectId: String, ticketName: String, ticketDescription: String): Ticket = {
    val id = session.execute(s"SELECT id from $keyspace.ticket WHERE project = '$projectId' limit 1;")
      .all().collectFirst{ case row => row.getString("id").split('-')(1).toInt + 1 }.getOrElse(0)

    val ticketId = s"$projectId-$id"

    val changedAt = Instant.now()
    val ticketState = TicketState.waiting

    session.execute(s"INSERT INTO $keyspace.ticket(project, id, name, description, state, changed_at) VALUES ('$projectId', '$ticketId', '$ticketName', '$ticketDescription', '$ticketState', '$changedAt')")

    Ticket(ticketId, ticketName, ticketDescription, ticketState, changedAt)
  }

  def updateTicket(projectId: String, ticketId: String, ticketName: String, ticketDescription: String, ticketState: TicketState): Ticket = {

    val changedAt = Instant.now()

    session.execute(s"INSERT INTO $keyspace.ticket(project, id, name, description, state, changed_at) VALUES ('$projectId', '$ticketId', '$ticketName', '$ticketDescription', '$ticketState', '$changedAt')")

    Ticket(ticketId, ticketName, ticketDescription, ticketState, changedAt)
  }

  def delete(projectId: String, ticketId: String): Done = {
    session.execute(s"DELETE FROM $keyspace.ticket WHERE project = '$projectId' AND id = '$ticketId'")
    Done
  }

}