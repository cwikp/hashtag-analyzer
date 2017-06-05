import java.time.LocalDate

import akka.actor.{ActorSystem, Props}
import akka.pattern.ask
import akka.util.Timeout
import twitter.{TweetsDownloader, ProfilesDownloader}

import scala.concurrent.duration._

object Main extends App {

  val system = ActorSystem("Hashtag_analyzer")
  val profilesDownloader = system.actorOf(Props(new ProfilesDownloader()), "profilesDownloader")
  val tweetsDownloader = system.actorOf(Props(new TweetsDownloader()),"tweetsDownloader")
  implicit val timeout = Timeout(100 seconds)
//  var users = profilesDownloader ? ProfilesDownloader.DownloadProfilesByLatLong("50.0611591", "19.9383446")
  var users = profilesDownloader ? ProfilesDownloader.DownloadProfilesByLocation("Krakow")
  println(users)
  var topHashtags = tweetsDownloader ? TweetsDownloader.DownloadTopHashtags(1963405968,12,LocalDate.now(),12)
  println(topHashtags)

}
