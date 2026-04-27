package com.example.pcraft.di

import android.content.Context
import androidx.room.Room
import com.example.pcraft.data.database.AppDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideAppDatabase(@ApplicationContext context: Context): AppDatabase {
        return Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            "pcraft_database"
        ).build()
    }

    @Provides
    fun provideComponentDao(database: AppDatabase) = database.componentDao()

    @Provides
    fun provideBuildDao(database: AppDatabase) = database.buildDao()
}
