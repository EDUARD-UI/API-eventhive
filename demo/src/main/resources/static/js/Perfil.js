let usuarioActual = null;

document.addEventListener('DOMContentLoaded', () => {
  cargarPerfil();
  initTabs();
  initModal();

  document.getElementById('btnLogout').addEventListener('click', async () => {
    await fetch('/api/auth/logout', { method: 'POST', credentials: 'include' });
    window.location.href = '/index.html';
  });
});

// PERFIL verifica sesión
// GET /api/pagos
async function cargarPerfil() {
  try {
    const res = await fetch('/api/pagos', { credentials: 'include' });
    if (!res.ok) { window.location.href = '/pages/login.html'; return; }
    const json = await res.json();
    if (!json.data) { window.location.href = '/pages/login.html'; return; }
    usuarioActual = json.data;
    document.getElementById('perfilNombre').textContent = `${usuarioActual.nombre || ''} ${usuarioActual.apellido || ''}`.trim();
    document.getElementById('perfilEmail').textContent   = usuarioActual.correo || '';
    document.getElementById('editNombre').value   = usuarioActual.nombre   || '';
    document.getElementById('editEmail').value    = usuarioActual.correo   || '';
    document.getElementById('editTelefono').value = usuarioActual.telefono || '';
    cargarCompras();
    cargarDeseados();
    cargarComentarios();
  } catch { window.location.href = '/pages/login.html'; }
}

// COMPRAS (tiquetes)
// GET /api/compras/historial → lista del usuario logueado
async function cargarCompras() {
  try {
    const res = await fetch('/api/compras/historial', { credentials: 'include' });
    if (!res.ok) { renderTiquetes([]); return; }
    const json = await res.json();
    renderTiquetes(Array.isArray(json.data) ? json.data : []);
  } catch { renderTiquetes([]); }
}

function renderTiquetes(compras) {
  const lista = document.getElementById('tiquetesLista');
  const vacio = document.getElementById('tiquetesVacio');
  const link  = document.getElementById('linkHistorial');
  if (!compras.length) { vacio.classList.remove('hidden'); return; }
  if (compras.length > 3) link.classList.remove('hidden');
  const top3 = compras.slice(0, 3);
  lista.innerHTML = top3.map(c => `
    <div class="bg-white rounded-3xl border border-gray-100 shadow-sm p-6 flex items-center gap-5">
      <div class="w-14 h-14 rounded-2xl bg-brand/10 flex flex-col items-center justify-center shrink-0 text-brand">
        <span class="text-xs font-bold uppercase">${formatMes(c.eventoFecha)}</span>
        <span class="text-xl font-extrabold leading-none">${formatDia(c.eventoFecha)}</span>
      </div>
      <div class="flex-1 min-w-0">
        <p class="font-extrabold text-dark text-sm line-clamp-1">${esc(c.eventoTitulo || 'Evento')}</p>
        <p class="text-xs text-dark/40 mt-0.5">${esc(c.localidadNombre || '')} · ${c.cantidad || 1} boleto${(c.cantidad || 1) > 1 ? 's' : ''}</p>
        <p class="text-xs font-bold text-brand mt-1">$${Number(c.total || 0).toLocaleString('es-CO')} COP</p>
      </div>
      <a href="/pages/misBoletos.html?id=${c.id}"
        class="bg-accent text-dark text-xs font-bold px-4 py-2 rounded-xl hover:bg-yellow-400 transition shrink-0">
        <i class="fas fa-qrcode mr-1"></i> Ver boletos
      </a>
    </div>`).join('');
}

// DESEADOS
// GET /api/eventos-deseados → lista EventoDeseadoDTO del usuario logueado
async function cargarDeseados() {
  try {
    const res = await fetch('/api/eventos-deseados', { credentials: 'include' });
    if (!res.ok) { renderDeseados([]); return; }
    const json = await res.json();
    renderDeseados(Array.isArray(json.data) ? json.data : []);
  } catch { renderDeseados([]); }
}

//render del html de un eventodeseado
function renderDeseados(lista) {
  const el    = document.getElementById('deseadosLista');
  const vacio = document.getElementById('deseadosVacio');
  if (!lista.length) { vacio.classList.remove('hidden'); return; }
  el.innerHTML = lista.map(d => `
    <a href="/pages/infoEvento.html?id=${d.eventoId}" class="block bg-white rounded-2xl border border-gray-100 shadow-sm overflow-hidden hover:shadow-md transition">
      <div class="h-36 bg-gray-100 flex items-center justify-center">
        ${d.eventoFoto
          ? `<img src="/uploads/eventos/${esc(d.eventoFoto)}" class="w-full h-full object-cover" onerror="this.parentElement.innerHTML='<i class=\\'fas fa-calendar-alt text-3xl text-gray-200\\'></i>'"/>`
          : '<i class="fas fa-calendar-alt text-3xl text-gray-200"></i>'}
      </div>
      <div class="p-4">
        <p class="font-bold text-dark text-sm line-clamp-2">${esc(d.eventoTitulo || '')}</p>
        <p class="text-xs text-dark/40 mt-1"><i class="fas fa-map-marker-alt text-brand mr-1"></i>${esc(d.eventoLugar || '')}</p>
      </div>
    </a>`).join('');
}

