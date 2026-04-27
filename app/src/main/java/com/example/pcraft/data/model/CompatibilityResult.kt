package com.example.pcraft.data.model

data class CompatibilityResult(
    val componentId: String,
    val componentName: String,
    val status: CompatibilityStatus,
    val message: String
)
