// demo/src/main/resources/static/js/organizador/org-main.js

const ORG = {
  usuario: null,
  dashboard: null,  // datos del dashboard (eventos, localidades, etc.)
};

const TITLES = {
  dashboard: 'Dashboard',
  eventos: 'Mis Eventos',
  localidades: 'Localidades',
  perfil: 'Mi Perfil',
};

/* ── INIT ── */
document.addEventListener('DOMContentLoaded', async () => {
  await cargarSesion();
  initSidebar();
  await navegarA('dashboard');
});

/* ── SESIÓN ── */
async function cargarSesion() {
  try {
    const res = await fetch('/api/pagos', { credentials: 'include' });
    const json = await res.json();
    if (!json.data || json.data.rolNombre !== 'organizador') {
      window.location.href = '/login.html';
      return;
    }
    ORG.usuario = json.data;
    document.getElementById('sb-av').textContent =
      ((ORG.usuario.nombre||'O')[0] + (ORG.usuario.apellido||'G')[0]).toUpperCase();
    document.getElementById('sb-nm').textContent =
      `${ORG.usuario.nombre||''} ${ORG.usuario.apellido||''}`.trim();
  } catch {
    window.location.href = '/login.html';
  }
}

/* ── SIDEBAR ── */
function initSidebar() {
  document.querySelectorAll('.nav-item[data-s]').forEach(el => {
    el.addEventListener('click', e => {
      e.preventDefault();
      navegarA(el.dataset.s);
    });
  });
  document.getElementById('btn-logout')?.addEventListener('click', async () => {
    await fetch('/api/auth/logout', { method: 'POST', credentials: 'include' });
    window.location.href = '/login.html';
  });
}

/* ── ROUTER ── */
async function navegarA(sec) {
  document.querySelectorAll('.nav-item').forEach(i => i.classList.remove('active'));
  document.querySelector(`.nav-item[data-s="${sec}"]`)?.classList.add('active');
  document.getElementById('topbar-title').textContent = TITLES[sec] || sec;

  const pageContent = document.getElementById('page-content');
  pageContent.innerHTML = '<div style="padding:40px;color:var(--muted)">Cargando...</div>';

  try {
    const res = await fetch(`/organizador/org-${sec}.html`);
    if (!res.ok) throw new Error('Página no encontrada');
    pageContent.innerHTML = await res.text();
  } catch (err) {
    pageContent.innerHTML = `<div style="padding:40px;color:var(--red)">Error: ${err.message}</div>`;
    return;
  }

  // Inicializar la sección
  switch (sec) {
    case 'dashboard':   await initDashboard();   break;
    case 'eventos':     await initEventos();     break;
    case 'localidades': await initLocalidades(); break;
    case 'perfil':      initPerfil();            break;
  }
}

/* ── TOAST ── */
function toast(msg, type = 'ok') {
  const wrap = document.getElementById('toast-wrap');
  const el = document.createElement('div');
  el.className = `toast ${type}`;
  el.innerHTML = `<span>${type === 'ok' ? '✓' : '✕'}</span><span>${msg}</span>`;
  wrap.appendChild(el);
  setTimeout(() => el.remove(), 3500);
}

/* ── MODAL ── */
function openModal(id) {
  const el = document.getElementById(id);
  el?.classList.add('open');
  el?.querySelector('form')?.reset();
  const hid = el?.querySelector('input[type=hidden]');
  if (hid) hid.value = '';
}
function closeModal(id) {
  document.getElementById(id)?.classList.remove('open');
}