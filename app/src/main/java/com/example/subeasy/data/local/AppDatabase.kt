package com.example.subeasy.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.subeasy.data.local.dao.ServiceDao
import com.example.subeasy.data.local.dao.SubscriptionDao
import com.example.subeasy.data.local.dao.UserDao
import com.example.subeasy.data.local.entities.Service
import com.example.subeasy.data.local.entities.Subscription
import com.example.subeasy.data.local.entities.User

@Database(entities = [User::class, Service::class, Subscription::class], version = 2)
abstract class AppDatabase : RoomDatabase() {

    abstract fun userDao(): UserDao
    abstract fun serviceDao(): ServiceDao
    abstract fun subscriptionDao(): SubscriptionDao

    companion object {
        // Волатильная переменная для хранения экземпляра базы данных
        @Volatile
        private var INSTANCE: AppDatabase? = null

        // Метод для получения экземпляра базы данных
        fun getInstance(context: Context): AppDatabase {
            // Используем паттерн "Double-checked locking" для потокобезопасности
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "app_database"
                )
                    .fallbackToDestructiveMigration() // Удалить данные при изменении схемы (можно заменить на миграции)
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}