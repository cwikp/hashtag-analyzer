import Main.Finder
import Main.Finder.Finder
import MainActor.{BeginAnalysis, DownloadTimeout}
import akka.actor.Actor
import akka.event.LoggingReceive
import com.danielasfregola.twitter4s.entities.Tweet
import twitter.{ProfilesDownloader, TweetsAnalyzer, TweetsDownloader}
import scala.concurrent.ExecutionContext.Implicits.global

import scala.concurrent.duration._

class MainActor extends Actor {

  var userProfiles = 0
  var userProfilesProcessed = 0
  var tweetsList: List[Tweet] = List.empty
  var processed = false

  override def receive: Receive = LoggingReceive {
    case BeginAnalysis =>
      downloadProfiles()
      println(s"BEGIN ANALYSIS")
      println(s"Place: ${Main.LOCATION}, Profiles Number: ${Main.PROFILES_NUMBER}")
      println(s"Date: ${Main.DATE}, Number of days back: ${Main.NUMBER_OF_DAYS_BACK}")
    case ProfilesDownloader.DownloadProfilesComplete(data: Seq[Long]) =>
      userProfiles = data.size
      println(s"\nProfiles downloaded: $userProfiles")
      println(s"\nDownloading tweets from users")
      setDownloadTimeout()
      data.foreach(id => downloadTweets(id))
    case TweetsDownloader.DownloadTweetsComplete(data: Seq[Tweet]) =>
      tweetsList = tweetsList ++ data
      checkIfAllTweetsDownloaded()
    case DownloadTimeout => processTweets()
    case TweetsAnalyzer.HashtagDates(hashtag, dates) =>
      plotCharts(hashtag, dates)
  }

  def plotCharts(hashtag: String, dates: Seq[(Long, Int)]): Unit = {
    val data: Seq[(String, Seq[(Long, Int)])] = Seq((hashtag, dates))
    context.actorSelection("../PlotDrawer") ! PlotDrawer.Draw(data, single = true)
  }

  def downloadProfiles(): Unit = {
    // or by lat and long: "50.0611591", "19.9383446"
    context.actorSelection("../ProfilesDownloader") ! ProfilesDownloader.DownloadProfilesByLatLong("50.0611591", "19.9383446", 100)
  }

  def downloadTweets(userId: Long): Unit = {
    context.actorSelection("../TweetsDownloader") ! TweetsDownloader.DownloadTweets(userId, Main.DATE, Main.NUMBER_OF_DAYS_BACK)
  }

  def checkIfAllTweetsDownloaded(): Unit = {
    userProfilesProcessed += 1
    print(".")
    if (userProfilesProcessed >= userProfiles && !processed) {
      processTweets()
    }
  }

  private def processTweets() = {
    processed = true
    println("TWEETS TOTAL NUMBER: " + tweetsList.size)
    val topHashtagsFinder = hashtagFinder(Main.finder)
    context.actorSelection("../TweetsAnalyzer") ! TweetsAnalyzer.AnalyzeTweets(tweetsList, topHashtagsFinder)
  }

  private def hashtagFinder(finder: Finder) = {
    finder match {
      case Finder.TopHashtagFinder        => context.actorSelection("../TopHashtagsFinder")
      case Finder.TopSimilarHashtagFinder => context.actorSelection("../TopSimilarHashtagFinder")
      case Finder.UserAwareFinder         => context.actorSelection("../UserAwareTopHashtagsFinder")
    }
  }

  private def setDownloadTimeout() = {
    context.system.scheduler.scheduleOnce(Main.TIMEOUT seconds, self, DownloadTimeout)
  }

}

object MainActor {

  case object BeginAnalysis

  case object DownloadTimeout

}
