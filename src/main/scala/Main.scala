import java.time.LocalDate

import akka.actor.{ActorSystem, Props}
import akka.pattern.ask
import akka.util.Timeout

import scala.concurrent.duration._

object Main extends App {

  val system = ActorSystem("Hashtag_analyzer")
  val twitterDownloader = system.actorOf(Props(new TwitterDownloader()), "twitterDownloader")
  val topHashtagDownloader = system.actorOf(Props(new TopHashtagsDownloader()),"topHashtagDownloader")
  implicit val timeout = Timeout(100 seconds)
  var users = twitterDownloader ? Downloader.DownloadProfiles("50.0611591", "19.9383446")
  println(users)
  var topHashtags = topHashtagDownloader ? HashtagDownloader.DownloadTopHashtags(1963405968,12,LocalDate.now(),12)
  println(topHashtags)

  val plotter = system.actorOf(Props(new PlotDrawer()), "plotDrawer")
  plotter ! PlotDrawer.Draw(Map[String, Long]())

}
