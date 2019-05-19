package com.analytics.by.vodzianova.utils

import java.util.concurrent.TimeUnit

/** Utility object to find time of App execution
  * Result is showed in minutes
  */
object SetupUtils {
  def findTimeOfExecution[R](block: => R): R = {
    val t0 = System.nanoTime()
    val result = block
    val t1 = System.nanoTime()

    println("Elapsed time: " + TimeUnit.MINUTES.convert(t1 - t0, TimeUnit.NANOSECONDS) + " minutes")
    result
  }
}
