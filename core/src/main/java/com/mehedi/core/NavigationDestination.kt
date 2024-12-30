package com.mehedi.core


import androidx.navigation.NamedNavArgument
import androidx.navigation.NavType
import androidx.navigation.navArgument

sealed class NavigationDestination(
    private val route: String,
    private val args: Map<String, NavType<*>> = emptyMap()
) {
    // Home (XML)
    object Home : NavigationDestination("home")


    object ProductList : NavigationDestination(
        route = "product_list",

        ) {
        fun createRoute(productId: String): String {
            return "product_list"
        }
    }

    object ProductDetail : NavigationDestination(
        route = "product_detail",
        args = mapOf(
            "productId" to NavType.StringType
        )
    ) {
        fun createRoute(productId: String): String {
            return "product_detail/$productId"
        }
    }

    fun getRoute(): String {
        return if (args.isEmpty()) {
            route
        } else {
            val argKeys = args.keys.joinToString("/") { "{$it}" }
            "$route/$argKeys"
        }
    }

    fun getArguments(): List<NamedNavArgument> {
        return args.map { (key, navType) ->
            navArgument(key) { type = navType }
        }
    }
}