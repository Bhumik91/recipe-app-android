package com.example.recipeapp.core.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.recipeapp.storage.notificationlog.NotificationLogDao
import com.example.recipeapp.storage.notificationlog.NotificationLogEntity
import com.example.recipeapp.storage.notificationlog.RecipeActionConverter

@Database(
    entities = [NotificationLogEntity::class],
    version = 1,
    exportSchema = false
)
@TypeConverters(RecipeActionConverter::class)
abstract class AppDatabase : RoomDatabase() {

    abstract fun notificationLogDao(): NotificationLogDao

    companion object {
        fun build(context: Context): AppDatabase =
            Room.databaseBuilder(context.applicationContext, AppDatabase::class.java, "recipe_app.db")
                .fallbackToDestructiveMigration(true)
                .build()
    }
}
