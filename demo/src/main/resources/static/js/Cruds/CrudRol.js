// Crear/Actualizar Rol
document.querySelector('#modal-rol form').addEventListener('submit', async function(event) {
    event.preventDefault();
    
    const rolId = document.getElementById('rol-id').value;
    const isEdit = rolId && rolId !== '';
    const nombreRol = document.getElementById('rol-nombre').value.trim().toLowerCase();
    
    const formData = new FormData();
    if (isEdit) {
        formData.append('id', rolId);
    }
    formData.append('nombre', nombreRol); // Guardado en minúsculas
    formData.append('descripcion', document.getElementById('rol-descripcion').value.trim());
    formData.append('estadoId', document.getElementById('rol-estado').value);

    try {
        const url = isEdit ? '/roles/actualizar' : '/roles/crear';
        const response = await fetch(url, {
            method: 'POST',
            body: formData
        });

        const data = await response.json();

        if (data.success) {
            Swal.fire({
                icon: 'success',
                title: '¡Éxito!',
                text: data.mensaje,
                timer: 2000,
                showConfirmButton: false
            }).then(() => {
                document.getElementById('modal-rol').classList.remove('show');
                // Mantener la sección activa
                const currentSection = document.querySelector('.content-section.active')?.id || 'seccion-roles';
                sessionStorage.setItem('activeSection', currentSection);
                location.reload();
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
            title: 'Error de conexión',
            text: 'No se pudo conectar con el servidor'
        });
    }
});

// Editar Rol
document.addEventListener('click', function(e) {
    const editButton = e.target.closest('.btn-secondary[data-action="edit"]');
    if (editButton && editButton.closest('#seccion-roles')) {
        const row = editButton.closest('tr');
        const cells = row.querySelectorAll('td');
        
        // Llenar el formulario con los datos actuales
        document.getElementById('rol-id').value = cells[0].textContent.trim();
        // No convertir a minúsculas al editar para mostrar el valor original
        document.getElementById('rol-nombre').value = cells[1].textContent.trim();
        document.getElementById('rol-descripcion').value = cells[2].textContent.trim();
        
        // Buscar y seleccionar el estado por nombre
        const estadoNombre = cells[3].textContent.trim();
        const estadoSelect = document.getElementById('rol-estado');
        for (let option of estadoSelect.options) {
            if (option.text === estadoNombre) {
                option.selected = true;
                break;
            }
        }
    }
});

// Eliminar Rol
document.addEventListener('click', function(e) {
    const deleteButton = e.target.closest('.btn-delete');
    if (deleteButton && deleteButton.closest('#seccion-roles')) {
        e.preventDefault();
        e.stopPropagation();
        
        const row = deleteButton.closest('tr');
        const rolId = row.querySelector('td').textContent.trim();
        const rolNombre = row.querySelectorAll('td')[1].textContent.trim();
        
        Swal.fire({
            title: '¿Estás seguro?',
            text: `¿Deseas eliminar el rol ${rolNombre}?`,
            icon: 'warning',
            showCancelButton: true,
            confirmButtonColor: '#d33',
            cancelButtonColor: '#3085d6',
            confirmButtonText: 'Sí, eliminar',
            cancelButtonText: 'Cancelar'
        }).then(async (result) => {
            if (result.isConfirmed) {
                try {
                    const response = await fetch(`/roles/eliminar/${rolId}`, {
                        method: 'DELETE'
                    });
                    
                    const data = await response.json();
                    
                    if (data.success) {
                        Swal.fire({
                            icon: 'success',
                            title: 'Eliminado',
                            text: data.mensaje,
                            timer: 2000,
                            showConfirmButton: false
                        }).then(() => {
                            // Mantener la sección activa
                            sessionStorage.setItem('activeSection', 'seccion-roles');
                            location.reload();
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
                        title: 'Error de conexión',
                        text: 'No se pudo conectar con el servidor'
                    });
                }
            }
        });
    }
});