let _eventos = [];
let _eventosFiltrados = [];
let _vista = 'grid';
const EV_CARDS_PAG = 8;
const EV_FILAS_PAG = 8;
let _evPagGrid = 1;
let _evPagTable = 1;

async function initEventos() {
  await Promise.all([cargarCategorias(), cargarEstados(), cargarEventos()]);
}

// CARDS/TABLE: trae eventos del organizador logueado
async function cargarEventos() {
  try {
    const res  = await fetch('/api/eventos/organizador', { credentials: 'include' });
    const json = await res.json();
    _eventos = json.data || [];
    _eventosFiltrados = [..._eventos];
    _evPagGrid = _evPagTable = 1;
    renderEventos();
    const nb = document.getElementById('nb-ev');
    if (nb) nb.textContent = _eventos.length;
  } catch (err) {
    Swal.fire({ icon: 'error', title: 'Error', text: 'No se pudo cargar los eventos: ' + err.message, confirmButtonColor: '#007bff' });
  }
}

// menu desplegable del modal: solo id+nombre via endpoint del organizador
async function cargarCategorias() {
  try {
    const res  = await fetch('/api/categorias/nombres', { credentials: 'include' });
    const json = await res.json();
    const sel  = document.getElementById('ev-cat');
    if (!sel) return;
    (json.data || []).forEach(c => {
      const o = document.createElement('option');
      o.value = c.id; o.textContent = c.nombre; sel.appendChild(o);
    });
  } catch {}
}

// menu desplegable del modal: estados
async function cargarEstados() {
  try {
    const res  = await fetch('/api/estados', { credentials: 'include' });
    const json = await res.json();
    const sel  = document.getElementById('ev-est');
    if (!sel) return;
    (json.data || []).forEach(e => {
      const o = document.createElement('option');
      o.value = e.id; o.textContent = e.nombre; sel.appendChild(o);
    });
  } catch {}
}

function filtrarEventos() {
  const el = document.getElementById('ev-srch');
  const q  = el ? el.value.toLowerCase() : '';
  _eventosFiltrados = _eventos.filter(e =>
    !q || e.titulo?.toLowerCase().includes(q) || e.lugar?.toLowerCase().includes(q)
  );
  _evPagGrid = _evPagTable = 1;
  renderEventos();
}

function toggleVista(v) {
  _vista = v;
  const evGrid     = document.getElementById('ev-grid');
  const evPagGrid  = document.getElementById('ev-pag-grid');
  const evTable    = document.getElementById('ev-table');
  const evPagTable = document.getElementById('ev-pag-table');
  const vtGrid     = document.getElementById('vt-grid');
  const vtTable    = document.getElementById('vt-table');
  if (evGrid)     evGrid.style.display     = v === 'grid'  ? 'grid'  : 'none';
  if (evPagGrid)  evPagGrid.style.display  = v === 'grid'  ? 'block' : 'none';
  if (evTable)    evTable.style.display    = v === 'table' ? 'block' : 'none';
  if (evPagTable) evPagTable.style.display = v === 'table' ? 'block' : 'none';
  if (vtGrid)     vtGrid.classList.toggle('active',  v === 'grid');
  if (vtTable)    vtTable.classList.toggle('active', v === 'table');
}

function renderEventos() {
  renderGridEventos();
  renderTablaEventos();
}

