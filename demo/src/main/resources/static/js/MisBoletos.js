document.addEventListener('DOMContentLoaded', () => {
  const id = new URLSearchParams(window.location.search).get('id');
  if (id) cargarBoletos(id);
  else mostrarVacio();
});

// GET /api/pagos/{id} → compra con sus tiquetes
// Compra: { id, fechaCompra, total, metodoPago, tiqueteCompras:[{tiquete:{id,codigoQR,localidad:{nombre,evento:{titulo,fecha,hora,lugar}}}}] }
async function cargarBoletos(id) {
  try {
    const res = await fetch(`/api/pagos/${id}`, { credentials: 'include' });
    if (!res.ok) { mostrarVacio(); return; }
    const json = await res.json();
    const compra = json.data;
    if (!compra) { mostrarVacio(); return; }
    renderBoletos(compra);
  } catch { mostrarVacio(); }
}

function renderBoletos(compra) {
  document.getElementById('skeleton').classList.add('hidden');
  document.getElementById('compraInfo').textContent =
    `Compra #${compra.id} · ${formatFecha(compra.fechaCompra)}`;

  const tiquetes = compra.tiqueteCompras || [];
  if (!tiquetes.length) { mostrarVacio(); return; }

  const cont = document.getElementById('boletos');
  cont.classList.remove('hidden');
  document.getElementById('acciones').classList.remove('hidden');

  cont.innerHTML = tiquetes.map((tc, i) => {
    const t  = tc.tiquete || tc;
    const lo = t.localidad || {};
    const ev = lo.evento   || {};
    const id = `qr-${t.id || i}`;
    return `
      <div class="boleto boleto-card rounded-3xl border border-gray-100 shadow-sm overflow-hidden">
        <!-- header -->
        <div class="bg-brand px-8 py-5 flex items-center justify-between">
          <div>
            <p class="text-white/70 text-xs font-bold uppercase tracking-wider">Boleto #${i+1}</p>
            <p class="text-white font-extrabold text-lg">${esc(ev.titulo||'Evento')}</p>
          </div>
          <span class="bg-accent text-dark text-xs font-bold px-3 py-1.5 rounded-full">Válido</span>
        </div>
        <!-- info + qr -->
        <div class="px-8 py-6 flex flex-col md:flex-row gap-8 items-start">
          <div class="flex-1 space-y-3">
            <div class="flex items-center gap-3 text-sm text-dark/60">
              <i class="fas fa-couch text-brand w-4 text-center"></i>
              <span>Localidad: <strong class="text-dark">${esc(lo.nombre||'—')}</strong></span>
            </div>
            <div class="flex items-center gap-3 text-sm text-dark/60">
              <i class="far fa-calendar text-brand w-4 text-center"></i>
              <span>${formatFecha(ev.fecha)}</span>
            </div>
            <div class="flex items-center gap-3 text-sm text-dark/60">
              <i class="far fa-clock text-brand w-4 text-center"></i>
              <span>${formatHora(ev.hora)}</span>
            </div>
            <div class="flex items-center gap-3 text-sm text-dark/60">
              <i class="fas fa-map-marker-alt text-brand w-4 text-center"></i>
              <span>${esc(ev.lugar||'—')}</span>
            </div>
            <div class="mt-4 pt-4 border-t border-gray-100">
              <p class="text-xs text-dark/30 font-bold uppercase tracking-wider mb-1">Código QR</p>
              <p class="font-mono text-xs text-dark/50 break-all">${esc(t.codigoQR||'')}</p>
            </div>
          </div>
          <div class="shrink-0 text-center">
            <div id="${id}" class="rounded-2xl overflow-hidden border border-gray-100 p-2 bg-white"></div>
            <p class="text-xs text-dark/30 mt-2">Presenta en la entrada</p>
          </div>
        </div>
        <!-- footer -->
        <div class="bg-gray-50 border-t border-gray-100 px-8 py-3 flex items-center gap-2 text-xs text-dark/30">
          <i class="fas fa-info-circle text-brand"></i>
          Evento organizado por EventHive · Cartagena, Colombia
        </div>
      </div>`;
  }).join('');

  // generar QRs
  tiquetes.forEach((tc, i) => {
    const t  = tc.tiquete || tc;
    const lo = t.localidad || {};
    const ev = lo.evento   || {};
    const texto = `TICKET:${t.id}|EVENTO:${ev.titulo||''}|LOCAL:${lo.nombre||''}|QR:${t.codigoQR||''}`;
    new QRCode(document.getElementById(`qr-${t.id||i}`), {
      text: texto, width: 140, height: 140,
      colorDark: '#212529', colorLight: '#ffffff',
      correctLevel: QRCode.CorrectLevel.H
    });
  });
}

function mostrarVacio() {
  document.getElementById('skeleton').classList.add('hidden');
  document.getElementById('vacio').classList.remove('hidden');
}

function formatFecha(v) {
  if (!v) return '—';
  if (Array.isArray(v)) { const [y,m,d]=v; return new Date(y,m-1,d).toLocaleDateString('es-ES',{day:'numeric',month:'long',year:'numeric'}); }
  const d = new Date(v);
  return isNaN(d) ? String(v) : d.toLocaleDateString('es-ES',{day:'numeric',month:'long',year:'numeric'});
}
function formatHora(v) {
  if (!v) return '—';
  if (Array.isArray(v)) { const [h,m]=v; return `${String(h).padStart(2,'0')}:${String(m).padStart(2,'0')}`; }
  return String(v).slice(0,5);
}
function esc(s) {
  if (!s) return '';
  return String(s).replace(/&/g,'&amp;').replace(/</g,'&lt;').replace(/>/g,'&gt;').replace(/"/g,'&quot;');
}