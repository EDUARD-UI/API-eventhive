const ORG = {
  usuario: null,
  dashboard: null,
};

const TITLES = {
  dashboard:   'Dashboard',
  eventos:     'Mis Eventos',
  localidades: 'Localidades',
  perfil:      'Mi Perfil',
};

document.addEventListener('DOMContentLoaded', async () => {
  await cargarSesion();
  initSidebar();
  await navegarA('dashboard');
});

async function cargarSesion() {
  try {
    const res  = await fetch('/api/pagos', { credentials: 'include' });
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

async function navegarA(sec) {
  document.querySelectorAll('.nav-item').forEach(i => i.classList.remove('active'));
  document.querySelector(`.nav-item[data-s="${sec}"]`)?.classList.add('active');
  document.getElementById('topbar-title').textContent = TITLES[sec] || sec;

  const pageContent = document.getElementById('page-content');
  pageContent.innerHTML = '<div style="padding:40px;color:var(--muted)">Cargando...</div>';

  try {
    const res = await fetch(`/organizador/org-${sec}.html`);
    if (!res.ok) throw new Error('Página no encontrada');
    const html = await res.text();

    // Separar HTML de scripts para ejecutarlos correctamente
    const tmp = document.createElement('div');
    tmp.innerHTML = html;

    // Extraer y remover scripts del fragment
    const scripts = Array.from(tmp.querySelectorAll('script'));
    scripts.forEach(s => s.remove());

    // Inyectar HTML sin scripts
    pageContent.innerHTML = tmp.innerHTML;

    // Ejecutar cada script manualmente (así el navegador los procesa)
    for (const oldScript of scripts) {
      const newScript = document.createElement('script');
      if (oldScript.src) {
        // Script externo — esperar a que cargue
        await new Promise((resolve, reject) => {
          newScript.src = oldScript.src;
          newScript.onload  = resolve;
          newScript.onerror = reject;
          document.head.appendChild(newScript);
        });
      } else {
        // Script inline
        newScript.textContent = oldScript.textContent;
        document.head.appendChild(newScript);
      }
    }

  } catch (err) {
    pageContent.innerHTML = `<div style="padding:40px;color:red">Error: ${err.message}</div>`;
    return;
  }

  // Inicializar sección tras cargar scripts
  switch (sec) {
    case 'dashboard':   await initDashboard();   break;
    case 'eventos':     await initEventos();     break;
    case 'localidades': await initLocalidades(); break;
    case 'perfil':      initPerfil();            break;
  }
}

/* ── TOAST (SWEETALERT2) ── */
function toast(msg, type = 'ok') {
  const iconMap = {
    'ok': 'success',
    'err': 'error',
    'info': 'info',
    'warning': 'warning'
  };
  
  Swal.fire({
    icon: iconMap[type] || 'info',
    title: msg,
    toast: true,
    position: 'top-end',
    showConfirmButton: false,
    timer: 3500,
    timerProgressBar: true,
    didOpen: (toast) => {
      toast.addEventListener('mouseenter', Swal.stopTimer)
      toast.addEventListener('mouseleave', Swal.resumeTimer)
    }
  });
}

/* ── MODAL ── */
function openModal(id) {
  const el = document.getElementById(id);
  if (!el) return;
  el.classList.add('open');
  el.querySelector('form')?.reset();
  const hid = el.querySelector('input[type=hidden]');
  if (hid) hid.value = '';
}
function closeModal(id) {
  document.getElementById(id)?.classList.remove('open');
}