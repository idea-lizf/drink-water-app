package com.drinkwater.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.drinkwater.app.ui.AppScaffold
import com.drinkwater.app.ui.theme.DrinkWaterTheme

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            DrinkWaterTheme {
                AppScaffold()
            }
        }
    }
}
