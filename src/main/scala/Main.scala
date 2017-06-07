import java.time.LocalDate

import MainActor.BeginAnalysis
import akka.actor.{ActorSystem, Props}
import twitter._

object Main extends App {

  val LOCATION = "Krakow"
  val PROFILES_NUMBER = 60
  val DATE = LocalDate.now()
  val NUMBER_OF_DAYS_BACK = 7
  val LEVENSHTEIN_DISTANCE = 1
  val TIMEOUT = 60
  val finder = Finder.UserAwareFinder

  val system = ActorSystem("Hashtag_analyzer")
  val mainActor = system.actorOf(Props(new MainActor()), "mainActor")
  val profilesDownloader = system.actorOf(Props(new ProfilesDownloader()), "ProfilesDownloader")
  val tweetsDownloader = system.actorOf(Props(new TweetsDownloader()),"TweetsDownloader")
  val tweetsAnalyzer = system.actorOf(Props(new TweetsAnalyzer()),"TweetsAnalyzer")
  val topHashtagsFinder = system.actorOf(Props(new TopHashtagsFinder()),"TopHashtagsFinder")
  val userAwareTopHashtagsFinder = system.actorOf(Props(new UserAwareTopHashTagFinder()),"UserAwareTopHashtagsFinder")
  val topSimilarHashtagFinder = system.actorOf(Props(new TopSimilarHashtagFinder(LEVENSHTEIN_DISTANCE)),"TopSimilarHashtagFinder")
  val plotter = system.actorOf(Props(new PlotDrawer()), "PlotDrawer")

  mainActor ! BeginAnalysis

  //  System.exit(0)

  object Finder extends Enumeration {
    type Finder = Value
    val TopHashtagFinder, UserAwareFinder, TopSimilarHashtagFinder = Value
  }

}
