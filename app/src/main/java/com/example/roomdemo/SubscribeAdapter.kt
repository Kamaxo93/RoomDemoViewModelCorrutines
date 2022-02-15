package com.example.roomdemo

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.roomdemo.databinding.RowSubscribeBinding
import com.example.roomdemo.db.Subscriber
import java.util.ArrayList

class SubscribeAdapter(private val clickListener: (Subscriber) -> Unit
) : RecyclerView.Adapter<SubscribeAdapter.SubscribeViewHolder>() {

    private val subscribersList =  ArrayList<Subscriber>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SubscribeViewHolder {
        val binding =
            RowSubscribeBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return SubscribeViewHolder(binding)
    }

    override fun onBindViewHolder(holder: SubscribeViewHolder, position: Int) {
        holder.bind(subscribersList[position], clickListener)
    }

    override fun getItemCount() = subscribersList.size

    fun setList(subscribers: List<Subscriber>) {
        subscribersList.clear()
        subscribersList.addAll(subscribers)
    }

    inner class SubscribeViewHolder(private val binding: RowSubscribeBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(subscriber: Subscriber, clickListener: (Subscriber) -> Unit) {
            binding.apply {
                rowSubscribeLabelName.text = subscriber.name
                rowSubscribeLabelEmail.text = subscriber.email
                binding.root.setOnClickListener {
                    clickListener(subscriber)
                }
            }

        }
    }

}

