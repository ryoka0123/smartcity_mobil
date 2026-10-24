package com.example.smartcity

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.smartcity.ui.screens.*
import com.example.smartcity.ui.theme.NavyPrimary
import com.example.smartcity.ui.theme.TealAccent
import com.example.smartcity.ui.viewmodel.ScreenDestination
import com.example.smartcity.ui.viewmodel.SmartCityViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SmartCityApp(
    viewModel: SmartCityViewModel = viewModel()
) {
    val currentScreen by viewModel.currentScreen.collectAsStateWithLifecycle()
    val kpis by viewModel.kpis.collectAsStateWithLifecycle()
    val reportes by viewModel.reportes.collectAsStateWithLifecycle()
    val sensores by viewModel.sensores.collectAsStateWithLifecycle()
    val lecturas by viewModel.lecturas.collectAsStateWithLifecycle()
    val cuadrillas by viewModel.cuadrillas.collectAsStateWithLifecycle()
    val estadoFilter by viewModel.estadoFilter.collectAsStateWithLifecycle()
    val snackBarMessage by viewModel.snackBarMessage.collectAsStateWithLifecycle()

    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(snackBarMessage) {
        snackBarMessage?.let { msg ->
            snackbarHostState.showSnackbar(msg)
            viewModel.clearSnackBar()
        }
    }

    val showBottomBar = currentScreen !is ScreenDestination.DetalleReporte &&
            currentScreen !is ScreenDestination.NuevoReporte

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            if (showBottomBar) {
                TopAppBar(
                    title = {
                        Column {
                            Text(
                                text = "SmartCity DataHub",
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "Bucaramanga, Santander • ODS 11",
                                style = MaterialTheme.typography.labelSmall,
                                color = Color(0xFF93C5FD)
                            )
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = NavyPrimary,
                        titleContentColor = Color.White
                    ),
                    actions = {
                        IconButton(
                            onClick = { viewModel.navigateTo(ScreenDestination.NuevoReporte) },
                            modifier = Modifier.testTag("action_nuevo_reporte")
                        ) {
                            Icon(Icons.Default.AddCircleOutline, contentDescription = "Nuevo Reporte", tint = Color.White)
                        }
                    }
                )
            }
        },
        bottomBar = {
            if (showBottomBar) {
                NavigationBar(
                    containerColor = MaterialTheme.colorScheme.surface,
                    tonalElevation = 8.dp,
                    modifier = Modifier.testTag("main_navigation_bar")
                ) {
                    NavigationBarItem(
                        selected = currentScreen is ScreenDestination.Dashboard,
                        onClick = { viewModel.navigateTo(ScreenDestination.Dashboard) },
                        icon = { Icon(Icons.Default.Dashboard, contentDescription = "Panel") },
                        label = { Text("Panel") },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = NavyPrimary,
                            indicatorColor = TealAccent.copy(alpha = 0.2f)
                        ),
                        modifier = Modifier.testTag("nav_tab_dashboard")
                    )

                    NavigationBarItem(
                        selected = currentScreen is ScreenDestination.Mapa,
                        onClick = { viewModel.navigateTo(ScreenDestination.Mapa) },
                        icon = { Icon(Icons.Default.Place, contentDescription = "Mapa") },
                        label = { Text("Mapa") },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = NavyPrimary,
                            indicatorColor = TealAccent.copy(alpha = 0.2f)
                        ),
                        modifier = Modifier.testTag("nav_tab_mapa")
                    )

                    NavigationBarItem(
                        selected = currentScreen is ScreenDestination.ReportesList,
                        onClick = { viewModel.navigateTo(ScreenDestination.ReportesList) },
                        icon = {
                            BadgedBox(
                                badge = {
                                    if (kpis.pendientes > 0) {
                                        Badge(containerColor = MaterialTheme.colorScheme.error) {
                                            Text("${kpis.pendientes}")
                                        }
                                    }
                                }
                            ) {
                                Icon(Icons.Default.List, contentDescription = "Incidencias")
                            }
                        },
                        label = { Text("Incidencias") },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = NavyPrimary,
                            indicatorColor = TealAccent.copy(alpha = 0.2f)
                        ),
                        modifier = Modifier.testTag("nav_tab_reportes")
                    )

                    NavigationBarItem(
                        selected = currentScreen is ScreenDestination.Sensores,
                        onClick = { viewModel.navigateTo(ScreenDestination.Sensores) },
                        icon = {
                            BadgedBox(
                                badge = {
                                    if (kpis.alertasCriticas > 0) {
                                        Badge(containerColor = MaterialTheme.colorScheme.error) {
                                            Text("${kpis.alertasCriticas}")
                                        }
                                    }
                                }
                            ) {
                                Icon(Icons.Default.Sensors, contentDescription = "Sensores IoT")
                            }
                        },
                        label = { Text("Sensores") },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = NavyPrimary,
                            indicatorColor = TealAccent.copy(alpha = 0.2f)
                        ),
                        modifier = Modifier.testTag("nav_tab_sensores")
                    )

                    NavigationBarItem(
                        selected = currentScreen is ScreenDestination.Cuadrillas,
                        onClick = { viewModel.navigateTo(ScreenDestination.Cuadrillas) },
                        icon = { Icon(Icons.Default.Engineering, contentDescription = "Cuadrillas") },
                        label = { Text("Cuadrillas") },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = NavyPrimary,
                            indicatorColor = TealAccent.copy(alpha = 0.2f)
                        ),
                        modifier = Modifier.testTag("nav_tab_cuadrillas")
                    )
                }
            }
        }
    ) { paddingValues ->
        Box(modifier = Modifier.padding(paddingValues)) {
            when (val screen = currentScreen) {
                is ScreenDestination.Dashboard -> {
                    DashboardScreen(
                        kpis = kpis,
                        recentReportes = reportes,
                        onNavigateToReportes = { filter ->
                            if (filter != null) viewModel.setFilter(filter)
                            viewModel.navigateTo(ScreenDestination.ReportesList)
                        },
                        onNavigateToDetalle = { id ->
                            viewModel.navigateTo(ScreenDestination.DetalleReporte(id))
                        },
                        onNavigateToNuevoReporte = {
                            viewModel.navigateTo(ScreenDestination.NuevoReporte)
                        },
                        onNavigateToSensores = {
                            viewModel.navigateTo(ScreenDestination.Sensores)
                        },
                        onNavigateToMapa = {
                            viewModel.navigateTo(ScreenDestination.Mapa)
                        }
                    )
                }

                is ScreenDestination.Mapa -> {
                    MapaScreen(
                        reportes = reportes,
                        sensores = sensores,
                        onNavigateToDetalle = { id ->
                            viewModel.navigateTo(ScreenDestination.DetalleReporte(id))
                        }
                    )
                }

                is ScreenDestination.ReportesList -> {
                    ReportesScreen(
                        reportes = reportes,
                        selectedFilter = estadoFilter,
                        onFilterChange = { viewModel.setFilter(it) },
                        onNavigateToDetalle = { id ->
                            viewModel.navigateTo(ScreenDestination.DetalleReporte(id))
                        },
                        onNavigateToNuevoReporte = {
                            viewModel.navigateTo(ScreenDestination.NuevoReporte)
                        }
                    )
                }

                is ScreenDestination.DetalleReporte -> {
                    val repDetail = viewModel.getReporteById(screen.idReporte)
                    DetalleReporteScreen(
                        reporte = repDetail,
                        disponiblesCuadrillas = viewModel.getCuadrillasDisponibles(),
                        onBack = { viewModel.navigateTo(ScreenDestination.ReportesList) },
                        onAsignarCuadrilla = { idReporte, idCuadrilla, diagnostico ->
                            viewModel.asignarCuadrilla(idReporte, idCuadrilla, diagnostico)
                        },
                        onCerrarIntervencion = { idIntervencion, costo ->
                            viewModel.cerrarIntervencion(idIntervencion, costo)
                        }
                    )
                }

                is ScreenDestination.NuevoReporte -> {
                    NuevoReporteScreen(
                        zonas = viewModel.getZonas(),
                        categorias = viewModel.getCategorias(),
                        onBack = { viewModel.navigateTo(ScreenDestination.ReportesList) },
                        onSubmit = { doc, nom, cor, tel, zona, cat, desc, lat, lng, foto ->
                            viewModel.crearReporte(doc, nom, cor, tel, zona, cat, desc, lat, lng, foto)
                        }
                    )
                }

                is ScreenDestination.Sensores -> {
                    SensoresScreen(
                        sensores = sensores,
                        lecturas = lecturas,
                        onSimularLectura = { sensorId ->
                            viewModel.simularLectura(sensorId)
                        }
                    )
                }

                is ScreenDestination.Cuadrillas -> {
                    CuadrillasScreen(cuadrillas = cuadrillas)
                }
            }
        }
    }
}
