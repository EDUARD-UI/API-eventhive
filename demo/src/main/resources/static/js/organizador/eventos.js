/* ════════════════════════════════════════
   EventHive — eventos.js
   CRUD completo de eventos + render.
════════════════════════════════════════ */

/* ─── Category chips ─── */
function buildCatChips() {
  const bar   = document.getElementById('cat-bar');
  const ccAll = document.getElementById('cc-all');
  if (!bar) return;

  /* Limpiar chips dinámicos anteriores (dejar solo "Todos") */
  bar.querySelectorAll('.chip:not([data-cat="all"]), .chip-divider').forEach(el => el.remove());
  if (ccAll) ccAll.textContent = Store.eventos.length;

  /* Contar por categoría */
  const counts = {};
  Store.eventos.forEach(ev => {
    const cat = ev.categoria?.nombre || 'Sin categoría';
    counts[cat] = (counts[cat] || 0) + 1;
  });

  if (Object.keys(counts).length === 0) return;

  /* Divider */
  const dv = document.createElement('span');
  dv.className = 'chip-divider';
  dv.style.cssText = 'width:1px;height:22px;background:var(--border);margin:0 2px;flex-shrink:0';
  bar.appendChild(dv);

  /* Chips */
  Object.entries(counts).forEach(([cat, cnt]) => {
    const btn = document.createElement('button');
    btn.className    = 'chip';
    btn.dataset.cat  = cat;
    btn.innerHTML    = `${cat} <span class="cc">${cnt}</span>`;
    btn.addEventListener('click', () => selCat(btn));
    bar.appendChild(btn);
  });
}

/* ─── Render ─── */
function renderEventos() {
  const { q, categoria } = Store.filtros.eventos;

  const list = Store.eventos.filter(ev => {
    const matchCat = categoria === 'all' || (ev.categoria?.nombre || 'Sin categoría') === categoria;
    const matchQ   = !q || ev.titulo?.toLowerCase().includes(q) || ev.lugar?.toLowerCase().includes(q);
    return matchCat && matchQ;
  });

  _renderEvGrid(list);
  _renderEvTable(list);
}

function _renderEvGrid(list) {
  const el = document.getElementById('ev-grid');
  if (!el) return;

  if (!list.length) {
    el.innerHTML = `<div style="grid-column:1/-1">${emptyHTML('🔍', 'Sin resultados', 'Prueba con otro filtro o búsqueda')}</div>`;
    return;
  }
  el.innerHTML = list.map(_evCard).join('');
}

function _renderEvTable(list) {
  const tb = document.getElementById('ev-tbody');
  if (!tb) return;

  if (!list.length) {
    tb.innerHTML = '<tr><td colspan="6" style="text-align:center;color:var(--muted);padding:40px">Sin eventos</td></tr>';
    return;
  }

  tb.innerHTML = list.map(ev => `
    <tr>
      <td><strong>${ev.titulo}</strong></td>
      <td><span class="pill p-blue">${ev.categoria?.nombre || '—'}</span></td>
      <td>${fmtDate(ev.fecha)}</td>
      <td>${ev.lugar || '—'}</td>
      <td><span class="pill p-green">${ev.estado?.nombre || '—'}</span></td>
      <td>
        <div style="display:flex;gap:6px">
          <button class="btn btn-ghost btn-sm" onclick="editEvento(${ev.id})">Editar</button>
          <button class="btn-del" onclick="delEvento(${ev.id})">Eliminar</button>
        </div>
      </td>
    </tr>
  `).join('');
}

