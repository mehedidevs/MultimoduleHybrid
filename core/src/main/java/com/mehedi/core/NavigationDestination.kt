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

    // Product List (XML)
    object ProductList : NavigationDestination("product_list")
   // object ProductDetails : NavigationDestination("product_details")

    // Product Detail (Compose)
    object ProductDetail : NavigationDestination(
        route = "product_detail",
        args = mapOf(
            "id" to NavType.StringType,
            "title" to NavType.StringType
        )
    ) {
        fun createRoute(id: String, title: String): String {
            return "product_detail?id=$id&title=$title"
        }
    }

    // Cart (Compose)
    object Cart : NavigationDestination(
        route = "cart",
        args = mapOf(
            "items" to NavType.StringType // JSON encoded cart items
        )
    ) {
        fun createRoute(cartItemsJson: String): String {
            return "cart?items=$cartItemsJson"
        }
    }

    fun getRoute(): String {
        return if (args.isEmpty()) {
            route
        } else {
            "$route?" + args.keys.joinToString("&") { key ->
                "$key={$key}"
            }
        }
    }

    fun getArguments(): List<NamedNavArgument> {
        return args.map { (key, navType) ->
            navArgument(key) { type = navType }
        }
    }
}