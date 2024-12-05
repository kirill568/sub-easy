package com.example.subeasy.data.local.converter

import androidx.room.TypeConverter
import com.example.subeasy.data.local.entities.Remind

class RemindConverter {
    @TypeConverter
    fun fromRemind(cycle: Remind): String {
        return cycle.name
    }

    @TypeConverter
    fun toRemind(value: String): Remind {
        return Remind.valueOf(value)
    }
}