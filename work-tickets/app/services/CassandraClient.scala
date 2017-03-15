package services

import com.datastax.driver.core.{Cluster, ResultSet, Row, Session}
import domain.{Project, Ticket}

import scala.collection.JavaConversions._

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
}
