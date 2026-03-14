let _localidades = [];
let _vistaLoc = 'grid';

async function initLocalidades() {
  await Promise.all([cargarEventosDropdown(), cargarLocalidades()]);
}

async function cargarLocalidades() {
  try {
    const resEv  = await fetch('/api/eventos/nombres-Eventos', { credentials: 'include' });
    const jsonEv = await resEv.json();
    const idsOrg = new Set((jsonEv.data || []).map(e => String(e.id)));

    const res  = await fetch('/api/localidades', { credentials: 'include' });
    const json = await res.json();
    _localidades = (json.data || []).filter(l => idsOrg.has(String(l.evento?.id)));
    renderLocalidades(_localidades);
  } catch (err) {
    Swal.fire({ icon: 'error', title: 'Error', text: 'No se pudo cargar las localidades: ' + err.message, confirmButtonColor: '#007bff' });
  }
}

async function cargarEventosDropdown() {
  try {
    const res  = await fetch('api/eventos/nombres-Eventos', { credentials: 'include' });
    const json = await res.json();
    const sel  = document.getElementById('loc-ev');
    (json.data || []).forEach(e => {
      const o = document.createElement('option');
      o.value = e.id; o.textContent = e.titulo; sel.appendChild(o);
    });
  } catch {}
}

function filtrarLocalidades() {
  const q = document.getElementById('loc-srch').value.toLowerCase();
  renderLocalidades(_localidades.filter(l =>
    !q || l.nombre?.toLowerCase().includes(q) || l.evento?.titulo?.toLowerCase().includes(q)
  ));
}

function toggleVistaLoc(v) {
  _vistaLoc = v;
  document.getElementById('loc-grid').style.display  = v === 'grid'  ? 'grid'  : 'none';
  document.getElementById('loc-table').style.display = v === 'table' ? 'block' : 'none';
  document.getElementById('vt-grid-loc').classList.toggle('active',  v === 'grid');
  document.getElementById('vt-table-loc').classList.toggle('active', v === 'table');
}

function renderLocalidades(lista) {
  const grid  = document.getElementById('loc-grid');
  const tbody = document.getElementById('loc-tbody');

  if (!lista.length) {
    grid.innerHTML = `<div style="grid-column:1/-1;text-align:center;padding:40px;color:var(--muted)">
      <div style="font-size:2rem;margin-bottom:8px">🏟</div>
      <div>Sin localidades. <button class="btn btn-primary btn-sm" onclick="openModal('modal-localidad')">Crear una</button></div>
    </div>`;
    tbody.innerHTML = '<tr><td colspan="7" style="text-align:center;color:var(--muted);padding:24px">Sin localidades</td></tr>';
    return;
  }

  grid.innerHTML = lista.map(l => {
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

  tbody.innerHTML = lista.map(l => {
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
}

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
    Swal.fire({
      icon: 'success',
      title: id ? '¡Localidad actualizada!' : '¡Localidad creada!',
      text: 'Los cambios se han guardado correctamente.',
      confirmButtonColor: '#007bff',
      timer: 3000,
      timerProgressBar: true
    });
    closeModal('modal-localidad');
    await cargarLocalidades();
  } catch (err) { 
    Swal.fire({ icon: 'error', title: 'Error', text: err.message, confirmButtonColor: '#007bff' });
  }
  finally {
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
  openModal('modal-localidad');
}

async function delLoc(id) {
  const c = await Swal.fire({
    title:'¿Eliminar localidad?', icon:'warning', showCancelButton:true,
    confirmButtonColor:'var(--red)', cancelButtonColor:'var(--muted)',
    confirmButtonText:'Sí, eliminar', cancelButtonText:'Cancelar'
  });
  if (!c.isConfirmed) return;
  try {
    const res  = await fetch(`/api/localidades/${id}`, { method:'DELETE', credentials:'include' });
    const json = await res.json();
    if (!res.ok) throw new Error(json.mensaje);
    Swal.fire({
      icon: 'success',
      title: '¡Localidad eliminada!',
      text: 'La localidad ha sido eliminada correctamente.',
      confirmButtonColor: '#007bff',
      timer: 2500,
      timerProgressBar: true
    });
    await cargarLocalidades();
  } catch (err) { 
    Swal.fire({ icon: 'error', title: 'Error', text: err.message, confirmButtonColor: '#007bff' });
  }
}

function esc(s) {
  return s ? String(s).replace(/&/g,'&amp;').replace(/</g,'&lt;').replace(/>/g,'&gt;') : '';
}