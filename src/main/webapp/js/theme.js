// Nội dung CHUẨN cho file: /js/theme.js
document.addEventListener('DOMContentLoaded', function () {

    

    // ===================================================
    // LOGIC 2: TỰ ĐỘNG TÔ SÁNG LINK SIDEBAR
    // ===================================================
    try {
        const currentAction = window.location.search.split('action=')[1].split('&')[0];
        if (currentAction) {
            document.querySelectorAll('.nav-link').forEach(link => {
                if (link.dataset.link === currentAction) {
                    link.classList.add('active');
                } else {
                    link.classList.remove('active');
                }
            });
        } else {
            throw new Error("No action found, check homepage.");
        }
    } catch (e) {
        const homeLink = document.querySelector('.nav-link[data-link="listAllEncounters"]');
        if (homeLink) {
            homeLink.classList.add('active');
        }
    }

    // ===================================================
    // LOGIC 3: THU GỌN SIDEBAR
    // ===================================================
    const sidebarToggleBtn = document.getElementById('sidebar-toggle-btn');
    const sidebar = document.getElementById('sidebar-nav');
    const mainContent = document.querySelector('.main-content');
    const sidebarKey = 'sidebar-state'; // Đổi tên key để không trùng

    if (sidebarToggleBtn && sidebar && mainContent) {
        const applySidebarState = (state) => {
            if (state === 'collapsed') {
                sidebar.classList.add('sidebar-collapsed');
                mainContent.classList.add('sidebar-collapsed');
            } else {
                sidebar.classList.remove('sidebar-collapsed');
                mainContent.classList.remove('sidebar-collapsed');
            }
        };

        const savedSidebarState = localStorage.getItem(sidebarKey) || 'expanded';
        applySidebarState(savedSidebarState);

        sidebarToggleBtn.addEventListener('click', () => {
            const isCollapsed = sidebar.classList.contains('sidebar-collapsed');
            let newState = isCollapsed ? 'expanded' : 'collapsed';
            applySidebarState(newState);
            localStorage.setItem(sidebarKey, newState);
        });
    }
});