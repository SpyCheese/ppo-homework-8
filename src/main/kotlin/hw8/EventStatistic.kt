package hw8

import java.io.PrintStream

interface EventsStatistic {
  fun incEvent(name: String)
  fun getEventStatisticByName(name: String): Double
  fun getAllEventStatistic(): Map<String, Double>
  fun printStatistic(out: PrintStream = System.out)
}
