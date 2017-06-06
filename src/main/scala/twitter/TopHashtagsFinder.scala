package twitter

import akka.actor.Actor
import akka.event.LoggingReceive
import com.danielasfregola.twitter4s.entities.{HashTag, Tweet}
import twitter.TopHashtagsFinder.{FindTopHashtags, TopHashtagsResult}

class TopHashtagsFinder extends Actor{

  def findTopHashtags(tweets: Seq[Tweet], hashtagsNumber: Int): Seq[String] ={
    val topHashtags: Seq[(String, Int)] = getTopHashtags(tweets, hashtagsNumber)
    val rankings = topHashtags.zipWithIndex.map { case ((entity, frequency), idx) => s"[${idx + 1}] $entity (found $frequency times)" }
    println(rankings.mkString("\n"))
    topHashtags.map(_._1)
  }

  def getTopHashtags(tweets: Seq[Tweet], n: Int): Seq[(String, Int)] = {
    val hashtags: Seq[Seq[HashTag]] = tweets.map { tweet =>
      tweet.entities.map(_.hashtags).getOrElse(Seq.empty)
    }

    val hashtagTexts: Seq[String] = hashtags.flatten.map(_.text.toLowerCase)
    val hashtagFrequencies: Map[String, Int] = hashtagTexts.groupBy(identity).mapValues(_.size)
    hashtagFrequencies.toSeq.sortBy { case (entity, frequency) => -frequency }.take(n)
  }

  override def receive: Receive = LoggingReceive {
    case FindTopHashtags(tweets, hashtagsNumber) =>
      TopHashtagsResult(findTopHashtags(tweets, hashtagsNumber))
  }

}

object TopHashtagsFinder {

  case class FindTopHashtags(tweets: Seq[Tweet], hashtagsNumber: Int)

  case class TopHashtagsResult(topHashtags: Seq[String])

}
