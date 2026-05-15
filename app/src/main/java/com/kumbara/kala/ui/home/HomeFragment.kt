package com.kumbara.kala.ui.home

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.kumbara.kala.databinding.FragmentHomeBinding
import com.kumbara.kala.ui.product.AddEditProductActivity
import com.kumbara.kala.ui.product.ProductAdapter
import com.kumbara.kala.ui.product.ProductDetailActivity
import com.kumbara.kala.viewmodel.ArtisanViewModel
import com.kumbara.kala.viewmodel.ProductViewModel

class HomeFragment : Fragment() {
    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private val productViewModel: ProductViewModel by activityViewModels()
    private val artisanViewModel: ArtisanViewModel by activityViewModels()
    private lateinit var adapter: ProductAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        adapter = ProductAdapter { product ->
            val intent = Intent(requireContext(), ProductDetailActivity::class.java)
            intent.putExtra("product_id", product.id)
            startActivity(intent)
        }

        binding.recyclerViewProducts.adapter = adapter

        productViewModel.allProducts.observe(viewLifecycleOwner) { products ->
            adapter.submitList(products)
            binding.emptyState.visibility = if (products.isEmpty()) View.VISIBLE else View.GONE
            binding.recyclerViewProducts.visibility = if (products.isEmpty()) View.GONE else View.VISIBLE
        }

        artisanViewModel.dailyFact.observe(viewLifecycleOwner) { fact ->
            if (!fact.isNullOrBlank()) {
                binding.dailyFactBanner.visibility = View.VISIBLE
                binding.tvDailyFact.text = fact
            }
        }

        artisanViewModel.isLoadingFact.observe(viewLifecycleOwner) { loading ->
            binding.factProgressBar.visibility = if (loading) View.VISIBLE else View.GONE
        }

        artisanViewModel.fetchDailyFact()

        binding.fabAddProduct.setOnClickListener {
            startActivity(Intent(requireContext(), AddEditProductActivity::class.java))
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
