package com.mehedi.product

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.mehedi.core.BaseFragment
import com.mehedi.core.NavigationDestination


class ProductListFragment : BaseFragment() {


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_product_list, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // Method 1: Using toolbar back button
        view.findViewById<ImageButton>(R.id.backButton).setOnClickListener {
            // Handle back press
            //requireActivity().onBackPressed()
            // For newer Android versions, you can use:
            // activity?.onBackPressedDispatcher?.onBackPressed()
            navigationManager.navigateBack()
        }

        val productsRecyclerView = view.findViewById<RecyclerView>(R.id.productsRecyclerView)
        productsRecyclerView.layoutManager = LinearLayoutManager(context)

        // Sample data
        val products = listOf(
            Product("1", "Product 1", "$99.99"),
            Product("2", "Product 2", "$149.99"),
            Product("3", "Product 3", "$199.99")
        )
//  navigationManager.navigateBack()
        productsRecyclerView.adapter = ProductsAdapter(products) { product ->
            val route = NavigationDestination.ProductDetail.createRoute(
                id = product.id,
                title = product.name
            )
            navigationManager.navigateToRoute(route)
        }
    }

    private fun shouldNavigateBack(): Boolean {
        // Add your custom logic here
        // For example, check if user has unsaved changes
        return false
    }

    private fun showExitDialog() {
        AlertDialog.Builder(requireContext())
            .setTitle("Exit")
            .setMessage("Are you sure you want to exit?")
            .setPositiveButton("Yes") { _, _ ->
                navigationManager.navigateBack()
                // requireActivity().onBackPressed()
            }
            .setNegativeButton("No", null)
            .show()
    }

}