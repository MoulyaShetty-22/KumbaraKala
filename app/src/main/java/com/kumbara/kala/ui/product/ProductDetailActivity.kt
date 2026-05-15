package com.kumbara.kala.ui.product

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.google.android.material.tabs.TabLayout
import com.kumbara.kala.R
import com.kumbara.kala.data.model.Product
import com.kumbara.kala.databinding.ActivityProductDetailBinding
import com.kumbara.kala.ui.card.BenefitCardActivity
import com.kumbara.kala.utils.ImageUtils
import com.kumbara.kala.viewmodel.ProductViewModel
import kotlinx.coroutines.launch
import java.io.File

class ProductDetailActivity : AppCompatActivity() {
    private lateinit var binding: ActivityProductDetailBinding
    private val viewModel: ProductViewModel by viewModels()
    private var product: Product? = null
    private var currentLanguage = "English"
    private var currentTab = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProductDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        val productId = intent.getLongExtra("product_id", -1L)
        if (productId == -1L) { finish(); return }

        lifecycleScope.launch {
            product = viewModel.getProductById(productId)
            product?.let { p ->
                supportActionBar?.title = p.name
                binding.tvProductName.text = p.name
                binding.tvPrice.text = "₹ %.2f".format(p.price)
                binding.tvCategory.text = p.category

                // Category tag color
                val tagColor = when (p.category) {
                    "Health"      -> android.graphics.Color.parseColor("#2E7D32")
                    "Eco"         -> android.graphics.Color.parseColor("#00897B")
                    "Traditional" -> android.graphics.Color.parseColor("#C8860A")
                    else          -> android.graphics.Color.parseColor("#7B2D00")
                }
                binding.tvCategory.backgroundTintList =
                    android.content.res.ColorStateList.valueOf(tagColor)

                // Load image (asset or file)
                when {
                    p.imagePath.startsWith("ASSET:") -> {
                        val assetName = p.imagePath.removePrefix("ASSET:")
                        val resId = resources.getIdentifier(assetName, "drawable", packageName)
                        if (resId != 0) Glide.with(this@ProductDetailActivity).load(resId)
                            .centerCrop().into(binding.ivProduct)
                        else binding.ivProduct.setImageResource(R.drawable.ic_clay_pot)
                    }
                    p.imagePath.isNotBlank() && File(p.imagePath).exists() ->
                        Glide.with(this@ProductDetailActivity).load(File(p.imagePath))
                            .centerCrop().into(binding.ivProduct)
                    else -> binding.ivProduct.setImageResource(R.drawable.ic_clay_pot)
                }

                showTabContent(0, p)
            }
        }

        binding.tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                currentTab = tab?.position ?: 0
                product?.let { showTabContent(currentTab, it) }
            }
            override fun onTabUnselected(tab: TabLayout.Tab?) {}
            override fun onTabReselected(tab: TabLayout.Tab?) {}
        })

        binding.btnLanguageToggle.setOnClickListener {
            currentLanguage = if (currentLanguage == "English") "Kannada" else "English"
            binding.btnLanguageToggle.text = if (currentLanguage == "English") "EN | KN" else "KN | EN"
        }

        viewModel.errorMessage.observe(this) { msg ->
            if (!msg.isNullOrBlank()) {
                Toast.makeText(this, msg, Toast.LENGTH_LONG).show()
                viewModel.clearError()
            }
        }
        viewModel.isLoading.observe(this) { loading ->
            binding.progressBar.visibility = if (loading) View.VISIBLE else View.GONE
        }
        viewModel.careGuideResult.observe(this) { guide ->
            if (!guide.isNullOrBlank()) {
                binding.tvTabContent.text = guide
                viewModel.clearCareGuideResult()
            }
        }
        viewModel.storyResult.observe(this) { story ->
            if (!story.isNullOrBlank()) {
                binding.tvTabContent.text = story
                viewModel.clearStoryResult()
            }
        }
    }

    private fun showTabContent(tab: Int, p: Product) {
        when (tab) {
            0 -> { // Care Guide
                if (p.careGuide.isNotBlank()) {
                    binding.tvTabContent.text = p.careGuide
                    binding.btnGenerateCard.text = "🔄 Regenerate Care Guide"
                    binding.btnGenerateCard.setOnClickListener {
                        viewModel.generateCareGuide(p.id, p.name, currentLanguage)
                    }
                } else {
                    binding.tvTabContent.text = "Tap below to generate a care guide for this product using AI."
                    binding.btnGenerateCard.text = "✨ Generate Care Guide"
                    binding.btnGenerateCard.setOnClickListener {
                        viewModel.generateCareGuide(p.id, p.name, currentLanguage)
                    }
                }
            }
            1 -> { // Story
                if (p.story.isNotBlank()) {
                    binding.tvTabContent.text = p.story
                    binding.btnGenerateCard.text = "🔄 Regenerate Story"
                    binding.btnGenerateCard.setOnClickListener {
                        viewModel.generateStory(p.id, p.name, currentLanguage)
                    }
                } else {
                    binding.tvTabContent.text = "Tap below to generate the product's first-person story using AI."
                    binding.btnGenerateCard.text = "✨ Generate Story"
                    binding.btnGenerateCard.setOnClickListener {
                        viewModel.generateStory(p.id, p.name, currentLanguage)
                    }
                }
            }
            2 -> { // Benefit Card
                if (p.benefitCard.isNotBlank()) {
                    val preview = p.benefitCard.replace("|||", "\n\n").take(200) + "..."
                    binding.tvTabContent.text = "✅ Benefit card ready!\n\n$preview"
                } else {
                    binding.tvTabContent.text = "Tap below to generate a shareable benefit card using Gemini Vision AI."
                }
                binding.btnGenerateCard.text = "✨ View / Generate Benefit Card"
                binding.btnGenerateCard.setOnClickListener {
                    val intent = Intent(this, BenefitCardActivity::class.java)
                    intent.putExtra("product_id", p.id)
                    intent.putExtra("language", currentLanguage)
                    startActivity(intent)
                }
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_product_detail, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_edit -> {
                val intent = Intent(this, AddEditProductActivity::class.java)
                intent.putExtra("product_id", product?.id ?: -1L)
                startActivity(intent)
                true
            }
            R.id.action_delete -> {
                AlertDialog.Builder(this)
                    .setTitle("Delete Product")
                    .setMessage("Delete ${product?.name}?")
                    .setPositiveButton("Delete") { _, _ ->
                        product?.let { viewModel.deleteProduct(it) }
                        Toast.makeText(this, "Product deleted", Toast.LENGTH_SHORT).show()
                        finish()
                    }
                    .setNegativeButton("Cancel", null).show()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onSupportNavigateUp(): Boolean { finish(); return true }
}