function renderGridEventos() {
  const grid    = document.getElementById('ev-grid');
  const pagWrap = document.getElementById('ev-pag-grid');
  if (!grid || !pagWrap) return;

  const total    = _eventosFiltrados.length;
  const totalPag = Math.ceil(total / EV_CARDS_PAG);

  if (!total) {
    grid.innerHTML = `<div style="grid-column:1/-1;text-align:center;padding:40px;color:var(--muted)">
      <div style="font-size:2rem;margin-bottom:8px">🗓</div>
      <div>Sin eventos. <button class="btn btn-primary btn-sm" onclick="openModal('modal-evento')">Crear uno</button></div>
    </div>`;
    pagWrap.innerHTML = '';
    return;
  }

  const slice = _eventosFiltrados.slice((_evPagGrid-1)*EV_CARDS_PAG, _evPagGrid*EV_CARDS_PAG);
  grid.innerHTML = slice.map(ev => {
    const d    = ev.fecha ? new Date(ev.fecha + 'T00:00:00') : null;
    const foto = ev.foto  ? `/uploads/eventos/${ev.foto}` : null;
    return `
      <div class="ev-card">
        <div class="ev-thumb">
          ${foto ? `<img src="${foto}" alt="${esc(ev.titulo)}" onerror="this.style.display='none'"/>` : ''}
          <span class="ev-ph">🗓</span>
          <span class="ev-cat-b">${esc(ev.categoria?.nombre || 'Evento')}</span>
          <div class="ev-dp">
            <div class="ev-dd">${d ? d.getDate() : '—'}</div>
            <div class="ev-dm">${d ? d.toLocaleDateString('es-CO',{month:'short'}).toUpperCase() : ''}</div>
          </div>
        </div>
        <div class="ev-body">
          <div class="ev-title">${esc(ev.titulo)}</div>
          <div class="ev-meta">
            <span>📍 ${esc(ev.lugar||'—')}</span>
            <span>🕐 ${ev.hora ? String(ev.hora).slice(0,5) : '—'}</span>
          </div>
          <span class="pill p-green">${esc(ev.estado?.nombre||'—')}</span>
          <div class="ev-foot">
            <button class="btn btn-ghost btn-sm" style="flex:1" onclick="navegarA('localidades')">Localidades</button>
            <button class="btn btn-ghost btn-sm" onclick="editEvento(${ev.id})">✏️</button>
            <button class="btn-del" onclick="delEvento(${ev.id})">🗑</button>
          </div>
        </div>
      </div>`;
  }).join('');

  pagWrap.innerHTML = buildPagHTML(totalPag, _evPagGrid, 'cambiarPagEventoGrid');
}

function renderTablaEventos() {
  const tbody   = document.getElementById('ev-tbody');
  const pagWrap = document.getElementById('ev-pag-table');
  if (!tbody || !pagWrap) return;

  const total    = _eventosFiltrados.length;
  const totalPag = Math.ceil(total / EV_FILAS_PAG);

  if (!total) {
    tbody.innerHTML = '<tr><td colspan="6" style="text-align:center;color:var(--muted);padding:24px">Sin eventos</td></tr>';
    pagWrap.innerHTML = '';
    return;
  }

  const slice = _eventosFiltrados.slice((_evPagTable-1)*EV_FILAS_PAG, _evPagTable*EV_FILAS_PAG);
  tbody.innerHTML = slice.map(ev => `
    <tr>
      <td><strong>${esc(ev.titulo)}</strong></td>
      <td><span class="pill p-blue">${esc(ev.categoria?.nombre||'—')}</span></td>
      <td>${ev.fecha ? new Date(ev.fecha+'T00:00:00').toLocaleDateString('es-CO') : '—'}</td>
      <td>${esc(ev.lugar||'—')}</td>
      <td><span class="pill p-green">${esc(ev.estado?.nombre||'—')}</span></td>
      <td>
        <div style="display:flex;gap:6px">
          <button class="btn btn-ghost btn-sm" onclick="editEvento(${ev.id})">Editar</button>
          <button class="btn-del" onclick="delEvento(${ev.id})">Eliminar</button>
        </div>
      </td>
    </tr>`).join('');

  pagWrap.innerHTML = buildPagHTML(totalPag, _evPagTable, 'cambiarPagEventoTable');
}

function cambiarPagEventoGrid(p)  { _evPagGrid  = p; renderGridEventos();  }
function cambiarPagEventoTable(p) { _evPagTable = p; renderTablaEventos(); }

