// Variables globales para los gráficos
let chartEventosPorCategoria, chartValoracionesPorEvento;

// Función para inicializar gráficos de reportes
function inicializarGraficosReportes() {
    console.log('Inicializando gráficos de reportes...');
    
    // Destruir gráficos existentes para evitar duplicados
    if (chartEventosPorCategoria) {
        chartEventosPorCategoria.destroy();
    }
    if (chartValoracionesPorEvento) {
        chartValoracionesPorEvento.destroy();
    }
    
    // Cargar datos dinámicos
    cargarEventosPorCategoria();
    cargarValoracionesPorEvento();
}

// Cargar gráfico de eventos por categoría
function cargarEventosPorCategoria() {
    console.log('Cargando eventos por categoría...');
    
    fetch('/organizador/reportes/eventos-por-categoria', {
        method: 'GET',
        headers: {
            'Content-Type': 'application/json'
        },
        credentials: 'same-origin'
    })
    .then(response => {
        console.log('Response status:', response.status);
        if (!response.ok) {
            throw new Error('Error al cargar datos: ' + response.status);
        }
        return response.json();
    })
    .then(data => {
        console.log('Datos recibidos:', data);
        const ctx = document.getElementById('chartReporteCategorias');
        if (ctx) {
            const labels = Array.from(data.labels);
            const valores = Array.from(data.data);
            
            // Validar si hay datos
            if (labels.length === 0) {
                const container = ctx.parentElement;
                container.innerHTML = '<h3>Eventos por categoría</h3><p style="text-align: center; padding: 40px; color: #666;">No hay eventos registrados aún</p>';
                return;
            }
            
            chartEventosPorCategoria = new Chart(ctx, {
                type: 'bar',
                data: {
                    labels: labels,
                    datasets: [{
                        label: 'Cantidad de Eventos',
                        data: valores,
                        backgroundColor: [
                            'rgba(255, 99, 132, 0.6)',
                            'rgba(54, 162, 235, 0.6)',
                            'rgba(255, 206, 86, 0.6)',
                            'rgba(75, 192, 192, 0.6)',
                            'rgba(153, 102, 255, 0.6)',
                            'rgba(255, 159, 64, 0.6)'
                        ],
                        borderColor: [
                            'rgba(255, 99, 132, 1)',
                            'rgba(54, 162, 235, 1)',
                            'rgba(255, 206, 86, 1)',
                            'rgba(75, 192, 192, 1)',
                            'rgba(153, 102, 255, 1)',
                            'rgba(255, 159, 64, 1)'
                        ],
                        borderWidth: 1
                    }]
                },
                options: {
                    responsive: true,
                    maintainAspectRatio: true,
                    scales: {
                        y: {
                            beginAtZero: true,
                            ticks: {
                                stepSize: 1,
                                precision: 0
                            }
                        }
                    },
                    plugins: {
                        legend: {
                            display: true,
                            position: 'top'
                        },
                        title: {
                            display: false
                        }
                    }
                }
            });
            console.log('Gráfico de categorías creado exitosamente');
        }
    })
    .catch(error => {
        console.error('Error al cargar eventos por categoría:', error);
        const ctx = document.getElementById('chartReporteCategorias');
        if (ctx) {
            const container = ctx.parentElement;
            container.innerHTML = '<h3>Eventos por categoría</h3><p style="text-align: center; padding: 40px; color: #e74c3c;">Error al cargar los datos. Por favor, recarga la página.</p>';
        }
    });
}

// Cargar gráfico de valoraciones por evento
function cargarValoracionesPorEvento() {
    console.log('Cargando valoraciones por evento...');
    
    fetch('/organizador/reportes/valoraciones-por-evento', {
        method: 'GET',
        headers: {
            'Content-Type': 'application/json'
        },
        credentials: 'same-origin'
    })
    .then(response => {
        console.log('Response status:', response.status);
        if (!response.ok) {
            throw new Error('Error al cargar datos: ' + response.status);
        }
        return response.json();
    })
    .then(data => {
        console.log('Datos recibidos:', data);
        const ctx = document.getElementById('chartReporteValoraciones');
        if (ctx) {
            const labels = Array.from(data.labels);
            const valores = Array.from(data.data);
            
            // Validar si hay datos
            if (labels.length === 0) {
                const container = ctx.parentElement;
                container.innerHTML = '<h3>Valoraciones por evento</h3><p style="text-align: center; padding: 40px; color: #666;">No hay eventos registrados aún</p>';
                return;
            }
            
            chartValoracionesPorEvento = new Chart(ctx, {
                type: 'pie',
                data: {
                    labels: labels,
                    datasets: [{
                        data: valores,
                        backgroundColor: [
                            'rgba(255, 99, 132, 0.7)',
                            'rgba(54, 162, 235, 0.7)',
                            'rgba(255, 206, 86, 0.7)',
                            'rgba(75, 192, 192, 0.7)',
                            'rgba(153, 102, 255, 0.7)',
                            'rgba(255, 159, 64, 0.7)',
                            'rgba(201, 203, 207, 0.7)',
                            'rgba(255, 99, 71, 0.7)'
                        ],
                        borderColor: [
                            'rgba(255, 99, 132, 1)',
                            'rgba(54, 162, 235, 1)',
                            'rgba(255, 206, 86, 1)',
                            'rgba(75, 192, 192, 1)',
                            'rgba(153, 102, 255, 1)',
                            'rgba(255, 159, 64, 1)',
                            'rgba(201, 203, 207, 1)',
                            'rgba(255, 99, 71, 1)'
                        ],
                        borderWidth: 2
                    }]
                },
                options: {
                    responsive: true,
                    maintainAspectRatio: true,
                    plugins: {
                        legend: {
                            display: true,
                            position: 'right'
                        },
                        title: {
                            display: false
                        },
                        tooltip: {
                            callbacks: {
                                label: function(context) {
                                    const label = context.label || '';
                                    const value = context.parsed || 0;
                                    return label + ': ' + value + ' valoración(es)';
                                }
                            }
                        }
                    }
                }
            });
            console.log('Gráfico de valoraciones creado exitosamente');
        }
    })
    .catch(error => {
        console.error('Error al cargar valoraciones por evento:', error);
        const ctx = document.getElementById('chartReporteValoraciones');
        if (ctx) {
            const container = ctx.parentElement;
            container.innerHTML = '<h3>Valoraciones por evento</h3><p style="text-align: center; padding: 40px; color: #e74c3c;">Error al cargar los datos. Por favor, recarga la página.</p>';
        }
    });
}