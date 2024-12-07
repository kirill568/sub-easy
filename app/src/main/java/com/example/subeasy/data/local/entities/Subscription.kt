package com.example.subeasy.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.TypeConverters
import com.example.subeasy.data.local.converter.CycleConverter
import com.example.subeasy.data.local.converter.RemindConverter
import java.math.BigDecimal
import java.math.RoundingMode
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
    fun getDue(): String {
        val nextPaymentDateMillis = this.calculateNextPaymentDate()
        val currentDate = Calendar.getInstance()
        val nextPaymentDate = Calendar.getInstance().apply { timeInMillis = nextPaymentDateMillis }

        val daysUntilNextPayment = ((nextPaymentDate.timeInMillis - currentDate.timeInMillis) / (1000 * 60 * 60 * 24)).toInt()
        val monthsUntilNextPayment = (nextPaymentDate.get(Calendar.MONTH) - currentDate.get(Calendar.MONTH) +
                (nextPaymentDate.get(Calendar.YEAR) - currentDate.get(Calendar.YEAR)) * 12)

        return if (daysUntilNextPayment == 0) {
            "Tomorrow"
        } else if (daysUntilNextPayment < 30) {
            "Due in $daysUntilNextPayment days"
        } else {
            "Due in $monthsUntilNextPayment months"
        }
    }

    fun calculateNextPaymentDate(): Long {
        val currentTime = System.currentTimeMillis()
        val calendar = Calendar.getInstance()
        calendar.timeInMillis = startedOn

        val elapsedTimeInMillis = currentTime - startedOn
        val elapsedCycles = (elapsedTimeInMillis / (cycle.months * 30L * 24 * 60 * 60 * 1000)).toInt()

        calendar.add(Calendar.MONTH, cycle.months * (elapsedCycles + 1))
        return calendar.timeInMillis
    }

    fun calculateTotalAmount(): Double {
        val currentTime = System.currentTimeMillis()
        val calendar = Calendar.getInstance()
        calendar.timeInMillis = startedOn

        var totalSpent: BigDecimal = BigDecimal.ZERO

        totalSpent += cost.toBigDecimal()

        while (calendar.timeInMillis < currentTime) {
            calendar.add(Calendar.MONTH, cycle.months)
            if (calendar.timeInMillis <= currentTime) {
                totalSpent += cost.toBigDecimal()
            }
        }

        return totalSpent.setScale(2, RoundingMode.HALF_EVEN).toDouble()
    }
}