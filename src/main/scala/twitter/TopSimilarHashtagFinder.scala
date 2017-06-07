package twitter

import akka.actor.Actor
import akka.event.LoggingReceive
import com.danielasfregola.twitter4s.entities.{HashTag, Tweet}
import twitter.TopHashtagsFinder.FindTopHashtags
import twitter.TopSimilarHashtagFinder.TopSimilarHashtags
import utils.Levenshtein._

import scala.collection.mutable

class TopSimilarHashtagFinder(val levenstheinDistance: Int) extends Actor {

  def findTopHashtags(tweets: Seq[Tweet], hashtagsNumber: Int): Seq[Seq[String]] = {
    val topHashtags: Seq[(Seq[String], Int)] = getTopHashtags(tweets, hashtagsNumber)
    val rankings = topHashtags.zipWithIndex.map { case ((entity, frequency), idx) => s"[${idx + 1}] $entity (found $frequency times)" }
    println("\nTOP HASHTAGS:")
    println(rankings.mkString("\n"))
    topHashtags.map(_._1)
  }

  def getTopHashtags(tweets: Seq[Tweet], n: Int): Seq[(Seq[String], Int)] = {
    val hashtags: Seq[Seq[HashTag]] = tweets.map { tweet =>
      tweet.entities.map(_.hashtags).getOrElse(Seq.empty)
    }
    val hashtagTexts: Seq[String] = hashtags.flatten.map(_.text.toLowerCase)

    val hashtagFrequencies = similarHashtags(hashtagTexts).map(similar => (similar.toSet.toSeq, similar.size))

    hashtagFrequencies.toSeq.sortBy { case (entity, frequency) => -frequency }.take(n)
  }

  override def receive: Receive = LoggingReceive {
    case FindTopHashtags(tweets, hashtagsNumber) =>
      sender ! TopSimilarHashtags(findTopHashtags(tweets, hashtagsNumber), tweets)
  }

  private def hashtags(tweets: Seq[Tweet]) = tweets.map { tweet =>
    tweet.entities.map(_.hashtags).getOrElse(Seq.empty)
  }


  private def similarHashtags(hashtags: Seq[String]) = {
    val grouped = mutable.Map.empty[String, mutable.Buffer[String]]

    for (hashtag <- hashtags) {
      val similar = grouped.getSimilar(hashtag, levenstheinDistance).getOrElse(mutable.ArrayBuffer.empty[String])
      similar += hashtag
      grouped += (hashtag -> similar)
    }

    grouped.values.toSet
  }

}

object TopSimilarHashtagFinder {

  case class TopSimilarHashtags(topHashtags: Seq[Seq[String]], tweets: Seq[Tweet])

}