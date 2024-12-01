package com.example.subeasy.data.local

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

object Migrations {
    val MIGRATION_4_5 = object : Migration(4, 5) {
        override fun migrate(db: SupportSQLiteDatabase) {
            val cursor = db.query("SELECT COUNT(*) FROM service")
            cursor.moveToFirst()
            val count = cursor.getInt(0)
            cursor.close()

            if (count == 0) {
                db.execSQL("INSERT INTO service (id, iconPath, name) VALUES (1, 'icon1.png', 'Service 1')")
                db.execSQL("INSERT INTO service (id, iconPath, name) VALUES (2, 'icon2.png', 'Service 2')")
                db.execSQL("INSERT INTO service (id, iconPath, name) VALUES (3, 'icon3.png', 'Service 3')")
            }
        }
    }
}