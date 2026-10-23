package com.drinkwater.app.ui.stats

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.drinkwater.app.data.DailyTotal
import com.drinkwater.app.data.DrinkRepository
import com.drinkwater.app.data.DrinkStats
import java.time.LocalDate
import java.time.ZoneId
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn

data class StatsUiState(
    val rangeDays: Int = 7,
    val dailyTotals: List<DailyTotal> = emptyList(),
    val averageMl: Int = 0,
    val maxDay: DailyTotal? = null,
    val totalMl: Int = 0
)

class StatsViewModel(repository: DrinkRepository) : ViewModel() {

    private val zone: ZoneId = ZoneId.systemDefault()
    private val rangeDays = MutableStateFlow(7)

    val uiState: StateFlow<StatsUiState> =
        combine(repository.allRecords, rangeDays) { records, days ->
            val today = LocalDate.now(zone)
            val totals = DrinkStats.dailyTotals(records, days, today, zone)
            StatsUiState(
                rangeDays = days,
                dailyTotals = totals,
                averageMl = DrinkStats.averageMl(totals),
                maxDay = DrinkStats.maxTotal(totals)?.takeIf { it.totalMl > 0 },
                totalMl = totals.sumOf { it.totalMl }
            )
        }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), StatsUiState())

    fun setRange(days: Int) {
        rangeDays.value = days
    }
}
