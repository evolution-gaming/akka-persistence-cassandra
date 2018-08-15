package akka.persistence.cassandra

import java.time.format.DateTimeFormatter

package object query {

  val firstBucketFormat = "yyyyMMdd'T'HH:mm"
  val firstBucketFormatter: DateTimeFormatter = DateTimeFormatter.ofPattern(firstBucketFormat)
}
