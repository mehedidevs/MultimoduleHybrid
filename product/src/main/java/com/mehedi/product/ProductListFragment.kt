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
        return inflater.inflate(R.layout.fragment_product_list, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        view.findViewById<ImageButton>(R.id.backButton).setOnClickListener {
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

        productsRecyclerView.adapter = ProductsAdapter(products) { product ->
            val route = NavigationDestination.ProductDetail.createRoute(
                productId = product.name,

                )
            navigationManager.navigateToRoute(route)
        }
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