package com.example.pcraft.data

import com.example.pcraft.data.model.BuildConfiguration
import com.example.pcraft.data.model.Component
import com.example.pcraft.data.model.ComponentType
import com.example.pcraft.data.model.CompatibilityStatus
import com.example.pcraft.data.model.StoreOffer
import java.net.URLEncoder
import java.nio.charset.StandardCharsets
import java.util.Date

object MockDataProvider {

    val componentTypes = listOf(
        ComponentType("cpu", "Процессор"),
        ComponentType("gpu", "Видеокарта"),
        ComponentType("motherboard", "Материнская плата"),
        ComponentType("ram", "Оперативная память"),
        ComponentType("psu", "Блок питания"),
        ComponentType("storage", "Накопитель"),
        ComponentType("cooler", "Охлаждение"),
        ComponentType("case", "Корпус")
    )

    private val cpu = componentTypes.first { it.id == "cpu" }
    private val gpu = componentTypes.first { it.id == "gpu" }
    private val motherboard = componentTypes.first { it.id == "motherboard" }
    private val ram = componentTypes.first { it.id == "ram" }
    private val psu = componentTypes.first { it.id == "psu" }
    private val storage = componentTypes.first { it.id == "storage" }
    private val cooler = componentTypes.first { it.id == "cooler" }
    private val case = componentTypes.first { it.id == "case" }

