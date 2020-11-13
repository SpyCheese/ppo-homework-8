package hw8

import java.time.Instant

interface Clock {
  fun now(): Instant
}

object NormalClock : Clock {
  override fun now() = Instant.now()!!
}

class SetableClock(private var time: Instant): Clock {
  override fun now() = time

  fun setTime(newTime: Instant) {
    time = newTime
  }
}