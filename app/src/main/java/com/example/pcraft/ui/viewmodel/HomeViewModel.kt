package com.example.pcraft.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.pcraft.data.model.Component
import com.example.pcraft.data.model.CompatibilityStatus
import com.example.pcraft.data.repository.BuildRepository
import com.example.pcraft.data.repository.ComponentRepository
import com.example.pcraft.domain.usecase.CheckCompatibilityUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

enum class CatalogSortOption {
    PRICE,
    NAME,
    BRAND,
    COMPATIBILITY
}

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val repository: ComponentRepository,
    private val buildRepository: BuildRepository,
    private val compatibilityUseCase: CheckCompatibilityUseCase
) : ViewModel() {

    private val _components = MutableStateFlow<List<Component>>(emptyList())
    val components: StateFlow<List<Component>> = _components

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery

    private val _selectedType = MutableStateFlow<String?>(null)
    val selectedType: StateFlow<String?> = _selectedType

    private val _sortOption = MutableStateFlow(CatalogSortOption.PRICE)
    val sortOption: StateFlow<CatalogSortOption> = _sortOption

    private var allComponentsCache: List<Component> = emptyList()

    init {
        viewModelScope.launch {
            repository.populateDatabase()
            repository.allComponents.collectLatest { all ->
                allComponentsCache = all
                _components.value = filterAndSortComponents(allComponentsCache)
            }
        }
    }

    fun setSearchQuery(query: String) {
        _searchQuery.value = query
        _components.value = filterAndSortComponents(allComponentsCache)
    }

    fun setSelectedType(type: String?) {
        _selectedType.value = type
        _components.value = filterAndSortComponents(allComponentsCache)
    }

    fun setSortOption(option: CatalogSortOption) {
        _sortOption.value = option
        _components.value = filterAndSortComponents(allComponentsCache)
    }

    private fun filterAndSortComponents(all: List<Component>): List<Component> {
        val filtered = all.filter { component ->
            (_searchQuery.value.isEmpty() || component.name.contains(_searchQuery.value, ignoreCase = true)) &&
                (_selectedType.value == null || component.type.id == _selectedType.value)
        }

        return when (_sortOption.value) {
            CatalogSortOption.PRICE -> filtered.sortedBy { it.minPrice }
            CatalogSortOption.NAME -> filtered.sortedBy { it.name }
            CatalogSortOption.BRAND -> filtered.sortedBy { it.brand }
            CatalogSortOption.COMPATIBILITY -> filtered.sortedBy { componentCompatibilityRank(it) }
        }
    }

    private fun componentCompatibilityRank(component: Component): Int {
        val selected = buildRepository.selectedComponents.value.values
            .filterNot { it.type.id == component.type.id }
            .toMutableList()

        selected += component
        val report = compatibilityUseCase.execute(selected)
        val result = report.perComponentResults.find { it.componentId == component.id }

        return when (result?.status ?: CompatibilityStatus.PARTIALLY_COMPATIBLE) {
            CompatibilityStatus.COMPATIBLE -> 0
            CompatibilityStatus.PARTIALLY_COMPATIBLE -> 1
            CompatibilityStatus.INCOMPATIBLE -> 2
        }
    }

    fun toggleFavorite(component: Component) {
        viewModelScope.launch {
            repository.toggleFavorite(component)
        }
    }
}
