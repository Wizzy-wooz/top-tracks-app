package com.analytics.by.vodzianova

import com.analytics.by.vodzianova.service.LastFmAnalyticsService
import com.analytics.by.vodzianova.utils.ConfigLoader
import com.analytics.by.vodzianova.utils.SetupUtils._
import org.apache.spark.internal.Logging
import org.apache.spark.sql.{SQLContext, SparkSession}

import scala.util.{Failure, Success, Try}

/**
  * Main object which starts a service, which accepts the path to the dataset
  * and produces a list of top 10 songs played in the top 50 longest user sessions by tracks count.
  * Each user session may be comprised of one or more songs played by that user,
  * where each song is started within 20 minutes of the previous song's start time.
  */
object TopTracksApp extends App with Logging{

    val spark =
      SparkSession
        .builder()
        .appName("TopTracksApp")
        .config("spark.master", ConfigLoader.sparkMaster)
        .config("spark.driver.host", ConfigLoader.sparkDriverHost)
        .config("spark.executor.memory", ConfigLoader.sparkExecutorMemory)
        .config("spark.driver.memory", ConfigLoader.sparkDriverMemory)
        .config("spark.hadoop.validateOutputSpecs", ConfigLoader.checkExistingOutput)
        .config("spark.sql.broadcastTimeout", ConfigLoader.broadcastTimeout)
        .getOrCreate()

    implicit val sqlContext: SQLContext = spark.sqlContext

    Try(
      findTimeOfExecution {
        new LastFmAnalyticsService(
          sqlContext
            .read
            .option("delimiter", "\t")
            .csv(
              Try(getClass.getResource(ConfigLoader.dataFile).getPath)
                .toOption.getOrElse(throw new RuntimeException("Failed to get path to dataset. Check application.conf!")))
            .toDF(ConfigLoader.sparkDfColNames: _*))
          .getTopSongsInLongestUsersSessions()
          .show(false)
      }
    ) match {
      case Success(_) => log.info("Successfully finished execution.")
      case Failure(f) =>
        log.error("Failed to execute Application due to: " + f.getCause)
        f.printStackTrace()
    }
}