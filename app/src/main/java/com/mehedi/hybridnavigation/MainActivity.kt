package com.mehedi.hybridnavigation

import NavigationManager
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.createGraph
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.fragment
import com.mehedi.core.NavigationDestination
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
    }

    private fun setupNavGraph() {
        val navGraph = navController.createGraph(
            startDestination = NavigationDestination.Home.getRoute()
        ) {
            // XML-based destinations
            fragment<HomeFragment>(NavigationDestination.Home.getRoute())
            fragment<ProductListFragment>(NavigationDestination.ProductList.getRoute())
            fragment<ProductDetailFragment>(NavigationDestination.ProductDetail.getRoute())

        }

        navController.graph = navGraph
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


                    else -> {}
                }
            }
        }
    }
}
