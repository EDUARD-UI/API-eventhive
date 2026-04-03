const BASE = '';

document.addEventListener('DOMContentLoaded', () => {
  document.getElementById('togglePwd').addEventListener('click', () => {
    const p = document.getElementById('password');
    const i = document.querySelector('#togglePwd i');
    p.type = p.type === 'password' ? 'text' : 'password';
    i.className = p.type === 'password' ? 'far fa-eye text-sm' : 'far fa-eye-slash text-sm';
  });

  document.getElementById('btnRegistrar').addEventListener('click', async () => {
    const nombre   = document.getElementById('nombre').value.trim();
    const apellido = document.getElementById('apellido').value.trim();
    const email    = document.getElementById('email').value.trim();
    const telefono = document.getElementById('telefono').value.trim();
    const password = document.getElementById('password').value;

    if (!nombre || !apellido || !email || !telefono || !password) {
      Swal.fire({ icon: 'warning', title: 'Campos requeridos', text: 'Completa todos los campos.', confirmButtonColor: '#007bff' });
      return;
    }

    try {
      const res = await fetch(`${BASE}/api/auth/registrar-organizador`, {
        method: 'POST',
        credentials: 'include',
        headers: { 'Content-Type': 'application/x-www-form-urlencoded' },
        body: new URLSearchParams({ nombre, apellido, correo: email, telefono, clave: password })
      });
      const json = await res.json();

      if (res.ok) {
        await Swal.fire({ icon: 'success', title: '¡Cuenta creada!', text: json.message || 'Tu cuenta de organizador fue creada.', confirmButtonColor: '#007bff' });
        window.location.href = '/pages/login.html';
      } else {
        Swal.fire({ icon: 'error', title: 'Error', text: json.message || 'No se pudo registrar.', confirmButtonColor: '#007bff' });
      }
    } catch {
      Swal.fire({ icon: 'error', title: 'Error de conexión', text: 'No se pudo conectar al servidor.', confirmButtonColor: '#007bff' });
    }
  });
});