package controllers

import javax.inject._

import play.api.libs.json.Json
import play.api.mvc._
import services.CassandraClient
import json.Json._

@Singleton
class ProjectsController @Inject()(cassandraClient: CassandraClient) extends Controller {

  def projects = Action {
    val projects = cassandraClient.projects()

    Ok(Json.toJson(projects))
  }

  def tickets(projectId: String) = Action {
    val tickets = cassandraClient.tickets(projectId)

    Ok(Json.toJson(tickets))
  }

}
