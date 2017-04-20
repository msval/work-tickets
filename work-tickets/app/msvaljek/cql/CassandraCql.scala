package msvaljek.cql

import com.datastax.driver.core._
import com.google.common.util.concurrent.{FutureCallback, Futures, ListenableFuture}

import scala.concurrent.{ExecutionContext, Future, Promise}
import scala.language.implicitConversions
import scalacache._
import guava._

object CassandraCql {

  implicit val scalaCache = ScalaCache(GuavaCache())

  implicit class CqlStrings(val context: StringContext) extends AnyVal {
    def cql(args: Any*)(implicit session: Session): Future[PreparedStatement] = caching(context.raw(args: _*)) {
      val statement = new SimpleStatement(context.raw(args: _*))
      session.prepareAsync(statement)
    }
  }

  implicit def listenableFutureToFuture[T](listenableFuture: ListenableFuture[T]): Future[T] = {
    val promise = Promise[T]()

    Futures.addCallback(listenableFuture, new FutureCallback[T] {
      override def onFailure(error: Throwable): Unit = {
        promise.failure(error)
        ()
      }

      override def onSuccess(result: T): Unit = {
        promise.success(result)
        ()
      }
    })

    promise.future
  }

  def execute(statement: Future[PreparedStatement], params: Any*)(implicit executionContext: ExecutionContext, session: Session): Future[ResultSet] = {
    statement.map(
      _.bind(params.map(_.asInstanceOf[AnyRef]) : _*)
    ).flatMap(session.executeAsync(_))
  }
}
