package com.example.subeasy.data.local

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

object Migrations {
    val MIGRATION_1_2 = object : Migration(1, 2) {
        override fun migrate(db: SupportSQLiteDatabase) {
            val cursor = db.query("SELECT COUNT(*) FROM service")
            cursor.moveToFirst()
            val count = cursor.getInt(0)
            cursor.close()

            if (count == 0) {
//                db.execSQL("INSERT INTO service (id, iconPath, name) VALUES (3, 'drawable/serviceIcons/youtube_premium.xml', 'YouTube Premium')")
//                db.execSQL("INSERT INTO service (id, iconPath, name) VALUES (4, 'drawable/serviceIcons/apple_music.xml', 'Apple Music')")
//                db.execSQL("INSERT INTO service (id, iconPath, name) VALUES (5, 'drawable/serviceIcons/amazon_prime.xml', 'Amazon Prime Video')")
//                db.execSQL("INSERT INTO service (id, iconPath, name) VALUES (6, 'drawable/serviceIcons/disney_plus.xml', 'Disney+')")
//                db.execSQL("INSERT INTO service (id, iconPath, name) VALUES (7, 'drawable/serviceIcons/hbo_max.xml', 'HBO Max')")
//                db.execSQL("INSERT INTO service (id, iconPath, name) VALUES (8, 'drawable/serviceIcons/hulu.xml', 'Hulu')")
//                db.execSQL("INSERT INTO service (id, iconPath, name) VALUES (9, 'drawable/serviceIcons/twitch.xml', 'Twitch')")
//                db.execSQL("INSERT INTO service (id, iconPath, name) VALUES (10, 'drawable/serviceIcons/zoom.xml', 'Zoom')")
//                db.execSQL("INSERT INTO service (id, iconPath, name) VALUES (11, 'drawable/serviceIcons/office365.xml', 'Microsoft Office 365')")
//                db.execSQL("INSERT INTO service (id, iconPath, name) VALUES (12, 'drawable/serviceIcons/adobe_cc.xml', 'Adobe Creative Cloud')")
//                db.execSQL("INSERT INTO service (id, iconPath, name) VALUES (13, 'drawable/serviceIcons/dropbox.xml', 'Dropbox')")
//                db.execSQL("INSERT INTO service (id, iconPath, name) VALUES (14, 'drawable/serviceIcons/google_one.xml', 'Google One')")
//                db.execSQL("INSERT INTO service (id, iconPath, name) VALUES (15, 'drawable/serviceIcons/evernote.xml', 'Evernote')")
//                db.execSQL("INSERT INTO service (id, iconPath, name) VALUES (16, 'drawable/serviceIcons/trello.xml', 'Trello')")
//                db.execSQL("INSERT INTO service (id, iconPath, name) VALUES (17, 'drawable/serviceIcons/slack.xml', 'Slack')")
//                db.execSQL("INSERT INTO service (id, iconPath, name) VALUES (18, 'drawable/serviceIcons/patreon.xml', 'Patreon')")
//                db.execSQL("INSERT INTO service (id, iconPath, name) VALUES (19, 'drawable/serviceIcons/zoominfo.xml', 'ZoomInfo')")
//                db.execSQL("INSERT INTO service (id, iconPath, name) VALUES (20, 'drawable/serviceIcons/avast.xml', 'Avast Premium')")
//                db.execSQL("INSERT INTO service (id, iconPath, name) VALUES (21, 'drawable/serviceIcons/kaspersky.xml', 'Kaspersky Total Security')")
//                db.execSQL("INSERT INTO service (id, iconPath, name) VALUES (22, 'drawable/serviceIcons/epic_games.xml', 'Epic Games Store')")
//                db.execSQL("INSERT INTO service (id, iconPath, name) VALUES (23, 'drawable/serviceIcons/steam.xml', 'Steam')")
//                db.execSQL("INSERT INTO service (id, iconPath, name) VALUES (24, 'drawable/serviceIcons/playstation_plus.xml', 'PlayStation Plus')")
//                db.execSQL("INSERT INTO service (id, iconPath, name) VALUES (25, 'drawable/serviceIcons/xbox_game_pass.xml', 'Xbox Game Pass')")
//                db.execSQL("INSERT INTO service (id, iconPath, name) VALUES (26, 'drawable/serviceIcons/okko.xml', 'Okko')")
//                db.execSQL("INSERT INTO service (id, iconPath, name) VALUES (27, 'drawable/serviceIcons/ivi.xml', 'Ivi.ru')")
//                db.execSQL("INSERT INTO service (id, iconPath, name) VALUES (28, 'drawable/serviceIcons/yandex_music.xml', 'Yandex Music')")
//                db.execSQL("INSERT INTO service (id, iconPath, name) VALUES (29, 'drawable/serviceIcons/kinopoisk.xml', 'Кинопоиск')")
//                db.execSQL("INSERT INTO service (id, iconPath, name) VALUES (30, 'drawable/serviceIcons/rutube.xml', 'RuTube')")
            }
        }
    }
}