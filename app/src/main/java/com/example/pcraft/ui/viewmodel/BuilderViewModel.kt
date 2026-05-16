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

data class BuilderInsightLine(
    val status: CompatibilityStatus,
    val title: String,
    val detail: String
)

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

    fun getBuildFitHighlights(): List<BuilderInsightLine> {
        val selected = selectedComponents.value
        val cpu = selected["cpu"]
        val gpu = selected["gpu"]
        val ram = selected["ram"]
        val storage = selected["storage"]
        val lines = mutableListOf<BuilderInsightLine>()

        val cpuCores = parseNumericValue(cpu?.specifications?.get("Ядра"))
        val ramGb = parseCapacityGb(ram?.specifications?.get("Объём"))
        val storageGb = parseCapacityGb(storage?.specifications?.get("Объём"))
        val gpuPrice = gpu?.minPrice ?: 0.0
        val hasGpu = gpu != null

        lines += when {
            hasGpu && ramGb >= 32 && gpuPrice >= 50000.0 ->
                BuilderInsightLine(
                    status = CompatibilityStatus.COMPATIBLE,
                    title = "Игры",
                    detail = "Сборка подходит для современных игр, проектов AAA-класса и высоких графических настроек."
                )

            hasGpu && ramGb >= 16 && gpuPrice >= 28000.0 ->
                BuilderInsightLine(
                    status = CompatibilityStatus.COMPATIBLE,
                    title = "Игры",
                    detail = "Сборка хорошо подходит для Full HD и большинства популярных игровых сценариев."
                )

            hasGpu && ramGb >= 16 ->
                BuilderInsightLine(
                    status = CompatibilityStatus.PARTIALLY_COMPATIBLE,
                    title = "Игры",
                    detail = "Сборка подходит для нетребовательных игр и киберспортивных проектов, но для тяжёлых новинок запас по графике ограничен."
                )

            else ->
                BuilderInsightLine(
                    status = CompatibilityStatus.PARTIALLY_COMPATIBLE,
                    title = "Игры",
                    detail = "Для полноценного игрового сценария стоит добавить видеокарту и оставить не менее 16 ГБ оперативной памяти."
                )
        }

        lines += when {
            cpuCores >= 8 && ramGb >= 32 && storageGb >= 1000 ->
                BuilderInsightLine(
                    status = CompatibilityStatus.COMPATIBLE,
                    title = "Монтаж и работа с медиа",
                    detail = "Сборка подходит для монтажа видео, работы с графикой, рендера и проектов с большим количеством файлов."
                )

            cpuCores >= 6 && ramGb >= 16 && storageGb >= 500 ->
                BuilderInsightLine(
                    status = CompatibilityStatus.PARTIALLY_COMPATIBLE,
                    title = "Монтаж и работа с медиа",
                    detail = "Сборка подойдёт для базового монтажа, фотообработки и учебных медиапроектов, но для тяжёлых задач ей нужен больший запас."
                )

            else ->
                BuilderInsightLine(
                    status = CompatibilityStatus.PARTIALLY_COMPATIBLE,
                    title = "Монтаж и работа с медиа",
                    detail = "Для серьёзного монтажа желательно больше ядер у процессора, не менее 32 ГБ памяти и быстрый накопитель от 1 ТБ."
                )
        }

        lines += when {
            cpu != null && ramGb >= 16 && storageGb >= 500 ->
                BuilderInsightLine(
                    status = CompatibilityStatus.COMPATIBLE,
                    title = "Учёба и повседневные задачи",
                    detail = "Сборка подходит для учёбы, программирования, браузера, документов, онлайн-занятий и повседневной многозадачности."
                )

            cpu != null ->
                BuilderInsightLine(
                    status = CompatibilityStatus.PARTIALLY_COMPATIBLE,
                    title = "Учёба и повседневные задачи",
                    detail = "Сборка справится с базовыми повседневными задачами, но больший объём памяти и накопителя сделает работу заметно комфортнее."
                )

            else ->
                BuilderInsightLine(
                    status = CompatibilityStatus.PARTIALLY_COMPATIBLE,
                    title = "Учёба и повседневные задачи",
                    detail = "Добавьте основные комплектующие, чтобы система оценила пригодность сборки для учебы и ежедневной работы."
                )
        }

        lines += when {
            hasGpu && gpuPrice >= 50000.0 && cpuCores >= 8 && ramGb >= 32 ->
                BuilderInsightLine(
                    status = CompatibilityStatus.COMPATIBLE,
                    title = "Стриминг и тяжёлые сценарии",
                    detail = "Сборка подходит для стриминга, записи игрового процесса, многозадачности и одновременной работы нескольких приложений."
                )

            hasGpu && cpuCores >= 6 && ramGb >= 16 ->
                BuilderInsightLine(
                    status = CompatibilityStatus.PARTIALLY_COMPATIBLE,
                    title = "Стриминг и тяжёлые сценарии",
                    detail = "Сборка может использоваться для стриминга и сложных рабочих сценариев, но для комфортного запаса лучше больше памяти и более мощная графика."
                )

            else ->
                BuilderInsightLine(
                    status = CompatibilityStatus.PARTIALLY_COMPATIBLE,
                    title = "Стриминг и тяжёлые сценарии",
                    detail = "Для стриминга и интенсивной многозадачности обычно нужен более мощный процессор, видеокарта и увеличенный объём памяти."
                )
        }

        return lines
    }

    fun getPlacementHighlights(): List<BuilderInsightLine> {
        val selected = selectedComponents.value
        val motherboard = selected["motherboard"]
        val case = selected["case"]
        val psuLoad = calculatePsuLoadUseCase.execute(selected.values.toList())
        val lines = mutableListOf<BuilderInsightLine>()

        lines += when {
            motherboard != null && case != null &&
                case.caseSupportedFormFactors.contains(motherboard.motherboardFormFactor) ->
                BuilderInsightLine(
                    status = CompatibilityStatus.COMPATIBLE,
                    title = "Корпус подходит по форм-фактору",
                    detail = "Корпус поддерживает материнскую плату формата ${motherboard.motherboardFormFactor}."
                )

            motherboard != null && case != null ->
                BuilderInsightLine(
                    status = CompatibilityStatus.INCOMPATIBLE,
                    title = "Корпус не подходит",
                    detail = "Материнская плата формата ${motherboard.motherboardFormFactor} не входит в список поддерживаемых для выбранного корпуса."
                )

            else ->
                BuilderInsightLine(
                    status = CompatibilityStatus.PARTIALLY_COMPATIBLE,
                    title = "Проверка размещения неполная",
                    detail = "Для проверки корпуса и форм-фактора добавьте материнскую плату и корпус."
                )
        }

        lines += when {
            psuLoad.psuPower != null && (psuLoad.loadPercent ?: 0.0) <= 85.0 ->
                BuilderInsightLine(
                    status = CompatibilityStatus.COMPATIBLE,
                    title = "Питание подобрано корректно",
                    detail = "Блок питания закрывает текущую нагрузку и оставляет запас по мощности."
                )

            psuLoad.psuPower != null && (psuLoad.loadPercent ?: 0.0) <= 100.0 ->
                BuilderInsightLine(
                    status = CompatibilityStatus.PARTIALLY_COMPATIBLE,
                    title = "Питание работает на высокой нагрузке",
                    detail = "Сборка запускается, но лучше выбрать блок питания с большим запасом."
                )

            psuLoad.psuPower != null ->
                BuilderInsightLine(
                    status = CompatibilityStatus.INCOMPATIBLE,
                    title = "Блока питания недостаточно",
                    detail = "Потребление сборки превышает возможности выбранного блока питания."
                )

            else ->
                BuilderInsightLine(
                    status = CompatibilityStatus.PARTIALLY_COMPATIBLE,
                    title = "Проверка питания неполная",
                    detail = "Добавьте блок питания, чтобы завершить проверку установки и питания."
                )
        }

        return lines
    }

    fun applyPreset(build: BuildConfiguration) {
        buildRepository.loadBuild(build)
        _buildName.value = build.name
        _buildNote.value = build.note
        _saveCompleted.value = false
        updatePriceAndCompatibility()
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

    private fun parseNumericValue(value: String?): Int {
        return value
            ?.filter(Char::isDigit)
            ?.toIntOrNull()
            ?: 0
    }

    private fun parseCapacityGb(value: String?): Int {
        if (value.isNullOrBlank()) return 0
        val numeric = value.filter(Char::isDigit).toIntOrNull() ?: return 0
        return when {
            value.contains("ТБ", ignoreCase = true) -> numeric * 1000
            else -> numeric
        }
    }
}

