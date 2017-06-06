import java.time.LocalDate

import akka.actor.{ActorSystem, Props}
import akka.pattern.ask
import akka.util.Timeout

import scala.concurrent.duration._
import scala.concurrent.ExecutionContext.Implicits.global

object Main extends App {

  val system = ActorSystem("Hashtag_analyzer")
  val twitterDownloader = system.actorOf(Props(new TwitterDownloader()), "twitterDownloader")
  val topHashtagDownloader = system.actorOf(Props(new TopHashtagsDownloader()),"topHashtagDownloader")
  implicit val timeout = Timeout(100 seconds)
  var users = twitterDownloader ? Downloader.DownloadProfiles("50.0611591", "19.9383446")
  println(users)
  var topHashtags = topHashtagDownloader ? HashtagDownloader.DownloadTopHashtags(1963405968,12,LocalDate.now(),12)

  topHashtags.onComplete(hashtags => println(hashtags))
  println(topHashtags)

  val plotter = system.actorOf(Props(new PlotDrawer()), "plotDrawer")
  var data =  List(Tuple2("myHashtag", List(Tuple2(1496771109006L, 10), Tuple2(1496771209006L, 16), Tuple2(1496771309006L, 12), Tuple2(1496771409006L, 20))))
  plotter ! PlotDrawer.Draw(data)


  Thread.sleep(1000000000)

}
