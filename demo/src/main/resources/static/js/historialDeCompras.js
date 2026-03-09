document.addEventListener('DOMContentLoaded', () => {
  cargarHistorial();
});

// GET /api/pagos → compras del usuario en sesión
async function cargarHistorial() {
  try {
    const res = await fetch('/api/pagos', { credentials: 'include' });
    if (!res.ok) { window.location.href = '/login.html'; return; }
    const json = await res.json();
    const compras = Array.isArray(json.data?.compras) ? json.data.compras
                  : Array.isArray(json.data) ? json.data : [];
    renderHistorial(compras);
  } catch {
    Swal.fire({icon:'error',title:'Error',text:'No se pudo cargar el historial.',confirmButtonColor:'#007bff'});
    renderHistorial([]);
  }
}

function renderHistorial(compras) {
  document.getElementById('skeleton').classList.add('hidden');
  if (!compras.length) { document.getElementById('vacio').classList.remove('hidden'); return; }

  const lista = document.getElementById('lista');
  lista.classList.remove('hidden');
  lista.innerHTML = compras.map(c => `
    <div class="bg-white rounded-3xl border border-gray-100 shadow-sm p-6 flex flex-col md:flex-row md:items-center gap-5">
      <!-- fecha stub -->
      <div class="w-16 h-16 rounded-2xl bg-brand/10 flex flex-col items-center justify-center shrink-0 text-brand">
        <span class="text-xs font-bold uppercase">${formatMes(c.eventoFecha)}</span>
        <span class="text-2xl font-extrabold leading-none">${formatDia(c.eventoFecha)}</span>
      </div>
      <!-- info -->
      <div class="flex-1 min-w-0">
        <p class="font-extrabold text-dark">${esc(c.eventoTitulo||'Evento')}</p>
        <div class="flex flex-wrap gap-3 mt-1 text-xs text-dark/40">
          <span><i class="fas fa-couch text-brand mr-1"></i>${esc(c.localidadNombre||'—')}</span>
          <span><i class="fas fa-ticket-alt text-brand mr-1"></i>${c.cantidad||1} boleto${(c.cantidad||1)>1?'s':''}</span>
          <span><i class="fas fa-credit-card text-brand mr-1"></i>${esc(c.metodoPago||'—')}</span>
          <span><i class="fas fa-receipt text-brand mr-1"></i>#${c.id}</span>
        </div>
        <p class="text-xs text-dark/30 mt-1">${formatFechaCompra(c.fechaCompra)}</p>
      </div>
      <!-- precio + acción -->
      <div class="flex flex-col items-end gap-3 shrink-0">
        <span class="font-extrabold text-brand text-lg">$${Number(c.total||0).toLocaleString('es-CO')} COP</span>
        <a href="/misBoletos.html?id=${c.id}"
          class="bg-accent text-dark text-xs font-bold px-4 py-2 rounded-xl hover:bg-yellow-400 transition flex items-center gap-2">
          <i class="fas fa-qrcode"></i> Ver boletos
        </a>
      </div>
    </div>`).join('');
}

function formatMes(v) {
  if (!v) return '—';
  if (Array.isArray(v)) return new Date(v[0],v[1]-1,v[2]).toLocaleDateString('es-ES',{month:'short'}).toUpperCase();
  return new Date(v).toLocaleDateString('es-ES',{month:'short'}).toUpperCase();
}
function formatDia(v) {
  if (!v) return '—';
  if (Array.isArray(v)) return v[2];
  return new Date(v).getDate();
}
function formatFechaCompra(v) {
  if (!v) return '';
  if (Array.isArray(v)) { const [y,m,d,h,mn]=v; return new Date(y,m-1,d,h,mn).toLocaleDateString('es-ES',{day:'numeric',month:'short',year:'numeric',hour:'2-digit',minute:'2-digit'}); }
  return new Date(v).toLocaleDateString('es-ES',{day:'numeric',month:'short',year:'numeric',hour:'2-digit',minute:'2-digit'});
}
function esc(s) {
  if (!s) return '';
  return String(s).replace(/&/g,'&amp;').replace(/</g,'&lt;').replace(/>/g,'&gt;').replace(/"/g,'&quot;');
}