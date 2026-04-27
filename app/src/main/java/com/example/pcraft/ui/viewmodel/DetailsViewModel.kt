package com.example.pcraft.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.pcraft.data.model.Component
import com.example.pcraft.data.model.StoreOffer
import com.example.pcraft.data.repository.BuildRepository
import com.example.pcraft.data.repository.ComponentRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DetailsViewModel @Inject constructor(
    private val repository: ComponentRepository,
    private val buildRepository: BuildRepository
) : ViewModel() {

    private val _component = MutableStateFlow<Component?>(null)
    val component: StateFlow<Component?> = _component

    private val _storeOffers = MutableStateFlow<List<StoreOffer>>(emptyList())
    val storeOffers: StateFlow<List<StoreOffer>> = _storeOffers

    fun loadComponent(componentId: String) {
        viewModelScope.launch {
            repository.getComponent(componentId)?.let { comp ->
                _component.value = comp
                _storeOffers.value = repository.getStoreOffersForComponent(componentId)
            }
        }
    }

    fun toggleFavorite() {
        _component.value?.let { comp ->
            viewModelScope.launch {
                repository.toggleFavorite(comp)
                _component.value = comp.copy(isFavorite = !comp.isFavorite)
            }
        }
    }

    fun toggleStoreSelection(offer: StoreOffer) {
        val updated = _storeOffers.value.map {
            if (it.id == offer.id) it.copy(isSelectedByUser = !it.isSelectedByUser) else it.copy(isSelectedByUser = false)
        }
        _storeOffers.value = updated
        _component.value?.type?.id?.let { typeId ->
            updated.find { it.isSelectedByUser }?.let { selected ->
                buildRepository.selectStoreOffer(typeId, selected)
            }
        }
    }

    fun addToBuilder() {
        _component.value?.let { buildRepository.addComponent(it) }
    }
}
