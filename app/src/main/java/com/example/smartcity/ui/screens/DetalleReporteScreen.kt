package com.example.smartcity.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.smartcity.model.CuadrillaOperativa
import com.example.smartcity.model.ReporteDetailView
import com.example.smartcity.ui.theme.*
import java.text.NumberFormat
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetalleReporteScreen(
    reporte: ReporteDetailView?,
    disponiblesCuadrillas: List<CuadrillaOperativa>,
    onBack: () -> Unit,
    onAsignarCuadrilla: (Int, Int, String) -> Unit,
    onCerrarIntervencion: (Int, Double) -> Unit,
    modifier: Modifier = Modifier
) {
    BackHandler { onBack() }

    var showAsignarDialog by remember { mutableStateOf(false) }
    var selectedCuadrillaId by remember { mutableStateOf<Int?>(null) }
    var diagnosticoInput by remember { mutableStateOf("") }

    var showCerrarDialog by remember { mutableStateOf(false) }
    var closingIntervencionId by remember { mutableStateOf<Int?>(null) }
    var costoInput by remember { mutableStateOf("") }

    val currencyFormat = remember {
        NumberFormat.getCurrencyInstance(Locale("es", "CO")).apply {
            maximumFractionDigits = 0
        }
    }

    if (reporte == null) {
        Box(
            modifier = modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(text = "Reporte no encontrado", style = MaterialTheme.typography.titleLarge)
                Spacer(modifier = Modifier.height(8.dp))
                Button(onClick = onBack) {
                    Text("Regresar")
                }
            }
        }
        return
    }

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

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Reporte #${reporte.reporte.idReporte}") },
                navigationIcon = {
                    IconButton(
                        onClick = onBack,
                        modifier = Modifier.testTag("detalle_back_button")
                    ) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Regresar")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = NavyPrimary,
                    titleContentColor = Color.White,
                    navigationIconContentColor = Color.White
                )
            )
        },
        modifier = modifier.fillMaxSize()
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
                .testTag("detalle_reporte_screen"),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Header Card
            item {
                Card(
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    border = androidx.compose.foundation.BorderStroke(1.dp, BorderLight),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = reporte.nombreCategoria,
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold,
                                color = NavyPrimary
                            )
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

                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = reporte.reporte.descripcionProblema,
                            style = MaterialTheme.typography.bodyLarge
                        )

                        Spacer(modifier = Modifier.height(16.dp))
                        HorizontalDivider(color = BorderLight)
                        Spacer(modifier = Modifier.height(12.dp))

                        // Metadata Grid
                        DetailMetaRow(
                            icon = Icons.Default.LocationOn,
                            label = "Zona Urbana",
                            value = "${reporte.nombreZona} (${reporte.reporte.latitud}, ${reporte.reporte.longitud})"
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        DetailMetaRow(
                            icon = Icons.Default.CalendarToday,
                            label = "Fecha Reporte",
                            value = reporte.reporte.fechaHoraReporte
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        DetailMetaRow(
                            icon = Icons.Default.Timer,
                            label = "Tiempo de Respuesta (SLA)",
                            value = "${reporte.slaHorasRespuesta} horas • Criticidad: ${reporte.nivelCriticidad}"
                        )
                    }
                }
            }

            // Citizen Info Card
            item {
                Card(
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    border = androidx.compose.foundation.BorderStroke(1.dp, BorderLight),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Person, contentDescription = null, tint = NavyPrimary)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Información del Ciudadano",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        Spacer(modifier = Modifier.height(10.dp))
                        DetailMetaRow(icon = Icons.Default.Badge, label = "Documento", value = reporte.documentoCiudadano)
                        Spacer(modifier = Modifier.height(6.dp))
                        DetailMetaRow(icon = Icons.Default.AccountCircle, label = "Nombre", value = reporte.nombreCiudadano)
                        Spacer(modifier = Modifier.height(6.dp))
                        DetailMetaRow(icon = Icons.Default.Email, label = "Correo", value = reporte.correoCiudadano)
                        reporte.telefonoCiudadano?.let { tel ->
                            Spacer(modifier = Modifier.height(6.dp))
                            DetailMetaRow(icon = Icons.Default.Phone, label = "Teléfono", value = tel)
                        }
                    }
                }
            }

            // Operative Actions (Asignar Cuadrilla if Pendiente)
            if (reporte.reporte.estadoActual == "Pendiente") {
                item {
                    Button(
                        onClick = {
                            selectedCuadrillaId = disponiblesCuadrillas.firstOrNull()?.idCuadrilla
                            diagnosticoInput = ""
                            showAsignarDialog = true
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = TealAccent),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("asignar_cuadrilla_button")
                    ) {
                        Icon(Icons.Default.Engineering, contentDescription = null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Asignar Cuadrilla Operativa", fontWeight = FontWeight.Bold)
                    }
                }
            }

            // Interventions Section Header
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Intervenciones de Mantenimiento",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "${reporte.intervenciones.size} registradas",
                        style = MaterialTheme.typography.bodyMedium,
                        color = TextSecondary
                    )
                }
            }

            if (reporte.intervenciones.isEmpty()) {
                item {
                    Card(
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFFFFFBEB)),
                        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFFDE68A)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Info, contentDescription = null, tint = Color(0xFFD97706))
                            Spacer(modifier = Modifier.width(10.dp))
                            Text(
                                text = "Aún no se ha asignado ninguna cuadrilla a esta incidencia.",
                                color = Color(0xFF92400E),
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }
                    }
                }
            } else {
                items(reporte.intervenciones) { interv ->
                    Card(
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        border = androidx.compose.foundation.BorderStroke(1.dp, BorderLight),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = interv.nombreCuadrilla,
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold
                                )
                                Surface(
                                    shape = RoundedCornerShape(6.dp),
                                    color = if (interv.intervencion.resultadoIntervencion == "Resuelto") StatusResolvedBg else StatusProgressBg
                                ) {
                                    Text(
                                        text = interv.intervencion.resultadoIntervencion,
                                        style = MaterialTheme.typography.labelSmall,
                                        color = if (interv.intervencion.resultadoIntervencion == "Resuelto") StatusResolvedText else StatusProgressText,
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "Especialidad: ${interv.especialidadTecnica}",
                                style = MaterialTheme.typography.bodyMedium,
                                color = TextSecondary
                            )

                            interv.intervencion.diagnosticoTecnico?.let { diag ->
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = "Diagnóstico: $diag",
                                    style = MaterialTheme.typography.bodyMedium
                                )
                            }

                            Spacer(modifier = Modifier.height(8.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = "Asignado: ${interv.intervencion.fechaAsignacion}",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = TextSecondary
                                )
                                Text(
                                    text = "Costo: ${currencyFormat.format(interv.intervencion.costoOperativo)}",
                                    style = MaterialTheme.typography.labelLarge,
                                    fontWeight = FontWeight.Bold,
                                    color = NavyPrimary
                                )
                            }

                            // If intervention is open, show Close button
                            if (interv.intervencion.resultadoIntervencion != "Resuelto") {
                                Spacer(modifier = Modifier.height(12.dp))
                                Button(
                                    onClick = {
                                        closingIntervencionId = interv.intervencion.idIntervencion
                                        costoInput = "100000"
                                        showCerrarDialog = true
                                    },
                                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF059669)),
                                    shape = RoundedCornerShape(8.dp),
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .testTag("cerrar_intervencion_button_${interv.intervencion.idIntervencion}")
                                ) {
                                    Icon(Icons.Default.Check, contentDescription = null)
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text("Cerrar Intervención / Marcar Resuelto")
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    // Modal Dialog: Asignar Cuadrilla
    if (showAsignarDialog) {
        AlertDialog(
            onDismissRequest = { showAsignarDialog = false },
            title = { Text("Asignar Cuadrilla Operativa") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text(
                        text = "Seleccione una cuadrilla disponible en Bucaramanga:",
                        style = MaterialTheme.typography.bodyMedium
                    )

                    if (disponiblesCuadrillas.isEmpty()) {
                        Text(
                            text = "No hay cuadrillas disponibles actualmente.",
                            color = CriticalHigh,
                            fontWeight = FontWeight.Bold
                        )
                    } else {
                        disponiblesCuadrillas.forEach { c ->
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp)
                            ) {
                                RadioButton(
                                    selected = selectedCuadrillaId == c.idCuadrilla,
                                    onClick = { selectedCuadrillaId = c.idCuadrilla }
                                )
                                Column(modifier = Modifier.padding(start = 8.dp)) {
                                    Text(text = c.nombreCuadrilla, fontWeight = FontWeight.Bold)
                                    Text(
                                        text = "${c.especialidadTecnica} • Responsable: ${c.responsableTurno}",
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = TextSecondary
                                    )
                                }
                            }
                        }
                    }

                    OutlinedTextField(
                        value = diagnosticoInput,
                        onValueChange = { diagnosticoInput = it },
                        label = { Text("Diagnóstico técnico inicial") },
                        placeholder = { Text("Ej. Falla en tarjeta de control o cableado") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("diagnostico_input")
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val cuadId = selectedCuadrillaId
                        if (cuadId != null) {
                            onAsignarCuadrilla(reporte.reporte.idReporte, cuadId, diagnosticoInput)
                            showAsignarDialog = false
                        }
                    },
                    enabled = selectedCuadrillaId != null,
                    colors = ButtonDefaults.buttonColors(containerColor = TealAccent),
                    modifier = Modifier.testTag("confirm_asignar_button")
                ) {
                    Text("Asignar")
                }
            },
            dismissButton = {
                TextButton(onClick = { showAsignarDialog = false }) {
                    Text("Cancelar")
                }
            }
        )
    }

    // Modal Dialog: Cerrar Intervención
    if (showCerrarDialog) {
        AlertDialog(
            onDismissRequest = { showCerrarDialog = false },
            title = { Text("Cerrar Intervención") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text(
                        text = "Ingrese el costo operativo final de la reparación ($ COP) para marcar el reporte como Resuelto:",
                        style = MaterialTheme.typography.bodyMedium
                    )

                    OutlinedTextField(
                        value = costoInput,
                        onValueChange = { costoInput = it },
                        label = { Text("Costo operativo ($ COP)") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("costo_operativo_input")
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val intervId = closingIntervencionId
                        val costo = costoInput.toDoubleOrNull() ?: 0.0
                        if (intervId != null) {
                            onCerrarIntervencion(intervId, costo)
                            showCerrarDialog = false
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF059669)),
                    modifier = Modifier.testTag("confirm_cerrar_button")
                ) {
                    Text("Finalizar y Resolver")
                }
            },
            dismissButton = {
                TextButton(onClick = { showCerrarDialog = false }) {
                    Text("Cancelar")
                }
            }
        )
    }
}

@Composable
fun DetailMetaRow(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    value: String
) {
    Row(verticalAlignment = Alignment.Top) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = NavyPrimary,
            modifier = Modifier
                .size(18.dp)
                .padding(top = 2.dp)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Column {
            Text(text = label, style = MaterialTheme.typography.labelSmall, color = TextSecondary)
            Text(text = value, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Medium)
        }
    }
}
