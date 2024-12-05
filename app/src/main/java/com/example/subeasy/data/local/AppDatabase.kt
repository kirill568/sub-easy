package com.example.subeasy.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.subeasy.R
import com.example.subeasy.data.local.converter.CycleConverter
import com.example.subeasy.data.local.converter.RemindConverter
import com.example.subeasy.data.local.dao.ServiceDao
import com.example.subeasy.data.local.dao.SubscriptionDao
import com.example.subeasy.data.local.dao.UserDao
import com.example.subeasy.data.local.entities.Service
import com.example.subeasy.data.local.entities.Subscription
import com.example.subeasy.data.local.entities.User
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.json.JSONArray

@Database(entities = [User::class, Service::class, Subscription::class], version = 1)
@TypeConverters(CycleConverter::class, RemindConverter::class)
abstract class AppDatabase : RoomDatabase() {

    abstract fun userDao(): UserDao
    abstract fun serviceDao(): ServiceDao
    abstract fun subscriptionDao(): SubscriptionDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "app_database"
                )
                    .addCallback(object:RoomDatabase.Callback() {
                        override fun onCreate(db: SupportSQLiteDatabase) {
                            super.onCreate(db)
                            GlobalScope.launch {
                                val serviceDao = getInstance(context).serviceDao()

                                val serviceList: JSONArray =
                                    context.resources.openRawResource(R.raw.services).bufferedReader().use {
                                        JSONArray(it.readText())
                                    }

                                serviceList.takeIf { it.length() > 0 }?.let { list ->
                                    for (index in 0 until list.length()) {
                                        val serviceObj = list.getJSONObject(index)
                                        serviceDao.insert(
                                            Service(
                                                iconPath = serviceObj.getString("iconPath"),
                                                name = serviceObj.getString("name")
                                            )
                                        )
                                    }
                                }
                            }
                        }
                    })
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}