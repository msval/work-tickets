package controllers

import javax.inject._

import play.api._
import play.api.mvc._
import services.CassandraClient

/**
 * This controller creates an `Action` to handle HTTP requests to the
 * application's home page.
 */
@Singleton
class HomeController @Inject()(cassandraClient: CassandraClient) extends Controller {

  /**
   * Create an Action to render an HTML page with a welcome message.
   * The configuration in the `routes` file means that this method
   * will be called when the application receives a `GET` request with
   * a path of `/`.
   */
  def index = Action {
    val tickets: Array[AnyRef] = cassandraClient.getTickets().all().stream().toArray

    val msg = tickets.mkString(",")

    Ok(views.html.index(msg))
  }

}
