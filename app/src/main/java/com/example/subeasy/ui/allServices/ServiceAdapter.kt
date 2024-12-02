package com.example.subeasy.ui.allServices

import android.graphics.drawable.Drawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.subeasy.R
import com.example.subeasy.data.local.entities.Service
import java.io.IOException

class ServiceAdapter : ListAdapter<Service, ServiceAdapter.ServiceViewHolder>(DIFF_CALLBACK) {

    companion object {
        private val DIFF_CALLBACK = object : DiffUtil.ItemCallback<Service>() {
            override fun areItemsTheSame(oldItem: Service, newItem: Service) = oldItem.id == newItem.id
            override fun areContentsTheSame(oldItem: Service, newItem: Service) = oldItem == newItem
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ServiceViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.all_services_item, parent, false)
        return ServiceViewHolder(view)
    }

    override fun onBindViewHolder(holder: ServiceViewHolder, position: Int) {
        val service = getItem(position)
        holder.bind(service)
    }

    class ServiceViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val nameTextView: TextView = itemView.findViewById(R.id.name)
        private val iconImageView: ImageView = itemView.findViewById(R.id.serviceIcon)

        fun bind(service: Service) {
            nameTextView.text = service.name

            try {
                val assetManager = itemView.context.assets
                val inputStream = assetManager.open("serviceIcons/" + service.iconPath) // Путь к файлу в папке serviceIcons
                val drawable = Drawable.createFromStream(inputStream, null)
                iconImageView.setImageDrawable(drawable)
            } catch (e: IOException) {
                e.printStackTrace() // Обработка ошибок
            }
        }
    }
}