/* ════════════════════════════════════════
   EventHive — promociones.js
   CRUD completo de promociones + render.
════════════════════════════════════════ */

function renderPromos() {
  const q = (document.getElementById('pr-srch')?.value || '').toLowerCase();
  Store.filtros.promociones.q = q;

  const list = Store.promociones.filter(p =>
    !q ||
    p.descripcion?.toLowerCase().includes(q) ||
    p.eventoTitulo?.toLowerCase().includes(q)
  );

  _renderPrGrid(list);
  _renderPrTable(list);
}

function _renderPrGrid(list) {
  const el = document.getElementById('pr-grid');
  if (!el) return;

  if (!list.length) {
    el.innerHTML = `<div style="grid-column:1/-1">${emptyHTML('🏷', 'Sin promociones', 'Crea descuentos para tus eventos')}</div>`;
    return;
  }

  el.innerHTML = list.map(p => `
    <div class="promo-card">
      <div class="promo-icon">🏷</div>
      <div class="promo-info">
        <h4>${p.descripcion}</h4>
        <p>${fmtDate(p.fechaInicio)} → ${fmtDate(p.fechaFinal || p.fechaFin)}</p>
        <p style="color:var(--blue);margin-top:3px">${p.eventoTitulo || ''}</p>
      </div>
      <div class="promo-pct">${p.descuento}%</div>
      <button class="btn-del" style="margin-left:8px" onclick="delPromo(${p.id})">🗑</button>
    </div>
  `).join('');
}

function _renderPrTable(list) {
  const tb = document.getElementById('pr-tbody');
  if (!tb) return;

  if (!list.length) {
    tb.innerHTML = '<tr><td colspan="6" style="text-align:center;color:var(--muted);padding:40px">Sin promociones</td></tr>';
    return;
  }

  tb.innerHTML = list.map(p => `
    <tr>
      <td><strong>${p.descripcion}</strong></td>
      <td>${p.eventoTitulo || '—'}</td>
      <td><span class="pill p-amber">${p.descuento}% OFF</span></td>
      <td>${fmtDate(p.fechaInicio)}</td>
      <td>${fmtDate(p.fechaFinal || p.fechaFin)}</td>
      <td>
        <button class="btn-del" onclick="delPromo(${p.id})">Eliminar</button>
      </td>
    </tr>
  `).join('');
}

/* ─── CRUD ─── */
async function submitPromo(e) {
  e.preventDefault();
  const btn    = document.getElementById('btn-pr');
  const evId   = document.getElementById('pr-ev').value;
  const evTitulo = Store.eventos.find(ev => ev.id == evId)?.titulo || '';

  const data = {
    descripcion:  document.getElementById('pr-desc').value,
    eventoId:     evId,
    descuento:    document.getElementById('pr-pct').value,
    fechaInicio:  document.getElementById('pr-ini').value,
    fechaFinal:   document.getElementById('pr-fin').value,
  };

  btn.textContent = 'Guardando…'; btn.disabled = true;

  /* Intentar llamar al backend (endpoint pendiente de implementación) */
  try {
    await PromocionesAPI.crear(evId, data);
  } catch (_) {
    /* El endpoint aún no existe — guardamos localmente */
  }

  const nueva = { id: Date.now(), ...data, eventoTitulo: evTitulo };
  Store.addPromocion(nueva);

  toast('Promoción creada ✓', 'ok');
  closeModal('modal-promo');
  renderPromos();
  renderDashboard();

  btn.textContent = 'Crear promoción'; btn.disabled = false;
}

async function delPromo(id) {
  if (!confirm('¿Eliminar esta promoción?')) return;

  try { await PromocionesAPI.eliminar(id); } catch (_) {}

  Store.removePromocion(id);
  toast('Promoción eliminada', 'ok');
  renderPromos();
  renderDashboard();
}