// ── CRUD ──
async function submitEvento(e) {
  e.preventDefault();
  const id  = document.getElementById('ev-id').value;
  const btn = document.getElementById('btn-ev');
  const fd  = new FormData();
  fd.append('titulo',      document.getElementById('ev-titulo').value);
  fd.append('descripcion', document.getElementById('ev-desc').value);
  fd.append('lugar',       document.getElementById('ev-lugar').value);
  fd.append('fecha',       document.getElementById('ev-fecha').value);
  fd.append('hora',        document.getElementById('ev-hora').value);
  fd.append('categoriaId', document.getElementById('ev-cat').value);
  fd.append('estadoId',    document.getElementById('ev-est').value);
  const foto = document.getElementById('ev-foto').files[0];
  if (foto) fd.append('foto', foto);

  btn.textContent = 'Guardando…'; btn.disabled = true;
  try {
    const url    = id ? `/api/eventos/${id}` : '/api/eventos';
    const method = id ? 'PUT' : 'POST';
    const res    = await fetch(url, { method, credentials: 'include', body: fd });
    const json   = await res.json();
    if (!res.ok) throw new Error(json.mensaje || 'Error al guardar');
    Swal.fire({ icon: 'success', title: id ? '¡Evento actualizado!' : '¡Evento creado!',
      text: 'Los cambios se han guardado correctamente.', confirmButtonColor: '#007bff', timer: 3000, timerProgressBar: true });
    closeModal('modal-evento');
    await cargarEventos();
  } catch (err) {
    Swal.fire({ icon: 'error', title: 'Error', text: err.message, confirmButtonColor: '#007bff' });
  } finally {
    btn.textContent = document.getElementById('ev-id').value ? 'Guardar cambios' : 'Crear evento';
    btn.disabled = false;
  }
}

function editEvento(id) {
  const ev = _eventos.find(x => x.id == id);
  if (!ev) return;
  document.getElementById('ev-id').value     = ev.id;
  document.getElementById('ev-titulo').value = ev.titulo      || '';
  document.getElementById('ev-desc').value   = ev.descripcion || '';
  document.getElementById('ev-lugar').value  = ev.lugar       || '';
  document.getElementById('ev-fecha').value  = ev.fecha       || '';
  document.getElementById('ev-hora').value   = ev.hora ? String(ev.hora).slice(0,5) : '';
  document.getElementById('ev-cat').value    = ev.categoria?.id || '';
  document.getElementById('ev-est').value    = ev.estado?.id    || '';
  document.getElementById('mt-ev').textContent  = 'Editar evento';
  document.getElementById('btn-ev').textContent = 'Guardar cambios';
  openModalEdit('modal-evento');
}

async function delEvento(id) {
  const c = await Swal.fire({
    title: '¿Eliminar evento?', text: 'Esta acción no se puede deshacer.',
    icon: 'warning', showCancelButton: true,
    confirmButtonColor: 'var(--red)', cancelButtonColor: 'var(--muted)',
    confirmButtonText: 'Sí, eliminar', cancelButtonText: 'Cancelar'
  });
  if (!c.isConfirmed) return;
  try {
    const res  = await fetch(`/api/eventos/${id}`, { method: 'DELETE', credentials: 'include' });
    const json = await res.json();
    if (!res.ok) throw new Error(json.mensaje);
    Swal.fire({ icon: 'success', title: '¡Evento eliminado!', confirmButtonColor: '#007bff', timer: 2500, timerProgressBar: true });
    await cargarEventos();
  } catch (err) {
    Swal.fire({ icon: 'error', title: 'Error', text: err.message, confirmButtonColor: '#007bff' });
  }
}

function esc(s) {
  return s ? String(s).replace(/&/g,'&amp;').replace(/</g,'&lt;').replace(/>/g,'&gt;') : '';
}