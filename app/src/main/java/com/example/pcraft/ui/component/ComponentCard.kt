package com.example.pcraft.ui.component

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.pcraft.data.model.CompatibilityStatus
import com.example.pcraft.data.model.Component

@Composable
fun ComponentCard(
    component: Component,
    onClick: () -> Unit,
    onFavoriteToggle: () -> Unit,
    compatibilityStatus: CompatibilityStatus? = null
) {
    val borderColor = when (compatibilityStatus) {
        CompatibilityStatus.INCOMPATIBLE -> Color(0x66F44336)
        CompatibilityStatus.PARTIALLY_COMPATIBLE -> Color(0x66FF9800)
        CompatibilityStatus.COMPATIBLE -> Color(0x664CAF50)
        null -> Color(0x33FFFFFF)
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .clickable(onClick = onClick)
            .border(1.dp, borderColor, MaterialTheme.shapes.medium),
        colors = CardDefaults.cardColors(containerColor = Color(0xCC20242C)),
        elevation = CardDefaults.cardElevation(defaultElevation = 10.dp)
    ) {
        Box {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(160.dp)
                    .background(
                        Brush.linearGradient(
                            colors = listOf(
                                Color(0xFF353B46),
                                Color(0xFF232831),
                                Color(0xFF1B2028)
                            )
                        )
                    ),
                contentAlignment = Alignment.Center
            ) {
                ComponentImage(
                    imageUrl = component.imageUrl,
                    contentDescription = component.name,
                    fallbackLabel = component.type.name.take(2).uppercase(),
                    modifier = Modifier.matchParentSize()
                )
            }

            Box(
                modifier = Modifier
                    .matchParentSize()
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(Color.Transparent, Color(0xD920242C))
                        )
                    )
            )

            IconButton(
                onClick = onFavoriteToggle,
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(10.dp)
                    .clip(MaterialTheme.shapes.small)
                    .background(Color(0x80333A46))
            ) {
                Icon(
                    imageVector = if (component.isFavorite) Icons.Filled.Favorite else Icons.Outlined.FavoriteBorder,
                    contentDescription = "Избранное",
                    tint = if (component.isFavorite) Color(0xFFFF6B81) else Color.White
                )
            }

            compatibilityStatus?.let { status ->
                Box(
                    modifier = Modifier
                        .align(Alignment.BottomStart)
                        .padding(12.dp)
                        .clip(MaterialTheme.shapes.small)
                        .background(
                            when (status) {
                                CompatibilityStatus.COMPATIBLE -> Color(0x664CAF50)
                                CompatibilityStatus.PARTIALLY_COMPATIBLE -> Color(0x66FF9800)
                                CompatibilityStatus.INCOMPATIBLE -> Color(0x66F44336)
                            }
                        )
                        .padding(horizontal = 10.dp, vertical = 6.dp)
                ) {
                    Text(
                        text = when (status) {
                            CompatibilityStatus.COMPATIBLE -> "Совместимо"
                            CompatibilityStatus.PARTIALLY_COMPATIBLE -> "Нужна проверка"
                            CompatibilityStatus.INCOMPATIBLE -> "Есть проблема"
                        },
                        style = MaterialTheme.typography.labelSmall,
                        color = Color.White
                    )
                }
            }
        }

        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = component.name,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = Color.White
                    )
                    Spacer(modifier = Modifier.size(2.dp))
                    Text(
                        text = "${component.brand} • ${component.type.name}",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color(0xFFB7C0CC)
                    )
                }
                Text(
                    text = "${component.minPrice.toInt()} руб.",
                    style = MaterialTheme.typography.titleMedium,
                    color = Color(0xFFFF8DCA),
                    fontWeight = FontWeight.Bold
                )
            }

            Text(
                text = component.description,
                style = MaterialTheme.typography.bodySmall,
                color = Color(0xFFD4D8DE)
            )
        }
    }
}

