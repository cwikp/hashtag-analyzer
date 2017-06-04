import akka.actor.Actor
import akka.event.LoggingReceive
import com.quantifind.charts.Highcharts._
import com.quantifind.charts.highcharts
import com.quantifind.charts.highcharts._
import com.quantifind.charts.highcharts.Highchart._

class PlotDrawer extends Actor {

  def drawPlot(data: Map[String, Long]): Long = {
    Highchart(Seq(Series(Seq(Data(1, 2)))), chart = Chart(zoomType = Zoom.xy), yAxis = None)
    areaspline(List(1, 2, 3, 4, 5), List(4, 1, 3, 2, 6))

    1
  }

  override def receive: Receive = LoggingReceive {
    case PlotDrawer.Draw(data) =>
      drawPlot(data)
  }
}

object PlotDrawer {

  case class Draw(data: Map[String, Long])

}