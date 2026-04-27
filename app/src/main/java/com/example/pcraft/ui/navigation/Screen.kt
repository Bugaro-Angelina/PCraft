package com.example.pcraft.ui.navigation

sealed class Screen(val route: String) {
    object Home : Screen("home") {
        fun createRoute(typeId: String? = null): String = if (typeId.isNullOrBlank()) {
            route
        } else {
            "home/$typeId"
        }
    }

    object Details : Screen("details/{componentId}") {
        fun createRoute(componentId: String) = "details/$componentId"
    }

    object Builder : Screen("builder")
    object CompatibilityReport : Screen("compatibility_report")
    object StoresList : Screen("stores_list")
    object Favorites : Screen("favorites")
    object Profile : Screen("profile")
}
