package com.example.subeasy.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.ForeignKey

@Entity(
    tableName = "subscription",
    foreignKeys = [
        ForeignKey(
            entity = Service::class,
            parentColumns = ["id"],
            childColumns = ["serviceId"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class Subscription(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val serviceId: Int,
    val startedOn: Long, // Timestamp
    val cycle: String,  // monthly, yearly
    val remind: String, // never, one_day, one_week, one_month
    val cost: Double,
    val description: String,
    val isActive: Boolean
)