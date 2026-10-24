package com.example.smartcity.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.smartcity.data.SmartCityRepository
import com.example.smartcity.model.*
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

sealed class ScreenDestination {
    data object Dashboard : ScreenDestination()
    data object Mapa : ScreenDestination()
    data object ReportesList : ScreenDestination()
    data class DetalleReporte(val idReporte: Int) : ScreenDestination()
    data object NuevoReporte : ScreenDestination()
    data object Sensores : ScreenDestination()
    data object Cuadrillas : ScreenDestination()
}

class SmartCityViewModel(
    private val repository: SmartCityRepository = SmartCityRepository()
) : ViewModel() {

    val kpis: StateFlow<DashboardKpis> = repository.dashboardKpisFlow
    val reportes: StateFlow<List<ReporteDetailView>> = repository.reportesFlow
    val sensores: StateFlow<List<SensorStatusView>> = repository.sensoresFlow
    val lecturas: StateFlow<List<LecturaItemView>> = repository.lecturasFlow
    val cuadrillas: StateFlow<List<CuadrillaStatsView>> = repository.cuadrillasFlow

    private val _estadoFilter = MutableStateFlow<String?>("Todos")
    val estadoFilter: StateFlow<String?> = _estadoFilter.asStateFlow()

    private val _currentScreen = MutableStateFlow<ScreenDestination>(ScreenDestination.Dashboard)
    val currentScreen: StateFlow<ScreenDestination> = _currentScreen.asStateFlow()

    private val _snackBarMessage = MutableStateFlow<String?>(null)
    val snackBarMessage: StateFlow<String?> = _snackBarMessage.asStateFlow()

    fun navigateTo(destination: ScreenDestination) {
        _currentScreen.value = destination
    }

    fun setFilter(estado: String) {
        _estadoFilter.value = if (estado == "Todos") null else estado
    }

    fun clearSnackBar() {
        _snackBarMessage.value = null
    }

    fun getZonas(): List<ZonaUrbana> = repository.getZonas()
    fun getCategorias(): List<CategoriaIncidencia> = repository.getCategorias()
    fun getCuadrillasDisponibles(): List<CuadrillaOperativa> = repository.getCuadrillasDisponibles()

    fun getReporteById(id: Int): ReporteDetailView? {
        return reportes.value.find { it.reporte.idReporte == id }
    }

    fun crearReporte(
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
    ) {
        viewModelScope.launch {
            val newId = repository.registrarReporte(
                documento = documento,
                nombre = nombre,
                correo = correo,
                telefono = telefono,
                idZona = idZona,
                idCategoria = idCategoria,
                descripcion = descripcion,
                latitud = latitud,
                longitud = longitud,
                fotoUrl = fotoUrl
            )
            _snackBarMessage.value = "Reporte #$newId registrado exitosamente."
            _currentScreen.value = ScreenDestination.DetalleReporte(newId)
        }
    }

    fun asignarCuadrilla(idReporte: Int, idCuadrilla: Int, diagnostico: String) {
        viewModelScope.launch {
            val success = repository.asignarCuadrilla(idReporte, idCuadrilla, diagnostico)
            if (success) {
                _snackBarMessage.value = "Cuadrilla asignada. El reporte pasó a 'En Atención'."
            }
        }
    }

    fun cerrarIntervencion(idIntervencion: Int, costo: Double) {
        viewModelScope.launch {
            val success = repository.cerrarIntervencion(idIntervencion, costo)
            if (success) {
                _snackBarMessage.value = "Intervención cerrada y reporte marcado como Resuelto."
            }
        }
    }

    fun simularLectura(idSensor: Int) {
        viewModelScope.launch {
            val lectura = repository.simularLecturaSensor(idSensor)
            if (lectura != null) {
                val alertaText = if (lectura.alertaUmbralCritico) " ⚠ ¡ALERTA CRÍTICA!" else ""
                _snackBarMessage.value = "Lectura registrada: ${lectura.valorMedido} ${lectura.unidadMedida}$alertaText"
            }
        }
    }
}
