package com.analytics.by.vodzianova.service

import org.apache.spark.sql._
import org.apache.spark.sql.expressions._
import org.apache.spark.sql.functions._
import org.apache.spark.sql.types._

/** Service  containing functionality to analyze LastFM history record data
  *
  *  @constructor creates a new service
  *  @param lastFmRecordDf dataframe to analyze
  *  @param spark SQLContext as implicit
  */
class LastFmAnalyticsService(lastFmRecordDf: DataFrame)(implicit spark: SQLContext) {
  import spark.implicits._

  /** gets top 10 (by default) song played in the 50 longest users' sessions
    * Each user session may be comprised of one or more songs played by that user,
    * where each song is started within 20 minutes of the previous song's start time.
    *
    *  @param topTracksNumber number of top songs to get
    *  @param sessionsNumber number of sessions to be defined as longest
    *  @param timeFrameInMinutes time window within the previous song's start time.
    *  @return dataset of top songs
    */
  def getTopSongsInLongestUsersSessions(
      topTracksNumber: Int = 10,
      sessionsNumber: Int = 50,
      timeFrameInMinutes: Long = 20): Dataset[Row] =

    dataFrameWithSession(timeFrameInMinutes)
      .groupBy($"sessionId")
      .agg(
        min("timestamp").as("sessionStartTime"),
        max("timestamp").as("sessionEndTime")
      )
      .withColumn("sessionDuration", $"sessionEndTime".cast(LongType) - $"sessionStartTime".cast(LongType))
      .orderBy($"sessionDuration".desc)
      .limit(sessionsNumber)
      .join(dataFrameWithSession(timeFrameInMinutes), "sessionId")
      .select(
        "trackId",
        "trackName"
      )
      .groupBy("trackId", "trackName")
      .count()
      .orderBy($"count".desc)
      .select("trackName")
      .limit(topTracksNumber)

  /** helps to identify user sessions
    *  @param minutes time window within the previous song's start time.
    *  @return dataframe
    */
  private def dataFrameWithSession(minutes: Long): DataFrame = {
    val window = Window
      .partitionBy("userId")
      .orderBy("timestamp")

    lastFmRecordDf
      .withColumn("timestamp", to_timestamp($"timestamp", "yyyy-MM-dd'T'HH:mm:ss'Z'")
        .cast(TimestampType))
      .orderBy("timestamp")
      .withColumn("previousTimestamp", lag($"timestamp".cast("long"), 1, 0)
        .over(window))
      .withColumn("diffMinutes", ($"timestamp".cast("long") - $"previousTimestamp") / 60D)
      .withColumn("isNewSession", ($"diffMinutes".cast(LongType) > minutes)
        .cast(IntegerType))
      .withColumn("sessionId", concat_ws("_", $"userId", sum("isNewSession")
        .over(window)))
  }
}