    val components: List<Component> = listOf(
        cpuComponent("cpu_12400f", "Intel Core i5-12400F OEM", "Intel", "LGA1700", "6", "12", "2.5-4.4 ГГц", 12890.0, 65, localImage("cpu_12400f")),
        cpuComponent("cpu_13400f", "Intel Core i5-13400F OEM", "Intel", "LGA1700", "10", "16", "2.5-4.6 ГГц", 17990.0, 65, localImage("cpu_13400f")),
        cpuComponent("cpu_12600kf", "Intel Core i5-12600KF OEM", "Intel", "LGA1700", "10", "16", "3.7-4.9 ГГц", 17890.0, 125, localImage("cpu_12600kf")),
        cpuComponent("cpu_12700f", "Intel Core i7-12700F OEM", "Intel", "LGA1700", "12", "20", "2.1-4.9 ГГц", 21490.0, 65, localImage("cpu_12700f")),
        cpuComponent("cpu_13700f", "Intel Core i7-13700F OEM", "Intel", "LGA1700", "16", "24", "2.1-5.2 ГГц", 30990.0, 65, localImage("cpu_13700f")),
        cpuComponent("cpu_5600", "AMD Ryzen 5 5600 OEM", "AMD", "AM4", "6", "12", "3.5-4.4 ГГц", 10290.0, 65, localImage("cpu_5600")),
        cpuComponent("cpu_5700x", "AMD Ryzen 7 5700X OEM", "AMD", "AM4", "8", "16", "3.4-4.6 ГГц", 15990.0, 65, localImage("cpu_5700x")),
        cpuComponent("cpu_7500f", "AMD Ryzen 5 7500F OEM", "AMD", "AM5", "6", "12", "3.7-5.0 ГГц", 14690.0, 65, localImage("cpu_7500f")),
        cpuComponent("cpu_7700", "AMD Ryzen 7 7700 OEM", "AMD", "AM5", "8", "16", "3.8-5.3 ГГц", 24990.0, 65, localImage("cpu_7700")),
        cpuComponent("cpu_7800x3d", "AMD Ryzen 7 7800X3D OEM", "AMD", "AM5", "8", "16", "4.2-5.0 ГГц", 38990.0, 120, localImage("cpu_7800x3d")),

        gpuComponent("gpu_4060", "ASUS Dual GeForce RTX 4060 OC Edition 8GB GDDR6", "ASUS", "8 ГБ GDDR6", "PCIe 4.0", "227 мм", 115, 35990.0, localImage("asus_prod1"), true),
        gpuComponent("gpu_4060_ti", "Palit GeForce RTX 4060 Ti Dual 8GB", "Palit", "8 ГБ GDDR6", "PCIe 4.0", "250 мм", 164, 46990.0, localImage("gpu_4060_ti")),
        gpuComponent("gpu_4070", "ASUS Dual GeForce RTX 4070 12GB", "ASUS", "12 ГБ GDDR6X", "PCIe 4.0", "267 мм", 200, 69990.0, localImage("gpu_4070")),
        gpuComponent("gpu_4070s", "Palit GeForce RTX 4070 SUPER Dual", "Palit", "12 ГБ GDDR6X", "PCIe 4.0", "269 мм", 220, 75990.0, localImage("gpu_4070s")),
        gpuComponent("gpu_4070_wf", "Gigabyte GeForce RTX 4070 SUPER WINDFORCE OC", "Gigabyte", "12 ГБ GDDR6X", "PCIe 4.0", "261 мм", 220, 78990.0, localImage("gpu_4070_wf")),
        gpuComponent("gpu_3060", "Palit GeForce RTX 3060 Dual 12GB", "Palit", "12 ГБ GDDR6", "PCIe 4.0", "245 мм", 170, 27990.0, localImage("gpu_3060")),
        gpuComponent("gpu_3050", "Gigabyte GeForce RTX 3050 WINDFORCE OC 8GB", "Gigabyte", "8 ГБ GDDR6", "PCIe 4.0", "191 мм", 130, 22990.0, localImage("gpu_3050_ldlc_2")),
        gpuComponent("gpu_rx7600", "Sapphire PULSE Radeon RX 7600 8GB", "Sapphire", "8 ГБ GDDR6", "PCIe 4.0", "240 мм", 165, 30990.0, localImage("gpu_rx7600")),
        gpuComponent("gpu_rx7700xt", "Sapphire PULSE Radeon RX 7700 XT 12GB", "Sapphire", "12 ГБ GDDR6", "PCIe 4.0", "280 мм", 245, 51990.0, localImage("gpu_rx7700xt")),
        gpuComponent("gpu_rx7800xt", "PowerColor Fighter Radeon RX 7800 XT 16GB", "PowerColor", "16 ГБ GDDR6", "PCIe 4.0", "300 мм", 263, 62990.0, localImage("gpu_rx7800xt_candidate1")),

        motherboardComponent("mb_b760m", "MSI PRO B760M-A WIFI DDR4", "MSI", "LGA1700", "Intel B760", "Micro-ATX", "DDR4", 15990.0, localImage("mb_b760m")),
        motherboardComponent("mb_b760_prime", "ASUS PRIME B760M-A WIFI D4", "ASUS", "LGA1700", "Intel B760", "Micro-ATX", "DDR4", 17290.0, localImage("mb_b760_prime")),
        motherboardComponent("mb_b760_gx", "Gigabyte B760 GAMING X DDR4", "Gigabyte", "LGA1700", "Intel B760", "ATX", "DDR4", 16490.0, localImage("mb_b760_gx")),
        motherboardComponent("mb_h610m", "ASUS PRIME H610M-K D4", "ASUS", "LGA1700", "Intel H610", "Micro-ATX", "DDR4", 8790.0, localImage("mb_h610m")),
        motherboardComponent("mb_b650m", "ASRock B650M Pro RS", "ASRock", "AM5", "AMD B650", "Micro-ATX", "DDR5", 14490.0, localImage("mb_b650m")),
        motherboardComponent("mb_b650k", "Gigabyte B650M K", "Gigabyte", "AM5", "AMD B650", "Micro-ATX", "DDR5", 13290.0, localImage("mb_b650k")),
        motherboardComponent("mb_b650s", "MSI PRO B650-S WIFI", "MSI", "AM5", "AMD B650", "ATX", "DDR5", 17690.0, localImage("mb_b650s")),
        motherboardComponent("mb_b550m_ds3h", "Gigabyte B550M DS3H", "Gigabyte", "AM4", "AMD B550", "Micro-ATX", "DDR4", 9990.0, localImage("mb_b550m_ds3h")),
        motherboardComponent("mb_b550m_vdh", "MSI B550M PRO-VDH WIFI", "MSI", "AM4", "AMD B550", "Micro-ATX", "DDR4", 11990.0, localImage("mb_b550m_vdh")),
        motherboardComponent("mb_tuf_b550", "ASUS TUF GAMING B550-PLUS", "ASUS", "AM4", "AMD B550", "ATX", "DDR4", 13990.0, localImage("mb_tuf_b550")),

        ramComponent("ram_ddr4", "Kingston FURY Beast Black 16 ГБ (2x8 ГБ) DDR4-3200", "Kingston", "DDR4", "16 ГБ", "2x8 ГБ", "3200 МГц", 3890.0, 6, localImage("ram_ddr4"), true),
        ramComponent("ram_ddr4_corsair16", "Corsair Vengeance LPX 16 ГБ (2x8 ГБ) DDR4-3200", "Corsair", "DDR4", "16 ГБ", "2x8 ГБ", "3200 МГц", 4190.0, 6, localImage("ram_ddr4_corsair16")),
        ramComponent("ram_ddr4_gskill32", "G.Skill Ripjaws V 32 ГБ (2x16 ГБ) DDR4-3600", "G.Skill", "DDR4", "32 ГБ", "2x16 ГБ", "3600 МГц", 7790.0, 8, localImage("ram_ddr4_gskill32")),
        ramComponent("ram_ddr4_patriot16", "Patriot Viper Steel 16 ГБ (2x8 ГБ) DDR4-3600", "Patriot", "DDR4", "16 ГБ", "2x8 ГБ", "3600 МГц", 4390.0, 6, localImage("ram_ddr4_patriot16")),
        ramComponent("ram_ddr4_adata32", "ADATA XPG GAMMIX D20 32 ГБ (2x16 ГБ) DDR4-3200", "ADATA XPG", "DDR4", "32 ГБ", "2x16 ГБ", "3200 МГц", 6990.0, 8, localImage("ram_ddr4_adata32")),
        ramComponent("ram_ddr5", "ADATA XPG Lancer Blade RGB 32 ГБ (2x16 ГБ) DDR5-6000", "ADATA XPG", "DDR5", "32 ГБ", "2x16 ГБ", "6000 МГц", 11290.0, 8, localImage("ram_ddr5")),
        ramComponent("ram_ddr5_kingston32", "Kingston FURY Beast 32 ГБ (2x16 ГБ) DDR5-5600", "Kingston", "DDR5", "32 ГБ", "2x16 ГБ", "5600 МГц", 9990.0, 8, localImage("ram_ddr5_kingston32")),
        ramComponent("ram_ddr5_team32", "TeamGroup T-Force Delta RGB 32 ГБ (2x16 ГБ) DDR5-6000", "TeamGroup", "DDR5", "32 ГБ", "2x16 ГБ", "6000 МГц", 11990.0, 8, localImage("ram_ddr5_team32")),
        ramComponent("ram_ddr5_crucial32", "Crucial Pro 32 ГБ (2x16 ГБ) DDR5-5600", "Crucial", "DDR5", "32 ГБ", "2x16 ГБ", "5600 МГц", 10490.0, 8, localImage("ram_ddr5_crucial32_alt")),
        ramComponent("ram_ddr5_flare32", "G.Skill Flare X5 32 ГБ (2x16 ГБ) DDR5-6000", "G.Skill", "DDR5", "32 ГБ", "2x16 ГБ", "6000 МГц", 11690.0, 8, localImage("ram_ddr5_flare32")),

        psuComponent("psu_550", "Corsair CV550", "Corsair", "550 Вт", "80+ Bronze", 550, 4890.0, localImage("psu_550_candidate1")),
        psuComponent("psu_750", "DeepCool PK750D", "DeepCool", "750 Вт", "80+ Bronze", 750, 6990.0, localImage("psu_750")),
        psuComponent("psu_cm650", "Cooler Master MWE 650 Bronze V2", "Cooler Master", "650 Вт", "80+ Bronze", 650, 6290.0, localImage("psu_cm650")),
        psuComponent("psu_pk650d", "DeepCool PK650D", "DeepCool", "650 Вт", "80+ Bronze", 650, 5890.0, localImage("psu_pk650d")),
        psuComponent("psu_sp10_650", "be quiet! System Power 10 650W", "be quiet!", "650 Вт", "80+ Bronze", 650, 6790.0, localImage("psu_sp10_650")),
        psuComponent("psu_rm750e", "Corsair RM750e", "Corsair", "750 Вт", "80+ Gold", 750, 10990.0, localImage("psu_rm750e")),
        psuComponent("psu_a650bn", "MSI MAG A650BN", "MSI", "650 Вт", "80+ Bronze", 650, 5490.0, localImage("psu_a650bn")),
        psuComponent("psu_bdf750c", "Chieftec Proton BDF-750C", "Chieftec", "750 Вт", "80+ Bronze", 750, 7390.0, localImage("psu_bdf750c")),
        psuComponent("psu_century850", "Montech Century G5 850W", "Montech", "850 Вт", "80+ Gold", 850, 9990.0, localImage("psu_century850_conrad_00")),
        psuComponent("psu_pp12m850", "be quiet! Pure Power 12 M 850W", "be quiet!", "850 Вт", "80+ Gold", 850, 14990.0, localImage("psu_pp12m850")),

        storageComponent("ssd_1tb", "Samsung 970 EVO Plus 1 ТБ", "Samsung", "1 ТБ", "M.2 2280", "PCIe 3.0 x4", 9890.0, 5, localImage("ssd_1tb")),
        storageComponent("ssd_2tb", "WD Black SN770 2 ТБ", "WD", "2 ТБ", "M.2 2280", "PCIe 4.0 x4", 14990.0, 6, localImage("ssd_2tb")),
        storageComponent("storage_990evo", "Samsung 990 EVO 1 ТБ", "Samsung", "1 ТБ", "M.2 2280", "PCIe 4.0 x4 / 5.0 x2", 10990.0, 5, localImage("ssd_990evo")),
        storageComponent("storage_nv2", "Kingston NV2 1 ТБ", "Kingston", "1 ТБ", "M.2 2280", "PCIe 4.0 x4", 5290.0, 5, localImage("storage_nv2")),
        storageComponent("storage_p3plus", "Crucial P3 Plus 1 ТБ", "Crucial", "1 ТБ", "M.2 2280", "PCIe 4.0 x4", 5890.0, 5, localImage("storage_p3plus")),
        storageComponent("storage_s980", "Samsung 980 1 ТБ", "Samsung", "1 ТБ", "M.2 2280", "PCIe 3.0 x4", 6990.0, 5, localImage("storage_s980")),
        storageComponent("storage_nm790", "Lexar NM790 2 ТБ", "Lexar", "2 ТБ", "M.2 2280", "PCIe 4.0 x4", 13990.0, 6, localImage("storage_nm790")),
        storageComponent("storage_kc3000", "Kingston KC3000 2 ТБ", "Kingston", "2 ТБ", "M.2 2280", "PCIe 4.0 x4", 16490.0, 6, localImage("storage_kc3000")),
        storageComponent("storage_legend960", "ADATA LEGEND 960 MAX 1 ТБ", "ADATA", "1 ТБ", "M.2 2280", "PCIe 4.0 x4", 8990.0, 5, localImage("storage_legend960")),
        storageComponent("storage_sn580", "WD Blue SN580 1 ТБ", "WD", "1 ТБ", "M.2 2280", "PCIe 4.0 x4", 6390.0, 5, localImage("storage_sn580")),

        coolerComponent("cooler_ag400", "DeepCool AG400 BK ARGB", "DeepCool", "Башенный", "150 мм", "220 Вт", 2590.0, 4, localImage("cooler_ag400")),
        coolerComponent("cooler_ak400d", "DeepCool AK400 Digital", "DeepCool", "Башенный", "156 мм", "220 Вт", 4190.0, 5, localImage("cooler_ak400d")),
        coolerComponent("cooler_ak620", "DeepCool AK620 Digital", "DeepCool", "Двухбашенный", "162 мм", "260 Вт", 7590.0, 6, localImage("cooler_ak620")),
        coolerComponent("cooler_peerless", "Thermalright Peerless Assassin 120 SE", "Thermalright", "Двухбашенный", "155 мм", "245 Вт", 3590.0, 5, localImage("cooler_peerless")),
        coolerComponent("cooler_se224", "ID-COOLING SE-224-XTS ARGB", "ID-COOLING", "Башенный", "151 мм", "220 Вт", 2490.0, 4, localImage("cooler_se224_1")),
        coolerComponent("cooler_paladin", "PCCooler Paladin 400", "PCCooler", "Башенный", "157 мм", "200 Вт", 2190.0, 4, localImage("cooler_paladin")),
        coolerComponent("cooler_ls520", "DeepCool LS520 SE", "DeepCool", "СЖО 240 мм", "240 мм", "260 Вт", 7990.0, 6, localImage("cooler_ls520")),
        coolerComponent("cooler_purerock2", "be quiet! Pure Rock 2", "be quiet!", "Башенный", "155 мм", "150 Вт", 4290.0, 4, localImage("cooler_purerock2")),
        coolerComponent("cooler_gammaxx400", "DeepCool GAMMAXX 400 V2", "DeepCool", "Башенный", "155 мм", "180 Вт", 1890.0, 4, localImage("cooler_gammaxx400")),
        coolerComponent("cooler_assassinx", "Thermalright Assassin X 120 R SE", "Thermalright", "Башенный", "148 мм", "200 Вт", 2390.0, 4, localImage("cooler_assassinx")),

        caseComponent("case_pop_air", "Fractal Design Pop Air RGB Black TG Clear Tint", "Fractal Design", "ATX, Micro-ATX, Mini-ITX", "Высокая продуваемость", 11690.0, localImage("case_pop_air")),
        caseComponent("case_compact", "Zalman S2 TG Black", "Zalman", "ATX, Micro-ATX, Mini-ITX", "Закалённое стекло", 4390.0, localImage("case_compact")),
        caseComponent("case_ch370", "DeepCool CH370", "DeepCool", "ATX, Micro-ATX, Mini-ITX", "Высокая продуваемость", 5990.0, localImage("case_ch370")),
        caseComponent("case_air100", "Montech Air 100 ARGB", "Montech", "Micro-ATX, Mini-ITX", "Сетка спереди и ARGB", 6990.0, localImage("case_air100")),
        caseComponent("case_mx330", "Cougar MX330-G Air", "Cougar", "ATX, Micro-ATX, Mini-ITX", "Стекло и сетка", 5490.0, localImage("case_mx330_candidate4")),
        caseComponent("case_forge100r", "MSI MAG FORGE 100R", "MSI", "ATX, Micro-ATX, Mini-ITX", "ARGB и стекло", 7590.0, localImage("case_forge100r")),
        caseComponent("case_lancool205", "Lian Li Lancool 205 Mesh", "Lian Li", "ATX, Micro-ATX, Mini-ITX", "Сетчатая панель", 8890.0, localImage("case_lancool205")),
        caseComponent("case_cc560", "DeepCool CC560 V2", "DeepCool", "ATX, Micro-ATX, Mini-ITX", "4 вентилятора в комплекте", 4890.0, localImage("case_cc560")),
        caseComponent("case_trilobite", "1STPLAYER Trilobite T3", "1STPLAYER", "ATX, Micro-ATX, Mini-ITX", "Стекло и подсветка", 4690.0, localImage("case_trilobite_candidate1")),
        caseComponent("case_s200", "Thermaltake S200 TG ARGB", "Thermaltake", "ATX, Micro-ATX, Mini-ITX", "ARGB и стекло", 7990.0, localImage("case_s200"))
    )

