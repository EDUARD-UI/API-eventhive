/* ════════════════════════════════════════
   EventHive — localidades.js
   CRUD completo de localidades + render.
════════════════════════════════════════ */

function renderLocalidades() {
  const q    = (document.getElementById('loc-srch')?.value || '').toLowerCase();
  Store.filtros.localidades.q = q;

  const list = Store.localidades.filter(l =>
    !q || l.nombre?.toLowerCase().includes(q) || l.eventoTitulo?.toLowerCase().includes(q)
  );

  _renderLocGrid(list);
  _renderLocTable(list);
}

function _renderLocGrid(list) {
  const el = document.getElementById('loc-grid');
  if (!el) return;

  if (!list.length) {
    el.innerHTML = `<div style="grid-column:1/-1">${emptyHTML('🏟', 'Sin localidades', 'Añade zonas a tus eventos')}</div>`;
    return;
  }

  el.innerHTML = list.map(l => {
    const ocu = _ocuPct(l);
    const col = _ocuColor(ocu);
    return `
      <div class="loc-card">
        <div class="loc-card-accent"></div>
        <h4>${l.nombre}</h4>
        <div class="loc-ev-ref">📅 ${l.eventoTitulo || '—'}</div>
        <div class="loc-stats">
          <div class="ls-item">
            <label>Capacidad</label>
            <span>${l.capacidad || '—'}</span>
          </div>
          <div class="ls-item">
            <label>Disponibles</label>
            <span style="color:${(l.disponibles || 0) > 0 ? 'var(--green)' : 'var(--red)'}">
              ${l.disponibles ?? 0}
            </span>
          </div>
          <div class="ls-item">
            <label>Precio</label>
            <span style="color:var(--blue)">${fmtCurrency(l.precio)}</span>
          </div>
        </div>
        <div class="loc-prog-wrap">
          <div class="prog-track">
            <div class="prog-fill" style="width:${ocu}%;background:${col}"></div>
          </div>
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
}

function _renderLocTable(list) {
  const tb = document.getElementById('loc-tbody');
  if (!tb) return;

  if (!list.length) {
    tb.innerHTML = '<tr><td colspan="7" style="text-align:center;color:var(--muted);padding:40px">Sin localidades</td></tr>';
    return;
  }

  tb.innerHTML = list.map(l => {
    const ocu = _ocuPct(l);
    const col = _ocuColor(ocu);
    return `
      <tr>
        <td><strong>${l.nombre}</strong></td>
        <td>${l.eventoTitulo || '—'}</td>
        <td>${l.capacidad || '—'}</td>
        <td><span class="pill ${(l.disponibles || 0) > 0 ? 'p-green' : 'p-red'}">${l.disponibles ?? 0}</span></td>
        <td>${fmtCurrency(l.precio)}</td>
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

/* ─── CRUD ─── */
async function submitLocalidad(e) {
  e.preventDefault();
  const id  = document.getElementById('loc-id').value;
  const btn = document.getElementById('btn-loc');

  const data = {
    nombre:      document.getElementById('loc-nom').value,
    eventoId:    document.getElementById('loc-ev').value,
    capacidad:   document.getElementById('loc-cap').value,
    disponibles: document.getElementById('loc-dis').value,
    precio:      document.getElementById('loc-pre').value,
  };

  btn.textContent = 'Guardando…'; btn.disabled = true;

  try {
    id
      ? await LocalidadesAPI.actualizar(id, data)
      : await LocalidadesAPI.crear(data);

    toast(id ? 'Localidad actualizada ✓' : 'Localidad creada ✓', 'ok');
    closeModal('modal-localidad');
    await reloadLocalidades();
    renderLocalidades();
    renderDashboard();

  } catch (err) {
    toast(err.message, 'err');
  } finally {
    btn.textContent = id ? 'Guardar cambios' : 'Crear localidad';
    btn.disabled = false;
  }
}

function editLoc(id) {
  const l = Store.localidades.find(x => x.id == id);
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
  if (!confirm('¿Eliminar esta localidad?')) return;
  try {
    await LocalidadesAPI.eliminar(id);
    Store.removeLocalidad(id);
    toast('Localidad eliminada', 'ok');
    renderLocalidades();
    renderDashboard();
  } catch (err) {
    toast(err.message, 'err');
  }
}

/* ─── Helpers ─── */
function _ocuPct(l) {
  if (!l.capacidad || l.capacidad <= 0) return 0;
  return Math.round(((l.capacidad - (l.disponibles || 0)) / l.capacidad) * 100);
}
function _ocuColor(pct) {
  if (pct >= 80) return 'var(--red)';
  if (pct >= 50) return 'var(--amber-d)';
  return 'var(--green)';
}

/* ─── Reload ─── */
async function reloadLocalidades() {
  try {
    const results = await Promise.allSettled(
      Store.eventos.map(ev => LocalidadesAPI.listarPorEvento(ev.id))
    );
    Store.setLocalidades(
      results.flatMap((r, i) =>
        r.status === 'fulfilled'
          ? (r.value?.data || []).map(l => ({
              ...l,
              eventoTitulo: Store.eventos[i]?.titulo || '—',
            }))
          : []
      )
    );
  } catch (err) {
    console.warn('Error recargando localidades:', err);
  }
}
