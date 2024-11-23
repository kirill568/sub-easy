package com.example.subeasy.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "service")
data class Service(
    @PrimaryKey val id: Int,
    val iconPath: String,
    val name: String
)