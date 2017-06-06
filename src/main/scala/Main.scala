import java.time.LocalDate

import MainActor.BeginAnalysis
import akka.actor.{ActorSystem, Props}
import twitter.{ProfilesDownloader, TopHashtagsFinder, TweetsAnalyzer, TweetsDownloader}

object Main extends App {

  val LOCATION = "Krakow"
  val PROFILES_NUMBER = 10
  val DATE = LocalDate.now()
  val NUMBER_OF_DAYS_BACK = 7

  val system = ActorSystem("Hashtag_analyzer")
  val mainActor = system.actorOf(Props(new MainActor()), "mainActor")
  val profilesDownloader = system.actorOf(Props(new ProfilesDownloader()), "ProfilesDownloader")
  val tweetsDownloader = system.actorOf(Props(new TweetsDownloader()),"TweetsDownloader")
  val tweetsAnalyzer = system.actorOf(Props(new TweetsAnalyzer()),"TweetsAnalyzer")
  val topHashtagsFinder = system.actorOf(Props(new TopHashtagsFinder()),"TopHashtagsFinder")
  val plotter = system.actorOf(Props(new PlotDrawer()), "plotDrawer")

  mainActor ! BeginAnalysis

  //  System.exit(0)
}
