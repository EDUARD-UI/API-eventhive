/* ════════════════════════════════════════
   EventHive — dashboard.js
   Lógica de la sección inicio / dashboard.
════════════════════════════════════════ */

function initDashboard() {
  /* Fecha en el hero */
  const d = new Date();
  const dateStr = d.toLocaleDateString('es-CO', { weekday: 'long', day: 'numeric', month: 'long', year: 'numeric' });
  document.getElementById('h-date').textContent    = dateStr;
  document.getElementById('topbar-sub').textContent = d.toLocaleDateString('es-CO', { day: 'numeric', month: 'short', year: 'numeric' });
}

function renderDashboard() {
  updateStatStrip();
  renderTimeline();
}

function updateStatStrip() {
  const stEv = document.getElementById('st-ev');
  const stLoc = document.getElementById('st-loc');
  const stPr = document.getElementById('st-pr');
  if (stEv) stEv.textContent = Store.eventos.length;
  if (stLoc) stLoc.textContent = Store.localidades.length;
  if (stPr) stPr.textContent = Store.promociones.length;
}

function renderTimeline() {
  const el = document.getElementById('dash-tl');
  if (!el) return;

  const sorted = [...Store.eventos]
    .filter(ev => ev.fecha)
    .sort((a, b) => (a.fecha > b.fecha ? 1 : -1))
    .slice(0, 5);

  if (!sorted.length) {
    el.innerHTML = emptyHTML('🗓', 'Sin eventos aún', 'Crea tu primer evento');
    return;
  }

  el.innerHTML = sorted.map(ev => `
    <div class="tl-item">
      <div class="tl-dot"></div>
      <div class="tl-date">${fmtDate(ev.fecha)}</div>
      <div class="tl-title">${ev.titulo}</div>
      <div class="tl-meta">📍 ${ev.lugar || '—'} · ${ev.categoria?.nombre || '—'}</div>
    </div>
  `).join('');
}

function updateHeroUser(usuario) {
  if (!usuario) return;
  const greeting = document.getElementById('h-greeting');
  if (greeting) {
    greeting.textContent = `Bienvenido, ${usuario.nombre || 'Organizador'} 👋`;
  }
}
