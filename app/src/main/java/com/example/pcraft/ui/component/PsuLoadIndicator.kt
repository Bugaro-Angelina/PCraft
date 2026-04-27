package com.example.pcraft.ui.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.pcraft.domain.usecase.PsuLoadResult

@Composable
fun PsuLoadIndicator(result: PsuLoadResult) {
    val accentColor = when (result.statusColor) {
        "red" -> Color(0xFFF44336)
        "yellow" -> Color(0xFFFF9800)
        "green" -> Color(0xFF4CAF50)
        else -> Color(0xFF6C7483)
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color(0xCC1C2028))
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "Нагрузка на блок питания",
                style = MaterialTheme.typography.titleLarge,
                color = Color.White
            )
            Spacer(modifier = Modifier.height(12.dp))

            if (result.psuPower != null && result.loadPercent != null) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "${result.totalConsumption}W из ${result.psuPower}W",
                        color = Color(0xFFDCE3EF),
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Text(
                        text = "${result.loadPercent.toInt()}% load",
                        color = accentColor,
                        style = MaterialTheme.typography.titleMedium
                    )
                }
                Spacer(modifier = Modifier.height(10.dp))
                LinearProgressIndicator(
                    progress = ((result.loadPercent / 100.0).coerceIn(0.0, 1.0)).toFloat(),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(8.dp)
                        .background(Color(0x332D3440)),
                    color = accentColor,
                    trackColor = Color(0x332D3440)
                )
                Spacer(modifier = Modifier.height(10.dp))
                Text(
                    text = when (result.statusColor) {
                        "red" -> "Блоку питания не хватает мощности."
                        "yellow" -> "Нагрузка высокая, лучше выбрать блок питания мощнее."
                        "green" -> "Нагрузка комфортная, запас по мощности есть."
                        else -> "Данные недоступны."
                    },
                    color = Color(0xFFB8C0D0),
                    style = MaterialTheme.typography.bodySmall
                )
            } else {
                Text(
                    text = "Сначала добавьте блок питания, чтобы увидеть нагрузку.",
                    color = Color(0xFFB8C0D0),
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }
    }
}

