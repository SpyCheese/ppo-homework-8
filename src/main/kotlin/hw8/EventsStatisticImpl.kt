package hw8

import java.io.PrintStream
import java.time.Duration
import java.time.Instant
import java.util.*

class EventsStatisticImpl(private val clock: Clock = NormalClock) : EventsStatistic {
  private val statistics = mutableMapOf<String, PriorityQueue<Instant>>()

  override fun incEvent(name: String) {
    statistics.getOrPut(name) { PriorityQueue<Instant>() }.add(clock.now())
    removeOld(name)
  }

  override fun getEventStatisticByName(name: String): Double {
    removeOld(name)
    val size = statistics[name]?.size ?: 0
    return size.toDouble() / MINUTES_IN_HOUR
  }

  override fun getAllEventStatistic(): Map<String, Double> {
    statistics.keys.toList().forEach { removeOld(it) }
    return statistics.mapValues { it.value.size.toDouble() / MINUTES_IN_HOUR }
  }

  override fun printStatistic(out: PrintStream) {
    getAllEventStatistic().toList()
      .sortedBy { it.first }
      .forEach { out.println("${it.first}: ${it.second} wpm") }
  }

  private fun removeOld(name: String) {
    val events = statistics[name] ?: return
    val oldTime = clock.now().minus(HOUR)
    while (events.peek()?.isBefore(oldTime) == true) {
      events.poll()
    }
    if (events.isEmpty()) statistics.remove(name)
  }

  companion object {
    private const val MINUTES_IN_HOUR = 60.0
    private val HOUR = Duration.ofHours(1L)
  }
}