package com.kumbara.kala.ui.product

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.kumbara.kala.R
import com.kumbara.kala.data.model.Product
import com.kumbara.kala.databinding.ItemProductBinding
import com.kumbara.kala.utils.ImageUtils
import java.io.File

class ProductAdapter(private val onItemClick: (Product) -> Unit) :
    ListAdapter<Product, ProductAdapter.ProductViewHolder>(DiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductViewHolder {
        val binding = ItemProductBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ProductViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ProductViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class ProductViewHolder(private val binding: ItemProductBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(product: Product) {
            binding.tvProductName.text = product.name
            binding.tvProductPrice.text = "₹ %.2f".format(product.price)
            binding.tvProductCategory.text = product.category

            // Category color
            val tagColor = when (product.category) {
                "Health"      -> android.graphics.Color.parseColor("#2E7D32")
                "Eco"         -> android.graphics.Color.parseColor("#00897B")
                "Traditional" -> android.graphics.Color.parseColor("#C8860A")
                else          -> android.graphics.Color.parseColor("#7B2D00")
            }
            binding.tvProductCategory.backgroundTintList =
                android.content.res.ColorStateList.valueOf(tagColor)

            // Load image — asset or file
            val ctx = binding.root.context
            when {
                product.imagePath.startsWith("ASSET:") -> {
                    val assetName = product.imagePath.removePrefix("ASSET:")
                    val resId = ctx.resources.getIdentifier(assetName, "drawable", ctx.packageName)
                    if (resId != 0) {
                        Glide.with(ctx).load(resId).centerCrop()
                            .placeholder(R.drawable.ic_clay_pot).into(binding.ivProduct)
                    } else {
                        binding.ivProduct.setImageResource(R.drawable.ic_clay_pot)
                    }
                }
                product.imagePath.isNotBlank() && File(product.imagePath).exists() -> {
                    Glide.with(ctx).load(File(product.imagePath)).centerCrop()
                        .placeholder(R.drawable.ic_clay_pot).into(binding.ivProduct)
                }
                else -> binding.ivProduct.setImageResource(R.drawable.ic_clay_pot)
            }

            binding.root.setOnClickListener { onItemClick(product) }
        }
    }

    class DiffCallback : DiffUtil.ItemCallback<Product>() {
        override fun areItemsTheSame(oldItem: Product, newItem: Product) = oldItem.id == newItem.id
        override fun areContentsTheSame(oldItem: Product, newItem: Product) = oldItem == newItem
    }
}
