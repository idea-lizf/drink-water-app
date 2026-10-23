package com.drinkwater.app.data

import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId

data class DailyTotal(
    val date: LocalDate,
    val totalMl: Int
)

object DrinkStats {

    fun toLocalDate(epochMillis: Long, zone: ZoneId): LocalDate =
        Instant.ofEpochMilli(epochMillis).atZone(zone).toLocalDate()

    fun todayRecords(
        records: List<DrinkRecord>,
        today: LocalDate,
        zone: ZoneId
    ): List<DrinkRecord> =
        records
            .filter { toLocalDate(it.createdAt, zone) == today }
            .sortedByDescending { it.createdAt }

    fun dailyTotals(
        records: List<DrinkRecord>,
        days: Int,
        today: LocalDate,
        zone: ZoneId
    ): List<DailyTotal> {
        val firstDay = today.minusDays((days - 1).toLong())
        val totalsByDay = records
            .filter { toLocalDate(it.createdAt, zone) >= firstDay }
            .groupBy { toLocalDate(it.createdAt, zone) }
            .mapValues { (_, dayRecords) -> dayRecords.sumOf { it.amountMl } }
        return (0 until days).map { offset ->
            val day = firstDay.plusDays(offset.toLong())
            DailyTotal(day, totalsByDay[day] ?: 0)
        }
    }

    fun averageMl(dailyTotals: List<DailyTotal>): Int =
        if (dailyTotals.isEmpty()) 0
        else dailyTotals.sumOf { it.totalMl } / dailyTotals.size

    fun maxTotal(dailyTotals: List<DailyTotal>): DailyTotal? =
        dailyTotals.maxByOrNull { it.totalMl }
}
