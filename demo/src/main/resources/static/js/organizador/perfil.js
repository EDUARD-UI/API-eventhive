/* ════════════════════════════════════════
   EventHive — perfil.js
   Carga y edición del perfil del organizador.
════════════════════════════════════════ */

function initPerfil() {
  cargarPerfil();
  updateCardStats();
}

/* Llena el formulario con los datos actuales del usuario en sesión */
function cargarPerfil() {
  const u = Store.usuario;
  if (!u) return;

  document.getElementById('pf-nom').value   = u.nombre   || '';
  document.getElementById('pf-ape').value   = u.apellido || '';
  document.getElementById('pf-email').value = u.correo   || '';
  document.getElementById('pf-tel').value   = u.telefono || '';
  document.getElementById('pf-pw').value    = '';
}

/* Actualiza los datos visibles en la "credencial" */
function updateProfileCard(usuario) {
  if (!usuario) return;
  const ini = getInitials(usuario.nombre, usuario.apellido);

  document.getElementById('cf-name').textContent = `${usuario.nombre || ''} ${usuario.apellido || ''}`.trim();
  document.getElementById('cf-av').textContent   = ini;
  document.getElementById('cf-id').textContent   = `ID · ${String(usuario.id || '').padStart(4, '0')}`;

  /* datos de contacto visibles (debajo de la card) */
  document.getElementById('pf-show-email').textContent = usuario.correo   || '—';
  document.getElementById('pf-show-tel').textContent   = usuario.telefono || '—';
}

/* Estadísticas en el reverso de la card */
function updateCardStats() {
  document.getElementById('cb-ev').textContent  = Store.eventos.length;
  document.getElementById('cb-loc').textContent = Store.localidades.length;
  document.getElementById('cb-pr').textContent  = Store.promociones.length;

  /* ocupación promedio */
  const locs = Store.localidades.filter(l => l.capacidad > 0);
  const ocu  = locs.length
    ? Math.round(locs.reduce((a, l) => a + ((l.capacidad - (l.disponibles || 0)) / l.capacidad) * 100, 0) / locs.length)
    : 0;

  document.getElementById('cb-ocu-pct').textContent = ocu + '%';
  document.getElementById('cb-ocu-bar').style.width = ocu + '%';
}

/* Submit del formulario de edición */
async function submitPerfil(e) {
  e.preventDefault();
  const btn = document.getElementById('btn-pf');

  const data = {
    nombre:    document.getElementById('pf-nom').value,
    correo:    document.getElementById('pf-email').value,
    telefono:  document.getElementById('pf-tel').value,
  };
  const pw = document.getElementById('pf-pw').value;
  if (pw) data.clave = pw;

  btn.textContent = 'Guardando…'; btn.disabled = true;

  try {
    await UsuariosAPI.actualizarPerfil(data);

    /* Actualizar store y UI */
    if (Store.usuario) Object.assign(Store.usuario, data);
    updateProfileCard(Store.usuario);
    updateSidebarUser(Store.usuario);
    updateHeroUser(Store.usuario);

    toast('Perfil actualizado ✓', 'ok');

  } catch (err) {
    toast(err.message, 'err');
  } finally {
    btn.textContent = 'Guardar cambios'; btn.disabled = false;
  }
}
