package com.example.subeasy

import android.app.Application
import com.example.subeasy.data.local.AppDatabase

class SubEasy: Application() {
    val database: AppDatabase by lazy {
        AppDatabase.getInstance(this)
    }

    override fun onCreate() {
        super.onCreate()
    }
}