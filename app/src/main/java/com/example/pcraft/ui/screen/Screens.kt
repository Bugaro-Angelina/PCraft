package com.example.pcraft.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.pcraft.data.MockDataProvider
import com.example.pcraft.data.model.CompatibilityStatus
import com.example.pcraft.data.model.Component
import com.example.pcraft.data.model.ComponentType
import com.example.pcraft.data.model.StoreOffer
import com.example.pcraft.ui.component.ComponentCard
import com.example.pcraft.ui.component.ComponentImage
import com.example.pcraft.ui.component.CompatibilityStatusIndicator
import com.example.pcraft.ui.component.EmptyBuilds
import com.example.pcraft.ui.component.EmptyComponentList
import com.example.pcraft.ui.component.EmptyFavorites
import com.example.pcraft.ui.component.LoadingState
import com.example.pcraft.ui.component.NoStoreOffers
import com.example.pcraft.ui.component.PsuLoadIndicator
import com.example.pcraft.ui.navigation.Screen
import com.example.pcraft.ui.viewmodel.AuthViewModel
import com.example.pcraft.ui.viewmodel.BuilderViewModel
import com.example.pcraft.ui.viewmodel.CatalogSortOption
import com.example.pcraft.ui.viewmodel.DetailsViewModel
import com.example.pcraft.ui.viewmodel.FavoritesViewModel
import com.example.pcraft.ui.viewmodel.HomeViewModel
import com.example.pcraft.ui.viewmodel.ProfileViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(navController: NavController, initialTypeId: String? = null) {
    val viewModel: HomeViewModel = hiltViewModel()
    val components by viewModel.components.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()
    val selectedType by viewModel.selectedType.collectAsState()
    val sortOption by viewModel.sortOption.collectAsState()

    LaunchedEffect(initialTypeId) {
        if (initialTypeId != selectedType) {
            viewModel.setSelectedType(initialTypeId)
        }
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF121418)),
        contentPadding = PaddingValues(bottom = 96.dp)
    ) {
        item {
            Text(
                text = "Каталог комплектующих",
                style = MaterialTheme.typography.headlineMedium,
                color = Color.White,
                modifier = Modifier.padding(16.dp)
            )

            OutlinedTextField(
                value = searchQuery,
                onValueChange = viewModel::setSearchQuery,
                label = { Text("Поиск комплектующих") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                singleLine = true
            )

            Row(
                modifier = Modifier
                    .horizontalScroll(rememberScrollState())
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                SortChip("Все", selectedType == null) { viewModel.setSelectedType(null) }
                MockDataProvider.componentTypes.forEach { type ->
                    SortChip(type.name, selectedType == type.id) {
                        viewModel.setSelectedType(type.id)
                    }
                }
            }

            Row(
                modifier = Modifier
                    .horizontalScroll(rememberScrollState())
                    .padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                SortChip("По цене", sortOption == CatalogSortOption.PRICE) {
                    viewModel.setSortOption(CatalogSortOption.PRICE)
                }
                SortChip("По названию", sortOption == CatalogSortOption.NAME) {
                    viewModel.setSortOption(CatalogSortOption.NAME)
                }
                SortChip("По бренду", sortOption == CatalogSortOption.BRAND) {
                    viewModel.setSortOption(CatalogSortOption.BRAND)
                }
                SortChip("По совместимости", sortOption == CatalogSortOption.COMPATIBILITY) {
                    viewModel.setSortOption(CatalogSortOption.COMPATIBILITY)
                }
            }
        }

        if (components.isEmpty()) {
            item { EmptyComponentList() }
        } else {
            items(components) { component ->
                ComponentCard(
                    component = component,
                    onClick = { navController.navigate(Screen.Details.createRoute(component.id)) },
                    onFavoriteToggle = { viewModel.toggleFavorite(component) }
                )
            }
        }
    }
}

