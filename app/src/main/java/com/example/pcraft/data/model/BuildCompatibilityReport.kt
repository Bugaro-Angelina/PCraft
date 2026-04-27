package com.example.pcraft.data.model

data class BuildCompatibilityReport(
    val overallStatus: CompatibilityStatus,
    val positives: List<String>,
    val warnings: List<String>,
    val problems: List<String>,
    val perComponentResults: List<CompatibilityResult>,
    val totalPowerConsumption: Int,
    val psuPower: Int?,
    val psuLoadPercent: Double?
) {
    fun getSummary(): String = when {
        overallStatus == CompatibilityStatus.INCOMPATIBLE && problems.isNotEmpty() ->
            "Обнаружено ${problems.size} проблем совместимости"

        overallStatus == CompatibilityStatus.PARTIALLY_COMPATIBLE && warnings.isNotEmpty() ->
            "Есть ${warnings.size} предупреждений"

        else -> "Сборка совместима"
    }
}

