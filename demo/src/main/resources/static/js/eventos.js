document.addEventListener('DOMContentLoaded', () => {
  checkSession();
  cargarCategorias();
  cargarTodo();

  document.getElementById('filtroCategoria').addEventListener('change', e => {
    const id = e.target.value;
    id ? filtrarPorCategoria(id) : cargarTodo();
  });
});

// validar sesion
async function checkSession() {
  try {
    const res = await fetch('/api/auth/me', { credentials: 'include' });
    if (!res.ok) { renderNavGuest(); return; }
    const json = await res.json();
    json.data ? renderNavAuth(json.data.nombre, json.data.rol?.nombre || '') : renderNavGuest();
  } catch { renderNavGuest(); }
}
function renderNavAuth(nombre, rol) {
  const el = document.getElementById('navAuth'); if (!el) return;
  el.innerHTML = `
    <span class="text-dark/50 hidden sm:block text-sm font-medium">${esc(nombre)}</span>
    ${rol === 'organizador'
      ? `<a href="/pages/organizador/index.html" class="bg-brand text-white text-xs font-bold px-4 py-2 rounded-xl hover:bg-blue-700 transition">Dashboard</a>`
      : `<a href="/pages/perfil.html" class="text-dark/60 hover:text-brand transition text-xs font-medium">Mi Perfil</a>`}
    <button id="btnLogout" class="text-dark/40 hover:text-brand text-xs transition font-medium">Salir</button>`;
  document.getElementById('btnLogout').addEventListener('click', async () => {
    await fetch('/api/auth/logout', { method: 'POST', credentials: 'include' });
    location.reload();
  });
}
function renderNavGuest() {
  const el = document.getElementById('navAuth'); if (!el) return;
  el.innerHTML = `
    <a href="/pages/login.html" class="text-dark/60 hover:text-brand transition text-sm font-medium">Iniciar sesión</a>
    <a href="/pages/signin.html" class="bg-brand text-white text-xs font-bold px-4 py-2 rounded-xl hover:bg-blue-700 transition shadow-sm">Registrarse</a>`;
}

// menu desplegable de categorias
async function cargarCategorias() {
  try {
    const res = await fetch('/api/categorias/nombres', { credentials: 'include' });
    const json = await res.json();
    const sel = document.getElementById('filtroCategoria');
    (json.data || []).forEach(c => {
      const o = document.createElement('option');
      o.value = c.id; o.textContent = c.nombre; sel.appendChild(o);
    });
  } catch {}
}

// categorias y sus eventos
async function cargarTodo() {
  try {
    const res = await fetch('/api/categorias/con-eventos', { credentials: 'include' });
    const json = await res.json();
    const lista = Array.isArray(json.data) ? json.data : [];
    renderCategorias(lista);
  } catch {
    document.getElementById('contenido').innerHTML =
      '<p class="text-center text-dark/30 py-20">Error al cargar eventos.</p>';
  }
}

function renderCategorias(lista) {
  const contenido = document.getElementById('contenido');
  const empty = document.getElementById('emptyState');
  const conEventos = lista.filter(c => c.eventos && c.eventos.length > 0);
  if (!conEventos.length) { contenido.innerHTML = ''; empty.classList.remove('hidden'); return; }
  empty.classList.add('hidden');
  contenido.innerHTML = conEventos.map(cat => `
    <div class="mb-16">
      <div class="flex items-end justify-between mb-6">
        <div>
          <p class="text-brand text-[11px] font-bold tracking-[0.3em] uppercase mb-1">Categoría</p>
          <h2 class="text-2xl font-extrabold text-dark">${esc(cat.nombre)}
            <span class="text-dark/25 font-normal text-lg ml-2">${cat.totalEventos} evento${cat.totalEventos !== 1 ? 's' : ''}</span>
          </h2>
        </div>
        <a href="/pages/infoCategoria.html?id=${cat.id}" class="text-sm text-brand hover:underline flex items-center gap-1 font-medium">
          Ver todos <i class="fas fa-arrow-right text-xs"></i>
        </a>
      </div>
      <div class="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 xl:grid-cols-4 gap-5">
        ${cat.eventos.map(cardEvento).join('')}
      </div>
      ${cat.totalEventos > 10 ? `
        <div class="mt-6 text-center">
          <a href="/pages/infoCategoria.html?id=${cat.id}"
            class="inline-flex items-center gap-2 border border-brand/30 text-brand text-sm font-bold px-6 py-3 rounded-2xl hover:bg-brand hover:text-white transition">
            Ver los ${cat.totalEventos} eventos <i class="fas fa-arrow-right text-xs"></i>
          </a>
        </div>` : ''}
    </div>`).join('');
}

//mostrar eventos solo de categoria seleccionada
async function filtrarPorCategoria(id) {
  try {
    const res = await fetch('/api/categorias/con-eventos', { credentials: 'include' });
    const json = await res.json();
    const lista = Array.isArray(json.data) ? json.data : [];
    const filtrada = lista.filter(c => String(c.id) === String(id));
    renderCategorias(filtrada);
  } catch {}
}

// card de evento
function cardEvento(e) {
  const img = e.foto ? `/uploads/eventos/${e.foto}` : null;
  const fecha = formatFecha(e.fecha);
  return `
    <a href="/pages/infoEvento.html?id=${e.id}" class="event-card block bg-white rounded-2xl overflow-hidden border border-gray-100 shadow-sm">
      <div class="relative h-48 bg-gray-100 overflow-hidden">
        ${img
          ? `<img src="${img}" alt="${esc(e.titulo)}" onerror="this.parentElement.innerHTML='<div class=\\'w-full h-full flex items-center justify-center bg-gray-50\\'><i class=\\'fas fa-calendar-alt text-3xl text-gray-200\\'></i></div>'" class="w-full h-full object-cover transition duration-500 hover:scale-105"/>`
          : `<div class="w-full h-full flex items-center justify-center bg-gray-50"><i class="fas fa-calendar-alt text-3xl text-gray-200"></i></div>`}
      </div>
      <div class="p-4">
        <h3 class="font-display font-bold text-dark text-sm mb-2 line-clamp-2">${esc(e.titulo)}</h3>
        <p class="text-dark/40 text-xs flex flex-col gap-1">
          <span><i class="far fa-calendar text-brand mr-1"></i>${fecha}</span>
          ${e.lugar ? `<span><i class="fas fa-map-marker-alt text-brand mr-1"></i>${esc(e.lugar)}</span>` : ''}
        </p>
      </div>
    </a>`;
}

//funciones de apoyo
function formatFecha(v) {
  if (!v) return 'Fecha por confirmar';
  if (Array.isArray(v)) { const [y,m,d]=v; return new Date(y,m-1,d).toLocaleDateString('es-ES',{day:'numeric',month:'short',year:'numeric'}); }
  const d = new Date(v);
  return isNaN(d) ? String(v) : d.toLocaleDateString('es-ES',{day:'numeric',month:'short',year:'numeric'});
}
function esc(s) {
  if (!s) return '';
  return String(s).replace(/&/g,'&amp;').replace(/</g,'&lt;').replace(/>/g,'&gt;').replace(/"/g,'&quot;');
}