package twitter

import java.util.Date

import akka.actor.{Actor, ActorRef, ActorSelection}
import akka.event.LoggingReceive
import com.danielasfregola.twitter4s.entities.Tweet
import twitter.TweetsAnalyzer.{AnalyzeTweets, HashtagDates}


class TweetsAnalyzer extends Actor {

  private var mainActorRef: ActorRef = _

  override def receive: Receive = LoggingReceive {
    case AnalyzeTweets(tweets, topHashtagFinder) =>
      topHashtagFinder ! TopHashtagsFinder.FindTopHashtags(tweets)
      mainActorRef = sender
    case TopHashtagsFinder.TopHashtags(topHashtags, tweets) =>
      sendTopHashtagDates(topHashtags, tweets)
  }

  private def sendTopHashtagDates(topHashtags: Seq[String], tweets: Seq[Tweet]): Unit = {
    topHashtags.foreach(topHashtag => {
      val tweetsContainingHashtag: Seq[Tweet] = tweets.filter(tweet =>
        tweet.entities
          .map(_.hashtags).getOrElse(Seq.empty)
          .map(_.text.toLowerCase).contains(topHashtag))
      val tweetDates = tweetsContainingHashtag
        .map(tweet => tweet.created_at)
      mainActorRef ! HashtagDates(topHashtag, groupDates(tweetDates))
    })
  }

  private def groupDates(dates: Seq[Date]): Seq[(Long, Int)] ={
    val timestamps = dates.map(_.getTime)
    val datesFrequencies: Map[Long, Int] = timestamps.groupBy(identity).mapValues(_.size)
    datesFrequencies.toSeq.sortBy { case (entity, frequency) => -frequency }
  }

}

object TweetsAnalyzer {

  case class AnalyzeTweets(tweets: List[Tweet], topHashtagFinder: ActorSelection)

  case class HashtagDates(hashtag: String, dates: Seq[(Long, Int)])

}
