package com.drinkwater.app

import android.app.Application
import com.drinkwater.app.data.AppDatabase
import com.drinkwater.app.data.DrinkRepository

class DrinkWaterApp : Application() {

    val repository: DrinkRepository by lazy {
        DrinkRepository(AppDatabase.getInstance(this).drinkRecordDao())
    }
}
