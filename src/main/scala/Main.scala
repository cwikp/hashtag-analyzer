import MainActor.BeginAnalysis
import akka.actor.{ActorSystem, Props}
import twitter.{ProfilesDownloader, TweetsDownloader}

object Main extends App {

  val LOCATION = "Krakow"
  val USER = 1963405968

  val system = ActorSystem("Hashtag_analyzer")
  val mainActor = system.actorOf(Props(new MainActor()), "mainActor")
  val profilesDownloader = system.actorOf(Props(new ProfilesDownloader()), "ProfilesDownloader")
  val tweetsDownloader = system.actorOf(Props(new TweetsDownloader()),"TweetsDownloader")

  mainActor ! BeginAnalysis

  //  System.exit(0)
}
