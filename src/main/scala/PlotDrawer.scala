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
      if (single) {
        hold()
        legendLabels = legendLabels ++ List(s"${d._1}")
      } else legendLabels = List(s"${d._1}")

      val chartTitle = legendLabels.map(h => s"#$h").mkString(", ")

      spline(d._2.map(t => t._1).toList, d._2.map(t => t._2))
      title(s"Hashtags: $chartTitle")
      xAxis("Date")
      xAxisType(AxisType.datetime)
      yAxis("Count")

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