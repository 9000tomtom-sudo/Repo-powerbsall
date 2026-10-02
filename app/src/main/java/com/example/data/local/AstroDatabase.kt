package com.example.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(
    entities = [SavedPredictionEntity::class, HistoricalDrawEntity::class],
    version = 1,
    exportSchema = false
)
abstract class AstroDatabase : RoomDatabase() {

    abstract fun astroDao(): AstroDao

    companion object {
        @Volatile
        private var INSTANCE: AstroDatabase? = null

        fun getDatabase(context: Context): AstroDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AstroDatabase::class.java,
                    "astro_predictor_db"
                ).fallbackToDestructiveMigration(true).build()
                INSTANCE = instance
                instance
            }
        }
    }
}