    val storeOffers: List<StoreOffer> = components.flatMap { component ->
        autoOffers(component.id, component.name, component.minPrice)
    }

    val savedBuilds = listOf(
        build(
            id = "build_gaming",
            name = "Игровой ПК",
            note = "Сбалансированная сборка для Full HD и 1440p.",
            componentIds = listOf("cpu_12400f", "gpu_4060", "mb_b760m", "ram_ddr4", "psu_750", "ssd_1tb", "cooler_ag400", "case_pop_air"),
            selectedOfferIds = listOf("cpu_12400f_regard", "gpu_4060_dns", "mb_b760m_regard", "ram_ddr4_regard", "psu_750_regard", "ssd_1tb_dns", "cooler_ag400_regard", "case_pop_air_regard"),
            daysAgo = 2,
            compatibilityStatus = CompatibilityStatus.COMPATIBLE
        ),
        build(
            id = "build_budget",
            name = "Бюджетный вариант",
            note = "Домашняя сборка с запасом на апгрейд.",
            componentIds = listOf("cpu_5600", "mb_b550m_ds3h", "ram_ddr4_corsair16", "psu_550", "storage_nv2", "cooler_gammaxx400", "case_cc560"),
            selectedOfferIds = listOf("cpu_5600_regard", "mb_b550m_ds3h_regard", "ram_ddr4_corsair16_regard", "psu_550_regard", "storage_nv2_regard", "cooler_gammaxx400_regard", "case_cc560_regard"),
            daysAgo = 5,
            compatibilityStatus = CompatibilityStatus.COMPATIBLE
        ),
        build(
            id = "build_problem",
            name = "Для монтажа",
            note = "Черновая сборка с намеренной проверкой совместимости.",
            componentIds = listOf("cpu_7800x3d", "gpu_rx7800xt", "mb_b650m", "ram_ddr5", "psu_550", "ssd_2tb", "cooler_ak620", "case_pop_air"),
            selectedOfferIds = listOf("cpu_7800x3d_regard", "gpu_rx7800xt_regard", "mb_b650m_regard", "ram_ddr5_regard", "psu_550_dns", "ssd_2tb_regard", "cooler_ak620_regard", "case_pop_air_city"),
            daysAgo = 1,
            compatibilityStatus = CompatibilityStatus.INCOMPATIBLE
        )
    )

