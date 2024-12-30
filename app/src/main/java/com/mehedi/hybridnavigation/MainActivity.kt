package com.mehedi.hybridnavigation

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.NavType
import androidx.navigation.createGraph
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.fragment
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import com.mehedi.core.NavigationDestination
import com.mehedi.core.NavigationManager
import com.mehedi.product.ProductListFragment
import com.mehedi.productdetails.ProductDetailFragment
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {
    private val navigationManager: NavigationManager by viewModels()
    private lateinit var navController: NavController

    private lateinit var notificationPermissionHandler: NotificationPermissionHandler
    private lateinit var notificationHelper: NotificationHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        navController = navHostFragment.navController

        setupNavGraph()
        setupNavigation()
       // handleIntent(intent)

        notificationPermissionHandler = NotificationPermissionHandler(this)
        notificationHelper = NotificationHelper(this)

        // Create notification channel early in app lifecycle
        notificationHelper.createNotificationChannel()
        requestNotificationPermission()
        // Or check permission before showing notification
        if (notificationHelper.checkNotificationPermission()) {
            notificationHelper.showProductNotification("123")
        } else {
            requestNotificationPermission()
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        handleIntent(intent)
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

    private fun handleIntent(intent: Intent?) {
        if (intent?.action == Intent.ACTION_VIEW && intent.data != null) {
            val uri = intent.data ?: return
            val isFromNotification =
                intent.getBooleanExtra(NotificationHelper.EXTRA_FROM_NOTIFICATION, false)

            try {
                when {
                    uri.pathSegments.firstOrNull() == "product" -> {
                        val productId = uri.pathSegments.getOrNull(1)
                        if (productId != null) {
                            // Create navigation route
                            val route = NavigationDestination.ProductDetail.createRoute(productId)

                            // If from notification, clear backstack to home before navigating
                            if (isFromNotification) {
                                navController.navigate(route) {
                                    // Clear back stack up to home
                                    popUpTo(NavigationDestination.Home.getRoute()) {
                                        saveState = true
                                    }
                                    // Avoid multiple copies of the same destination
                                    launchSingleTop = true
                                    // Restore state if exists
                                    restoreState = true
                                }
                            } else {
                                // Normal navigation for deep links
                                navigationManager.navigateToRoute(route)
                            }

                            // Track source if needed
                            if (isFromNotification) {
                                logNotificationClick(productId)
                            }
                        }
                    }
                }
            } catch (e: Exception) {
                Log.e("MainActivity", "Navigation failed", e)
                Toast.makeText(this, "Failed to open product", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun logNotificationClick(productId: String) {
        // Add your analytics logging here
    }
    /*    private fun handleDeepLink(intent: Intent) {
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
        }*/

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

    private fun requestNotificationPermission() {
        notificationPermissionHandler.checkNotificationPermission(
            onGranted = {
                // Permission granted, show notification
                showNotification()
            },
            onShouldShowRationale = {
                // Show explanation dialog
                showPermissionRationaleDialog()
            },
            onDenied = {
                // Handle denied case
                showPermissionDeniedMessage()
            }
        )
    }

    private fun showPermissionRationaleDialog() {
        MaterialAlertDialogBuilder(this)
            .setTitle("Notification Permission")
            .setMessage("We need notification permission to keep you updated about products and orders.")
            .setPositiveButton("Grant") { _, _ ->
                notificationPermissionHandler.checkNotificationPermission(
                    onGranted = { showNotification() }
                )
            }
            .setNegativeButton("Not Now", null)
            .show()
    }

    private fun showPermissionDeniedMessage() {
        Snackbar.make(
            findViewById(android.R.id.content),
            "Notifications are disabled. Enable them in settings for updates.",
            Snackbar.LENGTH_LONG
        ).setAction("Settings") {
            openNotificationSettings()
        }.show()
    }

    private fun openNotificationSettings() {
        Intent().also { intent ->
            intent.action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            intent.data = Uri.fromParts("package", packageName, null)
            startActivity(intent)
        }
    }

    private fun showNotification() {
        notificationHelper.showProductNotification("123")
    }
}