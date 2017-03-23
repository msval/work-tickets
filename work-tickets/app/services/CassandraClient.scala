package services

import akka.Done
import com.datastax.driver.core.{Cluster, Session}
import domain.{Project, Ticket}

import scala.collection.JavaConversions._
import scala.collection.mutable

class CassandraClient {

  val keyspace = "tickets"
  private lazy val omniBucket = "all"

  private val cluster = Cluster.builder()
    .addContactPoint("172.17.0.3")
    .withPort(9042)
    .build()

  val session: Session = cluster.connect()

  def tickets(projectId: String): List[Ticket] =
    session.execute(s"select id, name, description from $keyspace.ticket where project = '$projectId'")
      .all()
      .map { row => Ticket(row.getString("id"), row.getString("name"), row.getString("description")) }.toList

  def projects(): List[Project] =
    session.execute(s"select project, description from $keyspace.projects where bucket = '$omniBucket'")
      .all()
      .map { row => Project(row.getString("project"), row.getString("description")) }.toList

  def addTicket(projectId: String, ticketName: String, ticketDescription: String): Ticket = {
    val id = session.execute(s"select id from $keyspace.ticket where project = '$projectId' limit 1;")
      .all().collectFirst{ case row => row.getString("id").split('-')(1).toInt + 1 }.getOrElse(0)

    val ticketId = s"$projectId-$id"

    session.execute(s"insert into $keyspace.ticket(project, id, name, description) values ('$projectId', '$ticketId', '$ticketName', '$ticketDescription')")

    Ticket(ticketId, ticketName, ticketDescription)
  }

  def updateTicket(projectId: String, ticketId: String, ticketName: String, ticketDescription: String): Ticket = {

    session.execute(s"insert into $keyspace.ticket(project, id, name, description) values ('$projectId', '$ticketId', '$ticketName', '$ticketDescription')")

    Ticket(ticketId, ticketName, ticketDescription)
  }

  def delete(projectId: String, ticketId: String): Done = {
    session.execute(s"delete from $keyspace.ticket where project = '$projectId' and id = '$ticketId'")
    Done
  }

}
