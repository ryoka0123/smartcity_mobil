package com.example.smartcity.model

data class Ciudad(
    val idCiudad: Int,
    val nombreCiudad: String,
    val pais: String = "Colombia",
    val departamentoEstado: String = "Santander",
    val poblacionEstimada: Int = 623000
)

data class ZonaUrbana(
    val idZona: Int,
    val idCiudad: Int,
    val nombreZona: String,
    val codigoPostal: String?,
    val nivelPrioridadVial: String = "Media",
    val latCentro: Double,
    val lngCentro: Double
)

data class Ciudadano(
    val idCiudadano: Int,
    val documentoIdentidad: String,
    val nombreCompleto: String,
    val correoElectronico: String,
    val telefonoContacto: String?,
    val fechaRegistro: String
)

data class CategoriaIncidencia(
    val idCategoria: Int,
    val nombreCategoria: String,
    val tipoOdsMeta: String = "ODS 11",
    val slaHorasRespuesta: Int,
    val nivelCriticidad: String // "Alta", "Media", "Baja"
)

enum class EstadoReporte(val label: String) {
    PENDIENTE("Pendiente"),
    EN_ATENCION("En Atención"),
    RESUELTO("Resuelto")
}

data class ReporteIncidencia(
    val idReporte: Int,
    val idCiudadano: Int,
    val idZona: Int,
    val idCategoria: Int,
    val fechaHoraReporte: String,
    val descripcionProblema: String,
    val latitud: Double,
    val longitud: Double,
    val urlEvidenciaFoto: String?,
    val estadoActual: String
)

data class SensorIot(
    val idSensor: Int,
    val idZona: Int,
    val codigoSerie: String,
    val tipoSensor: String,
    val latitud: Double,
    val longitud: Double,
    val estadoOperativo: String = "Activo",
    val fechaInstalacion: String
)

data class LecturaSensor(
    val idLectura: Int,
    val idSensor: Int,
    val fechaHoraLectura: String,
    val valorMedido: Double,
    val unidadMedida: String,
    val alertaUmbralCritico: Boolean
)

data class CuadrillaOperativa(
    val idCuadrilla: Int,
    val nombreCuadrilla: String,
    val especialidadTecnica: String,
    val responsableTurno: String,
    val telefonoEmergencia: String?,
    val disponible: Boolean
)

data class IntervencionMantenimiento(
    val idIntervencion: Int,
    val idReporte: Int,
    val idCuadrilla: Int,
    val fechaAsignacion: String,
    val fechaCierre: String?,
    val diagnosticoTecnico: String?,
    val costoOperativo: Double = 0.0,
    val resultadoIntervencion: String = "En Proceso"
)

// UI Aggregate / Join Views

data class IntervencionDetailView(
    val intervencion: IntervencionMantenimiento,
    val nombreCuadrilla: String,
    val especialidadTecnica: String
)

data class ReporteDetailView(
    val reporte: ReporteIncidencia,
    val nombreZona: String,
    val nombreCategoria: String,
    val slaHorasRespuesta: Int,
    val nivelCriticidad: String,
    val nombreCiudadano: String,
    val correoCiudadano: String,
    val documentoCiudadano: String,
    val telefonoCiudadano: String?,
    val intervenciones: List<IntervencionDetailView> = emptyList()
)

data class SensorStatusView(
    val sensor: SensorIot,
    val nombreZona: String,
    val ultimoValor: Double?,
    val unidadMedida: String,
    val alerta: Boolean,
    val fechaLectura: String?
)

data class LecturaItemView(
    val lectura: LecturaSensor,
    val codigoSerie: String,
    val tipoSensor: String
)

data class CuadrillaStatsView(
    val cuadrilla: CuadrillaOperativa,
    val totalIntervenciones: Int,
    val costoTotal: Double
)

data class CategoriaConteo(
    val nombreCategoria: String,
    val total: Int
)

data class DashboardKpis(
    val totalReportes: Int,
    val pendientes: Int,
    val enAtencion: Int,
    val resueltos: Int,
    val alertasCriticas: Int,
    val desgloseCategorias: List<CategoriaConteo>
)
