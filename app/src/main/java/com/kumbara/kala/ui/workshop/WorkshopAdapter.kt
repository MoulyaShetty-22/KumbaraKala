package com.kumbara.kala.ui.workshop

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.kumbara.kala.data.model.Workshop
import com.kumbara.kala.databinding.ItemWorkshopBinding

class WorkshopAdapter(
    private val onBook: (Workshop) -> Unit,
    private val onDelete: (Workshop) -> Unit
) : ListAdapter<Workshop, WorkshopAdapter.WorkshopViewHolder>(DiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WorkshopViewHolder {
        val binding = ItemWorkshopBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return WorkshopViewHolder(binding)
    }

    override fun onBindViewHolder(holder: WorkshopViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class WorkshopViewHolder(private val binding: ItemWorkshopBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(workshop: Workshop) {
            binding.tvTitle.text = workshop.title
            binding.tvDate.text = "📅 ${workshop.date}"
            binding.tvDuration.text = "⏱ ${workshop.duration}"
            binding.tvPrice.text = "₹ ${"%.0f".format(workshop.price)}/person"
            binding.tvLocation.text = "📍 ${workshop.location}"

            if (workshop.slots <= 0) {
                binding.tvSlots.text = "FULLY BOOKED"
                binding.tvSlots.setTextColor(binding.root.context.getColor(android.R.color.holo_red_dark))
                binding.btnBook.isEnabled = false
                binding.btnBook.alpha = 0.5f
                binding.tvFullyBooked.visibility = View.VISIBLE
            } else {
                binding.tvSlots.text = "${workshop.slots} slots available"
                binding.tvSlots.setTextColor(binding.root.context.getColor(android.R.color.holo_green_dark))
                binding.btnBook.isEnabled = true
                binding.btnBook.alpha = 1.0f
                binding.tvFullyBooked.visibility = View.GONE
            }

            binding.btnBook.setOnClickListener { onBook(workshop) }
            binding.btnDelete.setOnClickListener { onDelete(workshop) }
        }
    }

    class DiffCallback : DiffUtil.ItemCallback<Workshop>() {
        override fun areItemsTheSame(oldItem: Workshop, newItem: Workshop) = oldItem.id == newItem.id
        override fun areContentsTheSame(oldItem: Workshop, newItem: Workshop) = oldItem == newItem
    }
}
