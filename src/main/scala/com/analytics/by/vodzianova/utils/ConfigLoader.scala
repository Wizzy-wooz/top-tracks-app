package com.analytics.by.vodzianova.utils

import com.typesafe.config.{ Config, ConfigFactory }

import scala.collection.JavaConversions._

/** Utility object to load config
  * Fetches info from application.conf
  */
object ConfigLoader {

  val conf: Config = ConfigFactory.load()

  val sparkMaster = conf.getString("sparkMaster")
  val sparkDriverHost = conf.getString("sparkDriverHost")
  val sparkExecutorMemory = conf.getString("sparkExecutorMemory")
  val sparkDriverMemory = conf.getString("sparkDriverMemory")
  val broadcastTimeout = conf.getString("broadcastTimeout")
  val sparkDfColNames = conf.getStringList("sparkDfColNames").toList

  val checkExistingOutput = conf.getString("checkExistingOutput")

  val dataFile = conf.getString("dataFile")
  val testDataFile = conf.getString("testDataFile")
}