    fun getStoreOffersForComponent(componentId: String): List<StoreOffer> {
        return storeOffers.filter { it.componentId == componentId }
    }

    private fun autoOffers(componentId: String, query: String, basePrice: Double): List<StoreOffer> {
        return listOf(
            offer("${componentId}_dns", componentId, "DNS", basePrice + 300.0, dnsSearch(query), true, "Сегодня"),
            offer("${componentId}_city", componentId, "Ситилинк", basePrice + 550.0, citilinkSearch(query), true, "1-2 дня"),
            offer("${componentId}_regard", componentId, "Регард", basePrice, regardSearch(query), true, "Завтра")
        )
    }

    private fun offer(
        id: String,
        componentId: String,
        storeName: String,
        price: Double,
        productUrl: String,
        inStock: Boolean,
        deliveryInfo: String
    ) = StoreOffer(
        id = id,
        componentId = componentId,
        storeName = storeName,
        price = price,
        productUrl = productUrl,
        inStock = inStock,
        deliveryInfo = deliveryInfo
    )

    private fun build(
        id: String,
        name: String,
        note: String,
        componentIds: List<String>,
        selectedOfferIds: List<String>,
        daysAgo: Long,
        compatibilityStatus: CompatibilityStatus
    ): BuildConfiguration {
        val selectedComponents = components.filter { it.id in componentIds }
        val selectedOffers = storeOffers.filter { it.id in selectedOfferIds }
        return BuildConfiguration(
            id = id,
            name = name,
            note = note,
            createdAt = Date(System.currentTimeMillis() - daysAgo * 24L * 60L * 60L * 1000L),
            selectedComponents = selectedComponents,
            totalMinimalPrice = selectedComponents.sumOf { it.minPrice },
            totalSelectedStoresPrice = selectedComponents.sumOf { component ->
                selectedOffers.find { it.componentId == component.id }?.price ?: component.minPrice
            },
            compatibilityStatus = compatibilityStatus,
            selectedStoreOffers = selectedOffers
        )
    }

