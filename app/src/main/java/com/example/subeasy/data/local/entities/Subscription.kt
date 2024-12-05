package com.example.subeasy.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.TypeConverters
import com.example.subeasy.data.local.converter.CycleConverter
import com.example.subeasy.data.local.converter.RemindConverter
import java.util.Calendar
import java.time.LocalDate
import java.time.temporal.ChronoUnit

enum class Cycle(val months: Int) {
    MONTHLY(1) {
        override fun toString(): String {
            return "Monthly"
        }
    },
    YEARLY(12) {
        override fun toString(): String {
            return "Yearly"
        }
    }
}

enum class Remind(val remind: String) {
    NEVER("never") {
        override fun toString(): String {
            return "Never"
        }
    },
    ONE_DAY("one_day") {
        override fun toString(): String {
            return "One day"
        }
    },
    ONE_WEEK("one_week") {
        override fun toString(): String {
            return "One week"
        }
    },
    ONE_MONTH("one_month") {
        override fun toString(): String {
            return "One month"
        }
    }
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
    var serviceId: Int,
    val startedOn: Long, // Timestamp
    @TypeConverters(CycleConverter::class) val cycle: Cycle,  // monthly, yearly
    @TypeConverters(RemindConverter::class) val remind: Remind, // never, one_day, one_week, one_month
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