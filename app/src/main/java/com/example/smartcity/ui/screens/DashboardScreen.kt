package com.example.smartcity.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.smartcity.model.*
import com.example.smartcity.ui.theme.*

@Composable
fun DashboardScreen(
    kpis: DashboardKpis,
    recentReportes: List<ReporteDetailView>,
    onNavigateToReportes: (String?) -> Unit,
    onNavigateToDetalle: (Int) -> Unit,
    onNavigateToNuevoReporte: () -> Unit,
    onNavigateToSensores: () -> Unit,
    onNavigateToMapa: () -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .testTag("dashboard_screen"),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Hero Header
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = NavyPrimary),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "SmartCity DataHub",
                                style = MaterialTheme.typography.headlineMedium,
                                color = Color.White,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "Bucaramanga, Santander • ODS 11",
                                style = MaterialTheme.typography.bodyMedium,
                                color = Color(0xFF93C5FD)
                            )
                        }
                        Surface(
                            shape = RoundedCornerShape(20.dp),
                            color = TealAccent.copy(alpha = 0.2f),
                            border = androidx.compose.foundation.BorderStroke(1.dp, TealAccent)
                        ) {
                            Text(
                                text = "PROYECTO COIL",
                                style = MaterialTheme.typography.labelSmall,
                                color = Color.White,
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = "Gestión inteligente de incidencias urbanas, cuadrillas operativas y sensores IoT para una ciudad sostenible.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color(0xFFE2E8F0)
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Button(
                        onClick = onNavigateToNuevoReporte,
                        colors = ButtonDefaults.buttonColors(containerColor = TealAccent),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("dashboard_nuevo_reporte_button")
                    ) {
                        Icon(Icons.Default.AddCircle, contentDescription = null, tint = Color.White)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Reportar Nueva Incidencia",
                            color = Color.White,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    OutlinedButton(
                        onClick = onNavigateToMapa,
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.White),
                        border = androidx.compose.foundation.BorderStroke(1.dp, Color.White.copy(alpha = 0.6f)),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("dashboard_ver_mapa_button")
                    ) {
                        Icon(Icons.Default.Place, contentDescription = null, tint = Color.White)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Ver Mapa Urbano de Incidencias",
                            color = Color.White,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }

        // KPI Summary Cards
        item {
            Text(
                text = "Indicadores Clave del Sistema",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(10.dp))

            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    KpiCard(
                        title = "Total Reportes",
                        value = "${kpis.totalReportes}",
                        icon = Icons.Default.Assessment,
                        color = NavyPrimary,
                        modifier = Modifier
                            .weight(1f)
                            .clickable { onNavigateToReportes("Todos") }
                    )
                    KpiCard(
                        title = "Pendientes",
                        value = "${kpis.pendientes}",
                        icon = Icons.Default.PendingActions,
                        color = CriticalMedium,
                        modifier = Modifier
                            .weight(1f)
                            .clickable { onNavigateToReportes("Pendiente") }
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    KpiCard(
                        title = "En Atención",
                        value = "${kpis.enAtencion}",
                        icon = Icons.Default.Engineering,
                        color = Color(0xFF2563EB),
                        modifier = Modifier
                            .weight(1f)
                            .clickable { onNavigateToReportes("En Atención") }
                    )
                    KpiCard(
                        title = "Resueltos",
                        value = "${kpis.resueltos}",
                        icon = Icons.Default.CheckCircle,
                        color = Color(0xFF059669),
                        modifier = Modifier
                            .weight(1f)
                            .clickable { onNavigateToReportes("Resuelto") }
                    )
                }

                // IoT Alertas KPI card
                Card(
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = if (kpis.alertasCriticas > 0) Color(0xFFFEF2F2) else Color(0xFFF0FDF4)
                    ),
                    border = androidx.compose.foundation.BorderStroke(
                        1.dp,
                        if (kpis.alertasCriticas > 0) CriticalHigh.copy(alpha = 0.5f) else Color(0xFF86EFAC)
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onNavigateToSensores() }
                        .testTag("kpi_alertas_card")
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(40.dp)
                                    .clip(CircleShape)
                                    .background(if (kpis.alertasCriticas > 0) CriticalHigh else Color(0xFF059669)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = if (kpis.alertasCriticas > 0) Icons.Default.Warning else Icons.Default.Sensors,
                                    contentDescription = null,
                                    tint = Color.White
                                )
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(
                                    text = if (kpis.alertasCriticas > 0) "Alertas Críticas IoT" else "Sensores IoT Estables",
                                    fontWeight = FontWeight.Bold,
                                    style = MaterialTheme.typography.titleMedium,
                                    color = if (kpis.alertasCriticas > 0) CriticalHigh else Color(0xFF065F46)
                                )
                                Text(
                                    text = if (kpis.alertasCriticas > 0) "Umbrales ambientales superados en Bucaramanga" else "Telemetría ambiental dentro de límites",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = TextSecondary
                                )
                            }
                        }
                        Text(
                            text = "${kpis.alertasCriticas}",
                            style = MaterialTheme.typography.headlineMedium,
                            fontWeight = FontWeight.Bold,
                            color = if (kpis.alertasCriticas > 0) CriticalHigh else Color(0xFF059669)
                        )
                    }
                }
            }
        }

        // Categorías breakdown
        item {
            Card(
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = androidx.compose.foundation.BorderStroke(1.dp, BorderLight),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Incidencias por Categoría",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(12.dp))

                    val total = kpis.totalReportes.coerceAtLeast(1)
                    kpis.desgloseCategorias.forEach { cat ->
                        val ratio = cat.total.toFloat() / total
                        Column(modifier = Modifier.padding(vertical = 4.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(text = cat.nombreCategoria, style = MaterialTheme.typography.bodyMedium)
                                Text(
                                    text = "${cat.total} (${(ratio * 100).toInt()}%)",
                                    style = MaterialTheme.typography.bodyMedium,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            LinearProgressIndicator(
                                progress = { ratio },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(8.dp)
                                    .clip(RoundedCornerShape(4.dp)),
                                color = TealAccent,
                                trackColor = Color(0xFFE2E8F0)
                            )
                        }
                    }
                }
            }
        }

        // Recent Reports Header
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Reportes Recientes",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
                TextButton(onClick = { onNavigateToReportes(null) }) {
                    Text(text = "Ver todos", color = NavyPrimary, fontWeight = FontWeight.Bold)
                }
            }
        }

        // Recent items
        items(recentReportes.take(3)) { rep ->
            ReporteCard(
                reporte = rep,
                onClick = { onNavigateToDetalle(rep.reporte.idReporte) }
            )
        }
    }
}

