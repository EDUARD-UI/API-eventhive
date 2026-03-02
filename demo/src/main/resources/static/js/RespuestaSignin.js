document.addEventListener('DOMContentLoaded', function() {

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

// Mostrar / esconder contraseña
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

const signinForm = document.getElementById('signinForm');

if (signinForm) {
    signinForm.addEventListener('submit', async function(event) {
        event.preventDefault();

        const nombre = document.getElementById('nombre').value.trim();
        const apellido = document.getElementById('apellido').value.trim();
        const correo = document.getElementById('email').value.trim();
        const telefono = document.getElementById('telefono').value.trim();
        const clave = document.getElementById('password').value.trim();

        // Validación nombre
        if (!nombre || nombre.length < 2 || nombre.length > 50) {
            Swal.fire({ icon: 'error', title: 'Nombre inválido', text: 'El nombre debe tener entre 2 y 50 caracteres' });
            return;
        }

        if (!validarTexto(nombre)) {
            Swal.fire({ icon: 'error', title: 'Nombre inválido', text: 'Solo se permiten letras y espacios' });
            return;
        }

        // Validación apellido
        if (!apellido || apellido.length < 2 || apellido.length > 50) {
            Swal.fire({ icon: 'error', title: 'Apellido inválido', text: 'El apellido debe tener entre 2 y 50 caracteres' });
            return;
        }

        if (!validarTexto(apellido)) {
            Swal.fire({ icon: 'error', title: 'Apellido inválido', text: 'Solo se permiten letras y espacios' });
            return;
        }

        // Email
        if (!validarEmail(correo)) {
            Swal.fire({ icon: 'error', title: 'Correo inválido', text: 'Ingresa un correo válido' });
            return;
        }

        // Teléfono
        if (!telefono) {
            Swal.fire({ icon: 'error', title: 'Teléfono requerido', text: 'Por favor ingresa tu teléfono' });
            return;
        }

        if (!validarTelefono(telefono)) {
            Swal.fire({ icon: 'error', title: 'Teléfono inválido', text: 'Formato de teléfono incorrecto' });
            return;
        }

        // Contraseña
        if (!validarClave(clave)) {
            Swal.fire({ icon: 'error', title: 'Contraseña inválida', text: 'Debe tener al menos 6 caracteres' });
            return;
        }

        // 📌 El backend exige rol y estado
        const rol = 2;     // Cliente por defecto
        const estado = 1;  // Activo por defecto

        const formData = new FormData();
        formData.append('nombre', nombre);
        formData.append('apellido', apellido);
        formData.append('correo', correo);
        formData.append('telefono', telefono);
        formData.append('clave', clave);
        formData.append('rol', rol);
        formData.append('estado', estado);

        try {
            const response = await fetch('/usuarios/crear', {
                method: 'POST',
                body: formData
            });

            let data;
            try {
                data = await response.json();
            } catch (e) {
                Swal.fire({ icon: 'error', title: 'Error', text: 'La respuesta del servidor no es válida' });
                return;
            }

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
                Swal.fire({ icon: 'error', title: 'Error', text: data.mensaje });
            }

        } catch (error) {
            Swal.fire({ icon: 'error', title: 'Error', text: 'No se pudo conectar con el servidor' });
        }
    });
}

// Validación en tiempo real
const nombreInput = document.getElementById('nombre');
if (nombreInput) {
    nombreInput.addEventListener('input', function() {
        this.value = this.value.replace(/[^a-zA-ZáéíóúÁÉÍÓÚñÑ\s'-]/g, '');
    });
}

const apellidoInput = document.getElementById('apellido');
if (apellidoInput) {
    apellidoInput.addEventListener('input', function() {
        this.value = this.value.replace(/[^a-zA-ZáéíóúÁÉÍÓÚñÑ\s'-]/g, '');
    });
}

const telefonoInput = document.getElementById('telefono');
if (telefonoInput) {
    telefonoInput.addEventListener('input', function() {
        this.value = this.value.replace(/[^\d\s+\-()]/g, '');
        if (this.value.length > 20) {
            this.value = this.value.slice(0, 20);
        }
    });
}

});
