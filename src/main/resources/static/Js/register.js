function setupPasswordToggle(inputSelector, btnSelector) {
    const input = document.querySelector(inputSelector);
    const btn = document.querySelector(btnSelector);

    btn.addEventListener('click', function () {
    // Alternar el tipo de input
    const type = input.getAttribute('type') === 'password' ? 'text' : 'password';
    input.setAttribute('type', type);

    // Alternar el icono
    this.classList.toggle('fa-eye');
    this.classList.toggle('fa-eye-slash');
});
}


// Inicializar para ambos campos

setupPasswordToggle('#password', '#btnPassword');
setupPasswordToggle('#confirmPassword', '#btnConfirm');
