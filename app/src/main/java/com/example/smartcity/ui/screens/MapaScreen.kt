package com.example.smartcity.ui.screens

import android.annotation.SuppressLint
import android.webkit.JavascriptInterface
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.example.smartcity.model.ReporteDetailView
import com.example.smartcity.model.SensorStatusView
import com.example.smartcity.ui.theme.*

class MapBridge(private val onReporteClicked: (Int) -> Unit) {
    @JavascriptInterface
    fun onSelectReport(id: Int) {
        onReporteClicked(id)
    }
}

@SuppressLint("SetJavaScriptEnabled")
@Composable
fun MapaScreen(
    reportes: List<ReporteDetailView>,
    sensores: List<SensorStatusView>,
    onNavigateToDetalle: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedFilter by remember { mutableStateOf("Todos") }
    var selectedReporteId by remember { mutableStateOf<Int?>(reportes.firstOrNull()?.reporte?.idReporte) }
    var webViewInstance by remember { mutableStateOf<WebView?>(null) }

    val filterOptions = listOf("Todos", "Pendiente", "En Atención", "Resuelto", "Sensores IoT")

    val filteredReportes = when (selectedFilter) {
        "Todos" -> reportes
        "Sensores IoT" -> emptyList()
        else -> reportes.filter { it.reporte.estadoActual.equals(selectedFilter, ignoreCase = true) }
    }

    val showSensores = selectedFilter == "Todos" || selectedFilter == "Sensores IoT"

    val selectedReporte = reportes.find { it.reporte.idReporte == selectedReporteId }

    // Generates the Leaflet HTML content with interactive OpenStreetMap
    fun generateMapHtml(): String {
        val markersJs = StringBuilder()

        filteredReportes.forEach { rep ->
            val color = when (rep.reporte.estadoActual) {
                "Pendiente" -> "#f59e0b"
                "En Atención" -> "#2563eb"
                else -> "#10b981"
            }
            val title = "#${rep.reporte.idReporte} ${rep.nombreCategoria}"
            val desc = rep.reporte.descripcionProblema.replace("'", "\\'").replace("\"", "\\\"")
            val zona = rep.nombreZona.replace("'", "\\'")

            markersJs.append(
                """
                var marker = L.circleMarker([${rep.reporte.latitud}, ${rep.reporte.longitud}], {
                    radius: 10,
                    fillColor: '$color',
                    color: '#ffffff',
                    weight: 2,
                    opacity: 1,
                    fillOpacity: 0.9
                }).addTo(map);
                
                marker.bindPopup(
                    "<div style='font-family:sans-serif; min-width:160px;'>" +
                    "<b style='color:#092c4c;'>$title</b><br>" +
                    "<span style='font-size:11px; color:#64748b;'>📍 $zona</span><br>" +
                    "<span style='font-size:12px;'>$desc</span><br><br>" +
                    "<button onclick='window.AndroidBridge.onSelectReport(${rep.reporte.idReporte})' style='background:#00a896; color:white; border:none; padding:5px 10px; border-radius:6px; cursor:pointer; font-weight:bold; font-size:11px; width:100%;'>Ver Detalle</button>" +
                    "</div>"
                );
                marker.on('click', function() {
                    window.AndroidBridge.onSelectReport(${rep.reporte.idReporte});
                });
                markers.push(marker);
                """.trimIndent()
            )
        }

        if (showSensores) {
            sensores.forEach { s ->
                val color = if (s.alerta) "#ef4444" else "#06b6d4"
                val tipo = s.sensor.tipoSensor.replace("'", "\\'")
                val serie = s.sensor.codigoSerie
                val valStr = if (s.ultimoValor != null) "${s.ultimoValor} ${s.unidadMedida}" else "Activo"

                markersJs.append(
                    """
                    var sensorMarker = L.circleMarker([${s.sensor.latitud}, ${s.sensor.longitud}], {
                        radius: 8,
                        fillColor: '$color',
                        color: '#092c4c',
                        weight: 2,
                        opacity: 1,
                        fillOpacity: 0.85
                    }).addTo(map);
                    
                    sensorMarker.bindPopup(
                        "<div style='font-family:sans-serif;'>" +
                        "<b style='color:#092c4c;'>📡 Sensor $serie</b><br>" +
                        "<span style='font-size:11px;'>$tipo</span><br>" +
                        "<b>Valor: $valStr</b>" +
                        "</div>"
                    );
                    markers.push(sensorMarker);
                    """.trimIndent()
                )
            }
        }

        return """
        <!DOCTYPE html>
        <html>
        <head>
            <meta charset="utf-8" />
            <meta name="viewport" content="width=device-width, initial-scale=1.0, maximum-scale=1.0, user-scalable=no" />
            <link rel="stylesheet" href="https://unpkg.com/leaflet@1.9.4/dist/leaflet.css" />
            <script src="https://unpkg.com/leaflet@1.9.4/dist/leaflet.js"></script>
            <style>
                html, body, #map { height: 100%; width: 100%; margin: 0; padding: 0; }
                .leaflet-popup-content-wrapper { border-radius: 12px; box-shadow: 0 4px 12px rgba(0,0,0,0.15); }
            </style>
        </head>
        <body>
            <div id="map"></div>
            <script>
                var map = L.map('map', { zoomControl: false }).setView([7.1186, -73.1108], 13);
                L.control.zoom({ position: 'topright' }).addTo(map);

                L.tileLayer('https://tile.openstreetmap.org/{z}/{x}/{y}.png', {
                    maxZoom: 19,
                    attribution: '&copy; OpenStreetMap'
                }).addTo(map);

                var markers = [];
                $markersJs

                window.centerOn = function(lat, lng) {
                    map.flyTo([lat, lng], 15, { duration: 1 });
                };
            </script>
        </body>
        </html>
        """.trimIndent()
    }

    LaunchedEffect(selectedFilter) {
        webViewInstance?.loadDataWithBaseURL("https://openstreetmap.org", generateMapHtml(), "text/html", "UTF-8", null)
    }

    LaunchedEffect(selectedReporteId) {
        selectedReporte?.let { rep ->
            webViewInstance?.evaluateJavascript("if (window.centerOn) { window.centerOn(${rep.reporte.latitud}, ${rep.reporte.longitud}); }", null)
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .testTag("mapa_screen")
    ) {
        // Filter Chips Row
        Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(bottom = 6.dp)
            ) {
                Icon(Icons.Default.Place, contentDescription = null, tint = NavyPrimary, modifier = Modifier.size(20.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "Mapa de Incidencias Urbanas",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
            }

            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                items(filterOptions) { option ->
                    val isSelected = selectedFilter == option
                    FilterChip(
                        selected = isSelected,
                        onClick = { selectedFilter = option },
                        label = { Text(text = option) },
                        shape = RoundedCornerShape(20.dp),
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = NavyPrimary,
                            selectedLabelColor = Color.White
                        ),
                        modifier = Modifier.testTag("map_filter_$option")
                    )
                }
            }
        }

        // Map View Container
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
        ) {
            AndroidView(
                factory = { ctx ->
                    WebView(ctx).apply {
                        settings.javaScriptEnabled = true
                        settings.domStorageEnabled = true
                        webViewClient = WebViewClient()
                        addJavascriptInterface(
                            MapBridge { id ->
                                selectedReporteId = id
                            },
                            "AndroidBridge"
                        )
                        loadDataWithBaseURL("https://openstreetmap.org", generateMapHtml(), "text/html", "UTF-8", null)
                        webViewInstance = this
                    }
                },
                update = { webView ->
                    webViewInstance = webView
                },
                modifier = Modifier.fillMaxSize()
            )

            // Legend Overlay in the top-left corner
            Surface(
                shape = RoundedCornerShape(8.dp),
                color = Color.White.copy(alpha = 0.92f),
                shadowElevation = 4.dp,
                modifier = Modifier
                    .padding(10.dp)
                    .align(Alignment.TopStart)
            ) {
                Column(modifier = Modifier.padding(8.dp)) {
                    Text("Convenciones", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(4.dp))
                    LegendItem(color = Color(0xFFF59E0B), label = "Pendiente")
                    LegendItem(color = Color(0xFF2563EB), label = "En Atención")
                    LegendItem(color = Color(0xFF10B981), label = "Resuelto")
                    LegendItem(color = Color(0xFF06B6D4), label = "Sensor IoT")
                }
            }
        }

        // Bottom Selected Report Card
        selectedReporte?.let { rep ->
            Card(
                shape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = androidx.compose.foundation.BorderStroke(1.dp, BorderLight),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("map_selected_report_card")
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
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
                                text = "#${rep.reporte.idReporte} • ${rep.nombreCategoria}",
                                style = MaterialTheme.typography.labelSmall,
                                color = NavyPrimary,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                fontWeight = FontWeight.Bold
                            )
                        }

                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = when (rep.reporte.estadoActual) {
                                "Pendiente" -> StatusPendingBg
                                "En Atención" -> StatusProgressBg
                                else -> StatusResolvedBg
                            }
                        ) {
                            Text(
                                text = rep.reporte.estadoActual,
                                style = MaterialTheme.typography.labelSmall,
                                color = when (rep.reporte.estadoActual) {
                                    "Pendiente" -> StatusPendingText
                                    "En Atención" -> StatusProgressText
                                    else -> StatusResolvedText
                                },
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = rep.reporte.descripcionProblema,
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Medium,
                        maxLines = 2
                    )

                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "📍 ${rep.nombreZona} (${rep.reporte.latitud}, ${rep.reporte.longitud})",
                        style = MaterialTheme.typography.labelSmall,
                        color = TextSecondary
                    )

                    Spacer(modifier = Modifier.height(10.dp))
                    Button(
                        onClick = { onNavigateToDetalle(rep.reporte.idReporte) },
                        colors = ButtonDefaults.buttonColors(containerColor = TealAccent),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("map_ver_detalle_button")
                    ) {
                        Icon(Icons.Default.OpenInNew, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Ver Ficha Completa del Reporte", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

@Composable
fun LegendItem(color: Color, label: String) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.padding(vertical = 1.dp)
    ) {
        Box(
            modifier = Modifier
                .size(8.dp)
                .clip(RoundedCornerShape(4.dp))
                .background(color)
        )
        Spacer(modifier = Modifier.width(6.dp))
        Text(text = label, style = MaterialTheme.typography.labelSmall, color = TextSecondary)
    }
}
