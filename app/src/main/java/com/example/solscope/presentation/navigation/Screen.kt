package com.example.solscope.presentation.navigation

sealed class Screen(val route: String) {
    object Onboarding : Screen("onboarding")
    object Home : Screen("home")
    object Result : Screen("result/{address}") {
        fun createRoute(address: String) = "result/$address"
    }
    object Activity : Screen("activity")
    object Insights : Screen("insights")
    object Settings : Screen("settings")
}
