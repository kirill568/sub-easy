package com.example.subeasy.ui.home

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
import com.example.subeasy.data.local.entities.SubscriptionWithService
import java.io.IOException
import java.util.Calendar

class SubscriptionDiffCallback : DiffUtil.ItemCallback<SubscriptionWithService>() {
    override fun areItemsTheSame(oldItem: SubscriptionWithService, newItem: SubscriptionWithService): Boolean {
        return oldItem.subscription.id == newItem.subscription.id
    }

    override fun areContentsTheSame(oldItem: SubscriptionWithService, newItem: SubscriptionWithService): Boolean {
        return oldItem == newItem
    }
}

class SubscriptionAdapter : ListAdapter<SubscriptionWithService, SubscriptionAdapter.SubscriptionViewHolder>(SubscriptionDiffCallback()) {

    inner class SubscriptionViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val nameTextView: TextView = itemView.findViewById(R.id.name)
        private val costTextView: TextView = itemView.findViewById(R.id.cost)
        private val cycleTextView: TextView = itemView.findViewById(R.id.cycle)
        private val dueTextView: TextView = itemView.findViewById(R.id.due)
        private val iconImageView: ImageView = itemView.findViewById(R.id.serviceIcon)

        fun bind(subscriptionWithService: SubscriptionWithService) {
            nameTextView.text = subscriptionWithService.service.name
            costTextView.text = "$" + subscriptionWithService.subscription.cost.toString()
            cycleTextView.text = "/" + subscriptionWithService.subscription.cycle.toString()
            dueTextView.text = daysOrMonthsUntilNextPayment(subscriptionWithService.subscription.calculateNextPaymentDate())

            try {
                val assetManager = itemView.context.assets
                val inputStream = assetManager.open("serviceIcons/" + subscriptionWithService.service.iconPath)
                val drawable = Drawable.createFromStream(inputStream, null)
                iconImageView.setImageDrawable(drawable)
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SubscriptionViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.my_subscription_item, parent, false)
        return SubscriptionViewHolder(view)
    }

    override fun onBindViewHolder(holder: SubscriptionViewHolder, position: Int) {
        val subscriptionWithService = getItem(position)
        holder.bind(subscriptionWithService)
    }

    private fun daysOrMonthsUntilNextPayment(nextPaymentDateMillis: Long): String {
        val currentDate = Calendar.getInstance()
        val nextPaymentDate = Calendar.getInstance().apply { timeInMillis = nextPaymentDateMillis }

        val daysUntilNextPayment = ((nextPaymentDate.timeInMillis - currentDate.timeInMillis) / (1000 * 60 * 60 * 24)).toInt()
        val monthsUntilNextPayment = (nextPaymentDate.get(Calendar.MONTH) - currentDate.get(Calendar.MONTH) +
                (nextPaymentDate.get(Calendar.YEAR) - currentDate.get(Calendar.YEAR)) * 12)

        return if (daysUntilNextPayment < 30) {
            "Due in $daysUntilNextPayment days"
        } else {
            "Due in $monthsUntilNextPayment months"
        }
    }
}
