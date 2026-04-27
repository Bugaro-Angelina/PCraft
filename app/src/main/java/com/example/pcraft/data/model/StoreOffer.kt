package com.example.pcraft.data.model

data class StoreOffer(
    val id: String,
    val componentId: String,
    val storeName: String,
    val price: Double,
    val productUrl: String,
    val inStock: Boolean,
    val deliveryInfo: String,
    val isSelectedByUser: Boolean = false
)
