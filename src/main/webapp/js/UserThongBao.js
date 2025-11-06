/* ==========================================================================
     LOGIC CHO TRANG THÔNG BÁO NGƯỜI DÙNG (thongBaoUser.js)
 ========================================================================== */

document.addEventListener('DOMContentLoaded', function () {

    // Tìm tất cả các form có class .form-delete-confirm
    const deleteForms = document.querySelectorAll('.form-delete-confirm');

    deleteForms.forEach(form => {
        form.addEventListener('submit', function (event) {

            // Hiển thị hộp thoại xác nhận
            const confirmed = confirm('Bạn có chắc chắn muốn xóa thông báo này?');

            // Nếu người dùng KHÔNG đồng ý (nhấn "Cancel")
            if (!confirmed) {
                event.preventDefault(); // Ngăn chặn form được gửi đi
            }
        });
    });
});