package controllers

import javax.inject._

import domain.TicketState
import json.Json._
import play.api.data.Forms._
import play.api.data._
import play.api.libs.concurrent.Execution.Implicits.defaultContext
import play.api.libs.json.Json
import play.api.mvc._
import services.TicketsCassandraClient

import scala.collection.immutable

@Singleton
class ProjectsController @Inject()(cassandraClient: TicketsCassandraClient) extends Controller {

  val addUpdateForm = Form(
    mapping(
      "ticketId" -> optional(text),
      "ticketName" -> text,
      "ticketDescription" -> text,
      "ticketState" -> optional(text)
    )(TicketData.apply)(TicketData.unapply))

  def projects: Action[AnyContent] = Action.async {
    val projects = cassandraClient.projects()

    projects.map(projects => Ok(Json.toJson(projects)))
  }

  def tickets(projectId: String): Action[AnyContent] = Action.async {
    val tickets = cassandraClient.tickets(projectId)

    tickets.map((tickets: immutable.Seq[domain.Ticket]) => Ok(Json.toJson(tickets)))
  }

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

  case class TicketData(ticketId: Option[String], ticketName: String, ticketDescription: String, ticketState: Option[String])

}
