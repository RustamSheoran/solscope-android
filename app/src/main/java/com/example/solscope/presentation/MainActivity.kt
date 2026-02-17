package com.example.solscope.presentation

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.solscope.presentation.home.HomeScreen
import com.example.solscope.presentation.navigation.Screen
import com.example.solscope.presentation.result.ResultScreen
import com.example.solscope.ui.theme.SolScopeTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            SolScopeTheme {
                val navController = rememberNavController()
                
                NavHost(
                    navController = navController,
                    startDestination = Screen.Home.route
                ) {
                    composable(Screen.Home.route) {
                        HomeScreen(
                            onAnalyze = { address ->
                                navController.navigate(
                                    Screen.Result.createRoute(address)
                                )
                            }
                        )
                    }
                    
                    composable(
                        route = Screen.Result.route,
                        arguments = listOf(
                            navArgument("address") { type = NavType.StringType }
                        )
                    ) { backStackEntry ->
                        val address = backStackEntry.arguments?.getString("address") ?: ""
                        ResultScreen(
                            address = address,
                            onBack = { navController.popBackStack() }
                        )
                    }
                }
            }
        }
    }
}