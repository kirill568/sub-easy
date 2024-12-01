package com.example.subeasy.data.local.converter

import androidx.room.TypeConverter
import com.example.subeasy.data.local.entities.Cycle

class CycleConverter {
    @TypeConverter
    fun fromCycle(cycle: Cycle): String {
        return cycle.name
    }

    @TypeConverter
    fun toCycle(value: String): Cycle {
        return Cycle.valueOf(value)
    }
}