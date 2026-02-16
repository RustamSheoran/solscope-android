package com.example.solscope.presentation
import com.example.solscope.ui.theme.SolScopeTheme
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.example.solscope.presentation.home.HomeScreen

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            SolScopeTheme {
                HomeScreen()
            }
        }
    }
}