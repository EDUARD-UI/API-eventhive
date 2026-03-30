const ORG = {
  usuario: null,
  dashboard: null,
};

const TITLES = {
  dashboard:   'Dashboard',
  eventos:     'Mis Eventos',
  localidades: 'Localidades',
  promociones: 'Promociones',
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
      window.location.href = '/pages/login.html';
      return;
    }
    ORG.usuario = json.data;
    document.getElementById('sb-av').textContent =
      ((ORG.usuario.nombre||'O')[0] + (ORG.usuario.apellido||'G')[0]).toUpperCase();
    document.getElementById('sb-nm').textContent =
      `${ORG.usuario.nombre||''} ${ORG.usuario.apellido||''}`.trim();
  } catch {
    window.location.href = '/pages/login.html';
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
    window.location.href = '/pages/login.html';
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

    const tmp = document.createElement('div');
    tmp.innerHTML = html;
    tmp.querySelectorAll('script').forEach(s => s.remove());
    pageContent.innerHTML = tmp.innerHTML;

  } catch (err) {
    pageContent.innerHTML = `<div style="padding:40px;color:red">Error: ${err.message}</div>`;
    return;
  }

  switch (sec) {
    case 'dashboard':   await initDashboard();   break;
    case 'eventos':     await initEventos();     break;
    case 'localidades': await initLocalidades(); break;
    case 'promociones': await initPromociones(); break;
    case 'perfil':      initPerfil();            break;
  }
}

//alertas de sweetalert personalizadas
function toast(msg, type = 'ok') {
  Swal.fire({
    icon: { ok:'success', err:'error', info:'info', warning:'warning' }[type] || 'info',
    title: msg, toast: true, position: 'top-end',
    showConfirmButton: false, timer: 3500, timerProgressBar: true,
    didOpen: t => {
      t.addEventListener('mouseenter', Swal.stopTimer);
      t.addEventListener('mouseleave', Swal.resumeTimer);
    }
  });
}

function openModal(id) {
  const el = document.getElementById(id);
  if (!el) return;
  // Limpiar form y hidden para crear nuevo
  el.querySelector('form')?.reset();
  const hid = el.querySelector('input[type=hidden]');
  if (hid) hid.value = '';
  el.classList.add('open');
}

function openModalEdit(id) {
  // Abrir sin reset — los datos ya fueron escritos por editXxx()
  const el = document.getElementById(id);
  if (!el) return;
  el.classList.add('open');
}

function closeModal(id) {
  document.getElementById(id)?.classList.remove('open');
}

//paginacion de las cards y tablas
function buildPagHTML(total, actual, fn) {
  if (total <= 1) return '';
  const rango = getPagRango(actual, total);

  const baseBtn  = `style="display:inline-flex;align-items:center;justify-content:center;
    min-width:36px;height:36px;padding:0 10px;border-radius:8px;font-size:.82rem;font-weight:500;
    cursor:pointer;border:1.5px solid #e2e8f0;background:#fff;color:#374151;
    transition:background .15s,color .15s,border-color .15s;"
    onmouseover="if(!this.disabled&&!this.classList.contains('pag-active')){this.style.background='#f1f5f9';this.style.borderColor='#cbd5e1'}"
    onmouseout="if(!this.disabled&&!this.classList.contains('pag-active')){this.style.background='#fff';this.style.borderColor='#e2e8f0'}"`;

  const activeBtn = `style="display:inline-flex;align-items:center;justify-content:center;
    min-width:36px;height:36px;padding:0 10px;border-radius:8px;font-size:.82rem;font-weight:600;
    cursor:default;border:1.5px solid #2563eb;background:#2563eb;color:#fff;"`;

  const disabledBtn = `style="display:inline-flex;align-items:center;justify-content:center;
    min-width:36px;height:36px;padding:0 10px;border-radius:8px;font-size:.82rem;font-weight:500;
    cursor:not-allowed;border:1.5px solid #e2e8f0;background:#f8fafc;color:#94a3b8;opacity:.6;"`;

  const dotsStyle = `style="display:inline-flex;align-items:center;justify-content:center;
    min-width:36px;height:36px;color:#94a3b8;font-size:.9rem;"`;

  let html = `<div style="display:flex;align-items:center;justify-content:center;gap:6px;padding:20px 0;">`;

  // ← Anterior
  if (actual === 1) {
    html += `<button disabled ${disabledBtn}>← Anterior</button>`;
  } else {
    html += `<button ${baseBtn} onclick="${fn}(${actual-1})">← Anterior</button>`;
  }

  // Números
  rango.forEach(p => {
    if (p === '…') {
      html += `<span ${dotsStyle}>…</span>`;
    } else if (p === actual) {
      html += `<button class="pag-active" ${activeBtn}>${p}</button>`;
    } else {
      html += `<button ${baseBtn} onclick="${fn}(${p})">${p}</button>`;
    }
  });

  // Siguiente →
  if (actual === total) {
    html += `<button disabled ${disabledBtn}>Siguiente →</button>`;
  } else {
    html += `<button ${baseBtn} onclick="${fn}(${actual+1})">Siguiente →</button>`;
  }

  html += `</div>`;
  return html;
}

function getPagRango(actual, total) {
  if (total <= 7) return Array.from({length:total}, (_,i) => i+1);
  if (actual <= 4) return [1,2,3,4,5,'…',total];
  if (actual >= total-3) return [1,'…',total-4,total-3,total-2,total-1,total];
  return [1,'…',actual-1,actual,actual+1,'…',total];
}