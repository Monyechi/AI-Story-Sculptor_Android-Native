package com.monyechi.aistorysculptor

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.monyechi.aistorysculptor.ui.navigation.AppNavHost
import com.monyechi.aistorysculptor.ui.theme.AIStorySculptorTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            AIStorySculptorTheme {
                AppNavHost()
            }
        }
    }
}
