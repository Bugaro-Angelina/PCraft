package com.example.pcraft.domain.usecase

import com.example.pcraft.data.model.Component

data class PsuLoadResult(
    val totalConsumption: Int,
    val psuPower: Int?,
    val loadPercent: Double?,
    val isValid: Boolean,
    val statusColor: String
)

class CalculatePsuLoadUseCase {

    fun execute(components: List<Component>): PsuLoadResult {
        val totalConsumption = components.sumOf { it.powerConsumptionWatts ?: 0 }
        val psu = components.find { it.type.id == "psu" }
        val psuPower = psu?.psuPowerWatts

        if (psuPower == null || psuPower == 0) {
            return PsuLoadResult(
                totalConsumption = totalConsumption,
                psuPower = null,
                loadPercent = null,
                isValid = false,
                statusColor = "gray"
            )
        }

        val loadPercent = (totalConsumption.toDouble() / psuPower) * 100
        val statusColor = when {
            totalConsumption > psuPower -> "red"
            loadPercent > 85 -> "yellow"
            else -> "green"
        }

        return PsuLoadResult(
            totalConsumption = totalConsumption,
            psuPower = psuPower,
            loadPercent = loadPercent,
            isValid = true,
            statusColor = statusColor
        )
    }

    fun getLoadPercentString(components: List<Component>): String {
        val result = execute(components)
        return if (result.isValid && result.loadPercent != null) {
            "${result.totalConsumption}W / ${result.psuPower}W (${String.format("%.0f", result.loadPercent)}%)"
        } else {
            "Данные недоступны"
        }
    }
}
