const http = require('http');
const fs = require('fs');
const path = require('path');

const PORT = 3000;
const HOST = '0.0.0.0';

// In-memory data store replicating the Android repository and 9 entities
const initialZonas = [
  { idZona: 1, idCiudad: 1, nombreZona: 'Cabecera del Llano', codigoPostal: '680003', latCentro: 7.1186, lngCentro: -73.1108 },
  { idZona: 2, idCiudad: 1, nombreZona: 'Ciudadela Real de Minas (UTS)', codigoPostal: '680005', latCentro: 7.1042, lngCentro: -73.1235 },
  { idZona: 3, idCiudad: 1, nombreZona: 'Centro', codigoPostal: '680006', latCentro: 7.1193, lngCentro: -73.1227 },
  { idZona: 4, idCiudad: 1, nombreZona: 'San Alonso', codigoPostal: '680004', latCentro: 7.1305, lngCentro: -73.1162 }
];

const initialCategorias = [
  { idCategoria: 1, nombreCategoria: 'Daño vial / hueco', tipoOdsMeta: 'ODS 11.2', slaHorasRespuesta: 48, nivelCriticidad: 'Media' },
  { idCategoria: 2, nombreCategoria: 'Falla semafórica', tipoOdsMeta: 'ODS 11.2', slaHorasRespuesta: 6, nivelCriticidad: 'Alta' },
  { idCategoria: 3, nombreCategoria: 'Alerta calidad del aire', tipoOdsMeta: 'ODS 11.6', slaHorasRespuesta: 12, nivelCriticidad: 'Alta' },
  { idCategoria: 4, nombreCategoria: 'Luminaria dañada', tipoOdsMeta: 'ODS 11.7', slaHorasRespuesta: 24, nivelCriticidad: 'Baja' }
];

let ciudadanos = [
  { idCiudadano: 1, documentoIdentidad: '1098000001', nombreCompleto: 'Jonathan Alexis Guevara Basto', correoElectronico: 'jonathan@correo.uts.edu.co', telefonoContacto: '3000000000' },
  { idCiudadano: 2, documentoIdentidad: '1098000002', nombreCompleto: 'María Fernanda Rojas', correoElectronico: 'maria.rojas@correo.com', telefonoContacto: '3101112233' },
  { idCiudadano: 3, documentoIdentidad: '1098000003', nombreCompleto: 'Carlos Andrés Pinzón', correoElectronico: 'carlos.pinzon@correo.com', telefonoContacto: '3159998877' }
];

let cuadrillas = [
  { idCuadrilla: 1, nombreCuadrilla: 'Cuadrilla Vial #1', especialidadTecnica: 'Pavimentación', responsableTurno: 'Ing. Luis Prada', telefonoEmergencia: '3201230001', disponible: true },
  { idCuadrilla: 2, nombreCuadrilla: 'Cuadrilla Eléctrica #2', especialidadTecnica: 'Semaforización', responsableTurno: 'Téc. Ana Ortiz', telefonoEmergencia: '3201230002', disponible: false },
  { idCuadrilla: 3, nombreCuadrilla: 'Cuadrilla Alumbrado #3', especialidadTecnica: 'Alumbrado público', responsableTurno: 'Téc. Jorge Mantilla', telefonoEmergencia: '3201230003', disponible: true }
];

let reportes = [
  { idReporte: 1, idCiudadano: 2, idZona: 2, idCategoria: 2, fechaHoraReporte: '2026-04-10 08:30', descripcionProblema: 'Semáforo intermitente en el cruce peatonal frente a la UTS.', latitud: 7.1042, longitud: -73.1235, estadoActual: 'En Atención' },
  { idReporte: 2, idCiudadano: 3, idZona: 1, idCategoria: 1, fechaHoraReporte: '2026-04-11 11:15', descripcionProblema: 'Hueco profundo en el carril derecho de la carrera 33.', latitud: 7.1186, longitud: -73.1108, estadoActual: 'Pendiente' },
  { idReporte: 3, idCiudadano: 2, idZona: 3, idCategoria: 4, fechaHoraReporte: '2026-04-09 19:40', descripcionProblema: 'Sector sin iluminación en la calle 36.', latitud: 7.1193, longitud: -73.1227, estadoActual: 'Resuelto' },
  { idReporte: 4, idCiudadano: 1, idZona: 4, idCategoria: 1, fechaHoraReporte: '2026-04-12 16:00', descripcionProblema: 'Hundimiento de calzada cerca del puente peatonal.', latitud: 7.1305, longitud: -73.1162, estadoActual: 'Pendiente' }
];

let intervenciones = [
  { idIntervencion: 1, idReporte: 1, idCuadrilla: 2, fechaAsignacion: '2026-04-10 09:00', fechaCierre: null, diagnosticoTecnico: 'Falla en controlador del semáforo.', costoOperativo: 180000, resultadoIntervencion: 'En Proceso' },
  { idIntervencion: 2, idReporte: 3, idCuadrilla: 3, fechaAsignacion: '2026-04-09 20:10', fechaCierre: '2026-04-09 22:30', diagnosticoTecnico: 'Cambio de balasto y bombilla LED.', costoOperativo: 95000, resultadoIntervencion: 'Resuelto' }
];

