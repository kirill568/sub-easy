package com.example.subeasy.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.TypeConverters
import com.example.subeasy.data.local.converter.CycleConverter
import java.util.Calendar
import java.time.LocalDate
import java.time.temporal.ChronoUnit

enum class Cycle(val months: Int) {
    MONTHLY(1),
    YEARLY(12)
}

@Entity(
    tableName = "subscription",
    foreignKeys = [
        ForeignKey(
            entity = Service::class,
            parentColumns = ["id"],
            childColumns = ["serviceId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("serviceId")]
)
data class Subscription(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val serviceId: Int,
    val startedOn: Long, // Timestamp
    @TypeConverters(CycleConverter::class) val cycle: Cycle,  // monthly, yearly
    val remind: String, // never, one_day, one_week, one_month
    val cost: Double,
    val description: String?,
    val isActive: Boolean
) {
    fun calculateNextPaymentDate(): Long {
        val calendar = Calendar.getInstance()
        calendar.timeInMillis = startedOn
        calendar.add(Calendar.MONTH, cycle.months)
        return calendar.timeInMillis
    }
}