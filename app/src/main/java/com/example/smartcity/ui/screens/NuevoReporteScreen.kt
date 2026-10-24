package com.example.smartcity.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
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
import com.example.smartcity.model.CategoriaIncidencia
import com.example.smartcity.model.ZonaUrbana
import com.example.smartcity.ui.theme.BorderLight
import com.example.smartcity.ui.theme.NavyPrimary
import com.example.smartcity.ui.theme.TealAccent

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NuevoReporteScreen(
    zonas: List<ZonaUrbana>,
    categorias: List<CategoriaIncidencia>,
    onBack: () -> Unit,
    onSubmit: (
        documento: String,
        nombre: String,
        correo: String,
        telefono: String?,
        idZona: Int,
        idCategoria: Int,
        descripcion: String,
        latitud: Double,
        longitud: Double,
        fotoUrl: String?
    ) -> Unit,
    modifier: Modifier = Modifier
) {
    BackHandler { onBack() }

    var documento by remember { mutableStateOf("") }
    var nombre by remember { mutableStateOf("") }
    var correo by remember { mutableStateOf("") }
    var telefono by remember { mutableStateOf("") }

    var selectedZonaId by remember { mutableStateOf(zonas.firstOrNull()?.idZona ?: 1) }
    var selectedCategoriaId by remember { mutableStateOf(categorias.firstOrNull()?.idCategoria ?: 1) }

    var descripcion by remember { mutableStateOf("") }
    var latitudStr by remember {
        mutableStateOf(zonas.firstOrNull()?.latCentro?.toString() ?: "7.1186")
    }
    var longitudStr by remember {
        mutableStateOf(zonas.firstOrNull()?.lngCentro?.toString() ?: "-73.1108")
    }
    var fotoUrl by remember { mutableStateOf("") }

    var errorMessage by remember { mutableStateOf<String?>(null) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Registrar Incidencia") },
                navigationIcon = {
                    IconButton(
                        onClick = onBack,
                        modifier = Modifier.testTag("nuevo_reporte_back_button")
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
                .testTag("nuevo_reporte_screen"),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Citizen Form Card
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
                                text = "Datos del Ciudadano",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        Spacer(modifier = Modifier.height(12.dp))

                        OutlinedTextField(
                            value = documento,
                            onValueChange = { documento = it },
                            label = { Text("Documento de Identidad (Cédula) *") },
                            leadingIcon = { Icon(Icons.Default.Badge, contentDescription = null) },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("input_documento")
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        OutlinedTextField(
                            value = nombre,
                            onValueChange = { nombre = it },
                            label = { Text("Nombre Completo *") },
                            leadingIcon = { Icon(Icons.Default.AccountCircle, contentDescription = null) },
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("input_nombre")
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        OutlinedTextField(
                            value = correo,
                            onValueChange = { correo = it },
                            label = { Text("Correo Electrónico *") },
                            leadingIcon = { Icon(Icons.Default.Email, contentDescription = null) },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("input_correo")
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        OutlinedTextField(
                            value = telefono,
                            onValueChange = { telefono = it },
                            label = { Text("Teléfono de Contacto (opcional)") },
                            leadingIcon = { Icon(Icons.Default.Phone, contentDescription = null) },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("input_telefono")
                        )
                    }
                }
            }

            // Incident Details Form Card
            item {
                Card(
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    border = androidx.compose.foundation.BorderStroke(1.dp, BorderLight),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.ReportProblem, contentDescription = null, tint = NavyPrimary)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Detalles de la Incidencia",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        Spacer(modifier = Modifier.height(12.dp))

                        // Zona Urbana Selector
                        Text(
                            text = "Zona Urbana de Bucaramanga *",
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        zonas.forEach { zona ->
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 2.dp)
                            ) {
                                RadioButton(
                                    selected = selectedZonaId == zona.idZona,
                                    onClick = {
                                        selectedZonaId = zona.idZona
                                        latitudStr = zona.latCentro.toString()
                                        longitudStr = zona.lngCentro.toString()
                                    }
                                )
                                Text(
                                    text = "${zona.nombreZona} (${zona.codigoPostal ?: "S/C"})",
                                    style = MaterialTheme.typography.bodyMedium
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        // Categoría Selector
                        Text(
                            text = "Categoría de Incidencia *",
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        categorias.forEach { cat ->
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 2.dp)
                            ) {
                                RadioButton(
                                    selected = selectedCategoriaId == cat.idCategoria,
                                    onClick = { selectedCategoriaId = cat.idCategoria }
                                )
                                Text(
                                    text = "${cat.nombreCategoria} (SLA: ${cat.slaHorasRespuesta}h • Criticidad ${cat.nivelCriticidad})",
                                    style = MaterialTheme.typography.bodyMedium
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        OutlinedTextField(
                            value = descripcion,
                            onValueChange = { descripcion = it },
                            label = { Text("Descripción del Problema *") },
                            placeholder = { Text("Describa claramente la incidencia y ubicación exacta") },
                            minLines = 3,
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("input_descripcion")
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        // GPS Coordinates
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            OutlinedTextField(
                                value = latitudStr,
                                onValueChange = { latitudStr = it },
                                label = { Text("Latitud") },
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                                modifier = Modifier
                                    .weight(1f)
                                    .testTag("input_latitud")
                            )
                            OutlinedTextField(
                                value = longitudStr,
                                onValueChange = { longitudStr = it },
                                label = { Text("Longitud") },
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                                modifier = Modifier
                                    .weight(1f)
                                    .testTag("input_longitud")
                            )
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        OutlinedTextField(
                            value = fotoUrl,
                            onValueChange = { fotoUrl = it },
                            label = { Text("URL Evidencia Fotográfica (opcional)") },
                            leadingIcon = { Icon(Icons.Default.AddPhotoAlternate, contentDescription = null) },
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("input_foto_url")
                        )
                    }
                }
            }

            // Error Message
            errorMessage?.let { err ->
                item {
                    Text(
                        text = err,
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            // Submit Button
            item {
                Button(
                    onClick = {
                        val doc = documento.trim()
                        val nom = nombre.trim()
                        val cor = correo.trim()
                        val desc = descripcion.trim()
                        val lat = latitudStr.toDoubleOrNull()
                        val lng = longitudStr.toDoubleOrNull()

                        if (doc.isEmpty() || nom.isEmpty() || cor.isEmpty() || desc.isEmpty()) {
                            errorMessage = "Por favor completa todos los campos obligatorios (*)."
                            return@Button
                        }
                        if (lat == null || lng == null) {
                            errorMessage = "Coordenadas de latitud o longitud inválidas."
                            return@Button
                        }

                        errorMessage = null
                        onSubmit(
                            doc,
                            nom,
                            cor,
                            telefono.takeIf { it.isNotBlank() },
                            selectedZonaId,
                            selectedCategoriaId,
                            desc,
                            lat,
                            lng,
                            fotoUrl.takeIf { it.isNotBlank() }
                        )
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = TealAccent),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                        .testTag("submit_report_button")
                ) {
                    Icon(Icons.Default.CheckCircle, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Registrar Incidencia",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}
