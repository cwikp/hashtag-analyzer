package utils

import scala.collection.mutable
import scala.math.min

object Levenshtein {

  def distance(s1: String, s2: String): Int = {
    val dist = Array.tabulate(s2.length + 1, s1.length + 1) { (j, i) => if (j == 0) i else if (i == 0) j else 0 }

    for (j <- 1 to s2.length; i <- 1 to s1.length)
      dist(j)(i) = if (s2(j - 1) == s1(i - 1)) dist(j - 1)(i - 1)
      else minimum(dist(j - 1)(i) + 1, dist(j)(i - 1) + 1, dist(j - 1)(i - 1) + 1)

    dist(s2.length)(s1.length)
  }

  private def minimum(i1: Int, i2: Int, i3: Int) = min(min(i1, i2), i3)


  implicit class MapWithLevenshtein(map: mutable.Map[String, mutable.Buffer[String]]) {
    def containsSimilar(similar: String, maxDistance: Int): Boolean = {
      map.keys.exists(key => distance(key, similar) <= maxDistance)
    }

    def getSimilar(similar: String, maxDistance: Int): Option[mutable.Buffer[String]] = {
      map.keys.find(key => distance(key, similar) <= maxDistance) match {
        case Some(key) => map.get(key)
        case _ => None
      }
    }
  }

}