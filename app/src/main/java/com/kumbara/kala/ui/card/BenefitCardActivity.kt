package com.kumbara.kala.ui.card

import android.content.ContentValues
import android.content.Intent
import android.graphics.*
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.speech.tts.TextToSpeech
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.google.zxing.BarcodeFormat
import com.google.zxing.MultiFormatWriter
import com.journeyapps.barcodescanner.BarcodeEncoder
import com.kumbara.kala.data.model.Artisan
import com.kumbara.kala.data.model.Product
import com.kumbara.kala.data.model.Workshop
import com.kumbara.kala.databinding.ActivityBenefitCardBinding
import com.kumbara.kala.utils.ImageUtils
import com.kumbara.kala.viewmodel.ArtisanViewModel
import com.kumbara.kala.viewmodel.ProductViewModel
import com.kumbara.kala.viewmodel.WorkshopViewModel
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileOutputStream
import java.util.*

class BenefitCardActivity : AppCompatActivity(), TextToSpeech.OnInitListener {
    private lateinit var binding: ActivityBenefitCardBinding
    private val productViewModel: ProductViewModel by viewModels()
    private val artisanViewModel: ArtisanViewModel by viewModels()
    private val workshopViewModel: WorkshopViewModel by viewModels()

    private var product: Product? = null
    private var artisan: Artisan? = null
    private var cardBitmap: Bitmap? = null
    private var tts: TextToSpeech? = null
    private var language = "English"
    private var cardText = ""
    private var selectedWorkshop: Workshop? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityBenefitCardBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = "Benefit Card"

        language = intent.getStringExtra("language") ?: "English"
        val productId = intent.getLongExtra("product_id", -1L)
        if (productId == -1L) { finish(); return }

        tts = TextToSpeech(this, this)

        lifecycleScope.launch {
            product = productViewModel.getProductById(productId)
            artisan = artisanViewModel.artisan.value ?: fetchArtisanOnce()
            product?.let { p ->
                if (p.benefitCard.isNotBlank()) {
                    cardText = p.benefitCard
                    loadImageAndBuildCard(p, cardText)
                } else {
                    generateCard(p)
                }
            }
        }

        artisanViewModel.artisan.observe(this) { a -> artisan = a }

        productViewModel.isLoading.observe(this) { loading ->
            binding.progressBar.visibility = if (loading) View.VISIBLE else View.GONE
        }

        productViewModel.errorMessage.observe(this) { msg ->
            if (!msg.isNullOrBlank()) {
                Toast.makeText(this, msg, Toast.LENGTH_LONG).show()
                productViewModel.clearError()
            }
        }

        productViewModel.benefitCardResult.observe(this) { text ->
            if (!text.isNullOrBlank()) {
                cardText = text
                product?.let { loadImageAndBuildCard(it, text, selectedWorkshop) }
                productViewModel.clearBenefitCardResult()
            }
        }

        binding.btnReadAloud.setOnClickListener {
            if (cardText.isNotBlank()) {
                val locale = if (language == "Kannada") Locale("kn", "IN") else Locale("en", "IN")
                tts?.language = locale
                tts?.speak(cardText.replace("|||", ". "), TextToSpeech.QUEUE_FLUSH, null, "card_tts")
            }
        }

        binding.btnShareWhatsApp.setOnClickListener { shareCardWhatsApp() }
        binding.btnSaveGallery.setOnClickListener { saveToGallery() }
        binding.btnRegenerateCard.setOnClickListener { product?.let { generateCard(it) } }

        // ── Workshop Spinner Setup ──────────────────────────────
        workshopViewModel.allWorkshops.observe(this) { workshops ->
            val options = mutableListOf("No workshop linked")
            options.addAll(workshops.map { "🎨 ${it.title}  •  ${it.date}  •  ₹${it.price}" })
            val adapter = ArrayAdapter(
                this,
                android.R.layout.simple_spinner_dropdown_item,
                options
            )
            binding.spinnerWorkshop.adapter = adapter
        }

