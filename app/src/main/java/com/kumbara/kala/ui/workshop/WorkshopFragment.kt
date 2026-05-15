package com.kumbara.kala.ui.workshop

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.kumbara.kala.databinding.FragmentWorkshopBinding
import com.kumbara.kala.viewmodel.ArtisanViewModel
import com.kumbara.kala.viewmodel.WorkshopViewModel

class WorkshopFragment : Fragment() {
    private var _binding: FragmentWorkshopBinding? = null
    private val binding get() = _binding!!
    private val workshopViewModel: WorkshopViewModel by activityViewModels()
    private val artisanViewModel: ArtisanViewModel by activityViewModels()
    private lateinit var adapter: WorkshopAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentWorkshopBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        adapter = WorkshopAdapter(
            onBook = { workshop ->
                val artisanName = artisanViewModel.artisan.value?.name ?: "Kumbara Artisan"
                val phone = artisanViewModel.artisan.value?.phone ?: ""
                if (phone.isBlank()) {
                    Toast.makeText(requireContext(), "Please add your WhatsApp number in Artisan Bio first", Toast.LENGTH_LONG).show()
                    return@WorkshopAdapter
                }
                val msg = "Hi $artisanName, I would like to book 1 slot for ${workshop.title} on ${workshop.date} at ₹${workshop.price}/person. Please confirm availability."
                val cleanPhone = phone.replace("+", "").replace(" ", "")
                val waIntent = Intent(Intent.ACTION_VIEW,
                    android.net.Uri.parse("https://wa.me/$cleanPhone?text=${android.net.Uri.encode(msg)}"))
                try {
                    startActivity(waIntent)
                } catch (e: Exception) {
                    Toast.makeText(requireContext(), "WhatsApp not installed", Toast.LENGTH_SHORT).show()
                }
            },
            onDelete = { workshop ->
                workshopViewModel.deleteWorkshop(workshop)
                Toast.makeText(requireContext(), "Workshop deleted", Toast.LENGTH_SHORT).show()
            }
        )

        binding.recyclerViewWorkshops.adapter = adapter

        workshopViewModel.allWorkshops.observe(viewLifecycleOwner) { workshops ->
            adapter.submitList(workshops)
            binding.emptyState.visibility = if (workshops.isEmpty()) View.VISIBLE else View.GONE
            binding.recyclerViewWorkshops.visibility = if (workshops.isEmpty()) View.GONE else View.VISIBLE
        }

        binding.fabAddWorkshop.setOnClickListener {
            startActivity(Intent(requireContext(), AddWorkshopActivity::class.java))
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
