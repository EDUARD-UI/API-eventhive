// Crear/Actualizar Estado
document.querySelector('#modal-estado form').addEventListener('submit', async function(event) {
    event.preventDefault();
    
    const estadoId = document.getElementById('estado-id').value;
    const isEdit = estadoId && estadoId !== '';
    
    const formData = new FormData();
    if (isEdit) {
        formData.append('id', estadoId);
    }
    formData.append('nombre', document.getElementById('estado-nombre').value.trim());
    formData.append('descripcion', document.getElementById('estado-descripcion').value.trim());

    try {
        const url = isEdit ? '/estados/actualizar' : '/estados/crear';
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
                document.getElementById('modal-estado').classList.remove('show');
                // Mantener la sección activa
                const currentSection = document.querySelector('.content-section.active')?.id || 'seccion-estados';
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

// Editar Estado
document.addEventListener('click', function(e) {
    const editButton = e.target.closest('.btn-secondary[data-action="edit"]');
    if (editButton && editButton.closest('#seccion-estados')) {
        const row = editButton.closest('tr');
        const cells = row.querySelectorAll('td');
        
        // Llenar el modal con los datos actuales
        document.getElementById('estado-id').value = cells[0].textContent.trim();
        document.getElementById('estado-nombre').value = cells[1].textContent.trim();
        document.getElementById('estado-descripcion').value = cells[2].textContent.trim();
    }
});

// Eliminar Estado
document.addEventListener('click', function(e) {
    const deleteButton = e.target.closest('.btn-delete');
    if (deleteButton && deleteButton.closest('#seccion-estados')) {
        e.preventDefault();
        e.stopPropagation();
        
        const row = deleteButton.closest('tr');
        const estadoId = row.querySelector('td').textContent.trim();
        const estadoNombre = row.querySelectorAll('td')[1].textContent.trim();
        
        Swal.fire({
            title: '¿Estás seguro?',
            text: `¿Deseas eliminar el estado ${estadoNombre}?`,
            icon: 'warning',
            showCancelButton: true,
            confirmButtonColor: '#d33',
            cancelButtonColor: '#3085d6',
            confirmButtonText: 'Sí, eliminar',
            cancelButtonText: 'Cancelar'
        }).then(async (result) => {
            if (result.isConfirmed) {
                try {
                    const response = await fetch(`/estados/eliminar/${estadoId}`, {
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
                            sessionStorage.setItem('activeSection', 'seccion-estados');
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