package com.example.smartcity.ui.screens

import androidx.compose.foundation.background
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.smartcity.model.LecturaItemView
import com.example.smartcity.model.SensorStatusView
import com.example.smartcity.ui.theme.*

@Composable
fun SensoresScreen(
    sensores: List<SensorStatusView>,
    lecturas: List<LecturaItemView>,
    onSimularLectura: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
            .testTag("sensores_screen"),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Header
        item {
            Column {
                Text(
                    text = "Sensores IoT Urbanos",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    color = NavyPrimary
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Monitoreo telemétrico de calidad ambiental y movilidad en Bucaramanga.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = TextSecondary
                )
            }
        }

        // Active Sensors Cards
        items(sensores) { s ->
            val icon = when {
                s.sensor.tipoSensor.contains("aire", ignoreCase = true) -> Icons.Default.Air
                s.sensor.tipoSensor.contains("vehicular", ignoreCase = true) -> Icons.Default.DirectionsCar
                else -> Icons.Default.VolumeUp
            }

            Card(
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(
                    containerColor = if (s.alerta) Color(0xFFFEF2F2) else MaterialTheme.colorScheme.surface
                ),
                border = androidx.compose.foundation.BorderStroke(
                    1.dp,
                    if (s.alerta) CriticalHigh.copy(alpha = 0.5f) else BorderLight
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("sensor_card_${s.sensor.idSensor}")
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(40.dp)
                                    .clip(CircleShape)
                                    .background(if (s.alerta) CriticalHigh.copy(alpha = 0.15f) else NavyPrimary.copy(alpha = 0.1f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = icon,
                                    contentDescription = null,
                                    tint = if (s.alerta) CriticalHigh else NavyPrimary
                                )
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(
                                    text = s.sensor.codigoSerie,
                                    style = MaterialTheme.typography.labelSmall,
                                    color = TextSecondary
                                )
                                Text(
                                    text = s.sensor.tipoSensor,
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }

                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = if (s.alerta) CriticalHighBg else StatusResolvedBg
                        ) {
                            Text(
                                text = if (s.alerta) "ALERTA CRÍTICA" else "NORMAL",
                                style = MaterialTheme.typography.labelSmall,
                                color = if (s.alerta) CriticalHigh else StatusResolvedText,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.Bottom
                    ) {
                        Column {
                            Text(
                                text = "Zona: ${s.nombreZona}",
                                style = MaterialTheme.typography.bodyMedium,
                                color = TextSecondary
                            )
                            s.fechaLectura?.let { fecha ->
                                Text(
                                    text = "Última medición: $fecha",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = TextSecondary
                                )
                            }
                        }

                        Text(
                            text = if (s.ultimoValor != null) "${s.ultimoValor} ${s.unidadMedida}" else "Sin datos",
                            style = MaterialTheme.typography.headlineMedium,
                            fontWeight = FontWeight.Bold,
                            color = if (s.alerta) CriticalHigh else NavyPrimary
                        )
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    Button(
                        onClick = { onSimularLectura(s.sensor.idSensor) },
                        colors = ButtonDefaults.buttonColors(containerColor = TealAccent),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("simulate_sensor_${s.sensor.idSensor}")
                    ) {
                        Icon(Icons.Default.Refresh, contentDescription = null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Simular Lectura en Vivo", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        // Telemetry History Section
        item {
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Historial Reciente de Telemetría",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
        }

        items(lecturas) { item ->
            Card(
                shape = RoundedCornerShape(10.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = androidx.compose.foundation.BorderStroke(1.dp, BorderLight),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "${item.codigoSerie} • ${item.tipoSensor}",
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.SemiBold
                            )
                            if (item.lectura.alertaUmbralCritico) {
                                Spacer(modifier = Modifier.width(6.dp))
                                Surface(
                                    shape = RoundedCornerShape(4.dp),
                                    color = CriticalHighBg
                                ) {
                                    Text(
                                        text = "UMBRAL",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = CriticalHigh,
                                        modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp),
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }
                        Text(
                            text = item.lectura.fechaHoraLectura,
                            style = MaterialTheme.typography.labelSmall,
                            color = TextSecondary
                        )
                    }

                    Text(
                        text = "${item.lectura.valorMedido} ${item.lectura.unidadMedida}",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = if (item.lectura.alertaUmbralCritico) CriticalHigh else NavyPrimary
                    )
                }
            }
        }
    }
}
