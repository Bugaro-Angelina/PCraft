package com.example.pcraft.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.pcraft.data.MockDataProvider
import com.example.pcraft.data.model.BuildCompatibilityReport
import com.example.pcraft.data.model.BuildConfiguration
import com.example.pcraft.data.model.CompatibilityStatus
import com.example.pcraft.data.model.Component
import com.example.pcraft.data.model.ComponentType
import com.example.pcraft.data.model.StoreOffer
import com.example.pcraft.data.repository.BuildRepository
import com.example.pcraft.domain.usecase.CalculateBuildPriceUseCase
import com.example.pcraft.domain.usecase.CalculatePsuLoadUseCase
import com.example.pcraft.domain.usecase.CheckCompatibilityUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import java.util.Date
import javax.inject.Inject
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@HiltViewModel
class BuilderViewModel @Inject constructor(
    private val buildRepository: BuildRepository,
    private val checkCompatibilityUseCase: CheckCompatibilityUseCase,
    private val calculatePriceUseCase: CalculateBuildPriceUseCase,
    private val calculatePsuLoadUseCase: CalculatePsuLoadUseCase
) : ViewModel() {

    val selectedComponents: StateFlow<Map<String, Component>> = buildRepository.selectedComponents
    val selectedStoreOffers: StateFlow<Map<String, StoreOffer>> = buildRepository.selectedStoreOffers

    private val _compatibilityReport = MutableStateFlow<BuildCompatibilityReport?>(null)
    val compatibilityReport: StateFlow<BuildCompatibilityReport?> = _compatibilityReport

    private val _buildName = MutableStateFlow("")
    val buildName: StateFlow<String> = _buildName

    private val _buildNote = MutableStateFlow("")
    val buildNote: StateFlow<String> = _buildNote

    private val _totalMinimalPrice = MutableStateFlow(0.0)
    val totalMinimalPrice: StateFlow<Double> = _totalMinimalPrice

    private val _totalSelectedPrice = MutableStateFlow(0.0)
    val totalSelectedPrice: StateFlow<Double> = _totalSelectedPrice

    private val _compatibilityFilter = MutableStateFlow("all")
    val compatibilityFilter: StateFlow<String> = _compatibilityFilter

    private val _saveCompleted = MutableStateFlow(false)
    val saveCompleted: StateFlow<Boolean> = _saveCompleted

    init {
        viewModelScope.launch {
            selectedComponents.collectLatest {
                updatePriceAndCompatibility()
            }
        }
    }

    fun addComponent(component: Component) {
        buildRepository.addComponent(component)
    }

    fun removeComponent(typeId: String) {
        buildRepository.removeComponent(typeId)
    }

    fun selectStoreOffer(typeId: String, offer: StoreOffer) {
        buildRepository.selectStoreOffer(typeId, offer)
        updatePrices()
    }

    private fun updatePriceAndCompatibility() {
        updatePrices()
        checkCompatibility()
    }

    private fun updatePrices() {
        val components = selectedComponents.value.values.toList()
        val (minimal, selected) = calculatePriceUseCase.execute(components, selectedStoreOffers.value)
        _totalMinimalPrice.value = minimal
        _totalSelectedPrice.value = selected
    }

    fun checkCompatibility() {
        _compatibilityReport.value = checkCompatibilityUseCase.execute(selectedComponents.value.values.toList())
    }

    fun getPsuLoadResult() = calculatePsuLoadUseCase.execute(selectedComponents.value.values.toList())

    fun setCompatibilityFilter(filter: String) {
        _compatibilityFilter.value = filter
    }

    fun getFilteredComponentTypes(): List<ComponentType> {
        val selectedByType = selectedComponents.value
        val report = _compatibilityReport.value

        return MockDataProvider.componentTypes.filter { type ->
            val selected = selectedByType[type.id]
            when (_compatibilityFilter.value) {
                "all" -> true
                else -> {
                    if (selected == null || report == null) {
                        false
                    } else {
                        val result = report.perComponentResults.find { it.componentId == selected.id }
                        when (_compatibilityFilter.value) {
                            "compatible" -> result?.status == CompatibilityStatus.COMPATIBLE
                            "problems" -> result?.status == CompatibilityStatus.INCOMPATIBLE ||
                                result?.status == CompatibilityStatus.PARTIALLY_COMPATIBLE
                            else -> true
                        }
                    }
                }
            }
        }
    }

    fun getComponentStatus(component: Component): CompatibilityStatus? {
        return _compatibilityReport.value
            ?.perComponentResults
            ?.find { it.componentId == component.id }
            ?.status
    }

    fun getCompatibilityProblems(): List<String> {
        return _compatibilityReport.value?.problems.orEmpty()
    }

    fun saveBuild() {
        viewModelScope.launch {
            val components = selectedComponents.value.values.toList()
            val build = BuildConfiguration(
                id = System.currentTimeMillis().toString(),
                name = _buildName.value.ifBlank { "Новая сборка" },
                note = _buildNote.value,
                createdAt = Date(),
                selectedComponents = components,
                totalMinimalPrice = _totalMinimalPrice.value,
                totalSelectedStoresPrice = _totalSelectedPrice.value,
                compatibilityStatus = _compatibilityReport.value?.overallStatus
                    ?: CompatibilityStatus.PARTIALLY_COMPATIBLE,
                selectedStoreOffers = selectedStoreOffers.value.values.toList()
            )
            buildRepository.insertBuild(build)
            _saveCompleted.value = true
            delay(1800)
            _saveCompleted.value = false
        }
    }

    fun setBuildName(name: String) {
        _buildName.value = name
    }

    fun setBuildNote(note: String) {
        _buildNote.value = note
    }
}

