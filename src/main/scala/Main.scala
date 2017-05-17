import akka.actor.{ActorSystem, Props}
import akka.pattern.ask
import akka.util.Timeout

import scala.concurrent.duration._

object Main extends App {

  val system = ActorSystem("Hashtag_analyzer")
  val twitterDownloader = system.actorOf(Props(new TwitterDownloader()), "twitterDownloader")

  implicit val timeout = Timeout(100 seconds)
  var users = twitterDownloader ? Downloader.DownloadProfiles("50.0611591", "19.9383446")
  println(users)

}
