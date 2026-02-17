package com.example.solscope.presentation

import android.os.Bundle
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.padding
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import com.example.solscope.presentation.home.HomeScreen
import kotlinx.coroutines.launch
import com.example.solscope.presentation.navigation.Screen
import com.example.solscope.presentation.result.ResultScreen
import com.example.solscope.ui.theme.SolScopeTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        enableEdgeToEdge()


        
        setContent {
            val watchlistRepository = com.example.solscope.data.watchlist.WatchlistRepository(applicationContext)
            val watchlistViewModel: com.example.solscope.presentation.watchlist.WatchlistViewModel = androidx.lifecycle.viewmodel.compose.viewModel(
                factory = com.example.solscope.presentation.watchlist.WatchlistViewModel.Factory(watchlistRepository)
            )

            // Wallet Connection Manager
            val walletManager = remember { com.example.solscope.data.wallet.WalletConnectionManager() }
            // Stub doesn't need sender
            // val walletSender = com.solana.mobilewalletadapter.clientlib.ActivityResultSender(this)
            
            val connectedWallet by walletManager.connectedWallet.collectAsState()
            val scope = rememberCoroutineScope()

            val systemDarkTheme = androidx.compose.foundation.isSystemInDarkTheme()
            var isDarkTheme by remember { mutableStateOf(systemDarkTheme) }

            SolScopeTheme(darkTheme = isDarkTheme) {
                val navController = rememberNavController()
                val currentScreen = navController.currentBackStackEntryAsState().value?.destination?.route

                // Show bottom bar only on top-level screens
                val showBottomBar = currentScreen == Screen.Home.route || currentScreen == Screen.Watchlist.route

                androidx.compose.material3.Scaffold(
                    bottomBar = {
                        if (showBottomBar) {
                            com.example.solscope.presentation.components.SolScopeBottomBar(navController)
                        }
                    }
                ) { innerPadding ->
                    NavHost(
                        navController = navController,
                        startDestination = Screen.Home.route,
                        modifier = androidx.compose.ui.Modifier.padding(innerPadding)
                    ) {
                        composable(Screen.Home.route) {
                            HomeScreen(
                                onAnalyze = { address ->
                                    navController.navigate(
                                        Screen.Result.createRoute(address)
                                    )
                                },
                                connectedWallet = connectedWallet,
                                onConnect = {
                                    scope.launch {
                                        walletManager.connect(null)
                                    }
                                },
                                onDisconnect = {
                                    walletManager.disconnect()
                                },
                                isDarkTheme = isDarkTheme,
                                onToggleTheme = { isDarkTheme = !isDarkTheme }
                            )
                        }

                        composable(Screen.Watchlist.route) {
                            com.example.solscope.presentation.watchlist.WatchlistScreen(
                                onAnalyze = { address ->
                                    navController.navigate(
                                        Screen.Result.createRoute(address)
                                    )
                                },
                                viewModel = watchlistViewModel,
                                isDarkTheme = isDarkTheme,
                                onToggleTheme = { isDarkTheme = !isDarkTheme }
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
                                onBack = { navController.popBackStack() },
                                watchlistViewModel = watchlistViewModel
                            )
                        }
                    }
                }
            }
        }
    }
}