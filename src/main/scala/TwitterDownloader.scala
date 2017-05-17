import Downloader.DownloadResult
import akka.actor.Actor
import akka.event.LoggingReceive
import com.danielasfregola.twitter4s.TwitterRestClient

import scala.concurrent.Await
import scala.concurrent.duration._

class TwitterDownloader extends Actor {

  val restClient = TwitterRestClient()

  def downloadProfiles(lat: String, long: String, range: Integer, number: BigDecimal, current: Long, page: Integer): Seq[Long] = {
    if (current > number)
      return Seq.empty

    var f = restClient.searchForUser("geocode:\"%s,%s,%smi\"".format(lat, long, range.toString), page = page)
    val result = Await.result(f, 100 second)

    result.data.foreach(d => println(d.id, d.location))
    result.data.map(f => f.id) ++ downloadProfiles(lat, long, range, number, current + result.data.size, page + 1)
  }


  override def receive: Receive = LoggingReceive {
    case Downloader.DownloadProfiles(lat, long, _, number) =>
      sender() ! DownloadResult(downloadProfiles(lat, long, 10, number, 0, 1))
  }
}


object Downloader {

  case class DownloadProfiles(lat: String, long: String, range: BigDecimal = 10, number: BigDecimal = 100)

  case class DownloadResult(data: Seq[Long])

}

