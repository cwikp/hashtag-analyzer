import MainActor.BeginAnalysis
import akka.actor.Actor
import akka.event.LoggingReceive
import com.danielasfregola.twitter4s.entities.Tweet
import twitter.{ProfilesDownloader, TweetsAnalyzer, TweetsDownloader}

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
    case TweetsAnalyzer.HashtagDates(hashtag, dates) =>
      println(">>>Hashtag: " + hashtag)
      println("Dates: " + dates.mkString(", "))
      plotCharts(hashtag, dates)
  }

  def plotCharts(hashtag: String, dates: Seq[(Long, Int)]): Unit = {
    val data: Seq[(String, Seq[(Long, Int)])] = Seq((hashtag, dates))
    context.actorSelection("../PlotDrawer") ! PlotDrawer.Draw(data)
  }

  def downloadProfiles(): Unit = {
    // or by lat and long: "50.0611591", "19.9383446"
    context.actorSelection("../ProfilesDownloader") ! ProfilesDownloader.DownloadProfilesByLocation(Main.LOCATION, Main.PROFILES_NUMBER)
  }

  def downloadTweets(userId: Long): Unit = {
    context.actorSelection("../TweetsDownloader") ! TweetsDownloader.DownloadTweets(userId, Main.DATE, Main.NUMBER_OF_DAYS_BACK)
  }

  def checkIfAllTweetsDownloaded(): Unit = {
    userProfilesProcessed += 1
    if (userProfilesProcessed >= userProfiles) {
      processTweets(tweetsList)
    }
  }

  def processTweets(tweets: List[Tweet]) = {
    println("TWEETS NUMBER: " + tweets.size)
    val topHashtagsFinder = context.actorSelection("../TopHashtagsFinder")
    context.actorSelection("../TweetsAnalyzer") ! TweetsAnalyzer.AnalyzeTweets(tweets, topHashtagsFinder)

  }

}

object MainActor {

  case object BeginAnalysis

}
