package com.mehedi.hybridnavigation

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.NavType
import androidx.navigation.createGraph
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.fragment
import com.mehedi.core.NavigationDestination
import com.mehedi.core.NavigationManager
import com.mehedi.product.ProductListFragment
import com.mehedi.productdetails.ProductDetailFragment
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {
    private val navigationManager: NavigationManager by viewModels()
    private lateinit var navController: NavController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        navController = navHostFragment.navController

        setupNavGraph()
        setupNavigation()
        handleDeepLink(intent)
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        handleDeepLink(intent)
    }

    private fun setupNavGraph() {
        val navGraph = navController.createGraph(
            startDestination = NavigationDestination.Home.getRoute()
        ) {

            fragment<HomeFragment>(route = NavigationDestination.Home.getRoute()) {
                deepLink { uriPattern = "https://yourdomain.com/home" }
            }

            fragment<ProductListFragment>(route = NavigationDestination.ProductList.getRoute()) {
                deepLink { uriPattern = "https://yourdomain.com/products" }
            }

            fragment<ProductDetailFragment>(route = NavigationDestination.ProductDetail.getRoute()) {
                argument("productId") {
                    type = NavType.StringType
                }
                deepLink {
                    uriPattern = "https://yourdomain.com/product/{productId}"
                }
            }
        }

        navController.graph = navGraph
    }

    private fun handleDeepLink(intent: Intent) {
        if (intent.action == Intent.ACTION_VIEW) {
            val uri = intent.data ?: return

            try {
                when {
                    uri.pathSegments.firstOrNull() == "product" -> {
                        val productId = uri.pathSegments.getOrNull(1)
                        if (productId != null) {
                            val route = NavigationDestination.ProductDetail.createRoute(productId)
                            navigationManager.navigateToRoute(route)
                        }
                    }
                    uri.pathSegments.firstOrNull() == "products" -> {
                        val productId = uri.pathSegments.getOrNull(1)
                        if (productId != null) {
                            val route = NavigationDestination.ProductList.createRoute(productId)
                            navigationManager.navigateToRoute(route)
                        }
                    }
                }
            } catch (e: Exception) {
                Log.e("MainActivity", "Deep link navigation failed", e)
            }
        }
    }

    private fun setupNavigation() {
        lifecycleScope.launch {
            navigationManager.navigationEvent.collect { event ->
                when (event) {
                    is NavigationManager.NavigationEvent.NavigateTo -> {
                        navController.navigate(event.destination.getRoute())
                    }

                    is NavigationManager.NavigationEvent.NavigateToRoute -> {
                        navController.navigate(event.route)
                    }

                    NavigationManager.NavigationEvent.NavigateBack -> {
                        navController.popBackStack()
                    }
                }
            }
        }
    }
}