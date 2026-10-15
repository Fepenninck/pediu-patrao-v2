// Alternar sidebar em telas pequenas
document.querySelector('.toggle-btn')?.addEventListener('click', () => {
    document.getElementById('sidebar').classList.toggle('active');
});

const sidebar = document.getElementById('sidebar');
const recolherMenu = document.getElementById('recolherMenu');
recolherMenu?.addEventListener('click', () => {
    if (window.matchMedia('(max-width: 767.98px)').matches) {
        sidebar.classList.remove('active');
        document.querySelector('.toggle-btn')?.focus();
        return;
    }
    const recolhido = sidebar.classList.toggle('collapsed');
    recolherMenu.setAttribute('aria-expanded', String(!recolhido));
    recolherMenu.setAttribute('aria-label', recolhido ? 'Expandir menu' : 'Recolher menu');
    recolherMenu.querySelector('i').className = recolhido ? 'bi bi-chevron-right' : 'bi bi-chevron-left';
});
sidebar?.querySelectorAll('a.nav-link').forEach(link => {
    link.title = link.textContent.trim();
});

// Alternar submenu
function toggleSubmenu(event, element) {
    event.preventDefault();
    element.parentElement.classList.toggle('open');
}
