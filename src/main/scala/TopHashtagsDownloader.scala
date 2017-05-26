import HashtagDownloader.DownloadResult
import akka.actor.Actor
import akka.event.LoggingReceive
import com.danielasfregola.twitter4s.TwitterRestClient
import com.danielasfregola.twitter4s.entities.{HashTag, Tweet}

import scala.concurrent.Await
import scala.concurrent.duration._

class TopHashtagsDownloader extends Actor {

  val restClient = TwitterRestClient()


  def downloadTopHashtags(user_id: Long, hashtag_number : Int): Seq[((String,Int),Int)] = {
    var f = restClient.userTimelineForUserId(user_id = user_id,count = 200)
    val response = Await.result(f, 100 second)
    val tweets = response.data
    val topHashtags: Seq[((String, Int), Int)] = getTopHashtags(tweets,hashtag_number).zipWithIndex
    val rankings = topHashtags.map { case ((entity, frequency), idx) => s"[${idx + 1}] $entity (found $frequency times)"}
    println(s"${user_id.toString.toUpperCase}'S TOP HASHTAGS:")
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
    case HashtagDownloader.DownloadTopHashtags(user_name,hashtag_number) =>
      sender() ! DownloadResult(downloadTopHashtags(user_name,hashtag_number))
  }
}


object HashtagDownloader {

  case class DownloadTopHashtags(user_id: Long,hashtag_number : Int)

  case class DownloadResult(data: Seq[((String,Int),Int)])

}

