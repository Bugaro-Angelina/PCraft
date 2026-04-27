package com.example.pcraft.ui.navigation

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.pcraft.ui.screen.BuilderScreen
import com.example.pcraft.ui.screen.CompatibilityReportScreen
import com.example.pcraft.ui.screen.DetailsScreen
import com.example.pcraft.ui.screen.FavoritesScreen
import com.example.pcraft.ui.screen.HomeScreen
import com.example.pcraft.ui.screen.ProfileScreen
import com.example.pcraft.ui.screen.StoresListScreen

@Composable
fun NavGraph(navController: NavHostController, padding: PaddingValues) {
    NavHost(
        navController = navController,
        startDestination = Screen.Home.route,
        modifier = Modifier.padding(padding)
    ) {
        composable(Screen.Home.route) {
            HomeScreen(navController = navController, initialTypeId = null)
        }
        composable("home/{typeId}") { backStackEntry ->
            HomeScreen(
                navController = navController,
                initialTypeId = backStackEntry.arguments?.getString("typeId")
            )
        }
        composable(Screen.Details.route) { backStackEntry ->
            val componentId = backStackEntry.arguments?.getString("componentId") ?: ""
            DetailsScreen(navController, componentId)
        }
        composable(Screen.Builder.route) {
            BuilderScreen(navController)
        }
        composable(Screen.CompatibilityReport.route) {
            CompatibilityReportScreen(navController)
        }
        composable(Screen.StoresList.route) {
            StoresListScreen(navController)
        }
        composable(Screen.Favorites.route) {
            FavoritesScreen(navController)
        }
        composable(Screen.Profile.route) {
            ProfileScreen(navController)
        }
    }
}