    private fun cpuComponent(
        id: String,
        name: String,
        brand: String,
        socket: String,
        cores: String,
        threads: String,
        frequency: String,
        price: Double,
        power: Int,
        imageUrl: String = "",
        favorite: Boolean = false
    ) = Component(
        id = id,
        name = name,
        type = cpu,
        brand = brand,
        description = "Процессор для игровых и рабочих сборок.",
        imageUrl = imageUrl,
        specifications = mapOf(
            "Ядра" to cores,
            "Потоки" to threads,
            "Сокет" to socket,
            "Частота" to frequency
        ),
        powerConsumptionWatts = power,
        requiredSocket = socket,
        minPrice = price,
        isFavorite = favorite
    )

    private fun gpuComponent(
        id: String,
        name: String,
        brand: String,
        memory: String,
        bus: String,
        length: String,
        power: Int,
        price: Double,
        imageUrl: String = "",
        favorite: Boolean = false
    ) = Component(
        id = id,
        name = name,
        type = gpu,
        brand = brand,
        description = "Видеокарта для игр и графических задач.",
        imageUrl = imageUrl,
        specifications = mapOf(
            "Память" to memory,
            "Интерфейс" to bus,
            "Длина" to length
        ),
        powerConsumptionWatts = power,
        minPrice = price,
        isFavorite = favorite
    )

