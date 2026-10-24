# SmartCity DataHub (Android)

Proyecto integrador COIL · Bases de Datos / Modelado de Datos · UTS (Bucaramanga, Colombia) · ODS 11 (Ciudades y Comunidades Sostenibles)  
Autor original: Jonathan Alexis Guevara Basto · Docente: Ricardo Andrés Medina Puentes

Aplicación móvil nativa para Android desarrollada en **Kotlin** y **Jetpack Compose** (Material Design 3) que implementa el modelo entidad-relación de 9 entidades para gestionar reportes ciudadanos de incidencias urbanas, cuadrillas operativas y sensores IoT en Bucaramanga, Santander.

## Arquitectura y Tecnologías

- **Lenguaje:** Kotlin 2.1
- **Interfaz:** Jetpack Compose + Material Design 3 (M3)
- **Arquitectura:** MVVM (Model-View-ViewModel) con Kotlin Coroutines y StateFlow
- **Persistencia y Modelado:** Repositorio reactivo en memoria que implementa las 9 entidades relacionales del proyecto COIL
- **Construcción:** Gradle (Kotlin DSL `.gradle.kts`) con Version Catalog (`libs.versions.toml`)

## Modelo Entidad-Relación (9 Entidades)

1. `Ciudad` (Bucaramanga, Santander)
2. `ZonaUrbana` (Cabecera del Llano, Ciudadela Real de Minas UTS, Centro, San Alonso)
3. `Ciudadano` (Identificación con documento único, contacto y trazabilidad)
4. `CategoriaIncidencia` (Daño vial, Falla semafórica, Alerta calidad de aire, Luminaria dañada con SLA y criticidad)
5. `ReporteIncidencia` (Estado: *Pendiente*, *En Atención*, *Resuelto*, coordenadas GPS y detalle)
6. `SensorIot` (Red de sensores ambientales y de movilidad: PM2.5, Flujo vehicular, Ruido)
7. `LecturaSensor` (Telemetría con detección en tiempo real de umbral crítico)
8. `CuadrillaOperativa` (Equipos técnicos: Pavimentación, Semaforización, Alumbrado público con disponibilidad)
9. `IntervencionMantenimiento` (Relación reporte-cuadrilla, diagnóstico técnico, fecha asignación/cierre y costo operativo)

## Módulos y Funcionalidades Principales

- **Panel de Control (Dashboard):**
  - Métricas clave en tiempo real: Total de incidencias, Pendientes, En Atención, Resueltas y Alertas Críticas IoT.
  - Distribución de problemas por categoría y avance porcentual.
  - Accesos directos a crear reportes y revisar incidencias prioritarias.
- **Gestión de Incidencias (Reportes):**
  - Listado completo con filtros rápidos por estado (*Todos*, *Pendiente*, *En Atención*, *Resuelto*).
  - Tarjetas con ID, categoría, SLA en horas, nivel de criticidad, zona urbana y datos del ciudadano.
- **Detalle Operativo y Mantenimiento:**
  - Consulta integral de la incidencia y coordenadas GPS.
  - Flujo de **Asignación de Cuadrilla Operativa**: selección de cuadrillas disponibles con diagnóstico técnico.
  - Flujo de **Cierre de Intervención**: registro del costo operativo ($ COP) y marcado automático a *Resuelto*.
- **Registro de Nueva Incidencia:**
  - Formulario con auto-asociación de ciudadanos por documento de identidad.
  - Selector de zona urbana con auto-completado de coordenadas de Bucaramanga.
  - Selección de categoría con SLA y criticidad.
- **Sensores IoT y Telemetría Urbana:**
  - Monitoreo en vivo de PM2.5, flujo vehicular y ruido ambiental.
  - Botón de **Simulación de Lectura IoT** con evaluación instantánea de superación de umbral crítico.
  - Historial de telemetría de las últimas lecturas registradas.
- **Cuadrillas Operativas:**
  - Estado de disponibilidad (*Disponible* / *En Operación*).
  - Responsable de turno y teléfono de contacto para emergencias.
  - Estadísticas de intervenciones atendidas e inversión operativa acumulada.
