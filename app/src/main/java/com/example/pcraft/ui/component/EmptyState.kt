package com.example.pcraft.ui.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp

@Composable
private fun EmptyState(title: String, message: String) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleLarge,
            color = Color.White,
            textAlign = TextAlign.Center
        )
        Text(
            text = message,
            style = MaterialTheme.typography.bodyMedium,
            color = Color(0xFFB8C0D0),
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(top = 8.dp)
        )
    }
}

@Composable
fun EmptyFavorites() {
    EmptyState(
        title = "Пока нет избранных товаров",
        message = "Добавляйте комплектующие в избранное, чтобы быстро к ним возвращаться."
    )
}

@Composable
fun EmptyComponentList() {
    EmptyState(
        title = "Ничего не найдено",
        message = "Попробуйте изменить запрос, фильтр или сортировку."
    )
}

@Composable
fun EmptyBuilds() {
    EmptyState(
        title = "История сборок пуста",
        message = "Сохраненные сборки появятся здесь."
    )
}

@Composable
fun NoStoreOffers() {
    EmptyState(
        title = "Нет предложений магазинов",
        message = "Для этого комплектующего пока не подготовлены предложения."
    )
}

