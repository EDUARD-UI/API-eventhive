document.addEventListener('DOMContentLoaded', () => {
  const id = new URLSearchParams(window.location.search).get('id');
  if (id) cargarBoletos(id);
  else mostrarVacio();
});

// GET /api/boletos/{compraId} → Compra con tiqueteCompras completos
async function cargarBoletos(id) {
  try {
    const res = await fetch(`/api/boletos/${id}`, { credentials: 'include' });
    if (res.status === 401 || res.status === 403) { window.location.href = '/login.html'; return; }
    if (!res.ok) { mostrarVacio(); return; }
    const json = await res.json();
    if (!json.success || !json.data) { mostrarVacio(); return; }
    renderBoletos(json.data);
  } catch { mostrarVacio(); }
}

//funcion de apoyo para renderizar el html del boleto
function renderBoletos(compra) {
  document.getElementById('skeleton').classList.add('hidden');
  document.getElementById('compraInfo').textContent =
    `Compra #${compra.id} · ${formatFechaCompra(compra.fechaCompra)}`;

  const tiquetes = compra.tiqueteCompras || [];
  if (!tiquetes.length) { mostrarVacio(); return; }

  const cont = document.getElementById('boletos');
  cont.classList.remove('hidden');
  document.getElementById('acciones').classList.remove('hidden');

  cont.innerHTML = tiquetes.map((tc, i) => {
    const t  = tc.tiquete || tc;
    const lo = t.localidad || {};
    const ev = lo.evento   || {};
    const qrId = `qr-${t.id != null ? t.id : i}`;
    return `
      <div class="boleto boleto-card rounded-3xl border border-gray-100 shadow-sm overflow-hidden">
        <div class="bg-brand px-8 py-5 flex items-center justify-between">
          <div>
            <p class="text-white/70 text-xs font-bold uppercase tracking-wider">Boleto #${i + 1}</p>
            <p class="text-white font-extrabold text-lg">${esc(ev.titulo || 'Evento')}</p>
          </div>
          <span class="bg-accent text-dark text-xs font-bold px-3 py-1.5 rounded-full">Válido</span>
        </div>
        <div class="px-8 py-6 flex flex-col md:flex-row gap-8 items-start">
          <div class="flex-1 space-y-3">
            <div class="flex items-center gap-3 text-sm text-dark/60">
              <i class="fas fa-couch text-brand w-4 text-center"></i>
              <span>Localidad: <strong class="text-dark">${esc(lo.nombre || '—')}</strong></span>
            </div>
            <div class="flex items-center gap-3 text-sm text-dark/60">
              <i class="far fa-calendar text-brand w-4 text-center"></i>
              <span>Fecha: <strong class="text-dark">${formatFecha(ev.fecha)}</strong></span>
            </div>
            <div class="flex items-center gap-3 text-sm text-dark/60">
              <i class="far fa-clock text-brand w-4 text-center"></i>
              <span>Hora: <strong class="text-dark">${formatHora(ev.hora)}</strong></span>
            </div>
            <div class="flex items-center gap-3 text-sm text-dark/60">
              <i class="fas fa-map-marker-alt text-brand w-4 text-center"></i>
              <span>${esc(ev.lugar || '—')}</span>
            </div>
            <div class="mt-4 pt-4 border-t border-gray-100">
              <p class="text-xs text-dark/30 font-bold uppercase tracking-wider mb-1">Código único</p>
              <p class="font-mono text-xs text-dark/50 break-all">${esc(t.codigoQR || '')}</p>
            </div>
          </div>
          <div class="shrink-0 text-center">
            <div id="${qrId}" class="rounded-2xl overflow-hidden border border-gray-100 p-2 bg-white"></div>
            <p class="text-xs text-dark/30 mt-2">Presenta este código QR en la entrada del evento</p>
          </div>
        </div>
        <div class="bg-gray-50 border-t border-gray-100 px-8 py-3 flex items-center gap-2 text-xs text-dark/30">
          <i class="fas fa-info-circle text-brand"></i>
          Evento organizado por EventHive · Cartagena, Colombia
        </div>
      </div>`;
  }).join('');

  // Generar QRs después de que el DOM esté listo
  tiquetes.forEach((tc, i) => {
    const t  = tc.tiquete || tc;
    const lo = t.localidad || {};
    const ev = lo.evento   || {};
    const qrId = `qr-${t.id != null ? t.id : i}`;
    const texto = `TICKET:${t.id}|EVENTO:${esc(ev.titulo || '')}|LOCAL:${esc(lo.nombre || '')}|QR:${t.codigoQR || ''}`;
    const el = document.getElementById(qrId);
    if (el) {
      new QRCode(el, {
        text: texto, width: 140, height: 140,
        colorDark: '#212529', colorLight: '#ffffff',
        correctLevel: QRCode.CorrectLevel.H
      });
    }
  });
}

function mostrarVacio() {
  document.getElementById('skeleton').classList.add('hidden');
  document.getElementById('vacio').classList.remove('hidden');
}

// funciones de apoyo
function formatFecha(v) {
  if (!v) return '—';
  if (Array.isArray(v)) {
    const [y, m, d] = v;
    return new Date(y, m - 1, d).toLocaleDateString('es-ES', { day: 'numeric', month: 'long', year: 'numeric' });
  }
  const d = new Date(v);
  return isNaN(d) ? String(v) : d.toLocaleDateString('es-ES', { day: 'numeric', month: 'long', year: 'numeric' });
}
function formatFechaCompra(v) {
  if (!v) return '';
  if (Array.isArray(v)) {
    const [y, m, d, h, mn] = v;
    return new Date(y, m - 1, d, h, mn).toLocaleDateString('es-ES', { day: 'numeric', month: 'short', year: 'numeric', hour: '2-digit', minute: '2-digit' });
  }
  return new Date(v).toLocaleDateString('es-ES', { day: 'numeric', month: 'short', year: 'numeric', hour: '2-digit', minute: '2-digit' });
}
function formatHora(v) {
  if (!v) return '—';
  if (Array.isArray(v)) { const [h, m] = v; return `${String(h).padStart(2, '0')}:${String(m).padStart(2, '0')}`; }
  return String(v).slice(0, 5);
}
function esc(s) {
  if (!s) return '';
  return String(s).replace(/&/g, '&amp;').replace(/</g, '&lt;').replace(/>/g, '&gt;').replace(/"/g, '&quot;');
}