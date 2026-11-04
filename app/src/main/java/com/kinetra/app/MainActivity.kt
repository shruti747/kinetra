package com.kinetra.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import com.kinetra.app.ui.main.MainContainer
import com.kinetra.app.ui.theme.KinetraTheme

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            KinetraTheme {
                Surface(
                    modifier = Modifier.fillMaxSize()
                ) {
                    MainContainer()
                }
            }
        }
    }
}
