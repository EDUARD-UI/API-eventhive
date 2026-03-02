// Validaciones
function validarTexto(texto) {
    return /^[a-zA-ZáéíóúÁÉÍÓÚñÑ\s'-]+$/.test(texto);
}

function validarTelefono(telefono) {
    return /^[\d\s+\-()]{7,20}$/.test(telefono);
}

function validarEmail(email) {
    return /^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(email);
}

function validarClave(clave) {
    return clave.length >= 6;
}

document.addEventListener('DOMContentLoaded', function() {
    // Función para mostrar/ocultar contraseña
    const togglePassword = document.querySelector('.toggle-password');
    if (togglePassword) {
        togglePassword.addEventListener('click', function() {
            const passwordInput = document.getElementById('password');
            const icon = this.querySelector('i');
            
            if (passwordInput.type === 'password') {
                passwordInput.type = 'text';
                icon.classList.replace('fa-eye', 'fa-eye-slash');
            } else {
                passwordInput.type = 'password';
                icon.classList.replace('fa-eye-slash', 'fa-eye');
            }
        });
    }

    // Manejo del formulario de registro
    const signinForm = document.getElementById('signinForm');
    if (signinForm) {
        signinForm.addEventListener('submit', async function(event) {
            event.preventDefault();
            
            const nombre = document.getElementById('nombre').value.trim();
            const apellido = document.getElementById('apellido').value.trim();
            const correo = document.getElementById('email').value.trim();
            const telefono = document.getElementById('telefono').value.trim();
            const clave = document.getElementById('password').value.trim();

            // Validar nombre
            if (!nombre || nombre.length < 2 || nombre.length > 50) {
                Swal.fire({
                    icon: 'error',
                    title: 'Nombre inválido',
                    text: 'El nombre debe tener entre 2 y 50 caracteres'
                });
                return;
            }

            if (!validarTexto(nombre)) {
                Swal.fire({
                    icon: 'error',
                    title: 'Nombre inválido',
                    text: 'El nombre solo puede contener letras, espacios, apostrofes y guiones'
                });
                return;
            }

            // Validar apellido
            if (!apellido || apellido.length < 2 || apellido.length > 50) {
                Swal.fire({
                    icon: 'error',
                    title: 'Apellido inválido',
                    text: 'El apellido debe tener entre 2 y 50 caracteres'
                });
                return;
            }

            if (!validarTexto(apellido)) {
                Swal.fire({
                    icon: 'error',
                    title: 'Apellido inválido',
                    text: 'El apellido solo puede contener letras, espacios, apostrofes y guiones'
                });
                return;
            }

            // Validar email
            if (!validarEmail(correo)) {
                Swal.fire({
                    icon: 'error',
                    title: 'Correo inválido',
                    text: 'Por favor ingresa un correo electrónico válido'
                });
                return;
            }

            // Validar teléfono
            if (!telefono) {
                Swal.fire({
                    icon: 'error',
                    title: 'Teléfono requerido',
                    text: 'Por favor ingresa tu número de teléfono'
                });
                return;
            }

            if (!validarTelefono(telefono)) {
                Swal.fire({
                    icon: 'error',
                    title: 'Teléfono inválido',
                    text: 'El teléfono debe contener entre 7 y 20 caracteres (números, espacios, +, -, ())'
                });
                return;
            }

            // Validar contraseña
            if (!validarClave(clave)) {
                Swal.fire({
                    icon: 'error',
                    title: 'Contraseña inválida',
                    text: 'La contraseña debe tener al menos 6 caracteres'
                });
                return;
            }

            const formData = new FormData();
            formData.append('nombre', nombre);
            formData.append('apellido', apellido);
            formData.append('correo', correo);
            formData.append('telefono', telefono);
            formData.append('clave', clave);

            try {
                const response = await fetch('/autentificacion/registrar-organizador', {
                    method: 'POST',
                    body: formData
                });

                const data = await response.json();

                if (data.success) {
                    Swal.fire({
                        icon: 'success',
                        title: '¡Registro exitoso!',
                        text: data.mensaje,
                        timer: 2000
                    }).then(() => {
                        window.location.href = '/login';
                    });
                } else {
                    Swal.fire({
                        icon: 'error',
                        title: 'Error',
                        text: data.mensaje
                    });
                }
            } catch (error) {
                Swal.fire({
                    icon: 'error',
                    title: 'Error',
                    text: 'Error de conexión'
                });
            }
        });
    }

    // Validación en tiempo real para nombre
    const nombreInput = document.getElementById('nombre');
    if (nombreInput) {
        nombreInput.addEventListener('input', function(e) {
            this.value = this.value.replace(/[^a-zA-ZáéíóúÁÉÍÓÚñÑ\s'-]/g, '');
        });
    }

    // Validación en tiempo real para apellido
    const apellidoInput = document.getElementById('apellido');
    if (apellidoInput) {
        apellidoInput.addEventListener('input', function(e) {
            this.value = this.value.replace(/[^a-zA-ZáéíóúÁÉÍÓÚñÑ\s'-]/g, '');
        });
    }

    // Validación en tiempo real para teléfono
    const telefonoInput = document.getElementById('telefono');
    if (telefonoInput) {
        telefonoInput.addEventListener('input', function(e) {
            this.value = this.value.replace(/[^\d\s+\-()]/g, '');
            if (this.value.length > 20) {
                this.value = this.value.slice(0, 20);
            }
        });
    }
});