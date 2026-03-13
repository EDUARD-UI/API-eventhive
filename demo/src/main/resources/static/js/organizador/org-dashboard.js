// demo/src/main/resources/static/js/organizador/org-dashboard.js

async function initDashboard() {
  // Fecha en hero
  document.getElementById('h-date').textContent =
    new Date().toLocaleDateString('es-CO', { weekday:'long', day:'numeric', month:'long', year:'numeric' });
  if (ORG.usuario) {
    document.getElementById('h-greeting').textContent =
      `Bienvenido, ${ORG.usuario.nombre || 'Organizador'} 👋`;
  }

  try {
    const res = await fetch('/api/organizador/dashboard', { credentials: 'include' });
    const json = await res.json();
    if (!json.success) throw new Error(json.mensaje);
    const d = json.data;

    document.getElementById('st-ev').textContent  = d.totalEventos;
    document.getElementById('st-loc').textContent = d.totalLocalidades;
    document.getElementById('nb-ev').textContent  = d.totalEventos;

    // Timeline de próximos eventos
    const tl = document.getElementById('dash-tl');
    if (!d.proximosEventos?.length) {
      tl.innerHTML = `<div style="color:var(--muted);font-size:.85rem;padding:16px 0">
        Sin eventos aún. <a href="#" onclick="navegarA('eventos')" style="color:var(--blue)">Crea tu primero</a>
      </div>`;
      return;
    }
    tl.innerHTML = d.proximosEventos.map(ev => `
      <div class="tl-item">
        <div class="tl-dot"></div>
        <div class="tl-date">${fmtDate(ev.fecha)}</div>
        <div class="tl-title">${esc(ev.titulo)}</div>
        <div class="tl-meta">📍 ${esc(ev.lugar||'—')} · ${esc(ev.categoriaNombre||'—')}</div>
      </div>`).join('');

    // Guardar nombres para dropdowns en otras secciones
    ORG.dashboard = d;

  } catch (err) {
    document.getElementById('dash-tl').innerHTML =
      `<div style="color:var(--red);font-size:.85rem">Error al cargar: ${esc(err.message)}</div>`;
  }
}

function fmtDate(s) {
  if (!s) return '—';
  return new Date(s + 'T00:00:00').toLocaleDateString('es-CO',
    { day:'numeric', month:'long', year:'numeric' });
}

function esc(s) {
  return s ? String(s).replace(/&/g,'&amp;').replace(/</g,'&lt;').replace(/>/g,'&gt;') : '';
}