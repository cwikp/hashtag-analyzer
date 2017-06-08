package twitter

import akka.actor.Actor
import akka.event.LoggingReceive
import com.danielasfregola.twitter4s.entities.{HashTag, Tweet, User}
import twitter.TopHashtagsFinder.{FindTopHashtags, TopHashtags}

class UserAwareTopHashtagFinder extends Actor {

  def findTopHashtags(tweets: Seq[Tweet], hashtagsNumber: Int): Seq[String] = {
    val topHashtags: Seq[(String, Int)] = getTopHashtags(tweets, hashtagsNumber)
    val rankings = topHashtags.zipWithIndex.map { case ((entity, frequency), idx) => s"[${idx + 1}] $entity (found $frequency times)" }
    println("TOP HASHTAGS:")
    println(rankings.mkString("\n"))
    topHashtags.map(_._1)
  }

  def getTopHashtags(tweets: Seq[Tweet], n: Int): Seq[(String, Int)] = {
    val hashtagsWithUniqueUsers = hashtagsWithUser(tweets).groupBy(_.hashtag).mapValues(_.toSet)

    val hashtagFrequencies: Map[String, Int] = hashtagsWithUniqueUsers.mapValues(_.size)
    hashtagFrequencies.toSeq.sortBy { case (entity, frequency) => -frequency }.take(n)
  }

  override def receive: Receive = LoggingReceive {
    case FindTopHashtags(tweets, hashtagsNumber) =>
      sender ! TopHashtags(findTopHashtags(tweets, hashtagsNumber), tweets)
  }

  private def hashtagsWithUser(tweets: Seq[Tweet]) = {
    def hashtags(tweet: Tweet): Seq[HashTag] = {
      tweet.entities.map(_.hashtags).getOrElse(Seq.empty)
    }

    def username(maybeUser: Option[User]): String = {
      maybeUser.map(_.name).getOrElse("SOME_USER")
    }

    tweets.flatMap(tweet => hashtags(tweet).map(hashtag => HashtagWithUser(hashtag.text.toLowerCase, username(tweet.user))))
  }

}

private case class HashtagWithUser(hashtag: String, user: String)