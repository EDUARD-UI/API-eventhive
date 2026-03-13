// demo/src/main/resources/static/js/organizador/org-eventos.js

let _eventos = [];
let _vista = 'grid';

async function initEventos() {
  await Promise.all([cargarCategorias(), cargarEstados(), cargarEventos()]);
}

async function cargarEventos() {
  try {
    const res = await fetch('/api/eventos', { credentials: 'include' });
    const json = await res.json();
    _eventos = (json.data || []).filter(e =>
      ORG.usuario && e.usuario?.id === ORG.usuario.id
    );
    renderEventos(_eventos);
    document.getElementById('nb-ev').textContent = _eventos.length;
  } catch (err) {
    toast('Error cargando eventos: ' + err.message, 'err');
  }
}

async function cargarCategorias() {
  try {
    const res = await fetch('/api/categorias', { credentials: 'include' });
    const json = await res.json();
    const sel = document.getElementById('ev-cat');
    (json.data || []).forEach(c => {
      const o = document.createElement('option');
      o.value = c.id; o.textContent = c.nombre; sel.appendChild(o);
    });
  } catch {}
}

async function cargarEstados() {
  try {
    const res = await fetch('/api/estados', { credentials: 'include' });
    const json = await res.json();
    const sel = document.getElementById('ev-est');
    (json.data || []).forEach(e => {
      const o = document.createElement('option');
      o.value = e.id; o.textContent = e.nombre; sel.appendChild(o);
    });
  } catch {}
}

function filtrarEventos() {
  const q = document.getElementById('ev-srch').value.toLowerCase();
  const lista = _eventos.filter(e =>
    !q || e.titulo?.toLowerCase().includes(q) || e.lugar?.toLowerCase().includes(q)
  );
  renderEventos(lista);
}

function toggleVista(v) {
  _vista = v;
  document.getElementById('ev-grid').style.display  = v === 'grid'  ? 'grid'  : 'none';
  document.getElementById('ev-table').style.display = v === 'table' ? 'block' : 'none';
  document.getElementById('vt-grid').classList.toggle('active',  v === 'grid');
  document.getElementById('vt-table').classList.toggle('active', v === 'table');
}

function renderEventos(lista) {
  // Grid
  const grid = document.getElementById('ev-grid');
  if (!lista.length) {
    grid.innerHTML = `<div style="grid-column:1/-1;text-align:center;padding:40px;color:var(--muted)">
      <div style="font-size:2rem;margin-bottom:8px">🗓</div>
      <div>Sin eventos. <button class="btn btn-primary btn-sm" onclick="openModal('modal-evento')">Crear uno</button></div>
    </div>`;
  } else {
    grid.innerHTML = lista.map(ev => {
      const d = ev.fecha ? new Date(ev.fecha + 'T00:00:00') : null;
      const foto = ev.foto ? `/uploads/eventos/${ev.foto}` : null;
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
  }

  // Tabla
  const tbody = document.getElementById('ev-tbody');
  tbody.innerHTML = lista.length
    ? lista.map(ev => `
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
        </tr>`).join('')
    : '<tr><td colspan="6" style="text-align:center;color:var(--muted);padding:24px">Sin eventos</td></tr>';
}

/* ── CRUD ── */
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
    toast(id ? 'Evento actualizado ✓' : 'Evento creado ✓');
    closeModal('modal-evento');
    await cargarEventos();
  } catch (err) {
    toast(err.message, 'err');
  } finally {
    btn.textContent = document.getElementById('ev-id').value ? 'Guardar cambios' : 'Crear evento';
    btn.disabled = false;
  }
}

function editEvento(id) {
  const ev = _eventos.find(x => x.id == id);
  if (!ev) return;
  document.getElementById('ev-id').value     = ev.id;
  document.getElementById('ev-titulo').value = ev.titulo    || '';
  document.getElementById('ev-desc').value   = ev.descripcion || '';
  document.getElementById('ev-lugar').value  = ev.lugar     || '';
  document.getElementById('ev-fecha').value  = ev.fecha     || '';
  document.getElementById('ev-hora').value   = ev.hora ? String(ev.hora).slice(0,5) : '';
  document.getElementById('ev-cat').value    = ev.categoria?.id || '';
  document.getElementById('ev-est').value    = ev.estado?.id    || '';
  document.getElementById('mt-ev').textContent  = 'Editar evento';
  document.getElementById('btn-ev').textContent = 'Guardar cambios';
  openModal('modal-evento');
}

async function delEvento(id) {
  const confirm = await Swal.fire({
    title: '¿Eliminar evento?', text: 'Esta acción no se puede deshacer.',
    icon: 'warning', showCancelButton: true,
    confirmButtonColor: 'var(--red)', cancelButtonColor: 'var(--muted)',
    confirmButtonText: 'Sí, eliminar', cancelButtonText: 'Cancelar'
  });
  if (!confirm.isConfirmed) return;
  try {
    const res = await fetch(`/api/eventos/${id}`, { method: 'DELETE', credentials: 'include' });
    const json = await res.json();
    if (!res.ok) throw new Error(json.mensaje);
    toast('Evento eliminado ✓');
    await cargarEventos();
  } catch (err) {
    toast(err.message, 'err');
  }
}

function esc(s) {
  return s ? String(s).replace(/&/g,'&amp;').replace(/</g,'&lt;').replace(/>/g,'&gt;') : '';
}