package twitter

import java.time.{LocalDate, ZoneId}
import java.util.Date

import akka.actor.Actor
import akka.event.LoggingReceive
import com.danielasfregola.twitter4s.TwitterRestClient
import com.danielasfregola.twitter4s.entities.{HashTag, Tweet}
import twitter.TweetsDownloader.DownloadResult

import scala.concurrent.Await
import scala.concurrent.duration._

class TweetsDownloader extends Actor {

  val restClient = TwitterRestClient()


  def downloadTopHashtagsInTime(user_id: Long, hashtag_number : Int, date : LocalDate, number_of_days_back : Int): Seq[((String,Int),Int)] = {
    val start_date = Date.from(date.minusDays(number_of_days_back).atStartOfDay(ZoneId.systemDefault).toInstant)
    val end_date = Date.from(date.atStartOfDay(ZoneId.systemDefault).toInstant)
    var f = restClient.userTimelineForUserId(user_id = user_id,count = 200)
    val response = Await.result(f, 100 second)
    val tweets = response.data
    val filteredTweets = tweets.filter((t) => (t.created_at.before(end_date))).filter((t) => (t.created_at.after(start_date)))
    val topHashtags: Seq[((String, Int), Int)] = getTopHashtags(filteredTweets,hashtag_number).zipWithIndex
    val rankings = topHashtags.map { case ((entity, frequency), idx) => s"[${idx + 1}] $entity (found $frequency times)"}
    println(s"${user_id.toString.toUpperCase}'S TOP HASHTAGS BETWEEN "+ end_date + " " + start_date)
    println(rankings.mkString("\n"))
    return topHashtags;
  }

  def getTopHashtags(tweets: Seq[Tweet], n: Int): Seq[(String, Int)] = {
    val hashtags: Seq[Seq[HashTag]] = tweets.map { tweet =>
      tweet.entities.map(_.hashtags).getOrElse(Seq.empty)
    }

    val hashtagTexts: Seq[String] = hashtags.flatten.map(_.text.toLowerCase)
    val hashtagFrequencies: Map[String, Int] = hashtagTexts.groupBy(identity).mapValues(_.size)
    hashtagFrequencies.toSeq.sortBy { case (entity, frequency) => -frequency }.take(n)
  }


  override def receive: Receive = LoggingReceive{
    case TweetsDownloader.DownloadTopHashtags(user_name,hashtag_number,date,number_of_days_back) =>
      sender() ! DownloadResult(downloadTopHashtagsInTime(user_name,hashtag_number,date,number_of_days_back))
  }
}


object TweetsDownloader {

  case class DownloadTopHashtags(user_id: Long,hashtag_number : Int, date : LocalDate, number_of_days_back : Int)

  case class DownloadResult(data: Seq[((String,Int),Int)])

}