@Composable
fun DetailsScreen(navController: NavController, componentId: String) {
    val viewModel: DetailsViewModel = hiltViewModel()
    val component by viewModel.component.collectAsState()
    val storeOffers by viewModel.storeOffers.collectAsState()
    val uriHandler = LocalUriHandler.current

    LaunchedEffect(componentId) {
        viewModel.loadComponent(componentId)
    }

    component?.let { comp ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFF121418)),
            contentPadding = PaddingValues(bottom = 96.dp)
        ) {
            item {
                ComponentCard(
                    component = comp,
                    onClick = {},
                    onFavoriteToggle = viewModel::toggleFavorite
                )
            }

            item {
                GlassSection("Характеристики") {
                    comp.specifications.forEach { (key, value) ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 6.dp),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(key, color = Color(0xFFB8C0D0))
                            Text(value, color = Color.White, fontWeight = FontWeight.SemiBold)
                        }
                    }
                }
            }

            item {
                GlassSection("Описание") {
                    Text(
                        text = comp.description,
                        color = Color(0xFFD9DEEA),
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }

            item {
                Button(
                    onClick = {
                        viewModel.addToBuilder()
                        navController.navigate(Screen.Builder.route)
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                ) {
                    Text("Добавить в конструктор")
                }
            }

            item {
                Text(
                    text = "Магазины и цены",
                    style = MaterialTheme.typography.titleLarge,
                    color = Color.White,
                    modifier = Modifier.padding(16.dp)
                )
            }

            if (storeOffers.isEmpty()) {
                item { NoStoreOffers() }
            } else {
                items(storeOffers) { offer ->
                    StoreOfferCard(
                        offer = offer,
                        onCheckedChange = { viewModel.toggleStoreSelection(offer) },
                        onOpenLink = { uriHandler.openUri(offer.productUrl) }
                    )
                }
            }
        }
    } ?: LoadingState("Загрузка комплектующего...")
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BuilderScreen(navController: NavController) {
    val viewModel: BuilderViewModel = hiltViewModel()
    val selectedComponents by viewModel.selectedComponents.collectAsState()
    val selectedStoreOffers by viewModel.selectedStoreOffers.collectAsState()
    val compatibilityReport by viewModel.compatibilityReport.collectAsState()
    val buildName by viewModel.buildName.collectAsState()
    val buildNote by viewModel.buildNote.collectAsState()
    val totalMinimalPrice by viewModel.totalMinimalPrice.collectAsState()
    val totalSelectedPrice by viewModel.totalSelectedPrice.collectAsState()
    val compatibilityFilter by viewModel.compatibilityFilter.collectAsState()
    val saveCompleted by viewModel.saveCompleted.collectAsState()

    val visibleTypes = viewModel.getFilteredComponentTypes()

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF121418)),
        contentPadding = PaddingValues(bottom = 96.dp)
    ) {
        item {
            Text(
                text = "Конструктор ПК",
                style = MaterialTheme.typography.headlineMedium,
                color = Color.White,
                modifier = Modifier.padding(16.dp)
            )
        }

        item {
            Row(
                modifier = Modifier
                    .horizontalScroll(rememberScrollState())
                    .padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                SortChip("Все", compatibilityFilter == "all") { viewModel.setCompatibilityFilter("all") }
                SortChip("Только совместимые", compatibilityFilter == "compatible") {
                    viewModel.setCompatibilityFilter("compatible")
                }
                SortChip("Проблемные", compatibilityFilter == "problems") {
                    viewModel.setCompatibilityFilter("problems")
                }
            }
        }

        item {
            GlassSection("Стоимость сборки") {
                PriceRow("Минимальная цена сборки", formatRubles(totalMinimalPrice))
                Spacer(modifier = Modifier.height(8.dp))
                PriceRow("Цена по выбранным магазинам", formatRubles(totalSelectedPrice))
            }
        }

        if (selectedComponents.isNotEmpty()) {
            item {
                GlassSection("Нагрузка на блок питания") {
                    PsuLoadIndicator(viewModel.getPsuLoadResult())
                }
            }
        }

        if (compatibilityReport?.problems?.isNotEmpty() == true) {
            item {
                GlassSection("Сводка проблем совместимости") {
                    compatibilityReport?.problems?.forEach { problem ->
                        Text(
                            text = "- $problem",
                            color = Color(0xFFFFB2C4),
                            modifier = Modifier.padding(bottom = 6.dp)
                        )
                    }
                }
            }
        }

        items(visibleTypes) { type ->
            val selected = selectedComponents[type.id]
            val status = selected?.let(viewModel::getComponentStatus)
            val selectedOffer = selectedStoreOffers[type.id]

            BuilderSlotCard(
                title = type.name,
                component = selected,
                status = status,
                selectedOffer = selectedOffer,
                onReplace = { navController.navigate(Screen.Home.createRoute(type.id)) },
                onRemove = { viewModel.removeComponent(type.id) },
                onChoose = { navController.navigate(Screen.Home.createRoute(type.id)) },
                onStoreClick = {
                    selected?.let { navController.navigate(Screen.Details.createRoute(it.id)) }
                }
            )
        }

        item {
            Button(
                onClick = {
                    viewModel.checkCompatibility()
                    navController.navigate(Screen.CompatibilityReport.route)
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
            ) {
                Text("Проверить совместимость")
            }
        }

        item {
            OutlinedButton(
                onClick = { navController.navigate(Screen.StoresList.route) },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp)
            ) {
                Text("Ссылки и цены всей сборки")
            }
        }

        item {
            GlassSection("Название и заметки") {
                OutlinedTextField(
                    value = buildName,
                    onValueChange = viewModel::setBuildName,
                    label = { Text("Название сборки") },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(12.dp))
                OutlinedTextField(
                    value = buildNote,
                    onValueChange = viewModel::setBuildNote,
                    label = { Text("Заметка") },
                    modifier = Modifier.fillMaxWidth(),
                    minLines = 3
                )
            }
        }

        item {
            Button(
                onClick = viewModel::saveBuild,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
            ) {
                Text(if (saveCompleted) "Сборка сохранена" else "Сохранить сборку")
            }
        }
    }
}

