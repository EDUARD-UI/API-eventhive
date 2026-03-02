document.getElementById('loginForm').addEventListener('submit', async function (event) {
    event.preventDefault();
    
    const email = document.getElementById('email').value;
    const password = document.getElementById('password').value;

    const response = await fetch('/autentificacion/login', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/x-www-form-urlencoded',
        },
        body: `correo=${encodeURIComponent(email)}&clave=${encodeURIComponent(password)}`
    });

    const data = await response.json();

    if (data.success) {
        Swal.fire({
            icon: 'success',
            title: data.mensaje,
            text: data.mensaje,
            timer: 2000,
            showConfirmButton: false
        }).then(() => {
            window.location.replace(data.redirectUrl);
        });
    } else {
        Swal.fire({
            icon: 'error',
            title: data.mensaje,
            text: data.mensaje
        });
    }
});