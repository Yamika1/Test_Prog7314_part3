package com.example.studymate

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(
    entities = [FocusSession::class],
    version = 1,
    exportSchema = false
)
abstract class FocusSessionDatabase : RoomDatabase() {

    abstract fun focusSession(): FocusSessionInterface

    companion object {

        @Volatile
        private var INSTANCE: FocusSessionDatabase? = null

        fun getDatabase(context: Context): FocusSessionDatabase {

            return INSTANCE ?: synchronized(this) {

                INSTANCE ?: Room.databaseBuilder(
                    context.applicationContext,
                    FocusSessionDatabase::class.java,
                    "focus_session_database"
                )
                    .build()
                    .also { INSTANCE = it }
            }
        }
    }
}