@Composable
fun StoresListScreen(navController: NavController) {
    val viewModel: BuilderViewModel = hiltViewModel()
    val selectedComponents by viewModel.selectedComponents.collectAsState()
    val selectedOffers by viewModel.selectedStoreOffers.collectAsState()
    val uriHandler = LocalUriHandler.current

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF121418)),
        contentPadding = PaddingValues(bottom = 96.dp)
    ) {
        item {
            Text(
                text = "Где купить",
                style = MaterialTheme.typography.headlineMedium,
                color = Color.White,
                modifier = Modifier.padding(16.dp)
            )
        }

        item {
            OrderSummarySection(
                selectedComponents = selectedComponents.values.toList(),
                selectedOffers = selectedOffers,
                uriHandlerOpen = uriHandler::openUri
            )
        }
    }
}

@Composable
fun FavoritesScreen(navController: NavController) {
    val viewModel: FavoritesViewModel = hiltViewModel()
    val components by viewModel.favoriteComponents.collectAsState(initial = emptyList())

    if (components.isEmpty()) {
        EmptyFavorites()
    } else {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFF121418)),
            contentPadding = PaddingValues(bottom = 96.dp)
        ) {
            item {
                Text(
                    text = "Избранные комплектующие",
                    style = MaterialTheme.typography.headlineMedium,
                    color = Color.White,
                    modifier = Modifier.padding(16.dp)
                )
            }

            items(components) { component ->
                ComponentCard(
                    component = component,
                    onClick = { navController.navigate(Screen.Details.createRoute(component.id)) },
                    onFavoriteToggle = { viewModel.toggleFavorite(component) }
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(navController: NavController) {
    val authViewModel: AuthViewModel = hiltViewModel()
    val profileViewModel: ProfileViewModel = hiltViewModel()

    val currentUser by authViewModel.currentUser.collectAsState()
    val email by authViewModel.email.collectAsState()
    val password by authViewModel.password.collectAsState()
    val isRegisterMode by authViewModel.isRegisterMode.collectAsState()
    val isLoading by authViewModel.isLoading.collectAsState()
    val errorMessage by authViewModel.errorMessage.collectAsState()
    val builds by profileViewModel.builds.collectAsState(initial = emptyList())

    if (currentUser == null) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFF121418))
                .padding(16.dp),
            contentAlignment = Alignment.Center
        ) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Color(0xCC1C2028))
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Вход",
                        style = MaterialTheme.typography.headlineMedium,
                        color = Color.White
                    )
                    Spacer(modifier = Modifier.height(16.dp))

                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        SortChip("Вход", !isRegisterMode) { authViewModel.setRegisterMode(false) }
                        SortChip("Регистрация", isRegisterMode) { authViewModel.setRegisterMode(true) }
                    }
                    Spacer(modifier = Modifier.height(12.dp))
                    OutlinedTextField(
                        value = email,
                        onValueChange = authViewModel::updateEmail,
                        label = { Text("Email") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    OutlinedTextField(
                        value = password,
                        onValueChange = authViewModel::updatePassword,
                        label = { Text("Пароль") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )
                    if (errorMessage != null) {
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(errorMessage.orEmpty(), color = Color(0xFFFFB2C4))
                    }
                    Spacer(modifier = Modifier.height(12.dp))
                    Button(
                        onClick = authViewModel::submit,
                        enabled = !isLoading,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(if (isRegisterMode) "Создать аккаунт" else "Войти")
                    }
                }
            }
        }
        return
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF121418)),
        contentPadding = PaddingValues(bottom = 96.dp)
    ) {
        item {
            Text(
                text = "Аккаунт",
                style = MaterialTheme.typography.headlineMedium,
                color = Color.White,
                modifier = Modifier.padding(16.dp)
            )
        }

        item {
            GlassSection("Профиль") {
                Text(
                    text = currentUser?.email.orEmpty(),
                    color = Color.White,
                    style = MaterialTheme.typography.titleMedium
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Сохраненных сборок: ${builds.size}",
                    color = Color(0xFFB8C0D0)
                )
                Spacer(modifier = Modifier.height(12.dp))
                OutlinedButton(
                    onClick = authViewModel::signOut,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Выйти")
                }
            }
        }

        item {
            Text(
                text = "История сборок",
                style = MaterialTheme.typography.titleLarge,
                color = Color.White,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
            )
        }

        if (builds.isEmpty()) {
            item { EmptyBuilds() }
        } else {
            items(builds) { build ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xCC1C2028))
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = build.name,
                            color = Color.White,
                            style = MaterialTheme.typography.titleLarge
                        )
                        if (build.note.isNotBlank()) {
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(build.note, color = Color(0xFFD9DEEA))
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Минимальная цена: ${formatRubles(build.totalMinimalPrice)}",
                            color = Color(0xFFFF8DCA)
                        )
                        Text(
                            text = "По выбранным магазинам: ${formatRubles(build.totalSelectedStoresPrice)}",
                            color = Color(0xFF8FB1FF)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        CompatibilityStatusIndicator(build.compatibilityStatus)
                        Spacer(modifier = Modifier.height(12.dp))
                        OutlinedButton(
                            onClick = { profileViewModel.deleteBuild(build.id) },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("Удалить сборку")
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun OrderSummarySection(
    selectedComponents: List<Component>,
    selectedOffers: Map<String, StoreOffer>,
    uriHandlerOpen: (String) -> Unit
) {
    if (selectedComponents.isEmpty()) {
        GlassSection("Итог заказа / Где купить") {
            Text(
                text = "Сначала выберите комплектующие и магазины в конструкторе.",
                color = Color(0xFFD9DEEA)
            )
        }
        return
    }

    val totalSelected = selectedOffers.values.sumOf { it.price }
    val totalMinimal = selectedComponents.sumOf { it.minPrice }

    GlassSection("Итог заказа / Где купить") {
        PriceRow("Минимальная цена сборки", formatRubles(totalMinimal))
        Spacer(modifier = Modifier.height(8.dp))
        PriceRow("Цена по выбранным магазинам", formatRubles(totalSelected))
        Spacer(modifier = Modifier.height(12.dp))

        selectedComponents.forEach { component ->
            val offer = selectedOffers[component.type.id]
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 6.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xAA222733))
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text(component.name, color = Color.White, fontWeight = FontWeight.SemiBold)
                    Spacer(modifier = Modifier.height(6.dp))
                    if (offer != null) {
                        Text("Магазин: ${offer.storeName}", color = Color(0xFFD9DEEA))
                        Text("Цена: ${formatRubles(offer.price)}", color = Color(0xFFFF8DCA))
                        Text(
                            "Наличие: ${if (offer.inStock) "В наличии" else "Под заказ"}",
                            color = Color(0xFFD9DEEA)
                        )
                        Text("Доставка: ${offer.deliveryInfo}", color = Color(0xFFD9DEEA))
                        Text(
                            text = offer.productUrl,
                            color = Color(0xFF8FB1FF),
                            modifier = Modifier.clickable { uriHandlerOpen(offer.productUrl) }
                        )
                    } else {
                        Text(
                            "Для этого комплектующего магазин пока не выбран.",
                            color = Color(0xFFFFB2C4)
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun BuilderSlotCard(
    title: String,
    component: Component?,
    status: CompatibilityStatus?,
    selectedOffer: StoreOffer?,
    onReplace: () -> Unit,
    onRemove: () -> Unit,
    onChoose: () -> Unit,
    onStoreClick: () -> Unit
) {
    val cardColor = when (status) {
        CompatibilityStatus.INCOMPATIBLE -> Color(0x33F44336)
        CompatibilityStatus.PARTIALLY_COMPATIBLE -> Color(0x33FF9800)
        CompatibilityStatus.COMPATIBLE -> Color(0x334CAF50)
        null -> Color(0xCC1C2028)
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        colors = CardDefaults.cardColors(containerColor = cardColor)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(title, color = Color.White, style = MaterialTheme.typography.titleLarge)
                    Spacer(modifier = Modifier.height(10.dp))

                    if (component != null) {
                        Text(component.name, color = Color.White, fontWeight = FontWeight.SemiBold)
                        Text(formatRubles(component.minPrice), color = Color(0xFFFF8DCA))
                    }
                }

                if (component != null) {
                    ComponentThumbnail(
                        component = component,
                        modifier = Modifier.size(56.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            if (component != null) {
                status?.let {
                    CompatibilityStatusIndicator(
                        status = it,
                        message = when (it) {
                            CompatibilityStatus.COMPATIBLE -> "Совместимо"
                            CompatibilityStatus.PARTIALLY_COMPATIBLE -> "Частично совместимо"
                            CompatibilityStatus.INCOMPATIBLE -> "Комплектующие не совместимы"
                        }
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                }

                if (selectedOffer != null) {
                    Text(
                        text = "Выбран магазин: ${selectedOffer.storeName}",
                        color = Color(0xFFD9DEEA)
                    )
                    Text(
                        text = "Цена: ${formatRubles(selectedOffer.price)}",
                        color = Color(0xFF8FB1FF)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedButton(onClick = onStoreClick) {
                        Text("Изменить магазин")
                    }
                } else {
                    Text(
                        text = "Магазин пока не выбран",
                        color = Color(0xFFFFB2C4),
                        modifier = Modifier.clickable(onClick = onStoreClick)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedButton(onClick = onStoreClick) {
                        Text("Выбрать магазин")
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedButton(onClick = onReplace) {
                        Text("Заменить")
                    }
                    OutlinedButton(onClick = onRemove) {
                        Text("Удалить")
                    }
                }
            } else {
                OutlinedButton(onClick = onChoose) {
                    Text("Выбрать комплектующее")
                }
            }
        }
    }
}

@Composable
private fun StoreOfferCard(
    offer: StoreOffer,
    onCheckedChange: () -> Unit,
    onOpenLink: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xCC1C2028))
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(offer.storeName, color = Color.White, fontWeight = FontWeight.SemiBold)
                    Text(formatRubles(offer.price), color = Color(0xFFFF8DCA), fontWeight = FontWeight.Bold)
                    Text(
                        text = if (offer.inStock) "В наличии" else "Под заказ",
                        color = if (offer.inStock) Color(0xFF8FB1FF) else Color(0xFFFF9BB2)
                    )
                    Text(offer.deliveryInfo, color = Color(0xFFB7C0CC))
                }
                Checkbox(
                    checked = offer.isSelectedByUser,
                    onCheckedChange = { onCheckedChange() }
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = offer.productUrl,
                color = Color(0xFF8FB1FF),
                modifier = Modifier.clickable { onOpenLink() }
            )
        }
    }
}

@Composable
private fun ComponentThumbnail(component: Component, modifier: Modifier = Modifier) {
    val label = component.type.name.take(2).uppercase()

    Box(
        modifier = modifier
            .clip(MaterialTheme.shapes.medium)
            .background(
                Brush.linearGradient(
                    colors = listOf(Color(0xFF2A2F39), Color(0xFF171B22))
                )
            ),
        contentAlignment = Alignment.Center
    ) {
        ComponentImage(
            imageUrl = component.imageUrl,
            contentDescription = component.name,
            fallbackLabel = label,
            modifier = Modifier.fillMaxSize()
        )
    }
}

@Composable
private fun PriceRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(label, color = Color(0xFFB8C0D0))
        Text(value, color = Color.White, fontWeight = FontWeight.Bold)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SortChip(label: String, selected: Boolean, onClick: () -> Unit) {
    FilterChip(
        selected = selected,
        onClick = onClick,
        label = { Text(label) }
    )
}

@Composable
private fun GlassSection(
    title: String,
    content: @Composable ColumnScope.() -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xCC1C2028))
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleLarge,
                color = Color.White
            )
            Spacer(modifier = Modifier.height(12.dp))
            content()
        }
    }
}

private fun formatRubles(value: Double): String = "${value.toInt()} руб."

