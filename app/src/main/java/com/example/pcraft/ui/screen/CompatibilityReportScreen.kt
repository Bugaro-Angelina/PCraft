package com.example.pcraft.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.pcraft.data.model.CompatibilityStatus
import com.example.pcraft.ui.component.CompatibilityStatusIndicator
import com.example.pcraft.ui.component.PsuLoadIndicator
import com.example.pcraft.ui.viewmodel.BuilderViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CompatibilityReportScreen(navController: NavController) {
    val viewModel: BuilderViewModel = hiltViewModel()
    val compatibilityReport by viewModel.compatibilityReport.collectAsState()

    compatibilityReport?.let { report ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFF121418))
        ) {
            TopAppBar(
                title = { Text("Отчет совместимости") },
                navigationIcon = {
                    IconButton(onClick = { navController.navigateUp() }) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = "Назад")
                    }
                }
            )

            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(
                            containerColor = when (report.overallStatus) {
                                CompatibilityStatus.COMPATIBLE -> Color(0x334CAF50)
                                CompatibilityStatus.PARTIALLY_COMPATIBLE -> Color(0x33FF9800)
                                CompatibilityStatus.INCOMPATIBLE -> Color(0x33F44336)
                            }
                        )
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(
                                text = "Общая оценка",
                                style = MaterialTheme.typography.titleLarge,
                                color = Color.White
                            )
                            Text(
                                text = report.getSummary(),
                                color = Color(0xFFD4D8DE),
                                modifier = Modifier.padding(top = 8.dp)
                            )
                        }
                    }
                }

                item { PsuLoadIndicator(viewModel.getPsuLoadResult()) }

                if (report.positives.isNotEmpty()) {
                    item { SectionTitle("Плюсы сборки") }
                    items(report.positives) { positive ->
                        ReportLine(positive, Color(0x334CAF50), Color(0xFFE7F7EA))
                    }
                }

                if (report.warnings.isNotEmpty()) {
                    item { SectionTitle("Предупреждения") }
                    items(report.warnings) { warning ->
                        ReportLine(warning, Color(0x33FF9800), Color(0xFFFFF0D8))
                    }
                }

                if (report.problems.isNotEmpty()) {
                    item { SectionTitle("Проблемы") }
                    items(report.problems) { problem ->
                        ReportLine(problem, Color(0x33F44336), Color(0xFFFFDADA))
                    }
                }

                item { SectionTitle("Статус комплектующих") }
                items(report.perComponentResults) { result ->
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = Color(0xCC1C2028))
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(
                                text = result.componentName,
                                color = Color.White,
                                fontWeight = FontWeight.SemiBold
                            )
                            Text(
                                text = result.message,
                                color = Color(0xFFD4D8DE),
                                modifier = Modifier.padding(top = 6.dp, bottom = 10.dp)
                            )
                            CompatibilityStatusIndicator(result.status)
                        }
                    }
                }
            }
        }
    } ?: Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Text("Отчет пока не сформирован", color = Color.White)
    }
}

@Composable
private fun SectionTitle(title: String) {
    Text(
        text = title,
        style = MaterialTheme.typography.titleLarge,
        color = Color.White
    )
}

@Composable
private fun ReportLine(text: String, background: Color, textColor: Color) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = background)
    ) {
        Text(
            text = text,
            color = textColor,
            modifier = Modifier.padding(14.dp)
        )
    }
}

