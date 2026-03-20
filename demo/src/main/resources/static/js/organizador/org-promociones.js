let _promociones    = [];
let _promoFiltradas = [];
let _vistaPr        = 'grid';
const PR_CARDS_PAG  = 8;
const PR_FILAS_PAG  = 8;
let _prPagGrid  = 1;
let _prPagTable = 1;

async function initPromociones() {
  await Promise.all([cargarEventosDropdownPromo(), cargarPromociones()]);
}

// CARDS/TABLE: endpoint del organizador logueado
async function cargarPromociones() {
  try {
    const res  = await fetch('/api/promociones/organizador', { credentials: 'include' });
    const json = await res.json();
    _promociones    = json.data || [];
    _promoFiltradas = [..._promociones];
    _prPagGrid = _prPagTable = 1;
    renderPromociones();
  } catch (err) {
    Swal.fire({ icon: 'error', title: 'Error', text: 'No se pudo cargar las promociones: ' + err.message, confirmButtonColor: '#007bff' });
  }
}

// menu desplegable del modal: solo id+titulo de eventos del organizador logueado
async function cargarEventosDropdownPromo() {
  try {
    const res  = await fetch('/api/eventos/organizador/nombres-Eventos', { credentials: 'include' });
    const json = await res.json();
    const sel  = document.getElementById('pr-ev');
    if (!sel) return;
    (json.data || []).forEach(e => {
      const o = document.createElement('option');
      o.value = e.id; o.textContent = e.titulo; sel.appendChild(o);
    });
  } catch {}
}

function filtrarPromociones() {
  const el = document.getElementById('pr-srch');
  const q  = el ? el.value.toLowerCase() : '';
  _promoFiltradas = _promociones.filter(p =>
    !q || p.descripcion?.toLowerCase().includes(q) || p.eventoTitulo?.toLowerCase().includes(q)
  );
  _prPagGrid = _prPagTable = 1;
  renderPromociones();
}

function toggleVistaPr(v) {
  _vistaPr = v;
  const prGrid     = document.getElementById('pr-grid');
  const prPagGrid  = document.getElementById('pr-pag-grid');
  const prTable    = document.getElementById('pr-table');
  const prPagTable = document.getElementById('pr-pag-table');
  const vtGrid     = document.getElementById('vt-grid-pr');
  const vtTable    = document.getElementById('vt-table-pr');
  if (prGrid)     prGrid.style.display     = v === 'grid'  ? 'grid'  : 'none';
  if (prPagGrid)  prPagGrid.style.display  = v === 'grid'  ? 'block' : 'none';
  if (prTable)    prTable.style.display    = v === 'table' ? 'block' : 'none';
  if (prPagTable) prPagTable.style.display = v === 'table' ? 'block' : 'none';
  if (vtGrid)     vtGrid.classList.toggle('active',  v === 'grid');
  if (vtTable)    vtTable.classList.toggle('active', v === 'table');
}

function renderPromociones() {
  renderGridPromo();
  renderTablaPromo();
}

function renderGridPromo() {
  const grid    = document.getElementById('pr-grid');
  const pagWrap = document.getElementById('pr-pag-grid');
  if (!grid || !pagWrap) return;

  const total    = _promoFiltradas.length;
  const totalPag = Math.ceil(total / PR_CARDS_PAG);

  if (!total) {
    grid.innerHTML = `<div style="grid-column:1/-1;text-align:center;padding:40px;color:var(--muted)">
      <div style="font-size:2rem;margin-bottom:8px">🏷</div>
      <div>Sin promociones. <button class="btn btn-amber btn-sm" onclick="openModal('modal-promo')">Crear una</button></div>
    </div>`;
    pagWrap.innerHTML = '';
    return;
  }

  const slice = _promoFiltradas.slice((_prPagGrid-1)*PR_CARDS_PAG, _prPagGrid*PR_CARDS_PAG);
  grid.innerHTML = slice.map(p => {
    const vigente = esVigente(p);
    return `
      <div class="promo-card" style="flex-direction:column;align-items:flex-start;gap:10px">
        <div style="display:flex;align-items:center;gap:14px;width:100%">
          <div class="promo-icon">🏷</div>
          <div class="promo-info" style="flex:1;min-width:0">
            <h4>${esc(p.descripcion)}</h4>
            <p style="color:var(--blue);margin:2px 0">${esc(p.eventoTitulo||'—')}</p>
            <p>${fmtFecha(p.fechaInicio)} → ${fmtFecha(p.fechaFinal)}</p>
          </div>
          <div class="promo-pct">${p.descuento}%</div>
        </div>
        <div style="display:flex;align-items:center;justify-content:space-between;width:100%;
                    padding-top:10px;border-top:1px solid var(--surface2)">
          <span class="pill ${vigente?'p-green':'p-red'}">${vigente?'Vigente':'Expirada'}</span>
          <div style="display:flex;gap:6px">
            <button class="btn btn-ghost btn-sm" onclick="editPromo(${p.id})">Editar</button>
            <button class="btn-del" onclick="delPromo(${p.id})">Eliminar</button>
          </div>
        </div>
      </div>`;
  }).join('');

  pagWrap.innerHTML = buildPagHTML(totalPag, _prPagGrid, 'cambiarPagPromoGrid');
}