    private fun motherboardComponent(
        id: String,
        name: String,
        brand: String,
        socket: String,
        chipset: String,
        formFactor: String,
        memoryType: String,
        price: Double,
        imageUrl: String = ""
    ) = Component(
        id = id,
        name = name,
        type = motherboard,
        brand = brand,
        description = "Материнская плата для современной сборки ПК.",
        imageUrl = imageUrl,
        specifications = mapOf(
            "Сокет" to socket,
            "Чипсет" to chipset,
            "Форм-фактор" to formFactor,
            "Память" to memoryType
        ),
        supportedSocket = socket,
        motherboardSupportedRamType = memoryType,
        motherboardFormFactor = formFactor,
        minPrice = price
    )

    private fun ramComponent(
        id: String,
        name: String,
        brand: String,
        ramTypeValue: String,
        volume: String,
        kit: String,
        frequency: String,
        price: Double,
        power: Int,
        imageUrl: String = "",
        favorite: Boolean = false
    ) = Component(
        id = id,
        name = name,
        type = ram,
        brand = brand,
        description = "Оперативная память для игровых и рабочих конфигураций.",
        imageUrl = imageUrl,
        specifications = mapOf(
            "Объём" to volume,
            "Комплект" to kit,
            "Тип" to ramTypeValue,
            "Частота" to frequency
        ),
        ramType = ramTypeValue,
        powerConsumptionWatts = power,
        minPrice = price,
        isFavorite = favorite
    )

