document.addEventListener('DOMContentLoaded', function() {
    const formPerfil = document.getElementById('form-perfil');
    const btnEliminarCuenta = document.getElementById('btn-eliminar-cuenta');

    // Validar que solo contenga letras y espacios
    function validarTexto(texto) {
        return /^[a-zA-ZáéíóúÁÉÍÓÚñÑ\s]+$/.test(texto);
    }

    // Validar teléfono (solo números, mínimo 7 dígitos, máximo 15)
    function validarTelefono(telefono) {
        return /^\d{7,15}$/.test(telefono);
    }

    // Validar email
    function validarEmail(email) {
        return /^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(email);
    }

    // Validar contraseña (mínimo 6 caracteres)
    function validarClave(clave) {
        return clave.length >= 6;
    }

    // Enviar formulario de edición de perfil
    formPerfil.addEventListener('submit', async function(e) {
        e.preventDefault();

        const id = document.getElementById('perfil-id').value;
        const nombre = document.getElementById('perfil-nombre').value.trim();
        const apellido = document.getElementById('perfil-apellido').value.trim();
        const correo = document.getElementById('perfil-correo').value.trim();
        const telefono = document.getElementById('perfil-telefono').value.trim();
        const clave = document.getElementById('perfil-clave').value;

        // Validaciones básicas
        if (!nombre || !apellido || !correo || !telefono) {
            Swal.fire({
                icon: 'error',
                title: 'Campos incompletos',
                text: 'Todos los campos son obligatorios',
                confirmButtonText: 'Aceptar'
            });
            return;
        }

        // Validar nombre
        if (!validarTexto(nombre)) {
            Swal.fire({
                icon: 'error',
                title: 'Nombre inválido',
                text: 'El nombre solo puede contener letras y espacios',
                confirmButtonText: 'Aceptar'
            });
            return;
        }

        if (nombre.length < 2 || nombre.length > 50) {
            Swal.fire({
                icon: 'error',
                title: 'Nombre inválido',
                text: 'El nombre debe tener entre 2 y 50 caracteres',
                confirmButtonText: 'Aceptar'
            });
            return;
        }

        // Validar apellido
        if (!validarTexto(apellido)) {
            Swal.fire({
                icon: 'error',
                title: 'Apellido inválido',
                text: 'El apellido solo puede contener letras y espacios',
                confirmButtonText: 'Aceptar'
            });
            return;
        }

        if (apellido.length < 2 || apellido.length > 50) {
            Swal.fire({
                icon: 'error',
                title: 'Apellido inválido',
                text: 'El apellido debe tener entre 2 y 50 caracteres',
                confirmButtonText: 'Aceptar'
            });
            return;
        }

        // Validar email
        if (!validarEmail(correo)) {
            Swal.fire({
                icon: 'error',
                title: 'Correo inválido',
                text: 'Por favor ingresa un correo electrónico válido',
                confirmButtonText: 'Aceptar'
            });
            return;
        }

        // Validar teléfono
        if (!validarTelefono(telefono)) {
            Swal.fire({
                icon: 'error',
                title: 'Teléfono inválido',
                text: 'El teléfono debe contener solo números (7-15 dígitos)',
                confirmButtonText: 'Aceptar'
            });
            return;
        }

        // Validar contraseña si se proporcionó
        if (clave && !validarClave(clave)) {
            Swal.fire({
                icon: 'error',
                title: 'Contraseña inválida',
                text: 'La contraseña debe tener al menos 6 caracteres',
                confirmButtonText: 'Aceptar'
            });
            return;
        }

        // Confirmación antes de enviar
        const confirmResult = await Swal.fire({
            title: '¿Guardar cambios?',
            text: "¿Estás seguro de que quieres actualizar tu perfil?",
            icon: 'question',
            showCancelButton: true,
            confirmButtonColor: '#3085d6',
            cancelButtonColor: '#d33',
            confirmButtonText: 'Sí, guardar',
            cancelButtonText: 'Cancelar'
        });

        if (!confirmResult.isConfirmed) {
            return;
        }

        // Preparar datos para enviar
        const formData = new FormData();
        formData.append('id', id);
        formData.append('nombre', nombre);
        formData.append('apellido', apellido);
        formData.append('correo', correo);
        formData.append('telefono', telefono);
        
        // Solo enviar clave si se proporcionó una nueva
        if (clave) {
            formData.append('clave', clave);
        }

        // Enviar solicitud al servidor
        try {
            const response = await fetch('/editarPerfil', {
                method: 'POST',
                body: formData
            });

            const resultado = await response.json();

            if (resultado.success) {
                await Swal.fire({
                    icon: 'success',
                    title: '¡Éxito!',
                    text: resultado.mensaje,
                    confirmButtonText: 'Aceptar'
                });
                
                // Limpiar campo de contraseña
                document.getElementById('perfil-clave').value = '';
                
                // Recargar la página para actualizar datos de sesión
                window.location.reload();
            } else {
                Swal.fire({
                    icon: 'error',
                    title: 'Error',
                    text: resultado.mensaje,
                    confirmButtonText: 'Aceptar'
                });
            }
        } catch (error) {
            console.error('Error al actualizar perfil:', error);
            Swal.fire({
                icon: 'error',
                title: 'Error',
                text: 'Error de conexión',
                confirmButtonText: 'Aceptar'
            });
        }
    });

    // Eliminar cuenta
    btnEliminarCuenta.addEventListener('click', function() {
        const id = document.getElementById('perfil-id').value;
        
        Swal.fire({
            title: '¿Eliminar cuenta?',
            text: "¡Esta acción no se puede deshacer!",
            icon: 'warning',
            showCancelButton: true,
            confirmButtonColor: '#d33',
            cancelButtonColor: '#3085d6',
            confirmButtonText: 'Sí, eliminar',
            cancelButtonText: 'Cancelar'
        }).then((result) => {
            if (result.isConfirmed) {
                eliminarCuenta(id);
            }
        });
    });

    // Función para eliminar cuenta
    async function eliminarCuenta(id) {
        try {
            const response = await fetch(`/usuarios/eliminar/${id}`, {
                method: 'DELETE'
            });

            const resultado = await response.json();

            if (resultado.success) {
                Swal.fire({
                    icon: 'success',
                    title: '¡Éxito!',
                    text: resultado.mensaje,
                    confirmButtonText: 'Aceptar'
                }).then(() => {
                    // Redirigir al login
                    window.location.href = '/autentificacion/logout';
                });
            } else {
                Swal.fire({
                    icon: 'error',
                    title: 'Error',
                    text: resultado.mensaje,
                    confirmButtonText: 'Aceptar'
                });
            }
        } catch (error) {
            console.error('Error al eliminar cuenta:', error);
            Swal.fire({
                icon: 'error',
                title: 'Error',
                text: 'Error de conexión',
                confirmButtonText: 'Aceptar'
            });
        }
    }

    // Validación en tiempo real para nombre
    const inputNombre = document.getElementById('perfil-nombre');
    inputNombre.addEventListener('input', function(e) {
        // Remover números y caracteres especiales
        this.value = this.value.replace(/[^a-zA-ZáéíóúÁÉÍÓÚñÑ\s]/g, '');
    });

    // Validación en tiempo real para apellido
    const inputApellido = document.getElementById('perfil-apellido');
    inputApellido.addEventListener('input', function(e) {
        // Remover números y caracteres especiales
        this.value = this.value.replace(/[^a-zA-ZáéíóúÁÉÍÓÚñÑ\s]/g, '');
    });

    // Validación en tiempo real para teléfono
    const inputTelefono = document.getElementById('perfil-telefono');
    inputTelefono.addEventListener('input', function(e) {
        // Remover cualquier carácter que no sea número
        this.value = this.value.replace(/[^\d]/g, '');
        
        // Limitar a 15 dígitos
        if (this.value.length > 15) {
            this.value = this.value.slice(0, 15);
        }
    });
});