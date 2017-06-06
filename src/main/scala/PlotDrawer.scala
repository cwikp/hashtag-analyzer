import akka.actor.Actor
import akka.event.LoggingReceive
import com.quantifind.charts.Highcharts._
import com.quantifind.charts.highcharts.Highchart._
import com.quantifind.charts.highcharts._

class PlotDrawer extends Actor {

  def drawPlot(data: Seq[Tuple2[String, Seq[Tuple2[Long, Int]]]]): Long = {

    Highchart(Seq(Series(Seq(Data(1, 2))), Series(Seq(Data(1, 2)))), chart = Chart(zoomType = Zoom.xy), yAxis = None )

    data.foreach(d => {
      spline(d._2.map(t => t._1).toList, d._2.map(t=>t._2))
    })

    1
  }

  override def receive: Receive = LoggingReceive {
    case PlotDrawer.Draw(data) =>
      drawPlot(data)
  }
}

object PlotDrawer {

  case class Draw(data: Seq[Tuple2[String, Seq[Tuple2[Long, Int]]]])

}