    private fun psuComponent(
        id: String,
        name: String,
        brand: String,
        wattage: String,
        certificate: String,
        psuWatts: Int,
        price: Double,
        imageUrl: String = ""
    ) = Component(
        id = id,
        name = name,
        type = psu,
        brand = brand,
        description = "Блок питания для стабильной работы системы.",
        imageUrl = imageUrl,
        specifications = mapOf(
            "Мощность" to wattage,
            "Сертификат" to certificate,
            "Модульность" to "Нет"
        ),
        psuPowerWatts = psuWatts,
        minPrice = price
    )

    private fun storageComponent(
        id: String,
        name: String,
        brand: String,
        volume: String,
        formFactor: String,
        bus: String,
        price: Double,
        power: Int,
        imageUrl: String = ""
    ) = Component(
        id = id,
        name = name,
        type = storage,
        brand = brand,
        description = "Накопитель для системы, игр и рабочих файлов.",
        imageUrl = imageUrl,
        specifications = mapOf(
            "Объём" to volume,
            "Форм-фактор" to formFactor,
            "Интерфейс" to bus
        ),
        powerConsumptionWatts = power,
        minPrice = price
    )

    private fun coolerComponent(
        id: String,
        name: String,
        brand: String,
        coolerType: String,
        height: String,
        tdp: String,
        price: Double,
        power: Int,
        imageUrl: String = ""
    ) = Component(
        id = id,
        name = name,
        type = cooler,
        brand = brand,
        description = "Охлаждение для процессора.",
        imageUrl = imageUrl,
        specifications = mapOf(
            "Тип" to coolerType,
            "Высота" to height,
            "TDP" to tdp
        ),
        powerConsumptionWatts = power,
        minPrice = price
    )

    private fun caseComponent(
        id: String,
        name: String,
        brand: String,
        supportedBoards: String,
        airflow: String,
        price: Double,
        imageUrl: String = ""
    ) = Component(
        id = id,
        name = name,
        type = case,
        brand = brand,
        description = "Корпус для домашнего, рабочего или игрового ПК.",
        imageUrl = imageUrl,
        specifications = mapOf(
            "Форм-фактор" to "Mid Tower",
            "Поддержка плат" to supportedBoards,
            "Охлаждение" to airflow
        ),
        caseSupportedFormFactors = supportedBoards.split(", ").map { it.trim() },
        minPrice = price
    )

    private fun dnsSearch(query: String): String {
        return "https://www.dns-shop.ru/search/?q=${urlEncode(query)}"
    }

    private fun citilinkSearch(query: String): String {
        return "https://www.citilink.ru/search/?text=${urlEncode(query)}"
    }

    private fun regardSearch(query: String): String {
        return "https://www.regard.ru/search?t=${urlEncode(query)}"
    }

    private fun localImage(name: String): String {
        return "android.resource://com.example.pcraft/drawable/$name"
    }

    private fun urlEncode(value: String): String {
        return URLEncoder.encode(value, StandardCharsets.UTF_8.toString())
    }
}

