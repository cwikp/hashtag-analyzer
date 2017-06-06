package twitter

import java.time.{LocalDate, ZoneId}
import java.util.Date

import akka.actor.Actor
import akka.event.LoggingReceive
import com.danielasfregola.twitter4s.TwitterRestClient
import com.danielasfregola.twitter4s.entities.{HashTag, Tweet}
import twitter.TweetsDownloader.DownloadResult

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class TweetsDownloader extends Actor {

  val restClient = TwitterRestClient()

  def downloadTweets(userId: Long, hashtagsNumber: Int, date: LocalDate, numberOfDaysBack: Int): Future[Unit] = {
    val startDate = Date.from(date.minusDays(numberOfDaysBack).atStartOfDay(ZoneId.systemDefault).toInstant)
    val endDate = Date.from(date.atStartOfDay(ZoneId.systemDefault).toInstant)
    restClient.userTimelineForUserId(user_id = userId, count = 200).map { tweets =>
      val filteredTweets = tweets.data
        .filter(_.created_at.before(endDate))
        .filter(_.created_at.after(startDate))
        .filter(_.entities.map(_.hashtags).get.nonEmpty)
      println(filteredTweets.map(t => t.entities.map(_.hashtags)).mkString("\n"))
    }
  }


  override def receive: Receive = LoggingReceive {
    case TweetsDownloader.DownloadTweets(user_name, hashtag_number, date, number_of_days_back) =>
      sender() ! DownloadResult(downloadTweets(user_name, hashtag_number, date, number_of_days_back))
  }
}


object TweetsDownloader {

  case class DownloadTweets(userId: Long, hashtagsNumber: Int, date: LocalDate, numberOfDaysBack: Int)

  case class DownloadResult(data: Future[Unit])

}

