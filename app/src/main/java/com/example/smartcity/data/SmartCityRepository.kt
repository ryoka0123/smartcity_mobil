package com.example.smartcity.data

import com.example.smartcity.model.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlin.random.Random

class SmartCityRepository {

    private val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault())

    private fun now(): String = dateFormat.format(Date())

    // In-memory data store replicating the 9-entity relational model
    private val ciudad = Ciudad(1, "Bucaramanga", "Colombia", "Santander", 623000)

    private val zonas = mutableListOf(
        ZonaUrbana(1, 1, "Cabecera del Llano", "680003", "Alta", 7.1186, -73.1108),
        ZonaUrbana(2, 1, "Ciudadela Real de Minas (UTS)", "680005", "Alta", 7.1042, -73.1235),
        ZonaUrbana(3, 1, "Centro", "680006", "Alta", 7.1193, -73.1227),
        ZonaUrbana(4, 1, "San Alonso", "680004", "Media", 7.1305, -73.1162)
    )

    private val categorias = mutableListOf(
        CategoriaIncidencia(1, "Daño vial / hueco", "ODS 11.2", 48, "Media"),
        CategoriaIncidencia(2, "Falla semafórica", "ODS 11.2", 6, "Alta"),
        CategoriaIncidencia(3, "Alerta calidad del aire", "ODS 11.6", 12, "Alta"),
        CategoriaIncidencia(4, "Luminaria dañada", "ODS 11.7", 24, "Baja")
    )

    private val ciudadanos = mutableListOf(
        Ciudadano(1, "1098000001", "Jonathan Alexis Guevara Basto", "jonathan@correo.uts.edu.co", "3000000000", "2026-03-01 10:00"),
        Ciudadano(2, "1098000002", "María Fernanda Rojas", "maria.rojas@correo.com", "3101112233", "2026-03-05 14:30"),
        Ciudadano(3, "1098000003", "Carlos Andrés Pinzón", "carlos.pinzon@correo.com", "3159998877", "2026-03-10 09:15")
    )

    private val cuadrillas = mutableListOf(
        CuadrillaOperativa(1, "Cuadrilla Vial #1", "Pavimentación", "Ing. Luis Prada", "3201230001", true),
        CuadrillaOperativa(2, "Cuadrilla Eléctrica #2", "Semaforización", "Téc. Ana Ortiz", "3201230002", false),
        CuadrillaOperativa(3, "Cuadrilla Alumbrado #3", "Alumbrado público", "Téc. Jorge Mantilla", "3201230003", true)
    )

    private val reportes = mutableListOf(
        ReporteIncidencia(1, 2, 2, 2, "2026-04-10 08:30", "Semáforo intermitente en el cruce peatonal frente a la UTS.", 7.1042, -73.1235, null, EstadoReporte.EN_ATENCION.label),
        ReporteIncidencia(2, 3, 1, 1, "2026-04-11 11:15", "Hueco profundo en el carril derecho de la carrera 33.", 7.1186, -73.1108, null, EstadoReporte.PENDIENTE.label),
        ReporteIncidencia(3, 2, 3, 4, "2026-04-09 19:40", "Sector sin iluminación en la calle 36.", 7.1193, -73.1227, null, EstadoReporte.RESUELTO.label),
        ReporteIncidencia(4, 1, 4, 1, "2026-04-12 16:00", "Hundimiento de calzada cerca del puente peatonal.", 7.1305, -73.1162, null, EstadoReporte.PENDIENTE.label)
    )

    private val intervenciones = mutableListOf(
        IntervencionMantenimiento(1, 1, 2, "2026-04-10 09:00", null, "Falla en controlador del semáforo.", 180000.0, "En Proceso"),
        IntervencionMantenimiento(2, 3, 3, "2026-04-09 20:10", "2026-04-09 22:30", "Cambio de balasto y bombilla LED.", 95000.0, "Resuelto")
    )

    private val sensores = mutableListOf(
        SensorIot(1, 2, "SEN-BGA-01", "Calidad del aire PM2.5", 7.1050, -73.1230, "Activo", "2026-03-10"),
        SensorIot(2, 3, "SEN-BGA-02", "Flujo vehicular", 7.1190, -73.1230, "Activo", "2026-03-12"),
        SensorIot(3, 1, "SEN-BGA-03", "Ruido ambiental", 7.1180, -73.1110, "Activo", "2026-04-02")
    )

    private val lecturas = mutableListOf(
        LecturaSensor(1, 1, "2026-04-12 14:00", 18.4, "µg/m³", false),
        LecturaSensor(2, 2, "2026-04-12 14:05", 82.0, "veh/min", true),
        LecturaSensor(3, 3, "2026-04-12 14:10", 64.2, "dB", false)
    )

    private val sensorConfig = mapOf(
        "Calidad del aire PM2.5" to SensorThresholdConfig("µg/m³", 35.0, 8.0, 60.0),
        "Flujo vehicular" to SensorThresholdConfig("veh/min", 75.0, 20.0, 100.0),
        "Ruido ambiental" to SensorThresholdConfig("dB", 70.0, 45.0, 85.0)
    )

    data class SensorThresholdConfig(
        val unidad: String,
        val umbral: Double,
        val minRango: Double,
        val maxRango: Double
    )

    // Reactive StateFlow emitters
    private val _dashboardKpisFlow = MutableStateFlow(computeKpis())
    val dashboardKpisFlow: StateFlow<DashboardKpis> = _dashboardKpisFlow.asStateFlow()

    private val _reportesFlow = MutableStateFlow(buildReportesList())
    val reportesFlow: StateFlow<List<ReporteDetailView>> = _reportesFlow.asStateFlow()

    private val _sensoresFlow = MutableStateFlow(buildSensoresList())
    val sensoresFlow: StateFlow<List<SensorStatusView>> = _sensoresFlow.asStateFlow()

    private val _lecturasFlow = MutableStateFlow(buildRecentLecturas())
    val lecturasFlow: StateFlow<List<LecturaItemView>> = _lecturasFlow.asStateFlow()

    private val _cuadrillasFlow = MutableStateFlow(buildCuadrillasList())
    val cuadrillasFlow: StateFlow<List<CuadrillaStatsView>> = _cuadrillasFlow.asStateFlow()

    private fun computeKpis(): DashboardKpis {
        val total = reportes.size
        val pend = reportes.count { it.estadoActual == EstadoReporte.PENDIENTE.label }
        val aten = reportes.count { it.estadoActual == EstadoReporte.EN_ATENCION.label }
        val res = reportes.count { it.estadoActual == EstadoReporte.RESUELTO.label }
        val alertas = lecturas.count { it.alertaUmbralCritico }

        val breakdown = categorias.map { cat ->
            val count = reportes.count { it.idCategoria == cat.idCategoria }
            CategoriaConteo(cat.nombreCategoria, count)
        }

        return DashboardKpis(
            totalReportes = total,
            pendientes = pend,
            enAtencion = aten,
            resueltos = res,
            alertasCriticas = alertas,
            desgloseCategorias = breakdown
        )
    }

    private fun buildReportesList(): List<ReporteDetailView> {
        return reportes.sortedByDescending { it.idReporte }.map { rep ->
            val zona = zonas.find { it.idZona == rep.idZona }?.nombreZona ?: "Zona ${rep.idZona}"
            val cat = categorias.find { it.idCategoria == rep.idCategoria }
            val c = ciudadanos.find { it.idCiudadano == rep.idCiudadano }

            val repIntervenciones = intervenciones.filter { it.idReporte == rep.idReporte }
                .sortedByDescending { it.idIntervencion }
                .map { i ->
                    val q = cuadrillas.find { it.idCuadrilla == i.idCuadrilla }
                    IntervencionDetailView(
                        intervencion = i,
                        nombreCuadrilla = q?.nombreCuadrilla ?: "Cuadrilla #${i.idCuadrilla}",
                        especialidadTecnica = q?.especialidadTecnica ?: "Técnico"
                    )
                }

            ReporteDetailView(
                reporte = rep,
                nombreZona = zona,
                nombreCategoria = cat?.nombreCategoria ?: "Incidencia",
                slaHorasRespuesta = cat?.slaHorasRespuesta ?: 24,
                nivelCriticidad = cat?.nivelCriticidad ?: "Media",
                nombreCiudadano = c?.nombreCompleto ?: "Ciudadano",
                correoCiudadano = c?.correoElectronico ?: "",
                documentoCiudadano = c?.documentoIdentidad ?: "",
                telefonoCiudadano = c?.telefonoContacto,
                intervenciones = repIntervenciones
            )
        }
    }

    private fun buildSensoresList(): List<SensorStatusView> {
        return sensores.map { s ->
            val zona = zonas.find { it.idZona == s.idZona }?.nombreZona ?: "Zona ${s.idZona}"
            val lastLectura = lecturas.filter { it.idSensor == s.idSensor }.maxByOrNull { it.idLectura }
            val cfg = sensorConfig[s.tipoSensor]

            SensorStatusView(
                sensor = s,
                nombreZona = zona,
                ultimoValor = lastLectura?.valorMedido,
                unidadMedida = lastLectura?.unidadMedida ?: (cfg?.unidad ?: "u"),
                alerta = lastLectura?.alertaUmbralCritico ?: false,
                fechaLectura = lastLectura?.fechaHoraLectura
            )
        }
    }

    private fun buildRecentLecturas(): List<LecturaItemView> {
        return lecturas.sortedByDescending { it.idLectura }.take(15).map { l ->
            val s = sensores.find { it.idSensor == l.idSensor }
            LecturaItemView(
                lectura = l,
                codigoSerie = s?.codigoSerie ?: "SEN-00",
                tipoSensor = s?.tipoSensor ?: "Sensor"
            )
        }
    }

    private fun buildCuadrillasList(): List<CuadrillaStatsView> {
        return cuadrillas.map { q ->
            val qIntervenciones = intervenciones.filter { it.idCuadrilla == q.idCuadrilla }
            val total = qIntervenciones.size
            val costo = qIntervenciones.sumOf { it.costoOperativo }
            CuadrillaStatsView(
                cuadrilla = q,
                totalIntervenciones = total,
                costoTotal = costo
            )
        }
    }

    private fun notifyStateChanged() {
        _dashboardKpisFlow.value = computeKpis()
        _reportesFlow.value = buildReportesList()
        _sensoresFlow.value = buildSensoresList()
        _lecturasFlow.value = buildRecentLecturas()
        _cuadrillasFlow.value = buildCuadrillasList()
    }

    fun getZonas(): List<ZonaUrbana> = zonas.toList()
    fun getCategorias(): List<CategoriaIncidencia> = categorias.toList()
    fun getCuadrillasDisponibles(): List<CuadrillaOperativa> = cuadrillas.filter { it.disponible }

    @Synchronized
    fun registrarReporte(
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
    ): Int {
        var ciudadano = ciudadanos.find { it.documentoIdentidad.trim() == documento.trim() }
        val idCiudadano = if (ciudadano != null) {
            ciudadano.idCiudadano
        } else {
            val newId = (ciudadanos.maxOfOrNull { it.idCiudadano } ?: 0) + 1
            ciudadano = Ciudadano(newId, documento.trim(), nombre.trim(), correo.trim(), telefono?.trim(), now())
            ciudadanos.add(ciudadano)
            newId
        }

        val nextReporteId = (reportes.maxOfOrNull { it.idReporte } ?: 0) + 1
        val nuevoReporte = ReporteIncidencia(
            idReporte = nextReporteId,
            idCiudadano = idCiudadano,
            idZona = idZona,
            idCategoria = idCategoria,
            fechaHoraReporte = now(),
            descripcionProblema = descripcion.trim(),
            latitud = latitud,
            longitud = longitud,
            urlEvidenciaFoto = fotoUrl?.takeIf { it.isNotBlank() },
            estadoActual = EstadoReporte.PENDIENTE.label
        )
        reportes.add(nuevoReporte)
        notifyStateChanged()
        return nextReporteId
    }

    @Synchronized
    fun asignarCuadrilla(idReporte: Int, idCuadrilla: Int, diagnostico: String): Boolean {
        val rep = reportes.find { it.idReporte == idReporte } ?: return false
        val cuad = cuadrillas.find { it.idCuadrilla == idCuadrilla } ?: return false

        val nextIntervencionId = (intervenciones.maxOfOrNull { it.idIntervencion } ?: 0) + 1
        intervenciones.add(
            IntervencionMantenimiento(
                idIntervencion = nextIntervencionId,
                idReporte = idReporte,
                idCuadrilla = idCuadrilla,
                fechaAsignacion = now(),
                fechaCierre = null,
                diagnosticoTecnico = diagnostico.trim(),
                costoOperativo = 0.0,
                resultadoIntervencion = "En Proceso"
            )
        )

        val repIndex = reportes.indexOfFirst { it.idReporte == idReporte }
        if (repIndex >= 0) {
            reportes[repIndex] = rep.copy(estadoActual = EstadoReporte.EN_ATENCION.label)
        }

        val cuadIndex = cuadrillas.indexOfFirst { it.idCuadrilla == idCuadrilla }
        if (cuadIndex >= 0) {
            cuadrillas[cuadIndex] = cuad.copy(disponible = false)
        }

        notifyStateChanged()
        return true
    }

    @Synchronized
    fun cerrarIntervencion(idIntervencion: Int, costo: Double): Boolean {
        val interv = intervenciones.find { it.idIntervencion == idIntervencion } ?: return false
        val intervIndex = intervenciones.indexOfFirst { it.idIntervencion == idIntervencion }

        if (intervIndex >= 0) {
            intervenciones[intervIndex] = interv.copy(
                fechaCierre = now(),
                costoOperativo = costo,
                resultadoIntervencion = "Resuelto"
            )
        }

        val repIndex = reportes.indexOfFirst { it.idReporte == interv.idReporte }
        if (repIndex >= 0) {
            reportes[repIndex] = reportes[repIndex].copy(estadoActual = EstadoReporte.RESUELTO.label)
        }

        val cuadIndex = cuadrillas.indexOfFirst { it.idCuadrilla == interv.idCuadrilla }
        if (cuadIndex >= 0) {
            cuadrillas[cuadIndex] = cuadrillas[cuadIndex].copy(disponible = true)
        }

        notifyStateChanged()
        return true
    }

    @Synchronized
    fun simularLecturaSensor(idSensor: Int): LecturaSensor? {
        val sensor = sensores.find { it.idSensor == idSensor } ?: return null
        val cfg = sensorConfig[sensor.tipoSensor] ?: SensorThresholdConfig("u", 100.0, 0.0, 120.0)

        val rawVal = cfg.minRango + Random.nextDouble() * (cfg.maxRango - cfg.minRango)
        val valor = Math.round(rawVal * 10.0) / 10.0
        val alerta = valor > cfg.umbral

        val nextId = (lecturas.maxOfOrNull { it.idLectura } ?: 0) + 1
        val nuevaLectura = LecturaSensor(
            idLectura = nextId,
            idSensor = idSensor,
            fechaHoraLectura = now(),
            valorMedido = valor,
            unidadMedida = cfg.unidad,
            alertaUmbralCritico = alerta
        )
        lecturas.add(nuevaLectura)
        notifyStateChanged()
        return nuevaLectura
    }
}
