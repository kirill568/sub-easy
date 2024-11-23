package com.example.subeasy.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "user")
data class User(
    @PrimaryKey val id: Int,
    val firstName: String,
    val lastName: String,
    val avatarPath: String
)