const sensores = [
  { idSensor: 1, idZona: 2, codigoSerie: 'SEN-BGA-01', tipoSensor: 'Calidad del aire PM2.5', latitud: 7.1050, longitud: -73.1230, unidad: 'µg/m³', umbral: 35, min: 8, max: 60 },
  { idSensor: 2, idZona: 3, codigoSerie: 'SEN-BGA-02', tipoSensor: 'Flujo vehicular', latitud: 7.1190, longitud: -73.1230, unidad: 'veh/min', umbral: 75, min: 20, max: 100 },
  { idSensor: 3, idZona: 1, codigoSerie: 'SEN-BGA-03', tipoSensor: 'Ruido ambiental', latitud: 7.1180, longitud: -73.1110, unidad: 'dB', umbral: 70, min: 45, max: 85 }
];

let lecturas = [
  { idLectura: 1, idSensor: 1, fechaHoraLectura: '2026-04-12 14:00', valorMedido: 18.4, unidadMedida: 'µg/m³', alertaUmbralCritico: false },
  { idLectura: 2, idSensor: 2, fechaHoraLectura: '2026-04-12 14:05', valorMedido: 82.0, unidadMedida: 'veh/min', alertaUmbralCritico: true },
  { idLectura: 3, idSensor: 3, fechaHoraLectura: '2026-04-12 14:10', valorMedido: 64.2, unidadMedida: 'dB', alertaUmbralCritico: false }
];

function now() {
  const d = new Date();
  return d.toISOString().replace('T', ' ').substring(0, 16);
}

