package com.example.pcraft.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.pcraft.data.model.CompatibilityStatus
import java.util.Date

@Entity(tableName = "build_configurations")
data class BuildConfiguration(
    @PrimaryKey val id: String,
    val name: String,
    val note: String,
    val createdAt: Date,
    val selectedComponents: List<Component>,
    val totalMinimalPrice: Double,
    val totalSelectedStoresPrice: Double,
    val compatibilityStatus: CompatibilityStatus,
    val selectedStoreOffers: List<StoreOffer>
)
