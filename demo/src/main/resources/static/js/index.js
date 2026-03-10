// ── CONFIG ────────────────────────────────────────────────
const BASE = '';

document.addEventListener('DOMContentLoaded', () => {
  checkSession();
  loadEventosDestacados();
  loadCategoriasDestacadas();
  loadTodasLasCategorias();
});

async function checkSession() {
  try {
    const res = await fetch(`${BASE}/api/pagos`, { credentials: 'include' });
    const json = await res.json();
    json.data ? renderNavAuth(json.data.nombre, json.data.rolNombre || '') : renderNavGuest();
  } catch { renderNavGuest(); }
}

function renderNavAuth(nombre, rol) {
  const el = document.getElementById('navAuth');
  if (!el) return;
  el.innerHTML = `
    <span class="text-dark/50 hidden sm:block text-sm font-medium">${esc(nombre)}</span>
    ${rol === 'organizador'
      ? `<a href="/organizador/dashboard" class="bg-brand text-white text-xs font-bold px-4 py-2 rounded-xl hover:bg-blue-700 transition">Dashboard</a>`
      : `<a href="/perfil.html" class="text-dark/60 hover:text-brand transition text-xs font-medium">Mi Perfil</a>`
    }
    <button id="btnLogout" class="text-dark/40 hover:text-brand text-xs transition font-medium">Salir</button>
  `;
  document.getElementById('btnLogout').addEventListener('click', async () => {
    await fetch(`${BASE}/api/auth/logout`, { method: 'POST', credentials: 'include' });
    location.reload();
  });
}

function renderNavGuest() {
  const el = document.getElementById('navAuth');
  if (!el) return;
  el.innerHTML = `
    <a href="/login.html" class="text-dark/60 hover:text-brand transition text-sm font-medium">Iniciar sesión</a>
    <a href="/signin.html" class="bg-brand text-white text-xs font-bold px-4 py-2 rounded-xl hover:bg-blue-700 transition shadow-sm">Registrarse</a>
  `;
}

async function loadEventosDestacados() {
  const grid = document.getElementById('eventGrid');
  try {
    const res = await fetch(`${BASE}/api/eventos/destacados`, { credentials: 'include' });
    if (res.ok) {
      const json = await res.json();
      const eventos = extraerArray(json);
      if (eventos.length > 0) {
        grid.innerHTML = eventos.map(cardEvento).join('');
        return; // ✓ éxito
      }
    }
    console.warn('[Eventos] /destacados falló (HTTP', res.status, ') — usando fallback /api/eventos');
  } catch (e) {
    console.warn('[Eventos] /destacados excepción:', e.message, '— usando fallback');
  }

  try {
    const res = await fetch(`${BASE}/api/eventos`, { credentials: 'include' });
    if (!res.ok) throw new Error('HTTP ' + res.status);
    const json   = await res.json();
    const todos  = extraerArray(json);
    const eventos = todos.slice(0, 3);
    if (eventos.length > 0) {
      grid.innerHTML = eventos.map(cardEvento).join('');
    } else {
      grid.innerHTML = '<p class="col-span-3 text-center text-dark/30 py-10">No hay eventos disponibles.</p>';
    }
  } catch (e) {
    console.error('[Eventos] fallback también falló:', e.message);
    grid.innerHTML = '<p class="col-span-3 text-center text-dark/30 py-10">No se pudieron cargar los eventos.</p>';
  }
}

