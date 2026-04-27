package com.example.pcraft.data.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.pcraft.data.dao.BuildDao
import com.example.pcraft.data.dao.ComponentDao
import com.example.pcraft.data.model.BuildConfiguration
import com.example.pcraft.data.model.Component

@Database(
    entities = [Component::class, BuildConfiguration::class],
    version = 1,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun componentDao(): ComponentDao
    abstract fun buildDao(): BuildDao
}
