// Nội dung mới cho file: /js/danhSachThuoc.js

document.addEventListener('DOMContentLoaded', () => {

    // Lấy các phần tử của Modal
    const modalOverlay = document.getElementById('stock-modal-overlay');
    
    // Thoát nếu trang không có modal này
    if (!modalOverlay) return;

    const modalForm = document.getElementById('stock-modal-form');
    const closeButton = document.getElementById('stock-close-button');
    const cancelButton = document.getElementById('stock-cancel-button');

    // Các trường dữ liệu trong modal
    const modalTitle = document.getElementById('stock-modal-title');
    const thuocIdInput = document.getElementById('stock-thuocId-input');
    const thuocNameSpan = document.getElementById('stock-thuocName');
    const currentStockSpan = document.getElementById('stock-current');
    const soLuongThayDoiInput = document.getElementById('soLuongThayDoi');

    // Lắng nghe tất cả các nút "Cập nhật tồn kho"
    document.querySelectorAll('.update-stock-btn').forEach(button => {
        button.addEventListener('click', () => {
            // Lấy dữ liệu từ data-attributes của nút
            const thuocId = button.dataset.id;
            const thuocName = button.dataset.name;
            const currentStock = button.dataset.currentStock;

            // Điền dữ liệu vào modal
            modalTitle.textContent = `Cập nhật tồn kho: ${thuocName}`;
            thuocIdInput.value = thuocId;
            thuocNameSpan.textContent = thuocName;
            currentStockSpan.textContent = currentStock;

            // Xóa giá trị cũ và focus vào input
            soLuongThayDoiInput.value = '';

            // Hiển thị modal bằng cách thêm class 'active' (ĐÃ SỬA)
            modalOverlay.classList.add('active');
            soLuongThayDoiInput.focus();
        });
    });

    // Hàm để đóng modal
    const closeModal = () => {
        modalOverlay.classList.remove('active'); // (ĐÃ SỬA)
    };

    // Gán sự kiện đóng modal
    closeButton.addEventListener('click', closeModal);
    cancelButton.addEventListener('click', closeModal);

    // Đóng modal khi nhấp ra ngoài khu vực modal-content
    modalOverlay.addEventListener('click', (event) => {
        // Chỉ đóng nếu click vào chính overlay (phần nền mờ)
        if (event.target === modalOverlay) {
            closeModal();
        }
    });

    // Đóng modal khi nhấn phím Escape
    document.addEventListener('keydown', (event) => {
        if (event.key === 'Escape' && modalOverlay.classList.contains('active')) { // (ĐÃ SỬA)
            closeModal();
        }
    });

    // Ngăn form submit nếu số lượng thay đổi là 0
    modalForm.addEventListener('submit', (event) => {
        const soLuong = parseInt(soLuongThayDoiInput.value, 10);
        if (soLuong === 0 || isNaN(soLuong)) {
            alert('Vui lòng nhập số lượng thay đổi khác 0.');
            event.preventDefault(); // Ngăn form gửi đi
            soLuongThayDoiInput.focus();
        }
    });
});