@Composable
fun KpiCard(
    title: String,
    value: String,
    icon: ImageVector,
    color: Color,
    modifier: Modifier = Modifier
) {
    Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = androidx.compose.foundation.BorderStroke(1.dp, BorderLight),
        modifier = modifier
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(color.copy(alpha = 0.12f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(imageVector = icon, contentDescription = null, tint = color)
            }
            Spacer(modifier = Modifier.height(10.dp))
            Text(
                text = value,
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = color
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = title,
                style = MaterialTheme.typography.bodyMedium,
                color = TextSecondary
            )
        }
    }
}

@Composable
fun ReporteCard(
    reporte: ReporteDetailView,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val estadoColor = when (reporte.reporte.estadoActual) {
        "Pendiente" -> StatusPendingText
        "En Atención" -> StatusProgressText
        else -> StatusResolvedText
    }
    val estadoBg = when (reporte.reporte.estadoActual) {
        "Pendiente" -> StatusPendingBg
        "En Atención" -> StatusProgressBg
        else -> StatusResolvedBg
    }

    Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = androidx.compose.foundation.BorderStroke(1.dp, BorderLight),
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .testTag("reporte_card_${reporte.reporte.idReporte}")
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = NavyPrimary.copy(alpha = 0.08f)
                ) {
                    Text(
                        text = "#${reporte.reporte.idReporte} • ${reporte.nombreCategoria}",
                        style = MaterialTheme.typography.labelSmall,
                        color = NavyPrimary,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        fontWeight = FontWeight.Bold
                    )
                }

                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = estadoBg
                ) {
                    Text(
                        text = reporte.reporte.estadoActual,
                        style = MaterialTheme.typography.labelSmall,
                        color = estadoColor,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = reporte.reporte.descripcionProblema,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
                maxLines = 2
            )

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        Icons.Default.LocationOn,
                        contentDescription = null,
                        tint = TextSecondary,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = reporte.nombreZona,
                        style = MaterialTheme.typography.bodyMedium,
                        color = TextSecondary
                    )
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        Icons.Default.Timer,
                        contentDescription = null,
                        tint = TextSecondary,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "SLA ${reporte.slaHorasRespuesta}h",
                        style = MaterialTheme.typography.labelSmall,
                        color = TextSecondary
                    )
                }
            }
        }
    }
}
