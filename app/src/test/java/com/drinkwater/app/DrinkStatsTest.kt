package com.drinkwater.app

import com.drinkwater.app.data.DailyTotal
import com.drinkwater.app.data.DrinkRecord
import com.drinkwater.app.data.DrinkStats
import java.time.LocalDate
import java.time.ZoneId
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class DrinkStatsTest {

    private val zone = ZoneId.of("Asia/Shanghai")

    private fun recordAt(date: String, hour: Int = 10, minute: Int = 0, amountMl: Int): DrinkRecord {
        val millis = LocalDate.parse(date)
            .atTime(hour, minute)
            .atZone(zone)
            .toInstant()
            .toEpochMilli()
        return DrinkRecord(id = 0, amountMl = amountMl, createdAt = millis)
    }

    @Test
    fun todayRecords_filtersByDayAndSortsNewestFirst() {
        val today = LocalDate.parse("2026-10-23")
        val records = listOf(
            recordAt("2026-10-22", amountMl = 200),
            recordAt("2026-10-23", hour = 9, amountMl = 150),
            recordAt("2026-10-23", hour = 20, amountMl = 250),
            recordAt("2026-10-23", hour = 12, amountMl = 500),
            recordAt("2026-10-24", amountMl = 999)
        )
        val result = DrinkStats.todayRecords(records, today, zone)
        assertEquals(listOf(250, 500, 150), result.map { it.amountMl })
    }

    @Test
    fun dailyTotals_groupsSumsAndFillsGaps() {
        val today = LocalDate.parse("2026-10-23")
        val records = listOf(
            recordAt("2026-10-23", hour = 9, amountMl = 150),
            recordAt("2026-10-23", hour = 10, amountMl = 250),
            recordAt("2026-10-21", amountMl = 300),
            recordAt("2026-09-01", amountMl = 5000)
        )
        val totals = DrinkStats.dailyTotals(records, days = 7, today = today, zone = zone)

        assertEquals(7, totals.size)
        assertEquals(LocalDate.parse("2026-10-17"), totals.first().date)
        assertEquals(LocalDate.parse("2026-10-23"), totals.last().date)
        assertEquals(400, totals.last().totalMl)
        assertEquals(300, totals[4].totalMl)
        assertEquals(0, totals.first().totalMl)
    }

    @Test
    fun dailyTotals_respectsLocalTimezone() {
        val records = listOf(
            DrinkRecord(
                id = 0,
                amountMl = 100,
                createdAt = LocalDate.parse("2026-10-24")
                    .atTime(1, 0)
                    .atZone(zone)
                    .toInstant()
                    .toEpochMilli()
            )
        )
        val today = LocalDate.parse("2026-10-24")
        val totals = DrinkStats.dailyTotals(records, days = 1, today = today, zone = zone)
        assertEquals(100, totals[0].totalMl)

        val utcTotals = DrinkStats.dailyTotals(
            records, days = 1,
            today = LocalDate.parse("2026-10-23"),
            zone = ZoneId.of("UTC")
        )
        assertEquals(100, utcTotals[0].totalMl)
    }

    @Test
    fun averageAndMax() {
        val totals = listOf(
            DailyTotal(LocalDate.parse("2026-10-21"), 300),
            DailyTotal(LocalDate.parse("2026-10-22"), 0),
            DailyTotal(LocalDate.parse("2026-10-23"), 600)
        )
        assertEquals(300, DrinkStats.averageMl(totals))
        assertEquals(600, DrinkStats.maxTotal(totals)?.totalMl)
    }

    @Test
    fun emptyInputReturnsZeroOrEmpty() {
        assertEquals(0, DrinkStats.averageMl(emptyList()))
        assertNull(DrinkStats.maxTotal(emptyList()))
        val totals = DrinkStats.dailyTotals(
            emptyList(), days = 7,
            today = LocalDate.parse("2026-10-23"),
            zone = zone
        )
        assertEquals(7, totals.size)
        assertTrue(totals.all { it.totalMl == 0 })
    }
}
