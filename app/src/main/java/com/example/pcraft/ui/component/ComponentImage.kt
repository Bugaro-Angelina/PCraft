package com.example.pcraft.ui.component

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import coil.compose.SubcomposeAsyncImage

@Composable
fun ComponentImage(
    imageUrl: String,
    contentDescription: String,
    fallbackLabel: String,
    modifier: Modifier = Modifier,
    contentScale: ContentScale = ContentScale.Crop
) {
    val context = LocalContext.current
    val resourceId = rememberDrawableResId(imageUrl, context.packageName)

    when {
        resourceId != null -> {
            Image(
                painter = painterResource(id = resourceId),
                contentDescription = contentDescription,
                modifier = modifier,
                contentScale = contentScale
            )
        }

        imageUrl.isNotBlank() -> {
            SubcomposeAsyncImage(
                model = imageUrl,
                contentDescription = contentDescription,
                modifier = modifier,
                contentScale = contentScale,
                loading = {
                    FallbackImageLabel(fallbackLabel)
                },
                error = {
                    FallbackImageLabel(fallbackLabel)
                }
            )
        }

        else -> {
            FallbackImageLabel(fallbackLabel)
        }
    }
}

@Composable
private fun FallbackImageLabel(label: String) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = label,
            color = Color.White,
            fontWeight = FontWeight.Bold,
            style = MaterialTheme.typography.titleMedium
        )
    }
}

@Composable
private fun rememberDrawableResId(imageUrl: String, packageName: String): Int? {
    val drawableName = parseDrawableName(imageUrl) ?: return null
    val context = LocalContext.current
    val resourceId = context.resources.getIdentifier(drawableName, "drawable", packageName)
    return resourceId.takeIf { it != 0 }
}

private fun parseDrawableName(imageUrl: String): String? {
    val marker = "/drawable/"
    val start = imageUrl.indexOf(marker)
    if (start == -1) return null
    return imageUrl.substring(start + marker.length).substringBefore('?')
}