function renderTablaPromo() {
  const tbody   = document.getElementById('pr-tbody');
  const pagWrap = document.getElementById('pr-pag-table');
  if (!tbody || !pagWrap) return;

  const total    = _promoFiltradas.length;
  const totalPag = Math.ceil(total / PR_FILAS_PAG);

  if (!total) {
    tbody.innerHTML = '<tr><td colspan="7" style="text-align:center;color:var(--muted);padding:24px">Sin promociones</td></tr>';
    pagWrap.innerHTML = '';
    return;
  }

  const slice = _promoFiltradas.slice((_prPagTable-1)*PR_FILAS_PAG, _prPagTable*PR_FILAS_PAG);
  tbody.innerHTML = slice.map(p => {
    const vigente = esVigente(p);
    return `
      <tr>
        <td><strong>${esc(p.descripcion)}</strong></td>
        <td>${esc(p.eventoTitulo||'—')}</td>
        <td><span class="pill p-amber">${p.descuento}% OFF</span></td>
        <td>${fmtFecha(p.fechaInicio)}</td>
        <td>${fmtFecha(p.fechaFinal)}</td>
        <td><span class="pill ${vigente?'p-green':'p-red'}">${vigente?'Vigente':'Expirada'}</span></td>
        <td>
          <div style="display:flex;gap:6px">
            <button class="btn btn-ghost btn-sm" onclick="editPromo(${p.id})">Editar</button>
            <button class="btn-del" onclick="delPromo(${p.id})">Eliminar</button>
          </div>
        </td>
      </tr>`;
  }).join('');

  pagWrap.innerHTML = buildPagHTML(totalPag, _prPagTable, 'cambiarPagPromoTable');
}

function cambiarPagPromoGrid(p)  { _prPagGrid  = p; renderGridPromo();  }
function cambiarPagPromoTable(p) { _prPagTable = p; renderTablaPromo(); }

async function submitPromo(e) {
  e.preventDefault();
  const id  = document.getElementById('pr-id').value;
  const btn = document.getElementById('btn-pr');
  const data = new URLSearchParams({
    descripcion: document.getElementById('pr-desc').value,
    descuento:   document.getElementById('pr-pct').value,
    fechaInicio: document.getElementById('pr-ini').value,
    fechaFin:    document.getElementById('pr-fin').value,
    eventoId:    document.getElementById('pr-ev').value,
  });

  btn.textContent = 'Guardando…'; btn.disabled = true;
  try {
    const res  = await fetch(id ? `/api/promociones/${id}` : '/api/promociones',
      { method: id ? 'PUT' : 'POST', credentials: 'include',
        headers: { 'Content-Type': 'application/x-www-form-urlencoded' }, body: data });
    const json = await res.json();
    if (!res.ok) throw new Error(json.mensaje || 'Error al guardar');
    Swal.fire({ icon: 'success', title: id ? '¡Promoción actualizada!' : '¡Promoción creada!',
      text: 'Los cambios se guardaron correctamente.', confirmButtonColor: '#007bff', timer: 3000, timerProgressBar: true });
    closeModal('modal-promo');
    await cargarPromociones();
  } catch (err) {
    Swal.fire({ icon: 'error', title: 'Error', text: err.message, confirmButtonColor: '#007bff' });
  } finally {
    btn.textContent = document.getElementById('pr-id').value ? 'Guardar cambios' : 'Crear promoción';
    btn.disabled = false;
  }
}

function editPromo(id) {
  const p = _promociones.find(x => x.id == id);
  if (!p) return;
  document.getElementById('pr-id').value   = p.id;
  document.getElementById('pr-desc').value = p.descripcion || '';
  document.getElementById('pr-ev').value   = p.eventoId    || '';
  document.getElementById('pr-pct').value  = p.descuento   || '';
  document.getElementById('pr-ini').value  = fmtISO(p.fechaInicio);
  document.getElementById('pr-fin').value  = fmtISO(p.fechaFinal);
  document.getElementById('mt-pr').textContent  = 'Editar promoción';
  document.getElementById('btn-pr').textContent = 'Guardar cambios';
  openModalEdit('modal-promo');
}

async function delPromo(id) {
  const c = await Swal.fire({
    title: '¿Eliminar promoción?', icon: 'warning', showCancelButton: true,
    confirmButtonColor: 'var(--red)', cancelButtonColor: 'var(--muted)',
    confirmButtonText: 'Sí, eliminar', cancelButtonText: 'Cancelar'
  });
  if (!c.isConfirmed) return;
  try {
    const res  = await fetch(`/api/promociones/${id}`, { method: 'DELETE', credentials: 'include' });
    const json = await res.json();
    if (!res.ok) throw new Error(json.mensaje);
    Swal.fire({ icon: 'success', title: '¡Promoción eliminada!', confirmButtonColor: '#007bff', timer: 2500, timerProgressBar: true });
    await cargarPromociones();
  } catch (err) {
    Swal.fire({ icon: 'error', title: 'Error', text: err.message, confirmButtonColor: '#007bff' });
  }
}

// funciones de apoyo
function esVigente(p) {
  const hoy = new Date().toISOString().slice(0,10);
  return fmtISO(p.fechaInicio) <= hoy && hoy <= fmtISO(p.fechaFinal);
}

function fmtISO(v) {
  if (!v) return '';
  if (Array.isArray(v))
    return `${v[0]}-${String(v[1]).padStart(2,'0')}-${String(v[2]).padStart(2,'0')}`;
  return String(v).slice(0,10);
}

function fmtFecha(v) {
  const s = fmtISO(v);
  if (!s) return '—';
  const [y,m,d] = s.split('-');
  return `${d}/${m}/${y}`;
}

function esc(s) {
  return s ? String(s).replace(/&/g,'&amp;').replace(/</g,'&lt;').replace(/>/g,'&gt;') : '';
}