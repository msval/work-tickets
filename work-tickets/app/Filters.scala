import javax.inject._
import play.api._
import play.api.http._
import play.filters.cors._

class Filters @Inject() (corsFilter: CORSFilter)
  extends DefaultHttpFilters(corsFilter)
