package com.example.pcraft.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.pcraft.data.model.BuildConfiguration
import kotlinx.coroutines.flow.Flow

@Dao
interface BuildDao {
    @Query("SELECT * FROM build_configurations ORDER BY createdAt DESC")
    fun getAllBuilds(): Flow<List<BuildConfiguration>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBuild(build: BuildConfiguration)

    @Query("DELETE FROM build_configurations WHERE id = :id")
    suspend fun deleteBuild(id: String)
}
