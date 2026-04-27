package com.example.pcraft.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.pcraft.data.model.BuildConfiguration
import com.example.pcraft.data.model.Component
import kotlinx.coroutines.flow.Flow

@Dao
interface ComponentDao {
    @Query("SELECT * FROM components")
    fun getAllComponents(): Flow<List<Component>>

    @Query("SELECT * FROM components WHERE isFavorite = 1")
    fun getFavoriteComponents(): Flow<List<Component>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertComponent(component: Component)

    @Update
    suspend fun updateComponent(component: Component)

    @Query("DELETE FROM components WHERE id = :id")
    suspend fun deleteComponent(id: String)
}
