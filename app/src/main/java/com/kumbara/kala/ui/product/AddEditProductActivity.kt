package com.kumbara.kala.ui.product

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import com.bumptech.glide.Glide
import com.kumbara.kala.R
import com.kumbara.kala.data.model.Product
import com.kumbara.kala.databinding.ActivityAddEditProductBinding
import com.kumbara.kala.viewmodel.ProductViewModel
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

class AddEditProductActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAddEditProductBinding
    private val viewModel: ProductViewModel by viewModels()

    private var currentPhotoPath: String = ""
    private var photoUri: Uri? = null
    private var editingProductId: Long = -1L

    private val categories = listOf("Health", "Eco", "Handmade", "Traditional")

    private val cameraLauncher = registerForActivityResult(ActivityResultContracts.TakePicture()) { success ->
        if (success) {
            currentPhotoPath = photoUri?.path ?: ""
            Glide.with(this).load(photoUri).centerCrop().into(binding.ivProductPhoto)
        }
    }

    private val galleryLauncher = registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        uri?.let {
            currentPhotoPath = copyImageToPrivateStorage(it)
            Glide.with(this).load(File(currentPhotoPath)).centerCrop().into(binding.ivProductPhoto)
        }
    }

    private val permissionLauncher = registerForActivityResult(ActivityResultContracts.RequestPermission()) { granted ->
        if (granted) openCamera()
        else Toast.makeText(this, "Camera permission required", Toast.LENGTH_SHORT).show()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddEditProductBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        val categoryAdapter = ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, categories)
        binding.spinnerCategory.adapter = categoryAdapter

        editingProductId = intent.getLongExtra("product_id", -1L)
        if (editingProductId != -1L) {
            loadProductForEdit(editingProductId)
            supportActionBar?.title = "Edit Product"
        } else {
            supportActionBar?.title = "Add Product"
        }

        binding.btnCamera.setOnClickListener {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
                openCamera()
            } else {
                permissionLauncher.launch(Manifest.permission.CAMERA)
            }
        }

        binding.btnGallery.setOnClickListener {
            galleryLauncher.launch("image/*")
        }

        binding.btnSave.setOnClickListener {
            saveProduct()
        }
    }

    private fun openCamera() {
        val photoFile = createImageFile()
        photoUri = FileProvider.getUriForFile(this, "$packageName.provider", photoFile)
        currentPhotoPath = photoFile.absolutePath
        cameraLauncher.launch(photoUri)
    }

    private fun createImageFile(): File {
        val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
        val storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        return File.createTempFile("KUMBARA_${timeStamp}_", ".jpg", storageDir)
    }

    private fun copyImageToPrivateStorage(uri: Uri): String {
        val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
        val destFile = File(getExternalFilesDir(Environment.DIRECTORY_PICTURES), "KUMBARA_${timeStamp}.jpg")
        contentResolver.openInputStream(uri)?.use { input ->
            destFile.outputStream().use { output ->
                input.copyTo(output)
            }
        }
        return destFile.absolutePath
    }

    private fun loadProductForEdit(id: Long) {
        viewModel.allProducts.observe(this) { products ->
            products.find { it.id == id }?.let { product ->
                binding.etProductName.setText(product.name)
                binding.etProductPrice.setText(product.price.toString())
                val idx = categories.indexOf(product.category)
                if (idx >= 0) binding.spinnerCategory.setSelection(idx)
                currentPhotoPath = product.imagePath
                if (currentPhotoPath.isNotBlank() && File(currentPhotoPath).exists()) {
                    Glide.with(this).load(File(currentPhotoPath)).centerCrop().into(binding.ivProductPhoto)
                }
            }
        }
    }

    private fun saveProduct() {
        val name = binding.etProductName.text.toString().trim()
        val priceStr = binding.etProductPrice.text.toString().trim()
        val category = binding.spinnerCategory.selectedItem.toString()

        if (name.isBlank()) { binding.etProductName.error = "Name required"; return }
        if (priceStr.isBlank()) { binding.etProductPrice.error = "Price required"; return }

        val price = priceStr.toDoubleOrNull() ?: run {
            binding.etProductPrice.error = "Invalid price"; return
        }

        if (editingProductId != -1L) {
            val updated = Product(editingProductId, name, price, category, currentPhotoPath)
            viewModel.updateProduct(updated)
        } else {
            val product = Product(name = name, price = price, category = category, imagePath = currentPhotoPath)
            viewModel.insertProduct(product)
        }
        Toast.makeText(this, "Product saved!", Toast.LENGTH_SHORT).show()
        finish()
    }

    override fun onSupportNavigateUp(): Boolean { finish(); return true }
}
