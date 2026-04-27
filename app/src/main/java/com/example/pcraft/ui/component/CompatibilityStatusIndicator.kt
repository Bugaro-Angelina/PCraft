package com.example.pcraft.ui.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.pcraft.data.model.CompatibilityStatus

@Composable
fun CompatibilityStatusIndicator(
    status: CompatibilityStatus,
    message: String = defaultMessage(status)
) {
    val color = when (status) {
        CompatibilityStatus.COMPATIBLE -> Color(0xFF4CAF50)
        CompatibilityStatus.PARTIALLY_COMPATIBLE -> Color(0xFFFF9800)
        CompatibilityStatus.INCOMPATIBLE -> Color(0xFFF44336)
    }

    Row(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(10.dp)
                .clip(CircleShape)
                .background(color)
        )
        Text(
            text = message,
            style = MaterialTheme.typography.bodySmall,
            color = color
        )
    }
}

private fun defaultMessage(status: CompatibilityStatus): String = when (status) {
    CompatibilityStatus.COMPATIBLE -> "Совместимо"
    CompatibilityStatus.PARTIALLY_COMPATIBLE -> "Частично совместимо"
    CompatibilityStatus.INCOMPATIBLE -> "Комплектующие не совместимы"
}

