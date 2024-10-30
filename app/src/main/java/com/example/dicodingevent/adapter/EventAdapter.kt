package com.example.dicodingevent.adapter


import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.dicodingevent.R
import com.example.dicodingevent.data.remote.response.EventItem
import com.example.dicodingevent.databinding.ItemRowEventsBinding

class EventAdapter(
    private val onClickItemListener: (EventItem) -> Unit
) : ListAdapter<EventItem, EventAdapter.EventViewHolder>(DIFF_CALLBACK) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EventViewHolder {
        val binding = ItemRowEventsBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return EventViewHolder(binding)
    }

    override fun onBindViewHolder(holder: EventViewHolder, position: Int) {
        val event = getItem(position)
        Log.d("EventAdapter", "Binding event: $event")
        holder.bind(event, onClickItemListener)
    }

    class EventViewHolder(private val binding: ItemRowEventsBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(event: EventItem, onItemClickListener: (EventItem) -> Unit) {
            Glide.with(itemView.context)
                .load(event.mediaCover)
                .placeholder(R.drawable.baseline_image)
                .into(binding.ivImage)
            binding.tvTitle.text = event.name

            itemView.setOnClickListener {
                onItemClickListener(event)
            }
        }
    }

    companion object {
        val DIFF_CALLBACK = object : DiffUtil.ItemCallback<EventItem>() {
            override fun areItemsTheSame(
                oldItem: EventItem,
                newItem: EventItem
            ): Boolean {
                return oldItem == newItem
            }

            override fun areContentsTheSame(
                oldItem: EventItem,
                newItem: EventItem
            ): Boolean {
                return oldItem == newItem
            }
        }
    }
}