package com.example.pcraft.domain.usecase

import com.example.pcraft.data.model.Component
import com.example.pcraft.data.model.StoreOffer

class CalculateBuildPriceUseCase {

    fun execute(components: List<Component>, selectedOffers: Map<String, StoreOffer>): Pair<Double, Double> {
        val minimalPrice = components.sumOf { it.minPrice }
        val selectedPrice = components.sumOf { comp ->
            selectedOffers[comp.type.id]?.price ?: selectedOffers[comp.id]?.price ?: comp.minPrice
        }
        return Pair(minimalPrice, selectedPrice)
    }

    fun calculateMinimalPrice(components: List<Component>): Double {
        return components.sumOf { it.minPrice }
    }

    fun calculateSelectedPrice(components: List<Component>, selectedOffers: Map<String, StoreOffer>): Double {
        return components.sumOf { comp ->
            selectedOffers[comp.type.id]?.price ?: selectedOffers[comp.id]?.price ?: comp.minPrice
        }
    }
}
