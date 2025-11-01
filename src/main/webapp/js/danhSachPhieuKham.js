// Nội dung cho file: /js/danhSachPhieuKham.js

document.addEventListener('DOMContentLoaded', function () {
    // ===================================================
    // LOGIC CHUYỂN ĐỔI GRID/LIST VIEW
    // ===================================================
    const gridViewBtn = document.getElementById('grid-view-btn');
    const listViewBtn = document.getElementById('list-view-btn');
    const cardGrid = document.querySelector('.card-grid');
    const viewKey = 'view-preference';

    // Hàm để áp dụng giao diện (grid/list)
    const applyView = (view) => {
        // Phải kiểm tra xem các phần tử có tồn tại không
        if (!cardGrid || !listViewBtn || !gridViewBtn) {
            return;
        }

        if (view === 'list') {
            cardGrid.classList.add('list-view');
            listViewBtn.classList.add('active');
            gridViewBtn.classList.remove('active');
        } else { // Mặc định là 'grid'
            cardGrid.classList.remove('list-view');
            gridViewBtn.classList.add('active');
            listViewBtn.classList.remove('active');
        }
    };

    // Lắng nghe sự kiện click (chỉ khi nút tồn tại)
    if (gridViewBtn) {
        gridViewBtn.addEventListener('click', () => {
            applyView('grid');
            localStorage.setItem(viewKey, 'grid');
        });
    }

    if (listViewBtn) {
        listViewBtn.addEventListener('click', () => {
            applyView('list');
            localStorage.setItem(viewKey, 'list');
        });
    }

    // Kiểm tra và áp dụng giao diện đã lưu khi tải trang
    const savedView = localStorage.getItem(viewKey) || 'grid'; // Mặc định là grid
    applyView(savedView);
});