function cardEvento(e) {
  const img   = e.foto ? `/uploads/eventos/${e.foto}` : null;
  const cat   = e.categoriaNombre || '';
  const fecha = formatFecha(e.fecha);
  return `
    <a href="/infoEvento.html?id=${e.id}"
      class="event-card block bg-white rounded-2xl overflow-hidden border border-gray-100 shadow-sm">
      <div class="relative h-52 overflow-hidden bg-gray-100">
        ${img
          ? `<img src="${img}" alt="${esc(e.titulo)}"
               onerror="this.parentElement.innerHTML='<div class=\\'w-full h-full flex items-center justify-center bg-gray-100\\'><i class=\\'fas fa-calendar-alt text-4xl text-gray-200\\'></i></div>'"
               class="w-full h-full object-cover transition duration-500 hover:scale-105"/>`
          : `<div class="w-full h-full flex items-center justify-center bg-gray-50">
               <i class="fas fa-calendar-alt text-4xl text-gray-200"></i>
             </div>`
        }
        ${cat ? `<span class="absolute top-3 left-3 bg-accent text-dark text-[10px] font-bold px-2.5 py-1 rounded-full uppercase tracking-wide">${esc(cat)}</span>` : ''}
      </div>
      <div class="p-5">
        <h3 class="font-display font-bold text-dark text-base mb-2 line-clamp-2">${esc(e.titulo)}</h3>
        <p class="text-dark/40 text-xs flex items-center gap-4">
          <span><i class="far fa-calendar text-brand mr-1"></i>${fecha}</span>
          ${e.lugar ? `<span><i class="fas fa-map-marker-alt text-brand mr-1"></i>${esc(e.lugar)}</span>` : ''}
        </p>
      </div>
    </a>`;
}

//funcion para las categorias destacadas
async function loadCategoriasDestacadas() {
  const grid = document.getElementById('categoryGrid');
  try {
    const res = await fetch(`${BASE}/api/categorias/destacadas`, { credentials: 'include' });
    if (!res.ok) { console.warn('[Cats] /destacadas HTTP', res.status); grid.innerHTML = ''; return; }
    const json = await res.json();
    const cats = extraerArray(json);
    grid.innerHTML = cats.length ? cats.map(cardCategoria).join('') : '';
  } catch (e) {
    console.error('[Cats] excepción:', e.message);
    grid.innerHTML = '';
  }
}

function cardCategoria(c) {
  const img = c.foto ? `/uploads/categorias/${c.foto}` : null;
  return `
    <a href="/infoCategoria.html?id=${c.id}"
      class="cat-card relative block h-44 rounded-2xl overflow-hidden bg-gray-200">
      ${img ? `<img src="${img}" alt="${esc(c.nombre)}" onerror="this.style.display='none'" class="w-full h-full object-cover"/>` : ''}
      <div class="absolute inset-0 bg-gradient-to-t from-black/70 via-black/20 to-transparent"></div>
      <span class="absolute bottom-4 left-4 font-display font-bold text-white text-sm drop-shadow">${esc(c.nombre)}</span>
    </a>`;
}

//cargar todas las categorias en el menu desplegable
async function loadTodasLasCategorias() {
  const sel = document.getElementById('categorySelect');
  try {
    const res = await fetch(`${BASE}/api/categorias`, { credentials: 'include' });
    if (!res.ok) return;
    const json = await res.json();
    extraerArray(json).forEach(c => {
      const opt = document.createElement('option');
      opt.value = c.id; opt.textContent = c.nombre; opt.className = 'bg-dark text-white';
      sel.appendChild(opt);
    });
  } catch { /* silencioso */ }
  sel.addEventListener('change', e => {
    if (e.target.value) location.href = `/infoCategoria.html?id=${e.target.value}`;
  });
}

//funciones de apoyo

function extraerArray(json) {
  if (!json) return [];
  if (Array.isArray(json))        return json;
  if (Array.isArray(json.data))   return json.data;
  if (json.data != null)          return [json.data];
  return [];
}

function formatFecha(v) {
  if (!v) return 'Fecha por confirmar';
  if (Array.isArray(v)) {
    const [y, m, d] = v;
    return new Date(y, m - 1, d).toLocaleDateString('es-ES', { day: 'numeric', month: 'short', year: 'numeric' });
  }
  const d = new Date(v);
  return isNaN(d) ? String(v) : d.toLocaleDateString('es-ES', { day: 'numeric', month: 'short', year: 'numeric' });
}


function esc(s) {
  if (!s) return '';
  return String(s).replace(/&/g,'&amp;').replace(/</g,'&lt;').replace(/>/g,'&gt;').replace(/"/g,'&quot;');
}