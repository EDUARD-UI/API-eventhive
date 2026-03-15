let _localidades = [];
let _localidadesFiltradas = [];
let _vistaLoc = 'grid';
const LOC_CARDS_PAG = 8;
const LOC_FILAS_PAG = 8;
let _locPagGrid = 1;
let _locPagTable = 1;

async function initLocalidades() {
  await Promise.all([cargarEventosDropdownLoc(), cargarLocalidades()]);
}

// CARDS/TABLE: endpoint del organizador logueado
async function cargarLocalidades() {
  try {
    const res  = await fetch('/api/localidades/organizador', { credentials: 'include' });
    const json = await res.json();
    _localidades = json.data || [];
    _localidadesFiltradas = [..._localidades];
    _locPagGrid = _locPagTable = 1;
    renderLocalidades();
  } catch (err) {
    Swal.fire({ icon: 'error', title: 'Error', text: 'No se pudo cargar las localidades: ' + err.message, confirmButtonColor: '#007bff' });
  }
}

// DROPDOWN del modal: solo id+titulo de eventos del organizador logueado
async function cargarEventosDropdownLoc() {
  try {
    const res  = await fetch('/api/eventos/organizador/nombres-Eventos', { credentials: 'include' });
    const json = await res.json();
    const sel  = document.getElementById('loc-ev');
    if (!sel) return;
    (json.data || []).forEach(e => {
      const o = document.createElement('option');
      o.value = e.id; o.textContent = e.titulo; sel.appendChild(o);
    });
  } catch {}
}

function filtrarLocalidades() {
  const el = document.getElementById('loc-srch');
  const q  = el ? el.value.toLowerCase() : '';
  _localidadesFiltradas = _localidades.filter(l =>
    !q || l.nombre?.toLowerCase().includes(q) || l.evento?.titulo?.toLowerCase().includes(q)
  );
  _locPagGrid = _locPagTable = 1;
  renderLocalidades();
}

function toggleVistaLoc(v) {
  _vistaLoc = v;
  const locGrid     = document.getElementById('loc-grid');
  const locPagGrid  = document.getElementById('loc-pag-grid');
  const locTable    = document.getElementById('loc-table');
  const locPagTable = document.getElementById('loc-pag-table');
  const vtGrid      = document.getElementById('vt-grid-loc');
  const vtTable     = document.getElementById('vt-table-loc');
  if (locGrid)     locGrid.style.display     = v === 'grid'  ? 'grid'  : 'none';
  if (locPagGrid)  locPagGrid.style.display  = v === 'grid'  ? 'block' : 'none';
  if (locTable)    locTable.style.display    = v === 'table' ? 'block' : 'none';
  if (locPagTable) locPagTable.style.display = v === 'table' ? 'block' : 'none';
  if (vtGrid)      vtGrid.classList.toggle('active',  v === 'grid');
  if (vtTable)     vtTable.classList.toggle('active', v === 'table');
}

function renderLocalidades() {
  renderGridLocalidades();
  renderTablaLocalidades();
}

function renderGridLocalidades() {
  const grid    = document.getElementById('loc-grid');
  const pagWrap = document.getElementById('loc-pag-grid');
  if (!grid || !pagWrap) return;

  const total    = _localidadesFiltradas.length;
  const totalPag = Math.ceil(total / LOC_CARDS_PAG);

  if (!total) {
    grid.innerHTML = `<div style="grid-column:1/-1;text-align:center;padding:40px;color:var(--muted)">
      <div style="font-size:2rem;margin-bottom:8px">🏟</div>
      <div>Sin localidades. <button class="btn btn-primary btn-sm" onclick="openModal('modal-localidad')">Crear una</button></div>
    </div>`;
    pagWrap.innerHTML = '';
    return;
  }

  const slice = _localidadesFiltradas.slice((_locPagGrid-1)*LOC_CARDS_PAG, _locPagGrid*LOC_CARDS_PAG);
  grid.innerHTML = slice.map(l => {
    const ocu = l.capacidad > 0 ? Math.round(((l.capacidad-(l.disponibles||0))/l.capacidad)*100) : 0;
    const col = ocu >= 80 ? 'var(--red)' : ocu >= 50 ? 'var(--amber-d)' : 'var(--green)';
    return `
      <div class="loc-card">
        <div class="loc-card-accent"></div>
        <h4>${esc(l.nombre)}</h4>
        <div class="loc-ev-ref">📅 ${esc(l.evento?.titulo||'—')}</div>
        <div class="loc-stats">
          <div class="ls-item"><label>Capacidad</label><span>${l.capacidad||'—'}</span></div>
          <div class="ls-item"><label>Disponibles</label>
            <span style="color:${(l.disponibles||0)>0?'var(--green)':'var(--red)'}">${l.disponibles??0}</span>
          </div>
          <div class="ls-item"><label>Precio</label>
            <span style="color:var(--blue)">$${Number(l.precio||0).toLocaleString('es-CO')}</span>
          </div>
        </div>
        <div class="loc-prog-wrap">
          <div class="prog-track"><div class="prog-fill" style="width:${ocu}%;background:${col}"></div></div>
        </div>
        <div class="loc-foot-row">
          <span style="font-size:.74rem;color:var(--muted)">${ocu}% ocupado</span>
          <div style="display:flex;gap:6px">
            <button class="btn btn-ghost btn-sm" onclick="editLoc(${l.id})">Editar</button>
            <button class="btn-del" onclick="delLoc(${l.id})">Eliminar</button>
          </div>
        </div>
      </div>`;
  }).join('');

  pagWrap.innerHTML = buildPagHTML(totalPag, _locPagGrid, 'cambiarPagLocGrid');
}

