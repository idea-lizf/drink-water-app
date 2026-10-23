package com.drinkwater.app.ui.today

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.drinkwater.app.data.DrinkRecord
import com.drinkwater.app.data.DrinkRepository
import com.drinkwater.app.data.DrinkStats
import java.time.LocalDate
import java.time.ZoneId
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class TodayUiState(
    val todayTotalMl: Int = 0,
    val todayRecords: List<DrinkRecord> = emptyList()
)

class TodayViewModel(private val repository: DrinkRepository) : ViewModel() {

    private val zone: ZoneId = ZoneId.systemDefault()

    val uiState: StateFlow<TodayUiState> = repository.allRecords
        .map { records ->
            val todayList = DrinkStats.todayRecords(records, LocalDate.now(zone), zone)
            TodayUiState(
                todayTotalMl = todayList.sumOf { it.amountMl },
                todayRecords = todayList
            )
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), TodayUiState())

    fun addWater(amountMl: Int) {
        if (amountMl <= 0) return
        viewModelScope.launch { repository.add(amountMl) }
    }

    fun deleteRecord(record: DrinkRecord) {
        viewModelScope.launch { repository.delete(record.id) }
    }

    fun restoreRecord(record: DrinkRecord) {
        viewModelScope.launch { repository.restore(record) }
    }
}
