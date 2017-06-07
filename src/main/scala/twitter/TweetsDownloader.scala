package twitter

import java.time.{LocalDate, ZoneId}
import java.util.Date

import akka.actor.{Actor, ActorRef}
import akka.event.LoggingReceive
import com.danielasfregola.twitter4s.TwitterRestClient
import com.danielasfregola.twitter4s.entities.Tweet
import twitter.TweetsDownloader.DownloadTweetsComplete

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class TweetsDownloader extends Actor {

  val restClient = TwitterRestClient()

  def downloadTweets(userId: Long, date: LocalDate, numberOfDaysBack: Int, sender: ActorRef): Future[Unit] = {
    val startDate = Date.from(date.minusDays(numberOfDaysBack).atStartOfDay(ZoneId.systemDefault).toInstant)
    val endDate = Date.from(date.atStartOfDay(ZoneId.systemDefault).toInstant)
    restClient.userTimelineForUserId(user_id = userId, count = 200).map { tweets =>
      val filteredTweets = tweets.data
        .filter(_.created_at.before(endDate))
        .filter(_.created_at.after(startDate))
        .filter(_.entities.map(_.hashtags).get.nonEmpty)
      sender ! DownloadTweetsComplete(filteredTweets)
    }
  }

  override def receive: Receive = LoggingReceive {
    case TweetsDownloader.DownloadTweets(userId, date, numberOfDaysBack) =>
      downloadTweets(userId, date, numberOfDaysBack, sender)
  }
}

object TweetsDownloader {

  case class DownloadTweets(userId: Long, date: LocalDate, numberOfDaysBack: Int)

  case class DownloadTweetsComplete(data: Seq[Tweet])

}

