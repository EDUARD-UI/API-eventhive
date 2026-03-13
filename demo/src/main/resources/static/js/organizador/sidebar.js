/* ════════════════════════════════════════
   EventHive — sidebar.js
   Lógica de navegación lateral y topbar.
════════════════════════════════════════ */

const SECTION_TITLES = {
  dashboard:   'Dashboard',
  eventos:     'Mis Eventos',
  localidades: 'Localidades',
  promociones: 'Promociones',
  perfil:      'Mi Perfil',
};

function initSidebar() {
  document.querySelectorAll('.nav-item[data-s]').forEach(el => {
    el.addEventListener('click', e => {
      e.preventDefault();
      navigate(el.dataset.s);
    });
  });

  // Cerrar sesión
  document.getElementById('btn-logout')?.addEventListener('click', cerrarSesion);
}

/**
 * Navega a una sección, cargando su HTML dinámicamente
 */
async function navigate(sec) {
  /* Actualizar nav items */
  document.querySelectorAll('.nav-item').forEach(i => i.classList.remove('active'));
  document.querySelector(`.nav-item[data-s="${sec}"]`)?.classList.add('active');

  /* Actualizar topbar title */
  document.getElementById('topbar-title').textContent = SECTION_TITLES[sec] || sec;

  try {
    /* Cargar el HTML de la página */
    const res = await fetch(`/organizador/${sec}.html`);
    if (!res.ok) throw new Error(`HTTP ${res.status}`);
    const html = await res.text();
    
    /* Inyectar en el DOM (pero sin los scripts) */
    const pageContent = document.getElementById('page-content');
    const parser = new DOMParser();
    const doc = parser.parseFromString(html, 'text/html');
    const scripts = doc.querySelectorAll('script');
    scripts.forEach(s => s.remove()); // Remover scripts del HTML
    pageContent.innerHTML = doc.body.innerHTML;

    /* Llamar a la función de inicialización correspondiente */
    switch(sec) {
      case 'dashboard':
        if (typeof initDashboard === 'function') initDashboard();
        if (typeof renderDashboard === 'function') renderDashboard();
        if (typeof buildCatChips === 'function') buildCatChips();
        if (typeof updateNavBadge === 'function') updateNavBadge(Store.eventos.length);
        break;
      case 'eventos':
        if (typeof fillSelect === 'function') {
          fillSelect('ev-cat', Store.categorias);
          fillSelect('ev-est', Store.estados);
          fillSelectEventos();
        }
        if (typeof buildCatChips === 'function') buildCatChips();
        if (typeof renderEventos === 'function') renderEventos();
        break;
      case 'localidades':
        if (typeof fillSelectEventos === 'function') fillSelectEventos(['loc-ev']);
        if (typeof renderLocalidades === 'function') renderLocalidades();
        break;
      case 'promociones':
        if (typeof fillSelectEventos === 'function') fillSelectEventos(['pr-ev']);
        if (typeof renderPromos === 'function') renderPromos();
        break;
      case 'perfil':
        if (typeof initPerfil === 'function') initPerfil();
        break;
    }

  } catch (err) {
    console.error(`Error cargando página ${sec}:`, err);
    const pageContent = document.getElementById('page-content');
    pageContent.innerHTML = `<div style="padding:40px;color:var(--red)">Error cargando la página: ${err.message}</div>`;
  }

  Router.currentPage = sec;
}

function updateSidebarUser(usuario) {
  if (!usuario) return;
  const ini = getInitials(usuario.nombre, usuario.apellido);
  document.getElementById('sb-av').textContent = ini;
  document.getElementById('sb-nm').textContent = `${usuario.nombre || ''} ${usuario.apellido || ''}`.trim();
}

function updateNavBadge(count) {
  const nb = document.getElementById('nb-ev');
  if (nb) nb.textContent = count;
}

async function cerrarSesion() {
  try { await AuthAPI.logout(); } catch (_) {}
  window.location.href = '/login';
}
