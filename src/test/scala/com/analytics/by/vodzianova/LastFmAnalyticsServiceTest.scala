package com.analytics.by.vodzianova

import com.analytics.by.vodzianova.service.LastFmAnalyticsService
import com.analytics.by.vodzianova.utils.ConfigLoader
import com.holdenkarau.spark.testing.{DatasetSuiteBase, SharedSparkContext}
import org.apache.spark.sql.Row
import org.apache.spark.sql.types._
import org.scalatest.FlatSpec

import scala.util.Try

class LastFmAnalyticsServiceTest extends FlatSpec with SharedSparkContext with DatasetSuiteBase {

  "getTopSongsInLongestUsersSessions " should
    "return  a list of top 3 songs played in the top 3 longest user sessions by tracks count " in {

    assertDatasetEquals(
       sqlContext.createDataFrame(
        sc.parallelize(
          List(
            Row("Song1"),
            Row("Song2"),
            Row("Song3")
          )
        ),
        StructType(Array(StructField("trackName", StringType, nullable = true)))
      ),
       new LastFmAnalyticsService(
         sqlContext
           .read
           .option("delimiter", "\t")
           .csv(
             Try(getClass.getResource(ConfigLoader.testDataFile).getPath)
               .toOption.getOrElse(throw new RuntimeException("Failed to get path to test dataset. Check application.conf!")))
           .toDF(ConfigLoader.sparkDfColNames: _*)
       ).getTopSongsInLongestUsersSessions(3, 3))
    }
  }