function _evCard(ev) {
  const d   = ev.fecha ? new Date(ev.fecha + 'T00:00:00') : null;
  const day = d ? d.getDate() : '—';
  const mon = d ? d.toLocaleDateString('es-CO', { month: 'short' }).toUpperCase() : '';
  const foto = ev.foto ? `/uploads/eventos/${ev.foto}` : null;

  return `
    <div class="ev-card">
      <div class="ev-thumb">
        ${foto ? `<img src="${foto}" alt="${ev.titulo}" onerror="this.style.display='none'"/>` : ''}
        <span class="ev-ph">🗓</span>
        <span class="ev-cat-b">${ev.categoria?.nombre || 'Evento'}</span>
        <div class="ev-dp">
          <div class="ev-dd">${day}</div>
          <div class="ev-dm">${mon}</div>
        </div>
      </div>
      <div class="ev-body">
        <div class="ev-title">${ev.titulo}</div>
        <div class="ev-meta">
          <span>📍 ${ev.lugar || '—'}</span>
          <span>🕐 ${ev.hora || '—'}</span>
        </div>
        <span class="pill p-green">${ev.estado?.nombre || '—'}</span>
        <div class="ev-foot">
          <button class="btn btn-ghost btn-sm" style="flex:1" onclick="navigate('localidades')">Localidades</button>
          <button class="btn btn-ghost btn-sm" onclick="editEvento(${ev.id})">✏️</button>
          <button class="btn-del" onclick="delEvento(${ev.id})">🗑</button>
        </div>
      </div>
    </div>`;
}

/* ─── CRUD ─── */
async function submitEvento(e) {
  e.preventDefault();
  const id  = document.getElementById('ev-id').value;
  const btn = document.getElementById('btn-ev');

  const fd = new FormData();
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
    id
      ? await EventosAPI.actualizar(id, fd)
      : await EventosAPI.crear(fd);

    toast(id ? 'Evento actualizado ✓' : 'Evento creado ✓', 'ok');
    closeModal('modal-evento');
    await reloadEventos();
    await reloadLocalidades();
    renderDashboard();
    renderEventos();
    buildCatChips();
    updateNavBadge(Store.eventos.length);

  } catch (err) {
    toast(err.message, 'err');
  } finally {
    btn.textContent = id ? 'Guardar cambios' : 'Crear evento';
    btn.disabled = false;
  }
}

function editEvento(id) {
  const ev = Store.eventos.find(x => x.id == id);
  if (!ev) return;

  document.getElementById('ev-id').value       = ev.id;
  document.getElementById('ev-titulo').value   = ev.titulo    || '';
  document.getElementById('ev-desc').value     = ev.descripcion || '';
  document.getElementById('ev-lugar').value    = ev.lugar     || '';
  document.getElementById('ev-fecha').value    = ev.fecha     || '';
  document.getElementById('ev-hora').value     = ev.hora      || '';
  document.getElementById('ev-cat').value      = ev.categoria?.id || '';
  document.getElementById('ev-est').value      = ev.estado?.id    || '';

  document.getElementById('mt-ev').textContent  = 'Editar evento';
  document.getElementById('btn-ev').textContent = 'Guardar cambios';
  openModal('modal-evento');
}

async function delEvento(id) {
  if (!confirm('¿Eliminar este evento? Esta acción no se puede deshacer.')) return;
  try {
    await EventosAPI.eliminar(id);
    Store.removeEvento(id);
    toast('Evento eliminado', 'ok');
    renderDashboard();
    renderEventos();
    buildCatChips();
    updateNavBadge(Store.eventos.length);
  } catch (err) {
    toast(err.message, 'err');
  }
}

/* ─── Reload helpers ─── */
async function reloadEventos() {
  const r = await EventosAPI.listar();
  Store.setEventos(r.data || []);
  fillSelectEventos();
}

async function reloadLocalidades() {
  const results = await Promise.allSettled(
    Store.eventos.map(ev => LocalidadesAPI.listarPorEvento(ev.id))
  );
  Store.setLocalidades(
    results.flatMap((r, i) =>
      r.status === 'fulfilled'
        ? (r.value.data || []).map(l => ({ ...l, eventoTitulo: Store.eventos[i]?.titulo || '—' }))
        : []
    )
  );
}
