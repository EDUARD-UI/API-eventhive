/* ════════════════════════════════════════
   EventHive — ui.js
   Utilidades de interfaz reutilizables.
════════════════════════════════════════ */

/* ─── Toast ─── */
function toast(msg, type = 'info') {
  const wrap = document.getElementById('toast-wrap');
  const el = document.createElement('div');
  el.className = `toast ${type}`;
  const icons = { ok: '✓', err: '✕', info: 'ℹ' };
  el.innerHTML = `<span style="flex-shrink:0">${icons[type] || 'ℹ'}</span><span>${msg}</span>`;
  wrap.appendChild(el);
  setTimeout(() => el.remove(), 3500);
}

/* ─── Modal ─── */
function openModal(id) {
  const overlay = document.getElementById(id);
  const form    = overlay?.querySelector('form');
  if (form) {
    form.reset();
    const hidden = form.querySelector('input[type=hidden]');
    if (hidden) hidden.value = '';
  }
  overlay?.classList.add('open');
}

function closeModal(id) {
  document.getElementById(id)?.classList.remove('open');
}

/** Cierra el modal al hacer clic fuera del cuadro */
function initModalOverlays() {
  document.querySelectorAll('.modal-overlay').forEach(o => {
    o.addEventListener('click', e => {
      if (e.target === o) closeModal(o.id);
    });
  });
}

/* ─── View Toggle (Cards / Tabla) ─── */
/**
 * @param {HTMLElement} btn  - botón clickeado
 * @param {string}      ns   - namespace: 'ev' | 'loc' | 'pr'
 * @param {'grid'|'table'} v - vista destino
 */
function toggleView(btn, ns, v) {
  btn.closest('.view-toggle').querySelectorAll('.vt').forEach(b => b.classList.remove('active'));
  btn.classList.add('active');
  Store.vistas[nsMap(ns)] = v;

  const gridId  = `${ns}-grid`;
  const tableId = `${ns}-table`;
  const gridEl  = document.getElementById(gridId);
  const tableEl = document.getElementById(tableId);

  if (gridEl)  gridEl.style.display  = v === 'grid'  ? (ns === 'pr' ? 'grid' : 'grid') : 'none';
  if (tableEl) tableEl.style.display = v === 'table' ? 'block' : 'none';
}

function nsMap(ns) {
  const m = { ev: 'eventos', loc: 'localidades', pr: 'promociones' };
  return m[ns] || ns;
}

/* ─── Category chips ─── */
function selCat(btn) {
  document.querySelectorAll('.chip').forEach(c => c.classList.remove('active'));
  btn.classList.add('active');
  Store.filtros.eventos.categoria = btn.dataset.cat;
  renderEventos();
}

/* ─── Populate <select> ─── */
function fillSelect(id, items, valueFn = i => i.id, labelFn = i => i.nombre) {
  const sel = document.getElementById(id);
  if (!sel) return;
  items.forEach(item => {
    const opt = document.createElement('option');
    opt.value       = valueFn(item);
    opt.textContent = labelFn(item);
    sel.appendChild(opt);
  });
}

function fillSelectEventos(selectIds = ['loc-ev', 'pr-ev']) {
  selectIds.forEach(id => {
    const sel = document.getElementById(id);
    if (!sel) return;
    sel.innerHTML = '<option value="">Seleccionar evento...</option>';
    Store.eventos.forEach(ev => {
      const opt = document.createElement('option');
      opt.value = ev.id;
      opt.textContent = ev.titulo;
      sel.appendChild(opt);
    });
  });
}

/* ─── Formatters ─── */
function fmtDate(s) {
  if (!s) return '—';
  const d = new Date(s + 'T00:00:00');
  return d.toLocaleDateString('es-CO', { day: 'numeric', month: 'short', year: 'numeric' });
}

function fmtCurrency(n) {
  if (n == null) return '—';
  return '$' + Number(n).toLocaleString('es-CO');
}

function getInitials(nombre = '', apellido = '') {
  return ((nombre[0] || 'O') + (apellido[0] || 'G')).toUpperCase();
}

/* ─── Empty state HTML ─── */
function emptyHTML(icon, title, subtitle) {
  return `<div class="empty">
    <span class="ei">${icon}</span>
    <h3>${title}</h3>
    <p>${subtitle}</p>
  </div>`;
}
