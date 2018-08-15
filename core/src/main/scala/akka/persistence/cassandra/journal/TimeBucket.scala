/*
 * Copyright (C) 2016 Typesafe Inc. <http://www.typesafe.com>
 */
package akka.persistence.cassandra.journal

import akka.persistence.cassandra.query.firstBucketFormatter

import java.time.LocalDateTime
import java.time.Instant
import java.time.ZoneOffset

import com.datastax.driver.core.utils.UUIDs
import java.util.UUID

import scala.concurrent.duration._
import scala.language.postfixOps

private[cassandra] object TimeBucket {

  def apply(timeuuid: UUID): TimeBucket =
    apply(UUIDs.unixTimestamp(timeuuid))

  def apply(epochTimestamp: Long): TimeBucket = {
    apply(LocalDateTime.ofInstant(Instant.ofEpochMilli(epochTimestamp), ZoneOffset.UTC))
  }
}

private[cassandra] final case class TimeBucket(private val time: LocalDateTime) {

  val hour: LocalDateTime = {
    val millisPerHour = (1 hour).toMillis
    val key: Long = time.toInstant(ZoneOffset.UTC).toEpochMilli / millisPerHour
    LocalDateTime.ofInstant(Instant.ofEpochMilli(key * millisPerHour), ZoneOffset.UTC)
  }

  val key: String = hour.toInstant(ZoneOffset.UTC).toEpochMilli.toString

  def next(): TimeBucket =
    TimeBucket(hour.plusHours(1))

  def isBefore(other: TimeBucket): Boolean =
    hour.isBefore(other.hour)

  override def toString: String = hour.format(firstBucketFormatter)
}
