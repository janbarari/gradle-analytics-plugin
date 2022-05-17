package io.github.janbarari.gradle.utils

import java.time.LocalDate
import java.time.ZoneId

object Clock {

    fun getCurrentDayMillis(): Long {
        return LocalDate.now().atStartOfDay(ZoneId.of("UTC")).toEpochSecond() * 1000
    }

}