        binding.btnApplyWorkshop.setOnClickListener {
            val pos = binding.spinnerWorkshop.selectedItemPosition
            val workshops = workshopViewModel.allWorkshops.value ?: emptyList()
            selectedWorkshop = if (pos == 0) null else workshops[pos - 1]
            product?.let { p ->
                val bmp = ImageUtils.loadBitmap(this, p.imagePath)
                    ?: createPlaceholderBitmap()
                buildCard(p, cardText, bmp, selectedWorkshop)
            }
            Toast.makeText(
                this,
                if (selectedWorkshop == null) "No workshop linked"
                else "✅ Workshop linked to card!",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    private suspend fun fetchArtisanOnce(): Artisan? {
        return try {
            com.kumbara.kala.data.db.AppDatabase.getDatabase(this)
                .artisanDao().getArtisanOnce()
        } catch (e: Exception) { null }
    }

    private fun loadImageAndBuildCard(
        p: Product,
        text: String,
        workshop: Workshop? = null
    ) {
        val bmp = ImageUtils.loadBitmap(this, p.imagePath)
        if (bmp != null) buildCard(p, text, bmp, workshop)
        else buildCard(p, text, createPlaceholderBitmap(), workshop)
    }

    private fun generateCard(p: Product) {
        binding.tvGenerating.visibility = View.VISIBLE
        binding.cardContainer.visibility = View.GONE
        val bmp = ImageUtils.loadBitmap(this, p.imagePath) ?: createPlaceholderBitmap()
        productViewModel.generateBenefitCard(p.id, p.name, bmp, language)
    }

    private fun createPlaceholderBitmap(): Bitmap {
        val bmp = Bitmap.createBitmap(600, 600, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bmp)
        val paint = Paint().apply { color = Color.parseColor("#F9EDE5") }
        canvas.drawRect(0f, 0f, 600f, 600f, paint)
        val textPaint = Paint().apply {
            color = Color.parseColor("#7B2D00")
            textSize = 60f
            textAlign = Paint.Align.CENTER
            isAntiAlias = true
        }
        canvas.drawText("🏺", 300f, 320f, textPaint)
        return bmp
    }

    private fun buildCard(
        p: Product,
        rawText: String,
        productBitmap: Bitmap,
        workshop: Workshop? = null
    ) {
        val parts = rawText.split("|||").map { it.trim() }
        val healthFact   = parts.getOrElse(0) { rawText }
        val scienceClaim = parts.getOrElse(1) { "" }
        val heritageNote = parts.getOrElse(2) { "" }

        val cardW = 1000
        val cardH = if (workshop != null) 1460 else 1400
        val bitmap = Bitmap.createBitmap(cardW, cardH, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)

        // Cream background
        canvas.drawColor(Color.parseColor("#F9EDE5"))

        // Product image top section
        val scaledProduct = Bitmap.createScaledBitmap(productBitmap, cardW, 520, true)
        canvas.drawBitmap(scaledProduct, 0f, 0f, null)

        // Gradient overlay on image bottom
        val gradPaint = Paint()
        val shader = LinearGradient(
            0f, 350f, 0f, 530f,
            Color.TRANSPARENT, Color.parseColor("#CC7B2D00"),
            Shader.TileMode.CLAMP
        )
        gradPaint.shader = shader
        canvas.drawRect(0f, 350f, cardW.toFloat(), 530f, gradPaint)

        // Product name on image
        val namePaint = Paint().apply {
            color = Color.WHITE; textSize = 52f; isAntiAlias = true
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        }
        canvas.drawText(p.name, 30f, 495f, namePaint)

        // Category badge
        val tagColor = when (p.category) {
            "Health"      -> Color.parseColor("#2E7D32")
            "Eco"         -> Color.parseColor("#00897B")
            "Traditional" -> Color.parseColor("#C8860A")
            else          -> Color.parseColor("#7B2D00")
        }
        val tagPaint = Paint().apply { color = tagColor; style = Paint.Style.FILL }
        canvas.drawRoundRect(RectF(30f, 510f, 200f, 558f), 24f, 24f, tagPaint)
        val tagTextPaint = Paint().apply {
            color = Color.WHITE; textSize = 26f; isAntiAlias = true
            textAlign = Paint.Align.CENTER
        }
        canvas.drawText(p.category, 115f, 542f, tagTextPaint)

        // Decorative divider
        val divPaint = Paint().apply {
            color = Color.parseColor("#7B2D00"); strokeWidth = 4f
        }
        canvas.drawLine(30f, 580f, (cardW - 30).toFloat(), 580f, divPaint)

        // Benefit sections
        val labelPaint = Paint().apply {
            color = Color.parseColor("#7B2D00"); textSize = 32f; isAntiAlias = true
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        }
        val bodyPaint = Paint().apply {
            color = Color.parseColor("#1A0A00"); textSize = 28f; isAntiAlias = true
        }

        var yPos = 630f
        if (healthFact.isNotBlank()) {
            canvas.drawText("🌿  HEALTH BENEFIT", 30f, yPos, labelPaint); yPos += 44f
            yPos = drawWrappedText(canvas, healthFact, 30f, yPos, (cardW - 60).toFloat(), bodyPaint)
            yPos += 28f
        }
        if (scienceClaim.isNotBlank()) {
            canvas.drawText("🔬  SCIENCE", 30f, yPos, labelPaint); yPos += 44f
            yPos = drawWrappedText(canvas, scienceClaim, 30f, yPos, (cardW - 60).toFloat(), bodyPaint)
            yPos += 28f
        }
        if (heritageNote.isNotBlank()) {
            canvas.drawText("🏺  HERITAGE", 30f, yPos, labelPaint); yPos += 44f
            yPos = drawWrappedText(canvas, heritageNote, 30f, yPos, (cardW - 60).toFloat(), bodyPaint)
        }

        // ── Workshop Strip (golden) ─────────────────────────────
        if (workshop != null) {
            val wsBarPaint = Paint().apply { color = Color.parseColor("#C8860A") }
            canvas.drawRect(0f, 1240f, cardW.toFloat(), 1310f, wsBarPaint)

            val wsIconPaint = Paint().apply {
                color = Color.WHITE; textSize = 24f; isAntiAlias = true
                typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
            }
            canvas.drawText("🎨  POTTERY WORKSHOP", 20f, 1266f, wsIconPaint)

            val wsDetailPaint = Paint().apply {
                color = Color.WHITE; textSize = 22f; isAntiAlias = true
            }
            canvas.drawText(
                "📅 ${workshop.date}   •   ₹${workshop.price}/person   •   📍 ${workshop.location}",
                20f, 1298f, wsDetailPaint
            )
        }

        // ── Bottom Bar ─────────────────────────────────────────
        val bottomY = if (workshop != null) 1310f else 1300f
        val barPaint = Paint().apply { color = Color.parseColor("#7B2D00") }
        canvas.drawRect(0f, bottomY, cardW.toFloat(), cardH.toFloat(), barPaint)

        val artisanName    = artisan?.name    ?: "Kumbara Artisan"
        val artisanPhone   = artisan?.phone   ?: ""
        val artisanVillage = artisan?.village ?: "Karnataka"

        val infoPaint = Paint().apply {
            color = Color.WHITE; textSize = 30f; isAntiAlias = true
        }
        canvas.drawText(
            "🏺  $artisanName  •  $artisanVillage",
            30f, bottomY + 48f, infoPaint
        )
        if (artisanPhone.isNotBlank()) {
            canvas.drawText("📞  $artisanPhone", 30f, bottomY + 86f, infoPaint)
        }

        // Watermark
        val wmPaint = Paint().apply {
            color = Color.parseColor("#FFCCAA"); textSize = 22f; isAntiAlias = true
            textAlign = Paint.Align.RIGHT
        }
        canvas.drawText("KumbaraKala", (cardW - 30).toFloat(), bottomY + 86f, wmPaint)

        // QR code
        if (artisanPhone.isNotBlank()) {
            try {
                val waUrl = "https://wa.me/${artisanPhone.replace("+", "").replace(" ", "")}"
                val matrix = MultiFormatWriter().encode(waUrl, BarcodeFormat.QR_CODE, 130, 130)
                val qrBmp = BarcodeEncoder().createBitmap(matrix)
                canvas.drawBitmap(qrBmp, (cardW - 165).toFloat(), bottomY + 5f, null)
            } catch (_: Exception) {}
        }

        cardBitmap = bitmap
        binding.ivCard.setImageBitmap(bitmap)
        binding.cardContainer.visibility = View.VISIBLE
        binding.tvGenerating.visibility = View.GONE
    }

    private fun drawWrappedText(
        canvas: Canvas, text: String, x: Float,
        startY: Float, maxWidth: Float, paint: Paint
    ): Float {
        val words = text.split(" ")
        var line = ""; var y = startY
        for (word in words) {
            val test = if (line.isEmpty()) word else "$line $word"
            if (paint.measureText(test) > maxWidth) {
                canvas.drawText(line, x, y, paint)
                y += paint.textSize + 8f
                line = word
            } else line = test
        }
        if (line.isNotEmpty()) {
            canvas.drawText(line, x, y, paint)
            y += paint.textSize + 8f
        }
        return y
    }

    private fun shareCardWhatsApp() {
        val bmp = cardBitmap ?: run {
            Toast.makeText(this, "Card not generated yet", Toast.LENGTH_SHORT).show()
            return
        }
        try {
            val file = File(cacheDir, "kumbara_card.png")
            FileOutputStream(file).use { bmp.compress(Bitmap.CompressFormat.PNG, 100, it) }
            val uri = FileProvider.getUriForFile(this, "$packageName.provider", file)
            val shareText = buildString {
                append("🏺 Authentic Karnataka clay product — made with 3000 years of tradition!\n\n")
                append("${product?.name ?: ""}\n")
                if (selectedWorkshop != null) {
                    append("\n🎨 Pottery Workshop: ${selectedWorkshop!!.title}\n")
                    append("📅 Date: ${selectedWorkshop!!.date}\n")
                    append("₹${selectedWorkshop!!.price}/person  •  📍 ${selectedWorkshop!!.location}\n")
                    append("\nInterested? WhatsApp me to book your slot!")
                }
            }
            val intent = Intent(Intent.ACTION_SEND).apply {
                type = "image/png"
                putExtra(Intent.EXTRA_STREAM, uri)
                putExtra(Intent.EXTRA_TEXT, shareText)
                setPackage("com.whatsapp")
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            }
            try { startActivity(intent) }
            catch (_: Exception) {
                intent.setPackage(null)
                startActivity(Intent.createChooser(intent, "Share Card"))
            }
        } catch (e: Exception) {
            Toast.makeText(this, "Share failed: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }

    private fun saveToGallery() {
        val bmp = cardBitmap ?: run {
            Toast.makeText(this, "Card not generated yet", Toast.LENGTH_SHORT).show()
            return
        }
        try {
            val filename = "KumbaraKala_${System.currentTimeMillis()}.png"
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                val values = ContentValues().apply {
                    put(MediaStore.Images.Media.DISPLAY_NAME, filename)
                    put(MediaStore.Images.Media.MIME_TYPE, "image/png")
                    put(MediaStore.Images.Media.RELATIVE_PATH,
                        Environment.DIRECTORY_PICTURES + "/KumbaraKala")
                }
                val uri = contentResolver.insert(
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values)
                uri?.let {
                    contentResolver.openOutputStream(it)?.use { out ->
                        bmp.compress(Bitmap.CompressFormat.PNG, 100, out)
                    }
                }
            } else {
                @Suppress("DEPRECATION")
                MediaStore.Images.Media.insertImage(
                    contentResolver, bmp, filename, "KumbaraKala card")
            }
            Toast.makeText(this, "✅ Card saved to gallery!", Toast.LENGTH_SHORT).show()
        } catch (e: Exception) {
            Toast.makeText(this, "Save failed: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onInit(status: Int) {
        if (status == TextToSpeech.SUCCESS) {
            tts?.language = if (language == "Kannada") Locale("kn", "IN") else Locale("en", "IN")
        }
    }

    override fun onDestroy() { tts?.stop(); tts?.shutdown(); super.onDestroy() }
    override fun onSupportNavigateUp(): Boolean { finish(); return true }
}