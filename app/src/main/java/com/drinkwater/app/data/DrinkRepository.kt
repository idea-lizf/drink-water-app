package com.drinkwater.app.data

import kotlinx.coroutines.flow.Flow

class DrinkRepository(private val dao: DrinkRecordDao) {

    val allRecords: Flow<List<DrinkRecord>> = dao.observeAll()

    suspend fun add(amountMl: Int) {
        dao.insert(DrinkRecord(amountMl = amountMl, createdAt = System.currentTimeMillis()))
    }

    suspend fun delete(id: Long) {
        dao.deleteById(id)
    }

    suspend fun restore(record: DrinkRecord) {
        dao.insert(record.copy(id = 0))
    }
}
