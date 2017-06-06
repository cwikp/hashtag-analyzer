import java.time.LocalDate

import MainActor.BeginAnalysis
import akka.actor.Actor
import akka.event.LoggingReceive
import com.danielasfregola.twitter4s.entities.Tweet
import twitter.{ProfilesDownloader, TweetsDownloader}

class MainActor extends Actor {

  var userProfiles = 0
  var userProfilesProcessed = 0
  var tweetsList: List[Tweet] = List.empty

  override def receive: Receive = LoggingReceive {
    case BeginAnalysis =>
      downloadProfiles()
    case ProfilesDownloader.DownloadProfilesComplete(data: Seq[Long]) =>
      userProfiles = data.size
      data.foreach(id => downloadTweets(id))
    case TweetsDownloader.DownloadTweetsComplete(data: Seq[Tweet]) =>
      tweetsList = tweetsList ++ data
      checkIfAllTweetsDownloaded()
  }

  def downloadProfiles(): Unit = {
    // or by lat and long: "50.0611591", "19.9383446"
    context.actorSelection("../ProfilesDownloader") ! ProfilesDownloader.DownloadProfilesByLocation(Main.LOCATION, 10)
  }

  def downloadTweets(userId: Long): Unit = {
    context.actorSelection("../TweetsDownloader") ! TweetsDownloader.DownloadTweets(userId, LocalDate.now(), 7)
  }

  def checkIfAllTweetsDownloaded(): Unit = {
    userProfilesProcessed += 1
    if (userProfilesProcessed >= userProfiles) {
      processTweets(tweetsList)
    }
  }

  def processTweets(tweetsList: List[Tweet]) = {
    println(">>> TWEETS LIST SIZE: " + tweetsList.size)
  }

}

object MainActor {

  case object BeginAnalysis

}
