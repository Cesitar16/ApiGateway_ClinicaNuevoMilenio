// --- LÓGICA PARA CAMBIAR ENTRE FORMULARIOS ---
const formWrapper = document.getElementById('form-wrapper');
const showRegisterBtn = document.getElementById('show-register-btn');
const showLoginBtn = document.getElementById('show-login-btn'); 

if (showRegisterBtn) {
    showRegisterBtn.addEventListener('click', () => {
        formWrapper.classList.add('active');
    });
}

if (showLoginBtn) {
    showLoginBtn.addEventListener('click', () => {
        formWrapper.classList.remove('active');
    });
}


// --- LÓGICA PARA APLICAR ESTILOS A CAMPOS DE REGISTRO ---
const inputBaseClasses = 'block w-full px-4 py-3 bg-gray-100 rounded-full text-gray-700 peer focus:outline-none focus:ring-2 focus:ring-blue-500 transition';
const labelBaseClasses = 'absolute text-gray-500 duration-300 transform -translate-y-4 scale-75 top-4 z-10 origin-[0] left-4 peer-placeholder-shown:scale-100 peer-placeholder-shown:translate-y-0 peer-focus:scale-75 peer-focus:-translate-y-4';

document.querySelectorAll('.input-float').forEach(el => el.className = inputBaseClasses);
document.querySelectorAll('.label-float').forEach(el => el.className = labelBaseClasses);


const loginForm = document.getElementById('login-form');
const loginContainer = document.getElementById('login-form-container');
const usernameInput = document.getElementById('login-username');
const passwordInput = document.getElementById('login-password');
const errorMessage = document.getElementById('login-error-message');

if (loginForm) {
    loginForm.addEventListener('submit', async (event) => {
        event.preventDefault();
        errorMessage.classList.add('hidden');

        const username = usernameInput.value;
        const password = passwordInput.value;

        try {
            const response = await fetch('http://localhost:8888/api/auth/login', {
                method: 'POST',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify({
                    nombreUsuario: username,
                    password: password
                })
            });

            if (response.ok) {
                const data = await response.json();

                localStorage.setItem('authToken', data.token);
                localStorage.setItem('username', data.nombreUsuario);
                localStorage.setItem('userRole', data.rol);
                localStorage.setItem('userId', data.userId);

                // --- LÓGICA DE REDIRECCIÓN CORREGIDA ---
                const userRole = data.rol.toUpperCase();

                if (userRole === 'MEDICO') {
                    window.location.href = '../VistaPrincipalMedico/index.html';

                } else if (userRole === 'JEFE DE PABELLON') { // <-- CORRECCIÓN: Se usa un espacio en lugar de guion bajo
                    window.location.href = '../VistaPrincipalJefeDePabellon/gestion-reservas.html';

                } else if (userRole === 'JEFE DE SERVICIOS') { // O el nombre exacto del rol
                      window.location.href = '../VistaJefeServicios/index.html';
                  }else {
                    loginContainer.innerHTML = `
                        <div class="text-center p-8 flex flex-col justify-center h-full animate-fade-in-up">
                            <h1 class="text-2xl font-bold text-gray-800">¡Bienvenido, ${data.nombreUsuario}!</h1>
                            <p class="text-gray-600 mt-2">Rol: ${data.rol}</p>
                            <p class="mt-6 text-sm text-amber-600 bg-amber-100 p-3 rounded-lg">
                                Inicio de sesión exitoso. El dashboard para tu rol aún está en desarrollo.
                            </p>
                            <button onclick="localStorage.clear(); window.location.reload();" class="w-full bg-red-500 hover:bg-red-600 text-white font-bold py-3 px-4 rounded-full mt-6 transition-all duration-300">
                                Cerrar Sesión
                            </button>
                        </div>
                    `;
                }
            } else {
                const errorData = await response.json();
                errorMessage.textContent = errorData.message || 'Usuario o contraseña incorrectos.';
                errorMessage.classList.remove('hidden');
            }
        } catch (error) {
            console.error('Error de conexión:', error);
            errorMessage.textContent = 'No se pudo conectar al servidor. Inténtelo más tarde.';
            errorMessage.classList.remove('hidden');
        }
    });
}