// demo/src/main/resources/static/js/organizador/org-perfil.js

function initPerfil() {
  const u = ORG.usuario;
  if (!u) return;

  const ini = ((u.nombre||'O')[0] + (u.apellido||'G')[0]).toUpperCase();
  document.getElementById('cf-name').textContent    = `${u.nombre||''} ${u.apellido||''}`.trim();
  document.getElementById('cf-av').textContent      = ini;
  document.getElementById('cf-id').textContent      = `ID · ${String(u.id||'').padStart(4,'0')}`;
  document.getElementById('pf-show-email').textContent = u.correo   || '—';
  document.getElementById('pf-show-tel').textContent   = u.telefono || '—';

  document.getElementById('pf-nom').value   = u.nombre   || '';
  document.getElementById('pf-ape').value   = u.apellido || '';
  document.getElementById('pf-email').value = u.correo   || '';
  document.getElementById('pf-tel').value   = u.telefono || '';
  document.getElementById('pf-pw').value    = '';

  // Estadísticas de la card
  cargarEstadisticas();
}

async function cargarEstadisticas() {
  try {
    const res = await fetch('/api/organizador/dashboard', { credentials: 'include' });
    const json = await res.json();
    if (json.success) {
      document.getElementById('cb-ev').textContent  = json.data.totalEventos;
      document.getElementById('cb-loc').textContent = json.data.totalLocalidades;
    }
  } catch {}
}

async function submitPerfil(e) {
  e.preventDefault();
  const btn = document.getElementById('btn-pf');
  const data = new URLSearchParams({
    nombre:   document.getElementById('pf-nom').value,
    correo:   document.getElementById('pf-email').value,
    telefono: document.getElementById('pf-tel').value,
  });
  const pw = document.getElementById('pf-pw').value;
  if (pw) data.append('clave', pw);

  btn.textContent = 'Guardando…'; btn.disabled = true;
  try {
    const res = await fetch('/api/usuarios/perfil', {
      method: 'PUT', credentials: 'include',
      headers: { 'Content-Type': 'application/x-www-form-urlencoded' },
      body: data
    });
    const json = await res.json();
    if (!res.ok) throw new Error(json.mensaje || 'Error al guardar');

    // Actualizar datos en memoria
    if (ORG.usuario) {
      ORG.usuario.nombre   = document.getElementById('pf-nom').value;
      ORG.usuario.apellido = document.getElementById('pf-ape').value;
      ORG.usuario.correo   = document.getElementById('pf-email').value;
      ORG.usuario.telefono = document.getElementById('pf-tel').value;
    }
    // Actualizar sidebar
    document.getElementById('sb-nm').textContent =
      `${ORG.usuario.nombre||''} ${ORG.usuario.apellido||''}`.trim();
    document.getElementById('sb-av').textContent =
      ((ORG.usuario.nombre||'O')[0]+(ORG.usuario.apellido||'G')[0]).toUpperCase();

    toast('Perfil actualizado ✓');
    initPerfil(); // refrescar card
  } catch (err) {
    toast(err.message, 'err');
  } finally {
    btn.textContent = 'Guardar cambios'; btn.disabled = false;
  }
}