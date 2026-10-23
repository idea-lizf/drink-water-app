package com.drinkwater.app.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface DrinkRecordDao {

    @Insert
    suspend fun insert(record: DrinkRecord): Long

    @Query("DELETE FROM drink_records WHERE id = :id")
    suspend fun deleteById(id: Long)

    @Query("SELECT * FROM drink_records ORDER BY created_at DESC")
    fun observeAll(): Flow<List<DrinkRecord>>
}
