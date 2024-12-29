package com.mehedi.productdetails

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.platform.ComposeView
import com.mehedi.core.BaseFragment


class ProductDetailFragment : BaseFragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        return ComposeView(requireContext()).apply {
            setContent {
                MaterialTheme {
                    val productId = arguments?.getString("title") ?: ""
                    val productTitle = arguments?.getString("productTitle") ?: ""

                    ProductDetailScreen(
                        productId = productId,
                        productTitle = productTitle,
                        onNavigateBack = {
                            navigationManager.navigateBack()
                        }
                    )
                }
            }
        }
    }
}

