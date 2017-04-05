package controllers

import javax.inject._

import play.api._
import domain.TicketState
import play.api.libs.json.Json
import play.api.mvc._
import services.TicketsCassandraClient
import json.Json._
import play.api.data._
import play.api.data.Forms._
import play.api.data.format.Formats._
import play.api.libs.concurrent.Execution.Implicits.defaultContext

import scala.concurrent.Future

@Singleton
class ProjectsController @Inject()(cassandraClient: TicketsCassandraClient) extends Controller {

  def projects = Action {
    val projects = cassandraClient.projects()

    Ok(Json.toJson(projects))
  }

  def tickets(projectId: String) = Action.async {
    val tickets= cassandraClient.tickets(projectId)

    tickets.map(tickets => Ok(Json.toJson(tickets)))
  }

  case class TicketData(ticketId: Option[String], ticketName: String, ticketDescription: String, ticketState: Option[String])

  val addUpdateForm = Form(
    mapping(
      "ticketId" -> optional(text),
      "ticketName" -> text,
      "ticketDescription" -> text,
      "ticketState" -> optional(text)
    )(TicketData.apply)(TicketData.unapply))

  def add(projectId: String) = Action { implicit request =>
    val ticket = addUpdateForm.bindFromRequest.get

    Ok(Json.toJson(cassandraClient.addTicket(projectId, ticket.ticketName, ticket.ticketDescription)))
  }

  def update(projectId: String) = Action { implicit request =>
    val ticket = addUpdateForm.bindFromRequest.get

    Ok(Json.toJson(cassandraClient.updateTicket(
      projectId,
      ticket.ticketId.get,
      ticket.ticketName,
      ticket.ticketDescription,
      TicketState.withName(ticket.ticketState.getOrElse(TicketState.waiting.toString))
    )))
  }

  def delete(projectId: String, ticketId: String) = Action {
    cassandraClient.delete(projectId, ticketId)

    Ok
  }

}