function renderTablaLocalidades() {
  const tbody   = document.getElementById('loc-tbody');
  const pagWrap = document.getElementById('loc-pag-table');
  if (!tbody || !pagWrap) return;

  const total    = _localidadesFiltradas.length;
  const totalPag = Math.ceil(total / LOC_FILAS_PAG);

  if (!total) {
    tbody.innerHTML = '<tr><td colspan="7" style="text-align:center;color:var(--muted);padding:24px">Sin localidades</td></tr>';
    pagWrap.innerHTML = '';
    return;
  }

  const slice = _localidadesFiltradas.slice((_locPagTable-1)*LOC_FILAS_PAG, _locPagTable*LOC_FILAS_PAG);
  tbody.innerHTML = slice.map(l => {
    const ocu = l.capacidad > 0 ? Math.round(((l.capacidad-(l.disponibles||0))/l.capacidad)*100) : 0;
    const col = ocu >= 80 ? 'var(--red)' : ocu >= 50 ? 'var(--amber-d)' : 'var(--green)';
    return `
      <tr>
        <td><strong>${esc(l.nombre)}</strong></td>
        <td>${esc(l.evento?.titulo||'—')}</td>
        <td>${l.capacidad||'—'}</td>
        <td><span class="pill ${(l.disponibles||0)>0?'p-green':'p-red'}">${l.disponibles??0}</span></td>
        <td>$${Number(l.precio||0).toLocaleString('es-CO')}</td>
        <td>
          <div style="display:flex;align-items:center;gap:8px">
            <div style="flex:1;height:5px;background:var(--surface2);border-radius:3px;overflow:hidden">
              <div style="height:100%;width:${ocu}%;background:${col};border-radius:3px"></div>
            </div>
            <span style="font-size:.72rem;color:var(--muted);min-width:30px">${ocu}%</span>
          </div>
        </td>
        <td>
          <div style="display:flex;gap:6px">
            <button class="btn btn-ghost btn-sm" onclick="editLoc(${l.id})">Editar</button>
            <button class="btn-del" onclick="delLoc(${l.id})">Eliminar</button>
          </div>
        </td>
      </tr>`;
  }).join('');

  pagWrap.innerHTML = buildPagHTML(totalPag, _locPagTable, 'cambiarPagLocTable');
}

function cambiarPagLocGrid(p)  { _locPagGrid  = p; renderGridLocalidades();  }
function cambiarPagLocTable(p) { _locPagTable = p; renderTablaLocalidades(); }

async function submitLocalidad(e) {
  e.preventDefault();
  const id  = document.getElementById('loc-id').value;
  const btn = document.getElementById('btn-loc');
  const data = new URLSearchParams({
    nombre:      document.getElementById('loc-nom').value,
    eventoId:    document.getElementById('loc-ev').value,
    capacidad:   document.getElementById('loc-cap').value,
    disponibles: document.getElementById('loc-dis').value,
    precio:      document.getElementById('loc-pre').value,
  });

  btn.textContent = 'Guardando…'; btn.disabled = true;
  try {
    const res  = await fetch(id ? `/api/localidades/${id}` : '/api/localidades',
      { method: id ? 'PUT' : 'POST', credentials: 'include',
        headers: { 'Content-Type': 'application/x-www-form-urlencoded' }, body: data });
    const json = await res.json();
    if (!res.ok) throw new Error(json.mensaje || 'Error al guardar');
    Swal.fire({ icon: 'success', title: id ? '¡Localidad actualizada!' : '¡Localidad creada!',
      text: 'Los cambios se han guardado correctamente.', confirmButtonColor: '#007bff', timer: 3000, timerProgressBar: true });
    closeModal('modal-localidad');
    await cargarLocalidades();
  } catch (err) {
    Swal.fire({ icon: 'error', title: 'Error', text: err.message, confirmButtonColor: '#007bff' });
  } finally {
    btn.textContent = document.getElementById('loc-id').value ? 'Guardar cambios' : 'Crear localidad';
    btn.disabled = false;
  }
}

function editLoc(id) {
  const l = _localidades.find(x => x.id == id);
  if (!l) return;
  document.getElementById('loc-id').value  = l.id;
  document.getElementById('loc-nom').value = l.nombre      || '';
  document.getElementById('loc-ev').value  = l.evento?.id  || '';
  document.getElementById('loc-cap').value = l.capacidad   || '';
  document.getElementById('loc-dis').value = l.disponibles || '';
  document.getElementById('loc-pre').value = l.precio      || '';
  document.getElementById('mt-loc').textContent  = 'Editar localidad';
  document.getElementById('btn-loc').textContent = 'Guardar cambios';
  openModalEdit('modal-localidad');
}

async function delLoc(id) {
  const c = await Swal.fire({
    title: '¿Eliminar localidad?', icon: 'warning', showCancelButton: true,
    confirmButtonColor: 'var(--red)', cancelButtonColor: 'var(--muted)',
    confirmButtonText: 'Sí, eliminar', cancelButtonText: 'Cancelar'
  });
  if (!c.isConfirmed) return;
  try {
    const res  = await fetch(`/api/localidades/${id}`, { method: 'DELETE', credentials: 'include' });
    const json = await res.json();
    if (!res.ok) throw new Error(json.mensaje);
    Swal.fire({ icon: 'success', title: '¡Localidad eliminada!', confirmButtonColor: '#007bff', timer: 2500, timerProgressBar: true });
    await cargarLocalidades();
  } catch (err) {
    Swal.fire({ icon: 'error', title: 'Error', text: err.message, confirmButtonColor: '#007bff' });
  }
}

function esc(s) {
  return s ? String(s).replace(/&/g,'&amp;').replace(/</g,'&lt;').replace(/>/g,'&gt;') : '';
}