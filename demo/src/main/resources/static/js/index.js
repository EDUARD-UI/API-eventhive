const BASE = '';

document.addEventListener('DOMContentLoaded', () => {
  checkSession();
  loadEventosDestacados();
  loadCategoriasDestacadas();
  loadTodasLasCategorias();
});

// ── Sesión ────────────────────────────────────────────────────────────

async function checkSession() {
  try {
    const res  = await fetch(`${BASE}/api/auth/me`, { credentials: 'include' });
    const json = await res.json();
    res.ok && json.data ? renderNavAuth(json.data) : renderNavGuest();
  } catch {
    renderNavGuest();
  }
}

function renderNavAuth({ nombre, rolNombre }) {
  const links = {
    organizador:  `<a href="/pages/organizador/index.html" class="btn-brand">Dashboard</a>`,
    administrador:`<a href="/administracion/dashboard"     class="btn-brand">Dashboard</a>`,
  };

  document.getElementById('navAuth').innerHTML = `
    <span class="text-dark/50 hidden sm:block text-sm font-medium">${esc(nombre)}</span>
    ${links[rolNombre] ?? `<a href="/pages/perfil.html" class="nav-link">Mi Perfil</a>`}
    <button id="btnLogout" class="nav-link">Salir</button>
  `;
  document.getElementById('btnLogout').addEventListener('click', handleLogout);
}

function renderNavGuest() {
  document.getElementById('navAuth').innerHTML = `
    <a href="/pages/login.html"  class="nav-link">Iniciar sesión</a>
    <a href="/pages/signin.html" class="btn-brand">Registrarse</a>
  `;
}

async function handleLogout() {
  await fetch(`${BASE}/api/auth/logout`, { method: 'POST', credentials: 'include' })
    .catch(() => {});
  location.reload();
}

// ── Eventos ───────────────────────────────────────────────────────────

async function loadEventosDestacados() {
  const grid = document.getElementById('eventGrid');
  if (!grid) return;

  // Intenta destacados, cae a todos si falla
  const urls = [`${BASE}/api/eventos/destacados`, `${BASE}/api/eventos`];

  for (const url of urls) {
    try {
      const res    = await fetch(url, { credentials: 'include' });
      const eventos = extraerArray(await res.json()).slice(0, 3);
      if (eventos.length) { grid.innerHTML = eventos.map(cardEvento).join(''); return; }
    } catch { /* intenta siguiente url */ }
  }

  grid.innerHTML = '<p class="col-span-3 text-center text-dark/30 py-10">No se pudieron cargar los eventos.</p>';
}

// ── Categorías ────────────────────────────────────────────────────────

async function loadCategoriasDestacadas() {
  const grid = document.getElementById('categoryGrid');
  if (!grid) return;
  try {
    const res  = await fetch(`${BASE}/api/categorias/destacadas`, { credentials: 'include' });
    const cats = res.ok ? extraerArray(await res.json()) : [];
    grid.innerHTML = cats.map(cardCategoria).join('');
  } catch { /* silencioso */ }
}

async function loadTodasLasCategorias() {
  const sel = document.getElementById('categorySelect');
  if (!sel) return;
  try {
    const res  = await fetch(`${BASE}/api/categorias`, { credentials: 'include' });
    if (res.ok) extraerArray(await res.json()).forEach(c => {
      const opt = Object.assign(document.createElement('option'), {
        value: c.id, textContent: c.nombre, className: 'bg-dark text-white'
      });
      sel.appendChild(opt);
    });
  } catch { /* silencioso */ }
  sel.addEventListener('change', ({ target }) => {
    if (target.value) location.href = `/pages/infoCategoria.html?id=${target.value}`;
  });
}

// ── Cards ─────────────────────────────────────────────────────────────

function cardEvento({ id, titulo, foto, fecha, lugar, categoriaNombre }) {
  const imgHTML = foto
    ? `<img src="/uploads/eventos/${foto}" alt="${esc(titulo)}"
         onerror="this.parentElement.innerHTML='<div class=\'w-full h-full flex items-center justify-center bg-gray-100\'><i class=\'fas fa-calendar-alt text-4xl text-gray-200\'></i></div>'"
         class="w-full h-full object-cover transition duration-500 hover:scale-105"/>`
    : `<i class="fas fa-calendar-alt text-4xl text-gray-200"></i>`;

  return `
    <a href="/pages/infoEvento.html?id=${id}"
      class="event-card block bg-white rounded-2xl overflow-hidden border border-gray-100 shadow-sm">
      <div class="relative h-52 overflow-hidden bg-gray-100 flex items-center justify-center">
        ${imgHTML}
        ${categoriaNombre ? `<span class="absolute top-3 left-3 bg-accent text-dark text-[10px] font-bold px-2.5 py-1 rounded-full uppercase tracking-wide">${esc(categoriaNombre)}</span>` : ''}
      </div>
      <div class="p-5">
        <h3 class="font-display font-bold text-dark text-base mb-2 line-clamp-2">${esc(titulo)}</h3>
        <p class="text-dark/40 text-xs flex items-center gap-4">
          <span><i class="far fa-calendar text-brand mr-1"></i>${formatFecha(fecha)}</span>
          ${lugar ? `<span><i class="fas fa-map-marker-alt text-brand mr-1"></i>${esc(lugar)}</span>` : ''}
        </p>
      </div>
    </a>`;
}

function cardCategoria({ id, nombre, foto }) {
  return `
    <a href="/pages/infoCategoria.html?id=${id}"
      class="cat-card relative block h-44 rounded-2xl overflow-hidden bg-gray-200">
      ${foto ? `<img src="/uploads/categorias/${foto}" alt="${esc(nombre)}" onerror="this.style.display='none'" class="w-full h-full object-cover"/>` : ''}
      <div class="absolute inset-0 bg-gradient-to-t from-black/70 via-black/20 to-transparent"></div>
      <span class="absolute bottom-4 left-4 font-display font-bold text-white text-sm drop-shadow">${esc(nombre)}</span>
    </a>`;
}

// ── Utilidades ────────────────────────────────────────────────────────

const extraerArray = json =>
  !json ? [] : Array.isArray(json) ? json : Array.isArray(json.data) ? json.data : json.data ? [json.data] : [];

const formatFecha = v => {
  if (!v) return 'Fecha por confirmar';
  const d = Array.isArray(v) ? new Date(v[0], v[1] - 1, v[2]) : new Date(v);
  return isNaN(d) ? String(v) : d.toLocaleDateString('es-ES', { day: 'numeric', month: 'short', year: 'numeric' });
};

const esc = s => s ? String(s).replace(/&/g,'&amp;').replace(/</g,'&lt;').replace(/>/g,'&gt;').replace(/"/g,'&quot;') : '';