const server = http.createServer((req, res) => {
  const url = new URL(req.url, `http://${req.headers.host}`);

  // API Endpoints
  if (url.pathname === '/api/state') {
    const reportesEnriched = reportes.map(r => {
      const z = initialZonas.find(x => x.idZona === r.idZona);
      const c = initialCategorias.find(x => x.idCategoria === r.idCategoria);
      const u = ciudadanos.find(x => x.idCiudadano === r.idCiudadano);
      const repIntervs = intervenciones.filter(i => i.idReporte === r.idReporte).map(i => {
        const q = cuadrillas.find(cq => cq.idCuadrilla === i.idCuadrilla);
        return { ...i, nombreCuadrilla: q?.nombreCuadrilla || '', especialidadTecnica: q?.especialidadTecnica || '' };
      });
      return {
        ...r,
        nombreZona: z?.nombreZona || '',
        nombreCategoria: c?.nombreCategoria || '',
        slaHorasRespuesta: c?.slaHorasRespuesta || 24,
        nivelCriticidad: c?.nivelCriticidad || 'Media',
        nombreCiudadano: u?.nombreCompleto || '',
        documentoCiudadano: u?.documentoIdentidad || '',
        correoCiudadano: u?.correoElectronico || '',
        telefonoCiudadano: u?.telefonoContacto || '',
        intervenciones: repIntervs
      };
    });

    const kpis = {
      total: reportes.length,
      pendientes: reportes.filter(r => r.estadoActual === 'Pendiente').length,
      enAtencion: reportes.filter(r => r.estadoActual === 'En Atención').length,
      resueltos: reportes.filter(r => r.estadoActual === 'Resuelto').length,
      alertas: lecturas.filter(l => l.alertaUmbralCritico).length,
      desglose: initialCategorias.map(c => ({
        nombre: c.nombreCategoria,
        total: reportes.filter(r => r.idCategoria === c.idCategoria).length
      }))
    };

    const sensoresStatus = sensores.map(s => {
      const z = initialZonas.find(x => x.idZona === s.idZona);
      const lastLect = lecturas.filter(l => l.idSensor === s.idSensor).slice(-1)[0];
      return {
        ...s,
        nombreZona: z?.nombreZona || '',
        ultimoValor: lastLect ? lastLect.valorMedido : null,
        alerta: lastLect ? lastLect.alertaUmbralCritico : false,
        fechaLectura: lastLect ? lastLect.fechaHoraLectura : null
      };
    });

    const cuadrillasStats = cuadrillas.map(q => {
      const qIntervs = intervenciones.filter(i => i.idCuadrilla === q.idCuadrilla);
      return {
        ...q,
        totalIntervenciones: qIntervs.length,
        costoTotal: qIntervs.reduce((acc, i) => acc + (i.costoOperativo || 0), 0)
      };
    });

    res.writeHead(200, { 'Content-Type': 'application/json' });
    return res.end(JSON.stringify({
      kpis,
      reportes: reportesEnriched,
      sensores: sensoresStatus,
      cuadrillas: cuadrillasStats,
      lecturas: lecturas.slice(-15).reverse(),
      zonas: initialZonas,
      categorias: initialCategorias
    }));
  }

  if (req.method === 'POST' && url.pathname === '/api/nuevo-reporte') {
    let body = '';
    req.on('data', chunk => body += chunk);
    req.on('end', () => {
      const data = JSON.parse(body);
      let c = ciudadanos.find(x => x.documentoIdentidad === data.documento.trim());
      if (!c) {
        c = {
          idCiudadano: ciudadanos.length + 1,
          documentoIdentidad: data.documento.trim(),
          nombreCompleto: data.nombre.trim(),
          correoElectronico: data.correo.trim(),
          telefonoContacto: data.telefono?.trim() || null
        };
        ciudadanos.push(c);
      }
      const newId = reportes.length + 1;
      reportes.unshift({
        idReporte: newId,
        idCiudadano: c.idCiudadano,
        idZona: Number(data.idZona),
        idCategoria: Number(data.idCategoria),
        fechaHoraReporte: now(),
        descripcionProblema: data.descripcion.trim(),
        latitud: Number(data.latitud),
        longitud: Number(data.longitud),
        estadoActual: 'Pendiente'
      });
      res.writeHead(200, { 'Content-Type': 'application/json' });
      res.end(JSON.stringify({ success: true, idReporte: newId }));
    });
    return;
  }

  if (req.method === 'POST' && url.pathname === '/api/asignar-cuadrilla') {
    let body = '';
    req.on('data', chunk => body += chunk);
    req.on('end', () => {
      const { idReporte, idCuadrilla, diagnostico } = JSON.parse(body);
      const rep = reportes.find(r => r.idReporte === Number(idReporte));
      const cuad = cuadrillas.find(q => q.idCuadrilla === Number(idCuadrilla));
      if (rep && cuad) {
        rep.estadoActual = 'En Atención';
        cuad.disponible = false;
        intervenciones.unshift({
          idIntervencion: intervenciones.length + 1,
          idReporte: Number(idReporte),
          idCuadrilla: Number(idCuadrilla),
          fechaAsignacion: now(),
          fechaCierre: null,
          diagnosticoTecnico: diagnostico || 'Intervención en curso',
          costoOperativo: 0,
          resultadoIntervencion: 'En Proceso'
        });
      }
      res.writeHead(200, { 'Content-Type': 'application/json' });
      res.end(JSON.stringify({ success: true }));
    });
    return;
  }

  if (req.method === 'POST' && url.pathname === '/api/cerrar-intervencion') {
    let body = '';
    req.on('data', chunk => body += chunk);
    req.on('end', () => {
      const { idIntervencion, costo } = JSON.parse(body);
      const interv = intervenciones.find(i => i.idIntervencion === Number(idIntervencion));
      if (interv) {
        interv.fechaCierre = now();
        interv.costoOperativo = Number(costo) || 0;
        interv.resultadoIntervencion = 'Resuelto';
        const rep = reportes.find(r => r.idReporte === interv.idReporte);
        if (rep) rep.estadoActual = 'Resuelto';
        const cuad = cuadrillas.find(q => q.idCuadrilla === interv.idCuadrilla);
        if (cuad) cuad.disponible = true;
      }
      res.writeHead(200, { 'Content-Type': 'application/json' });
      res.end(JSON.stringify({ success: true }));
    });
    return;
  }

  if (req.method === 'POST' && url.pathname === '/api/simular-sensor') {
    let body = '';
    req.on('data', chunk => body += chunk);
    req.on('end', () => {
      const { idSensor } = JSON.parse(body);
      const s = sensores.find(x => x.idSensor === Number(idSensor));
      if (s) {
        const val = +(s.min + Math.random() * (s.max - s.min)).toFixed(1);
        const alerta = val > s.umbral;
        lecturas.push({
          idLectura: lecturas.length + 1,
          idSensor: s.idSensor,
          fechaHoraLectura: now(),
          valorMedido: val,
          unidadMedida: s.unidad,
          alertaUmbralCritico: alerta
        });
      }
      res.writeHead(200, { 'Content-Type': 'application/json' });
      res.end(JSON.stringify({ success: true }));
    });
    return;
  }

  // HTML UI Rendering
  const html = `<!DOCTYPE html>
<html lang="es">
<head>
  <meta charset="UTF-8">
  <meta name="viewport" content="width=device-width, initial-scale=1.0">
  <title>SmartCity DataHub • Android & Jetpack Compose</title>
  <link rel="preconnect" href="https://fonts.googleapis.com">
  <link rel="preconnect" href="https://fonts.gstatic.com" crossorigin>
  <link href="https://fonts.googleapis.com/css2?family=Roboto:wght@300;400;500;700;900&display=swap" rel="stylesheet">
  <link rel="stylesheet" href="https://unpkg.com/leaflet@1.9.4/dist/leaflet.css" />
  <script src="https://unpkg.com/leaflet@1.9.4/dist/leaflet.js"></script>
  <style>
    * { box-sizing: border-box; margin: 0; padding: 0; font-family: 'Roboto', sans-serif; }
    body { background-color: #0b131e; color: #1e293b; display: flex; justify-content: center; min-height: 100vh; padding: 20px; }
    .device-wrapper { width: 100%; max-width: 480px; background: #f8fafc; border-radius: 28px; box-shadow: 0 25px 50px -12px rgba(0,0,0,0.5); overflow: hidden; display: flex; flex-direction: column; border: 4px solid #1e293b; height: 92vh; position: relative; }
    .status-bar { height: 26px; background: #092c4c; display: flex; justify-content: space-between; align-items: center; padding: 0 16px; color: #94a3b8; font-size: 11px; }
    .app-header { background: #092c4c; padding: 14px 18px; color: white; display: flex; justify-content: space-between; align-items: center; }
    .app-header h1 { font-size: 19px; font-weight: 700; letter-spacing: -0.3px; }
    .app-header .subtitle { font-size: 11px; color: #38bdf8; font-weight: 500; }
    .coil-badge { background: rgba(56,189,248,0.2); border: 1px solid #38bdf8; color: #38bdf8; font-size: 10px; font-weight: 700; padding: 3px 8px; border-radius: 12px; }
    .content-area { flex: 1; overflow-y: auto; padding: 16px; background: #f1f5f9; }
    .bottom-nav { background: white; border-top: 1px solid #e2e8f0; display: flex; justify-content: space-around; padding: 8px 0; }
    .nav-item { display: flex; flex-direction: column; align-items: center; gap: 4px; color: #64748b; font-size: 11px; font-weight: 600; cursor: pointer; padding: 6px 12px; border-radius: 12px; transition: 0.2s; border: none; background: none; }
    .nav-item.active { color: #092c4c; background: #e0f2fe; }
    .card { background: white; border-radius: 16px; padding: 16px; margin-bottom: 14px; border: 1px solid #e2e8f0; box-shadow: 0 1px 3px rgba(0,0,0,0.05); }
    .hero-card { background: linear-gradient(135deg, #092c4c, #0f3f6e); color: white; border-radius: 18px; padding: 18px; margin-bottom: 16px; }
    .hero-card p { font-size: 13px; color: #cbd5e1; margin: 8px 0 14px; line-height: 1.4; }
    .btn-primary { background: #00a896; color: white; border: none; padding: 10px 16px; border-radius: 10px; font-weight: 700; font-size: 13px; cursor: pointer; display: flex; align-items: center; justify-content: center; gap: 8px; width: 100%; }
    .btn-primary:hover { background: #028090; }
    .kpi-grid { display: grid; grid-template-columns: 1fr 1fr; gap: 10px; margin-bottom: 14px; }
    .kpi-box { background: white; border-radius: 14px; padding: 12px; border: 1px solid #e2e8f0; }
    .kpi-num { font-size: 26px; font-weight: 800; color: #092c4c; }
    .kpi-label { font-size: 12px; color: #64748b; font-weight: 500; }
    .alert-banner { background: #fef2f2; border: 1px solid #fecaca; border-radius: 12px; padding: 12px; display: flex; justify-content: space-between; align-items: center; margin-bottom: 14px; }
    .badge { padding: 4px 10px; border-radius: 12px; font-size: 11px; font-weight: 700; display: inline-block; }
    .badge-pending { background: #fef3c7; color: #92400e; }
    .badge-progress { background: #dbeafe; color: #1e40af; }
    .badge-resolved { background: #d1fae5; color: #065f46; }
    .badge-alert { background: #fee2e2; color: #b91c1c; }
    .filter-chips { display: flex; gap: 8px; overflow-x: auto; padding-bottom: 10px; margin-bottom: 12px; }
    .chip { padding: 6px 14px; border-radius: 20px; font-size: 12px; font-weight: 600; cursor: pointer; background: white; border: 1px solid #cbd5e1; color: #475569; }
    .chip.active { background: #092c4c; color: white; border-color: #092c4c; }
    .input-group { margin-bottom: 12px; }
    .input-group label { display: block; font-size: 12px; font-weight: 600; color: #475569; margin-bottom: 4px; }
    .input-group input, .input-group select, .input-group textarea { width: 100%; padding: 10px 12px; border-radius: 8px; border: 1px solid #cbd5e1; font-size: 13px; outline: none; }
    .modal-overlay { position: absolute; inset: 0; background: rgba(0,0,0,0.6); display: flex; align-items: center; justify-content: center; padding: 20px; z-index: 50; }
    .modal-content { background: white; border-radius: 20px; padding: 20px; width: 100%; max-height: 85%; overflow-y: auto; }
  </style>
</head>
<body>
  <div class="device-wrapper">
    <div class="status-bar">
      <span>12:00</span>
      <span>● LTE 94%</span>
    </div>
    
    <div class="app-header">
      <div>
        <h1>SmartCity DataHub</h1>
        <div class="subtitle">Bucaramanga, Santander • ODS 11</div>
      </div>
      <span class="coil-badge">ANDROID / M3</span>
    </div>

    <div class="content-area" id="main-view">
      <!-- Injected via JavaScript -->
    </div>

    <div class="bottom-nav">
      <button class="nav-item active" onclick="switchTab('dashboard')" id="nav-dashboard">
        <span>📊</span>
        <span>Panel</span>
      </button>
      <button class="nav-item" onclick="switchTab('mapa')" id="nav-mapa">
        <span>📍</span>
        <span>Mapa</span>
      </button>
      <button class="nav-item" onclick="switchTab('reportes')" id="nav-reportes">
        <span>📋</span>
        <span>Incidencias</span>
      </button>
      <button class="nav-item" onclick="switchTab('sensores')" id="nav-sensores">
        <span>📡</span>
        <span>Sensores</span>
      </button>
      <button class="nav-item" onclick="switchTab('cuadrillas')" id="nav-cuadrillas">
        <span>👷</span>
        <span>Cuadrillas</span>
      </button>
    </div>
  </div>

  <div id="modal-container"></div>

  <script>
    let appState = null;
    let currentTab = 'dashboard';
    let currentFilter = 'Todos';
    let mapFilter = 'Todos';
    let leafletMapInstance = null;

    async function loadState() {
      const res = await fetch('/api/state');
      appState = await res.json();
      renderCurrentTab();
    }

    function switchTab(tab) {
      currentTab = tab;
      document.querySelectorAll('.nav-item').forEach(el => el.classList.remove('active'));
      const activeBtn = document.getElementById('nav-' + tab);
      if (activeBtn) activeBtn.classList.add('active');
      renderCurrentTab();
    }

    function renderCurrentTab() {
      if (!appState) return;
      const container = document.getElementById('main-view');
      if (currentTab === 'dashboard') renderDashboard(container);
      else if (currentTab === 'mapa') renderMapa(container);
      else if (currentTab === 'reportes') renderReportes(container);
      else if (currentTab === 'sensores') renderSensores(container);
      else if (currentTab === 'cuadrillas') renderCuadrillas(container);
    }

    function renderDashboard(c) {
      const { kpis, reportes } = appState;
      c.innerHTML = `
        <div class="hero-card">
          <div style="font-size:16px; font-weight:700;">Gestión Inteligente Bucaramanga</div>
          <p>Monitoreo de incidencias ciudadanas, cuadrillas operativas y sensores telemétricos ODS 11.</p>
          <button class="btn-primary" onclick="openNuevoReporteModal()">+ Registrar Nueva Incidencia</button>
          <button class="btn-primary" style="margin-top:8px; background:transparent; border:1px solid #38bdf8; color:#38bdf8;" onclick="switchTab('mapa')">📍 Ver Mapa Urbano de Incidencias</button>
        </div>

        <div style="font-size:14px; font-weight:700; margin-bottom:10px;">Indicadores Clave (KPIs)</div>
        <div class="kpi-grid">
          <div class="kpi-box" onclick="switchTab('reportes')">
            <div class="kpi-num">\${kpis.total}</div>
            <div class="kpi-label">Total Reportes</div>
          </div>
          <div class="kpi-box" style="border-left:4px solid #f59e0b;" onclick="currentFilter='Pendiente'; switchTab('reportes');">
            <div class="kpi-num" style="color:#d97706;">\${kpis.pendientes}</div>
            <div class="kpi-label">Pendientes</div>
          </div>
          <div class="kpi-box" style="border-left:4px solid #2563eb;" onclick="currentFilter='En Atención'; switchTab('reportes');">
            <div class="kpi-num" style="color:#2563eb;">\${kpis.enAtencion}</div>
            <div class="kpi-label">En Atención</div>
          </div>
          <div class="kpi-box" style="border-left:4px solid #059669;" onclick="currentFilter='Resuelto'; switchTab('reportes');">
            <div class="kpi-num" style="color:#059669;">\${kpis.resueltos}</div>
            <div class="kpi-label">Resueltos</div>
          </div>
        </div>

        <div class="alert-banner" onclick="switchTab('sensores')">
          <div>
            <div style="font-weight:700; color:\${kpis.alertas > 0 ? '#b91c1c' : '#047857'}">
              \${kpis.alertas > 0 ? '⚠️ Alertas Críticas IoT Activas' : '✅ Red IoT Operando Normal'}
            </div>
            <div style="font-size:11px; color:#64748b;">\${kpis.alertas} lecturas con umbral crítico superado</div>
          </div>
          <div style="font-size:22px; font-weight:800; color:\${kpis.alertas > 0 ? '#b91c1c' : '#047857'}">\${kpis.alertas}</div>
        </div>

        <div class="card">
          <div style="font-size:14px; font-weight:700; margin-bottom:10px;">Distribución por Categoría</div>
          \${kpis.desglose.map(item => {
            const pct = kpis.total > 0 ? Math.round((item.total / kpis.total) * 100) : 0;
            return \`
              <div style="margin-bottom:8px;">
                <div style="display:flex; justify-content:space-between; font-size:12px; margin-bottom:3px;">
                  <span>\${item.nombre}</span>
                  <b>\${item.total} (\${pct}%)</b>
                </div>
                <div style="height:6px; background:#e2e8f0; border-radius:3px; overflow:hidden;">
                  <div style="height:100%; width:\${pct}%; background:#00a896;"></div>
                </div>
              </div>
            \`;
          }).join('')}
        </div>

        <div style="display:flex; justify-content:space-between; align-items:center; margin:14px 0 8px;">
          <div style="font-size:14px; font-weight:700;">Reportes Recientes</div>
          <span style="font-size:12px; color:#028090; font-weight:700; cursor:pointer;" onclick="switchTab('reportes')">Ver todos</span>
        </div>
        \${reportes.slice(0, 3).map(r => renderReportCardHtml(r)).join('')}
      \`;
    }

    function renderReportCardHtml(r) {
      const badgeCls = r.estadoActual === 'Pendiente' ? 'badge-pending' : (r.estadoActual === 'En Atención' ? 'badge-progress' : 'badge-resolved');
      return \`
        <div class="card" onclick="openReporteDetailModal(\${r.idReporte})" style="cursor:pointer;">
          <div style="display:flex; justify-content:space-between; align-items:center; margin-bottom:8px;">
            <span style="font-size:12px; font-weight:700; color:#092c4c;">#\${r.idReporte} • \${r.nombreCategoria}</span>
            <span class="badge \${badgeCls}">\${r.estadoActual}</span>
          </div>
          <div style="font-size:13px; font-weight:600; margin-bottom:8px; line-height:1.3;">\${r.descripcionProblema}</div>
          <div style="display:flex; justify-content:space-between; font-size:11px; color:#64748b;">
            <span>📍 \${r.nombreZona}</span>
            <span>⏱ SLA \${r.slaHorasRespuesta}h (\${r.nivelCriticidad})</span>
          </div>
        </div>
      \`;
    }

    function renderMapa(c) {
      const filters = ['Todos', 'Pendiente', 'En Atención', 'Resuelto', 'Sensores IoT'];
      c.innerHTML = `
        <div style="font-size:18px; font-weight:700; color:#092c4c; margin-bottom:4px;">Mapa Urbano de Incidencias</div>
        <div style="font-size:12px; color:#64748b; margin-bottom:12px;">Geolocalización en tiempo real en Bucaramanga.</div>
        <div class="filter-chips">
          ${filters.map(f => `<div class="chip ${mapFilter === f ? 'active' : ''}" onclick="mapFilter='${f}'; renderMapa(document.getElementById('main-view'));">${f}</div>`).join('')}
        </div>
        <div style="height:320px; width:100%; border-radius:14px; overflow:hidden; border:1px solid #cbd5e1; margin-bottom:14px;">
          <div id="leaflet-map" style="height:100%; width:100%;"></div>
        </div>
        <div id="map-selected-card"></div>
      `;

      setTimeout(() => {
        if (typeof L === 'undefined') return;
        const mapEl = document.getElementById('leaflet-map');
        if (!mapEl) return;
        if (leafletMapInstance) {
          leafletMapInstance.remove();
          leafletMapInstance = null;
        }

        const map = L.map('leaflet-map').setView([7.1186, -73.1108], 13);
        leafletMapInstance = map;

        L.tileLayer('https://tile.openstreetmap.org/{z}/{x}/{y}.png', {
          maxZoom: 19,
          attribution: '&copy; OpenStreetMap'
        }).addTo(map);

        const showReportes = mapFilter !== 'Sensores IoT';
        const showSensores = mapFilter === 'Todos' || mapFilter === 'Sensores IoT';

        const filtered = mapFilter === 'Todos' || mapFilter === 'Sensores IoT' 
          ? appState.reportes 
          : appState.reportes.filter(r => r.estadoActual === mapFilter);

        if (showReportes) {
          filtered.forEach(r => {
            const color = r.estadoActual === 'Pendiente' ? '#f59e0b' : (r.estadoActual === 'En Atención' ? '#2563eb' : '#10b981');
            const m = L.circleMarker([r.latitud, r.longitud], {
              radius: 10,
              fillColor: color,
              color: '#ffffff',
              weight: 2,
              fillOpacity: 0.9
            }).addTo(map);

            m.bindPopup(`
              <div style="font-family:sans-serif; min-width:150px;">
                <b style="color:#092c4c;">#${r.idReporte} • ${r.nombreCategoria}</b><br>
                <span style="font-size:11px; color:#64748b;">📍 ${r.nombreZona}</span><br>
                <span style="font-size:12px;">${r.descripcionProblema}</span><br><br>
                <button onclick="openReporteDetailModal(${r.idReporte})" style="background:#00a896; color:white; border:none; padding:5px 8px; border-radius:6px; cursor:pointer; font-weight:bold; font-size:11px; width:100%;">Ver Detalle</button>
              </div>
            `);

            m.on('click', () => {
              const card = document.getElementById('map-selected-card');
              if (card) card.innerHTML = renderReportCardHtml(r);
            });
          });
        }

        if (showSensores) {
          appState.sensores.forEach(s => {
            const color = s.alerta ? '#ef4444' : '#06b6d4';
            const sm = L.circleMarker([s.latitud, s.longitud], {
              radius: 8,
              fillColor: color,
              color: '#092c4c',
              weight: 2,
              fillOpacity: 0.85
            }).addTo(map);

            sm.bindPopup(`
              <div style="font-family:sans-serif;">
                <b style="color:#092c4c;">📡 Sensor ${s.codigoSerie}</b><br>
                <span style="font-size:11px;">${s.tipoSensor}</span><br>
                <b>Valor: ${s.ultimoValor !== null ? s.ultimoValor + ' ' + s.unidad : 'Activo'}</b>
              </div>
            `);
          });
        }

        if (filtered.length > 0) {
          const first = filtered[0];
          const card = document.getElementById('map-selected-card');
          if (card) card.innerHTML = renderReportCardHtml(first);
        }
      }, 50);
    }

    function renderReportes(c) {
      const filters = ['Todos', 'Pendiente', 'En Atención', 'Resuelto'];
      const filtered = currentFilter === 'Todos' ? appState.reportes : appState.reportes.filter(r => r.estadoActual === currentFilter);
      c.innerHTML = \`
        <div style="display:flex; justify-content:space-between; align-items:center; margin-bottom:12px;">
          <div style="font-size:18px; font-weight:700; color:#092c4c;">Incidencias Urbanas</div>
          <button class="btn-primary" style="width:auto; padding:6px 12px; font-size:12px;" onclick="openNuevoReporteModal()">+ Nuevo</button>
        </div>
        <div class="filter-chips">
          \${filters.map(f => \`<div class="chip \${currentFilter === f ? 'active' : ''}" onclick="currentFilter='\${f}'; renderCurrentTab();">\${f}</div>\`).join('')}
        </div>
        <div style="font-size:12px; color:#64748b; margin-bottom:10px;">\${filtered.length} reportes encontrados</div>
        \${filtered.map(r => renderReportCardHtml(r)).join('')}
      \`;
    }

    function renderSensores(c) {
      c.innerHTML = \`
        <div style="font-size:18px; font-weight:700; color:#092c4c; margin-bottom:4px;">Sensores IoT Urbanos</div>
        <div style="font-size:12px; color:#64748b; margin-bottom:14px;">Telemetría ambiental en tiempo real en Bucaramanga.</div>
        \${appState.sensores.map(s => \`
          <div class="card" style="\${s.alerta ? 'border-color:#fca5a5; background:#fff5f5;' : ''}">
            <div style="display:flex; justify-content:space-between; align-items:center; margin-bottom:8px;">
              <div>
                <span style="font-size:11px; color:#64748b;">\${s.codigoSerie}</span>
                <div style="font-size:14px; font-weight:700;">\${s.tipoSensor}</div>
              </div>
              <span class="badge \${s.alerta ? 'badge-alert' : 'badge-resolved'}">\${s.alerta ? 'ALERTA CRÍTICA' : 'NORMAL'}</span>
            </div>
            <div style="display:flex; justify-content:space-between; align-items:flex-end; margin:12px 0;">
              <div>
                <div style="font-size:11px; color:#64748b;">Zona: \${s.nombreZona}</div>
                <div style="font-size:10px; color:#94a3b8;">Última: \${s.fechaLectura || 'N/A'}</div>
              </div>
              <div style="font-size:24px; font-weight:800; color:\${s.alerta ? '#b91c1c' : '#092c4c'}">
                \${s.ultimoValor !== null ? s.ultimoValor + ' ' + s.unidad : 'Sin datos'}
              </div>
            </div>
            <button class="btn-primary" onclick="simularSensor(\${s.idSensor})">🔄 Simular Lectura IoT</button>
          </div>
        \`).join('')}
        <div style="font-size:14px; font-weight:700; margin:16px 0 8px;">Telemetría Reciente</div>
        \${appState.lecturas.map(l => \`
          <div class="card" style="padding:10px 14px; margin-bottom:8px; display:flex; justify-content:space-between; align-items:center;">
            <div>
              <div style="font-size:12px; font-weight:600;">Sensor #\${l.idSensor} \${l.alertaUmbralCritico ? '⚠️' : ''}</div>
              <div style="font-size:10px; color:#64748b;">\${l.fechaHoraLectura}</div>
            </div>
            <div style="font-size:14px; font-weight:700; color:\${l.alertaUmbralCritico ? '#b91c1c' : '#092c4c'}">
              \${l.valorMedido} \${l.unidadMedida}
            </div>
          </div>
        \`).join('')}
      \`;
    }

    function renderCuadrillas(c) {
      c.innerHTML = \`
        <div style="font-size:18px; font-weight:700; color:#092c4c; margin-bottom:4px;">Cuadrillas Operativas</div>
        <div style="font-size:12px; color:#64748b; margin-bottom:14px;">Equipos técnicos de campo en Bucaramanga.</div>
        \${appState.cuadrillas.map(q => \`
          <div class="card">
            <div style="display:flex; justify-content:space-between; align-items:center; margin-bottom:8px;">
              <div style="font-size:14px; font-weight:700; color:#092c4c;">\${q.nombreCuadrilla}</div>
              <span class="badge \${q.disponible ? 'badge-resolved' : 'badge-progress'}">\${q.disponible ? 'DISPONIBLE' : 'EN OPERACIÓN'}</span>
            </div>
            <div style="font-size:12px; color:#475569; margin-bottom:6px;">Especialidad: <b>\${q.especialidadTecnica}</b></div>
            <div style="font-size:11px; color:#64748b; margin-bottom:10px;">Responsable: \${q.responsableTurno} • Tel: \${q.telefonoEmergencia || 'N/A'}</div>
            <div style="background:#f8fafc; border-radius:8px; padding:10px; display:flex; justify-content:space-between; font-size:12px;">
              <div>Intervenciones: <b>\${q.totalIntervenciones}</b></div>
              <div>Costo acumulado: <b style="color:#00a896;">$\${Number(q.costoTotal).toLocaleString('es-CO')} COP</b></div>
            </div>
          </div>
        \`).join('')}
      \`;
    }

    function openNuevoReporteModal() {
      const modal = document.getElementById('modal-container');
      modal.innerHTML = \`
        <div class="modal-overlay" onclick="closeModal(event)">
          <div class="modal-content" onclick="event.stopPropagation()">
            <div style="display:flex; justify-content:space-between; align-items:center; margin-bottom:14px;">
              <div style="font-size:16px; font-weight:700;">Registrar Incidencia</div>
              <span style="cursor:pointer; font-size:18px;" onclick="closeModal()">✕</span>
            </div>
            <form onsubmit="submitNuevoReporte(event)">
              <div class="input-group">
                <label>Documento de Identidad (Cédula) *</label>
                <input id="in-doc" required placeholder="Ej. 1098000004">
              </div>
              <div class="input-group">
                <label>Nombre Completo *</label>
                <input id="in-nom" required placeholder="Ej. Juan Pérez">
              </div>
              <div class="input-group">
                <label>Correo Electrónico *</label>
                <input id="in-cor" type="email" required placeholder="ejemplo@correo.com">
              </div>
              <div class="input-group">
                <label>Zona Urbana de Bucaramanga *</label>
                <select id="in-zona" onchange="updateCoords()">
                  \${appState.zonas.map(z => \`<option value="\${z.idZona}" data-lat="\${z.latCentro}" data-lng="\${z.lngCentro}">\${z.nombreZona}</option>\`).join('')}
                </select>
              </div>
              <div class="input-group">
                <label>Categoría de Incidencia *</label>
                <select id="in-cat">
                  \${appState.categorias.map(cat => \`<option value="\${cat.idCategoria}">\${cat.nombreCategoria} (SLA \${cat.slaHorasRespuesta}h)</option>\`).join('')}
                </select>
              </div>
              <div class="input-group">
                <label>Descripción del Problema *</label>
                <textarea id="in-desc" required rows="3" placeholder="Describa la ubicación y el problema observado"></textarea>
              </div>
              <div style="display:grid; grid-template-columns:1fr 1fr; gap:8px;">
                <div class="input-group">
                  <label>Latitud</label>
                  <input id="in-lat" value="7.1186" step="any">
                </div>
                <div class="input-group">
                  <label>Longitud</label>
                  <input id="in-lng" value="-73.1108" step="any">
                </div>
              </div>
              <button type="submit" class="btn-primary" style="margin-top:10px;">Registrar Incidencia</button>
            </form>
          </div>
        </div>
      \`;
    }

    function updateCoords() {
      const select = document.getElementById('in-zona');
      const opt = select.options[select.selectedIndex];
      document.getElementById('in-lat').value = opt.getAttribute('data-lat');
      document.getElementById('in-lng').value = opt.getAttribute('data-lng');
    }

    async function submitNuevoReporte(e) {
      e.preventDefault();
      const payload = {
        documento: document.getElementById('in-doc').value,
        nombre: document.getElementById('in-nom').value,
        correo: document.getElementById('in-cor').value,
        idZona: document.getElementById('in-zona').value,
        idCategoria: document.getElementById('in-cat').value,
        descripcion: document.getElementById('in-desc').value,
        latitud: document.getElementById('in-lat').value,
        longitud: document.getElementById('in-lng').value
      };
      await fetch('/api/nuevo-reporte', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(payload)
      });
      closeModal();
      await loadState();
      switchTab('reportes');
    }

    function openReporteDetailModal(id) {
      const r = appState.reportes.find(x => x.idReporte === id);
      if (!r) return;
      const modal = document.getElementById('modal-container');
      const disponibles = appState.cuadrillas.filter(q => q.disponible);

      modal.innerHTML = \`
        <div class="modal-overlay" onclick="closeModal(event)">
          <div class="modal-content" onclick="event.stopPropagation()">
            <div style="display:flex; justify-content:space-between; align-items:center; margin-bottom:12px;">
              <div style="font-size:16px; font-weight:700;">Reporte #\${r.idReporte}</div>
              <span style="cursor:pointer; font-size:18px;" onclick="closeModal()">✕</span>
            </div>
            <div style="font-size:15px; font-weight:700; color:#092c4c; margin-bottom:4px;">\${r.nombreCategoria}</div>
            <div style="font-size:13px; margin-bottom:12px; color:#334155;">\${r.descripcionProblema}</div>
            <div style="background:#f8fafc; border-radius:10px; padding:12px; margin-bottom:14px; font-size:12px; line-height:1.6;">
              <div>📍 <b>Zona:</b> \${r.nombreZona} (\${r.latitud}, \${r.longitud})</div>
              <div>👤 <b>Ciudadano:</b> \${r.nombreCiudadano} (C.C. \${r.documentoCiudadano})</div>
              <div>⏱ <b>SLA:</b> \${r.slaHorasRespuesta}h • Criticidad \${r.nivelCriticidad}</div>
              <div>📅 <b>Fecha:</b> \${r.fechaHoraReporte}</div>
            </div>

            <div style="font-size:14px; font-weight:700; margin-bottom:8px;">Intervenciones de Cuadrilla</div>
            \${r.intervenciones.length === 0 ? '<div style="font-size:12px; color:#64748b; margin-bottom:12px;">Sin cuadrilla asignada aún.</div>' : r.intervenciones.map(i => \`
              <div style="background:#f1f5f9; border-radius:8px; padding:10px; margin-bottom:8px; font-size:12px;">
                <div style="display:flex; justify-content:space-between; font-weight:700;">
                  <span>\${i.nombreCuadrilla}</span>
                  <span class="badge \${i.resultadoIntervencion === 'Resuelto' ? 'badge-resolved' : 'badge-progress'}">\${i.resultadoIntervencion}</span>
                </div>
                <div>Diagnóstico: \${i.diagnosticoTecnico || 'Sin detalle'}</div>
                <div>Costo: $\${Number(i.costoOperativo).toLocaleString('es-CO')} COP</div>
                \${i.resultadoIntervencion !== 'Resuelto' ? \`
                  <button class="btn-primary" style="margin-top:8px; padding:6px 10px; background:#059669;" onclick="cerrarIntervencion(\${i.idIntervencion})">✅ Cerrar Intervención / Resolver</button>
                \` : ''}
              </div>
            \`).join('')}

            \${r.estadoActual === 'Pendiente' ? \`
              <div style="border-top:1px solid #e2e8f0; padding-top:14px; margin-top:14px;">
                <div style="font-size:13px; font-weight:700; margin-bottom:8px;">Asignar Cuadrilla Operativa</div>
                \${disponibles.length === 0 ? '<div style="color:#b91c1c; font-size:12px;">No hay cuadrillas disponibles en este momento.</div>' : \`
                  <select id="asig-cuad" style="width:100%; padding:8px; border-radius:6px; border:1px solid #cbd5e1; margin-bottom:8px; font-size:12px;">
                    \${disponibles.map(q => \`<option value="\${q.idCuadrilla}">\${q.nombreCuadrilla} (\${q.especialidadTecnica})</option>\`).join('')}
                  </select>
                  <input id="asig-diag" placeholder="Diagnóstico inicial" style="width:100%; padding:8px; border-radius:6px; border:1px solid #cbd5e1; margin-bottom:8px; font-size:12px;">
                  <button class="btn-primary" onclick="asignarCuadrilla(\${r.idReporte})">Asignar Cuadrilla</button>
                \`}
              </div>
            \` : ''}
          </div>
        </div>
      \`;
    }

    async function asignarCuadrilla(idReporte) {
      const idCuadrilla = document.getElementById('asig-cuad').value;
      const diagnostico = document.getElementById('asig-diag').value;
      await fetch('/api/asignar-cuadrilla', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ idReporte, idCuadrilla, diagnostico })
      });
      closeModal();
      await loadState();
      openReporteDetailModal(idReporte);
    }

    async function cerrarIntervencion(idIntervencion) {
      const costo = prompt('Ingrese el costo operativo ($ COP) para cerrar la intervención:', '95000');
      if (costo === null) return;
      await fetch('/api/cerrar-intervencion', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ idIntervencion, costo })
      });
      closeModal();
      await loadState();
    }

    async function simularSensor(idSensor) {
      await fetch('/api/simular-sensor', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ idSensor })
      });
      await loadState();
    }

    function closeModal(e) {
      if (!e || e.target.classList.contains('modal-overlay') || e.target.tagName === 'SPAN') {
        document.getElementById('modal-container').innerHTML = '';
      }
    }

    loadState();
  </script>
</body>
</html>`;

  res.writeHead(200, { 'Content-Type': 'text/html; charset=utf-8' });
  res.end(html);
});

server.listen(PORT, HOST, () => {
  console.log(`SmartCity DataHub server running at http://${HOST}:${PORT}`);
});
