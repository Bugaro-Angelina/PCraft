package com.example.pcraft.domain.usecase

import com.example.pcraft.data.model.BuildCompatibilityReport
import com.example.pcraft.data.model.CompatibilityResult
import com.example.pcraft.data.model.CompatibilityStatus
import com.example.pcraft.data.model.Component

class CheckCompatibilityUseCase {

    fun execute(components: List<Component>): BuildCompatibilityReport {
        val cpu = components.find { it.type.id == "cpu" }
        val motherboard = components.find { it.type.id == "motherboard" }
        val ram = components.find { it.type.id == "ram" }
        val psu = components.find { it.type.id == "psu" }
        val case = components.find { it.type.id == "case" }

        val positives = mutableListOf<String>()
        val warnings = mutableListOf<String>()
        val problems = mutableListOf<String>()
        val perComponentResults = mutableListOf<CompatibilityResult>()

        if (cpu != null && motherboard != null) {
            if (cpu.requiredSocket != motherboard.supportedSocket) {
                problems += "Сокет процессора не совпадает с материнской платой."
                perComponentResults += CompatibilityResult(cpu.id, cpu.name, CompatibilityStatus.INCOMPATIBLE, "Нужен сокет ${cpu.requiredSocket}, у платы ${motherboard.supportedSocket}.")
                perComponentResults += CompatibilityResult(motherboard.id, motherboard.name, CompatibilityStatus.INCOMPATIBLE, "Материнская плата не подходит процессору по сокету.")
            } else {
                positives += "Процессор и материнская плата совместимы по сокету."
                perComponentResults += CompatibilityResult(cpu.id, cpu.name, CompatibilityStatus.COMPATIBLE, "Сокет совпадает.")
                perComponentResults += CompatibilityResult(motherboard.id, motherboard.name, CompatibilityStatus.COMPATIBLE, "Сокет совпадает.")
            }
        }

        if (ram != null && motherboard != null) {
            if (ram.ramType != motherboard.motherboardSupportedRamType) {
                problems += "Оперативная память не поддерживается выбранной материнской платой."
                perComponentResults += CompatibilityResult(ram.id, ram.name, CompatibilityStatus.INCOMPATIBLE, "Тип памяти ${ram.ramType}, плата ожидает ${motherboard.motherboardSupportedRamType}.")
                perComponentResults += CompatibilityResult(motherboard.id, motherboard.name, CompatibilityStatus.INCOMPATIBLE, "Материнская плата не поддерживает выбранный тип памяти.")
            } else if (perComponentResults.none { it.componentId == ram.id }) {
                positives += "Оперативная память совместима с материнской платой."
                perComponentResults += CompatibilityResult(ram.id, ram.name, CompatibilityStatus.COMPATIBLE, "Тип памяти совпадает.")
            }
        }

        if (motherboard != null && case != null) {
            val supported = case.caseSupportedFormFactors.contains(motherboard.motherboardFormFactor)
            if (!supported) {
                problems += "Корпус не поддерживает форм-фактор материнской платы."
                perComponentResults += CompatibilityResult(motherboard.id, motherboard.name, CompatibilityStatus.INCOMPATIBLE, "Форм-фактор ${motherboard.motherboardFormFactor} не поддерживается корпусом.")
                perComponentResults += CompatibilityResult(case.id, case.name, CompatibilityStatus.INCOMPATIBLE, "Корпус поддерживает: ${case.caseSupportedFormFactors.joinToString()}.")
            } else if (perComponentResults.none { it.componentId == case.id }) {
                positives += "Корпус подходит под форм-фактор материнской платы."
                perComponentResults += CompatibilityResult(case.id, case.name, CompatibilityStatus.COMPATIBLE, "Форм-фактор поддерживается.")
            }
        }

        val totalPower = components.sumOf { it.powerConsumptionWatts ?: 0 }
        val psuPower = psu?.psuPowerWatts

        if (psu != null && psuPower != null) {
            val loadPercent = totalPower.toDouble() / psuPower * 100
            when {
                totalPower > psuPower -> {
                    problems += "Недостаточно мощности блока питания."
                    perComponentResults += CompatibilityResult(psu.id, psu.name, CompatibilityStatus.INCOMPATIBLE, "Нужно ${totalPower} Вт, блок рассчитан на ${psuPower} Вт.")
                }

                loadPercent > 85 -> {
                    warnings += "Блок питания работает почти на пределе. Лучше выбрать модель мощнее."
                    perComponentResults += CompatibilityResult(psu.id, psu.name, CompatibilityStatus.PARTIALLY_COMPATIBLE, "Нагрузка ${String.format("%.0f", loadPercent)}%.")
                }

                else -> {
                    positives += "Мощности блока питания хватает с запасом."
                    perComponentResults += CompatibilityResult(psu.id, psu.name, CompatibilityStatus.COMPATIBLE, "Нагрузка ${String.format("%.0f", loadPercent)}%.")
                }
            }
        } else if (components.isNotEmpty()) {
            warnings += "Блок питания не выбран, поэтому полная проверка невозможна."
        }

        components.forEach { component ->
            if (perComponentResults.none { it.componentId == component.id }) {
                perComponentResults += CompatibilityResult(
                    component.id,
                    component.name,
                    CompatibilityStatus.COMPATIBLE,
                    "Проблем не обнаружено."
                )
            }
        }

        val overallStatus = when {
            problems.isNotEmpty() -> CompatibilityStatus.INCOMPATIBLE
            warnings.isNotEmpty() -> CompatibilityStatus.PARTIALLY_COMPATIBLE
            else -> CompatibilityStatus.COMPATIBLE
        }

        return BuildCompatibilityReport(
            overallStatus = overallStatus,
            positives = positives,
            warnings = warnings,
            problems = problems,
            perComponentResults = perComponentResults,
            totalPowerConsumption = totalPower,
            psuPower = psuPower,
            psuLoadPercent = if (psuPower != null && psuPower > 0) totalPower.toDouble() / psuPower * 100 else null
        )
    }
}

