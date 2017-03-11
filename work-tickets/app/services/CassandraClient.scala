package services

import com.datastax.driver.core.Cluster

class CassandraClient {
  private val cluster = Cluster.builder()
    .addContactPoint("172.17.0.3")
    .withPort(9042)
    .build()

  val session = cluster.connect()

  def getTickets() = {
    session.execute("select * from tickets.tickets")
  }
}
