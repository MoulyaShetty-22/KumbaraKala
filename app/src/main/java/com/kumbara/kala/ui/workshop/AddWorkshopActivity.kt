package com.kumbara.kala.ui.workshop

import android.app.DatePickerDialog
import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.kumbara.kala.data.model.Workshop
import com.kumbara.kala.databinding.ActivityAddWorkshopBinding
import com.kumbara.kala.viewmodel.WorkshopViewModel
import java.text.SimpleDateFormat
import java.util.*

class AddWorkshopActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAddWorkshopBinding
    private val viewModel: WorkshopViewModel by viewModels()
    private var selectedDate = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddWorkshopBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = "Add Workshop"

        binding.btnPickDate.setOnClickListener {
            val cal = Calendar.getInstance()
            DatePickerDialog(this, { _, y, m, d ->
                selectedDate = "%04d-%02d-%02d".format(y, m + 1, d)
                binding.tvSelectedDate.text = selectedDate
            }, cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH)).also {
                it.datePicker.minDate = cal.timeInMillis
                it.show()
            }
        }

        binding.btnSaveWorkshop.setOnClickListener { saveWorkshop() }
    }

    private fun saveWorkshop() {
        val title = binding.etTitle.text.toString().trim()
        val duration = binding.etDuration.text.toString().trim()
        val priceStr = binding.etPrice.text.toString().trim()
        val slotsStr = binding.etSlots.text.toString().trim()
        val location = binding.etLocation.text.toString().trim()

        if (title.isBlank()) { binding.etTitle.error = "Title required"; return }
        if (selectedDate.isBlank()) { Toast.makeText(this, "Please select a date", Toast.LENGTH_SHORT).show(); return }
        if (duration.isBlank()) { binding.etDuration.error = "Duration required"; return }
        if (priceStr.isBlank()) { binding.etPrice.error = "Price required"; return }
        if (slotsStr.isBlank()) { binding.etSlots.error = "Slots required"; return }
        if (location.isBlank()) { binding.etLocation.error = "Location required"; return }

        val price = priceStr.toDoubleOrNull() ?: run { binding.etPrice.error = "Invalid"; return }
        val slots = slotsStr.toIntOrNull() ?: run { binding.etSlots.error = "Invalid"; return }

        // Validate date is today or future
        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val today = sdf.format(Date())
        if (selectedDate < today) {
            Toast.makeText(this, "Date must be today or in the future", Toast.LENGTH_SHORT).show()
            return
        }

        val workshop = Workshop(
            title = title, date = selectedDate, duration = duration,
            price = price, slots = slots, location = location
        )
        viewModel.insertWorkshop(workshop) {
            Toast.makeText(this, "Workshop added!", Toast.LENGTH_SHORT).show()
            finish()
        }
    }

    override fun onSupportNavigateUp(): Boolean { finish(); return true }
}
