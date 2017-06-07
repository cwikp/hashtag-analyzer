import akka.actor.Actor
import akka.event.LoggingReceive
import com.quantifind.charts.Highcharts._
import com.quantifind.charts.highcharts.Highchart._
import com.quantifind.charts.highcharts._


class PlotDrawer extends Actor {

  var legendLabels: List[String] = List.empty

  def drawPlot(data: Seq[(String, Seq[(Long, Int)])], single: Boolean): Long = {

    Highchart(Seq(Series(Seq(Data(1, 2))), Series(Seq(Data(1, 2)))), chart = Chart(zoomType = Zoom.xy), yAxis = None)

    data.foreach(d => {
      spline(d._2.map(t => t._1).toList, d._2.map(t => t._2))
      title(s"Hashtag: #${d._1}")
      xAxis("Date")
      xAxisType(AxisType.datetime)
      yAxis("Count")

      if (single) {
        hold()
        legendLabels = legendLabels ++ List(s"${d._1}")
      } else legendLabels = List(s"${d._1}")
      legend(legendLabels)
    })

    1
  }

  override def receive: Receive = LoggingReceive {
    case PlotDrawer.Draw(data, single) =>
      drawPlot(data, single)
  }
}

object PlotDrawer {

  case class Draw(data: Seq[(String, Seq[(Long, Int)])], single: Boolean = false)

}