// CARGAR VALORACIONES
// GET /api/valoraciones/usuario
async function cargarComentarios() {
  try {
    const res = await fetch('/api/valoraciones/usuario', { credentials: 'include' });
    if (!res.ok) { renderComentarios([]); return; }
    const json = await res.json();
    renderComentarios(Array.isArray(json.data) ? json.data : []);
  } catch { renderComentarios([]); }
}

// Usa eventoTitulo del ValoracionDTO
function renderComentarios(lista) {
  const el    = document.getElementById('comentariosLista');
  const vacio = document.getElementById('comentariosVacio');
  if (!lista.length) { vacio.classList.remove('hidden'); return; }
  el.innerHTML = lista.map(c => `
    <div class="bg-white rounded-3xl border border-gray-100 shadow-sm p-6">
      <div class="flex items-start justify-between mb-3">
        <p class="font-extrabold text-dark text-sm">${esc(c.eventoTitulo || 'Evento')}</p>
        <div class="flex items-center gap-1 text-accent text-sm font-bold shrink-0">
          <i class="fas fa-star"></i> ${c.calificacion}/5
        </div>
      </div>
      <p class="text-dark/55 text-sm leading-relaxed">${esc(c.comentario || '')}</p>
    </div>`).join('');
}

// EDITAR PERFIL
// PUT /api/usuarios/perfil
function initModal() {
  document.getElementById('btnEditar').addEventListener('click', () =>
    document.getElementById('modalEditar').classList.remove('hidden'));
  document.getElementById('cerrarModal').addEventListener('click', () =>
    document.getElementById('modalEditar').classList.add('hidden'));
  document.getElementById('modalEditar').addEventListener('click', e => {
    if (e.target === document.getElementById('modalEditar'))
      document.getElementById('modalEditar').classList.add('hidden');
  });

  document.getElementById('btnGuardar').addEventListener('click', async () => {
    const nombre   = document.getElementById('editNombre').value.trim();
    const correo   = document.getElementById('editEmail').value.trim();
    const telefono = document.getElementById('editTelefono').value.trim();
    const password = document.getElementById('editPassword').value;

    if (!nombre || !correo) {
      Swal.fire({ icon: 'warning', title: 'Campos requeridos', text: 'Nombre y correo son obligatorios.', confirmButtonColor: '#007bff' });
      return;
    }

    try {
      // URLSearchParams porque el backend usa @RequestParam
      const params = new URLSearchParams({ nombre, correo, telefono });
      if (password) params.append('clave', password);

      const res = await fetch('/api/usuarios/perfil', {
        method: 'PUT',
        credentials: 'include',
        headers: { 'Content-Type': 'application/x-www-form-urlencoded' },
        body: params.toString()
      });
      const json = await res.json();

      if (res.ok && json.success) {
        Swal.fire({ icon: 'success', title: '¡Actualizado!', text: json.mensaje || 'Perfil actualizado.', confirmButtonColor: '#007bff', timer: 1800, showConfirmButton: false });
        document.getElementById('perfilNombre').textContent = nombre;
        document.getElementById('perfilEmail').textContent  = correo;
        // Actualizar datos locales para que el modal refleje los cambios
        if (usuarioActual) { usuarioActual.nombre = nombre; usuarioActual.correo = correo; usuarioActual.telefono = telefono; }
        document.getElementById('modalEditar').classList.add('hidden');
      } else {
        Swal.fire({ icon: 'error', title: 'Error', text: json.mensaje || 'No se pudo actualizar.', confirmButtonColor: '#007bff' });
      }
    } catch {
      Swal.fire({ icon: 'error', title: 'Error de conexión', text: 'No se pudo conectar al servidor.', confirmButtonColor: '#007bff' });
    }
  });
}

// instanciar tabs
function initTabs() {
  document.querySelectorAll('.tab-btn').forEach(btn => {
    btn.addEventListener('click', () => {
      document.querySelectorAll('.tab-btn').forEach(b => b.classList.remove('active', 'bg-brand', 'text-white', 'border-brand'));
      btn.classList.add('active');
      const tab = btn.dataset.tab;
      ['tiquetes', 'deseados', 'comentarios'].forEach(t => {
        document.getElementById(`tab-${t}`).classList.toggle('hidden', t !== tab);
      });
    });
  });
}

// funciones de apoyo
function formatMes(v) {
  if (!v) return '—';
  if (Array.isArray(v)) return new Date(v[0], v[1] - 1, v[2]).toLocaleDateString('es-ES', { month: 'short' }).toUpperCase();
  return new Date(v).toLocaleDateString('es-ES', { month: 'short' }).toUpperCase();
}
function formatDia(v) {
  if (!v) return '—';
  if (Array.isArray(v)) return v[2];
  return new Date(v).getDate();
}
function esc(s) {
  if (!s) return '';
  return String(s).replace(/&/g, '&amp;').replace(/</g, '&lt;').replace(/>/g, '&gt;').replace(/"/g, '&quot;');
}