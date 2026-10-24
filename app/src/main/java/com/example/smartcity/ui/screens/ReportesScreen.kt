package com.example.smartcity.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.smartcity.model.ReporteDetailView
import com.example.smartcity.ui.theme.NavyPrimary
import com.example.smartcity.ui.theme.TealAccent

@Composable
fun ReportesScreen(
    reportes: List<ReporteDetailView>,
    selectedFilter: String?,
    onFilterChange: (String) -> Unit,
    onNavigateToDetalle: (Int) -> Unit,
    onNavigateToNuevoReporte: () -> Unit,
    modifier: Modifier = Modifier
) {
    val filterOptions = listOf("Todos", "Pendiente", "En Atención", "Resuelto")

    val filteredList = if (selectedFilter == null || selectedFilter == "Todos") {
        reportes
    } else {
        reportes.filter { it.reporte.estadoActual.equals(selectedFilter, ignoreCase = true) }
    }

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = onNavigateToNuevoReporte,
                containerColor = TealAccent,
                contentColor = Color.White,
                modifier = Modifier.testTag("create_report_fab")
            ) {
                Icon(Icons.Default.Add, contentDescription = "Nuevo Reporte")
            }
        },
        modifier = modifier.fillMaxSize()
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .testTag("reportes_screen")
        ) {
            // Filter Bar
            Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(bottom = 8.dp)
                ) {
                    Icon(
                        Icons.Default.FilterList,
                        contentDescription = null,
                        tint = NavyPrimary,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Filtrar por Estado",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                }

                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    filterOptions.forEach { option ->
                        val isSelected = (selectedFilter == null && option == "Todos") ||
                                selectedFilter == option
                        FilterChip(
                            selected = isSelected,
                            onClick = { onFilterChange(option) },
                            label = { Text(text = option) },
                            shape = RoundedCornerShape(20.dp),
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = NavyPrimary,
                                selectedLabelColor = Color.White
                            ),
                            modifier = Modifier.testTag("filter_chip_$option")
                        )
                    }
                }
            }

            // Results count
            Text(
                text = "${filteredList.size} incidencias encontradas",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)
            )

            // Reports List
            if (filteredList.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "No se encontraron reportes con el estado seleccionado.",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(filteredList) { rep ->
                        ReporteCard(
                            reporte = rep,
                            onClick = { onNavigateToDetalle(rep.reporte.idReporte) }
                        )
                    }
                }
            }
        }
    }
}
