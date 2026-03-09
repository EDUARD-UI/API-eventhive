// Formulario de contacto — solo feedback visual (no hay endpoint real aún)
document.addEventListener('DOMContentLoaded', () => {
  document.getElementById('btnEnviar').addEventListener('click', () => {
    const nombre  = document.getElementById('cNombre').value.trim();
    const email   = document.getElementById('cEmail').value.trim();
    const mensaje = document.getElementById('cMensaje').value.trim();

    if (!nombre || !email || !mensaje) {
      Swal.fire({ icon: 'warning', title: 'Campos requeridos', text: 'Completa todos los campos.', confirmButtonColor: '#007bff' });
      return;
    }

    // Cuando se integre el endpoint: fetch('/api/contacto', { method:'POST', body:... })
    Swal.fire({ icon: 'success', title: '¡Mensaje enviado!', text: 'Te responderemos pronto.', confirmButtonColor: '#007bff' })
      .then(() => {
        document.getElementById('cNombre').value  = '';
        document.getElementById('cEmail').value   = '';
        document.getElementById('cMensaje').value = '';
      });
  });
});