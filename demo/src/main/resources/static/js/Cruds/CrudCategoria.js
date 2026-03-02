document.querySelector('#modal-categoria form').addEventListener('submit', async function(event) {
    event.preventDefault();
    
    const categoriaId = document.getElementById('categoria-id').value;
    const isEdit = categoriaId && categoriaId !== '';
    
    const formData = new FormData();
    if (isEdit) {
        formData.append('id', categoriaId);
    }
    formData.append('nombre', document.getElementById('categoria-nombre').value.trim());
    
    // Agregar foto si existe
    const fotoInput = document.getElementById('categoria-foto');
    if (fotoInput && fotoInput.files.length > 0) {
        formData.append('foto', fotoInput.files[0]);
    }

    try {
        const url = isEdit ? '/categorias/actualizar' : '/categorias/crear';
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
                document.getElementById('modal-categoria').classList.remove('show');
                // Mantener la sección activa
                const currentSection = document.querySelector('.content-section.active')?.id || 'seccion-categorias';
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

// Editar Categoría
document.addEventListener('click', function(e) {
    const editButton = e.target.closest('.btn-secondary[data-action="edit"]');
    if (editButton && editButton.closest('#seccion-categorias')) {
        const row = editButton.closest('tr');
        const cells = row.querySelectorAll('td');
        
        // Llenar el modal con los datos actuales
        document.getElementById('categoria-id').value = cells[0].textContent.trim();
        document.getElementById('categoria-nombre').value = cells[1].textContent.trim();
    }
});

// Eliminar Categoría
document.addEventListener('click', function(e) {
    const deleteButton = e.target.closest('.btn-delete');
    if (deleteButton && deleteButton.closest('#seccion-categorias')) {
        e.preventDefault();
        e.stopPropagation();
        
        const row = deleteButton.closest('tr');
        const categoriaId = row.querySelector('td').textContent.trim();
        const categoriaNombre = row.querySelectorAll('td')[1].textContent.trim();
        
        Swal.fire({
            title: '¿Estás seguro?',
            text: `¿Deseas eliminar la categoría ${categoriaNombre}?`,
            icon: 'warning',
            showCancelButton: true,
            confirmButtonColor: '#d33',
            cancelButtonColor: '#3085d6',
            confirmButtonText: 'Sí, eliminar',
            cancelButtonText: 'Cancelar'
        }).then(async (result) => {
            if (result.isConfirmed) {
                try {
                    const response = await fetch(`/categorias/eliminar/${categoriaId}`, {
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
                            sessionStorage.setItem('activeSection', 'seccion-categorias');
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