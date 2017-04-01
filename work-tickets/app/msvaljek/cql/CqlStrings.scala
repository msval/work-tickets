package msvaljek.cql

import com.datastax.driver.core._
import com.google.common.util.concurrent.ListenableFuture

object CqlStrings {
  implicit class CqlStrings(val context: StringContext) extends AnyVal {
    def cql(args: Any*)(implicit session: Session): ListenableFuture[PreparedStatement] = {
      val statement = new SimpleStatement(context.raw(args: _*))
      session.prepareAsync(statement)
    }
  }
}
