package hw8

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.io.ByteArrayOutputStream
import java.io.PrintStream
import java.time.Instant

class EventsStatisticImplTest {
  private lateinit var clock: SetableClock
  private lateinit var eventStatistic: EventsStatistic

  @BeforeEach
  fun before() {
    clock = SetableClock(START_TIME)
    eventStatistic = EventsStatisticImpl(clock)
  }

  @Test
  fun testNothing() {
    assertEquals(0.0, eventStatistic.getEventStatisticByName("A"))
    assertEquals(emptyMap<String, Double>(), eventStatistic.getAllEventStatistic())
  }

  @Test
  fun testPrint() {
    repeat(120) { eventStatistic.incEvent("Beta") }
    repeat(30) { eventStatistic.incEvent("Alpha") }
    repeat(60) { eventStatistic.incEvent("Gamma") }
    val stream = ByteArrayOutputStream()
    PrintStream(stream).use { printStream ->
      eventStatistic.printStatistic(printStream)
    }
    assertEquals("""
      Alpha: ${0.5} wpm
      Beta: ${2.0} wpm
      Gamma: ${1.0} wpm
    """.trimIndent().trim(), String(stream.toByteArray(), Charsets.UTF_8).trim())
  }

  @Test
  fun testTimePassed() {
    eventStatistic.incEvent("A")
    assertEquals(1.0 / 60.0, eventStatistic.getEventStatisticByName("A"))
    eventStatistic.incEvent("A")
    assertEquals(2.0 / 60.0, eventStatistic.getEventStatisticByName("A"))

    clock.setTime(START_TIME.plusSeconds(30 * 60))
    eventStatistic.incEvent("A")
    assertEquals(3.0 / 60.0, eventStatistic.getEventStatisticByName("A"))

    clock.setTime(START_TIME.plusSeconds(75 * 60))
    assertEquals(1.0 / 60.0, eventStatistic.getEventStatisticByName("A"))

    clock.setTime(START_TIME.plusSeconds(91 * 60))
    assertEquals(0.0, eventStatistic.getEventStatisticByName("A"))
  }

  @Test
  fun testGetAllEventStatistic() {
    repeat(5) { eventStatistic.incEvent("Aaa") }
    repeat(3) { eventStatistic.incEvent("Bbb") }
    repeat(2) { eventStatistic.incEvent("Ccc") }
    assertEquals(mapOf("Aaa" to 5.0 / 60.0, "Bbb" to 3.0 / 60.0, "Ccc" to 2.0 / 60.0), eventStatistic.getAllEventStatistic())

    clock.setTime(START_TIME.plusSeconds(30 * 60))
    eventStatistic.incEvent("Aaa")
    eventStatistic.incEvent("Ccc")
    assertEquals(mapOf("Aaa" to 6.0 / 60.0, "Bbb" to 3.0 / 60.0, "Ccc" to 3.0 / 60.0), eventStatistic.getAllEventStatistic())

    clock.setTime(START_TIME.plusSeconds(75 * 60))
    assertEquals(mapOf("Aaa" to 1.0 / 60.0, "Ccc" to 1.0 / 60.0), eventStatistic.getAllEventStatistic())

    clock.setTime(START_TIME.plusSeconds(91 * 60))
    assertEquals(emptyMap<String, Double>(), eventStatistic.getAllEventStatistic())
  }

  companion object {
    private val START_TIME = Instant.ofEpochSecond(1000000000L)
  }
}