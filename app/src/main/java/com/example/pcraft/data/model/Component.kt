package com.example.pcraft.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "components")
data class Component(
    @PrimaryKey val id: String,
    val name: String,
    val type: ComponentType,
    val brand: String,
    val description: String,
    val imageUrl: String,
    val specifications: Map<String, String>,
    val powerConsumptionWatts: Int? = null,
    val requiredSocket: String? = null,
    val supportedSocket: String? = null,
    val ramType: String? = null,
    val motherboardSupportedRamType: String? = null,
    val motherboardFormFactor: String? = null,
    val caseSupportedFormFactors: List<String> = emptyList(),
    val psuPowerWatts: Int? = null,
    val minPrice: Double,
    val isFavorite: Boolean = false
)
