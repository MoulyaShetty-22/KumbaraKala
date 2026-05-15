package com.kumbara.kala.ui.artisan

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.bumptech.glide.Glide
import com.kumbara.kala.data.model.Artisan
import com.kumbara.kala.databinding.FragmentArtisanBinding
import com.kumbara.kala.viewmodel.ArtisanViewModel
import java.io.File

class ArtisanFragment : Fragment() {
    private var _binding: FragmentArtisanBinding? = null
    private val binding get() = _binding!!
    private val viewModel: ArtisanViewModel by activityViewModels()
    private var currentArtisan: Artisan? = null

    private val galleryLauncher = registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        uri?.let {
            val file = File(requireContext().filesDir, "artisan_photo.jpg")
            requireContext().contentResolver.openInputStream(it)?.use { input ->
                file.outputStream().use { output -> input.copyTo(output) }
            }
            currentArtisan = currentArtisan?.copy(profileImagePath = file.absolutePath)
            Glide.with(this).load(file).circleCrop().into(binding.ivArtisanPhoto)
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentArtisanBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.artisan.observe(viewLifecycleOwner) { artisan ->
            artisan?.let {
                currentArtisan = it
                // Don't show placeholder values — show empty fields instead
                binding.etName.setText(if (it.name == "Ravi Kumbara" && it.phone.isBlank()) "" else it.name)
                binding.etVillage.setText(if (it.village == "Udupi, Karnataka" && it.phone.isBlank()) "" else it.village)
                binding.etYears.setText(if (it.yearsOfCraft == 0) "" else it.yearsOfCraft.toString())
                binding.etBio.setText(it.biography)
                binding.etPhone.setText(it.phone)
                binding.etTags.setText(it.heritageTags)
                if (it.profileImagePath.isNotBlank() && File(it.profileImagePath).exists()) {
                    Glide.with(this).load(File(it.profileImagePath)).circleCrop().into(binding.ivArtisanPhoto)
                }
            } ?: run { currentArtisan = Artisan() }
        }

        binding.ivArtisanPhoto.setOnClickListener { galleryLauncher.launch("image/*") }
        binding.btnSaveProfile.setOnClickListener { saveProfile() }
        binding.btnCalculate.setOnClickListener { calculateEarnings() }

        viewModel.errorMessage.observe(viewLifecycleOwner) { msg ->
            if (!msg.isNullOrBlank()) {
                Toast.makeText(requireContext(), msg, Toast.LENGTH_SHORT).show()
                viewModel.clearError()
            }
        }
    }

    private fun saveProfile() {
        val name    = binding.etName.text.toString().trim()
        val village = binding.etVillage.text.toString().trim()
        val yearsStr = binding.etYears.text.toString().trim()
        val bio     = binding.etBio.text.toString().trim()
        val phone   = binding.etPhone.text.toString().trim()
        val tags    = binding.etTags.text.toString().trim()

        if (name.isBlank()) { binding.etName.error = "Name required"; return }
        if (bio.length > 300) { binding.etBio.error = "Max 300 characters"; return }

        val years = yearsStr.toIntOrNull() ?: 0
        val updated = (currentArtisan ?: Artisan()).copy(
            id = 1, name = name, village = village, yearsOfCraft = years,
            biography = bio, phone = phone, heritageTags = tags
        )
        viewModel.saveArtisan(updated)
        Toast.makeText(requireContext(), "✅ Profile saved!", Toast.LENGTH_SHORT).show()
    }

    private fun calculateEarnings() {
        val potsStr  = binding.etPotsPerWeek.text.toString().trim()
        val priceStr = binding.etPricePerPot.text.toString().trim()
        if (potsStr.isBlank() || priceStr.isBlank()) {
            Toast.makeText(requireContext(), "Enter pots per week and price", Toast.LENGTH_SHORT).show()
            return
        }
        val pots  = potsStr.toIntOrNull() ?: 0
        val price = priceStr.toDoubleOrNull() ?: 0.0
        val directIncome     = pots * price
        val middlemanIncome  = directIncome * 0.60
        val difference       = directIncome - middlemanIncome

        binding.tvEarningsResult.visibility = View.VISIBLE
        binding.tvEarningsResult.text = """
📊  Weekly Earnings Comparison

✅  Direct Selling:       ₹ ${"%.2f".format(directIncome)}
❌  Through Middleman: ₹ ${"%.2f".format(middlemanIncome)}
─────────────────────────────
💸  You LOSE:              ₹ ${"%.2f".format(difference)} / week
                              ₹ ${"%.2f".format(difference * 52)} / year

Sell directly — keep 100% of your craft's value! 🏺
        """.trimIndent()
    }

    override fun onDestroyView() { super.onDestroyView(); _binding = null }
}
