/**
 * File: lichHenForm.js
 * Chức năng: Xử lý logic cho form tạo lịch hẹn, bao gồm:
 * 1. Tải danh sách bác sĩ tự động khi chọn khoa (Dropdown phụ thuộc).
 * 2. Xử lý lỗi `required` cho dropdown bị vô hiệu hóa.
 */

// Đợi cho đến khi toàn bộ cây DOM của trang được tải xong
document.addEventListener('DOMContentLoaded', function () {

    // 1. Lấy các phần tử DOM cần thiết
    const khoaSelect = document.getElementById('khoaId');
    const bacSiSelect = document.getElementById('bacSiId');

    // Lấy URL gốc một cách an toàn từ một thuộc tính data-* trên form
    // (Chúng ta sẽ thêm thuộc tính này vào JSP)
    const form = document.querySelector('.form-grid');
    if (!form || !khoaSelect || !bacSiSelect) {
        console.error("Không tìm thấy các phần tử form cần thiết.");
        return;
    }
    const baseUrl = form.dataset.doctorsUrl;

    // 2. Gán sự kiện 'change' cho dropdown Khoa
    khoaSelect.addEventListener('change', function () {
        const khoaId = this.value; // Lấy ID của khoa vừa chọn

        // Gọi hàm để tải bác sĩ
        loadDoctors(khoaId);
    });

    /**
     * Hàm gọi AJAX đến Controller để lấy danh sách bác sĩ theo khoaId
     * @param {string} khoaId - ID của khoa được chọn
     */
    function loadDoctors(khoaId) {
        if (!khoaId) {
            // Nếu người dùng chọn "-- Vui lòng chọn khoa...", reset lại
            bacSiSelect.innerHTML = '<option value="">-- Vui lòng chọn khoa --</option>';
            bacSiSelect.disabled = true;
            bacSiSelect.required = true;
            return;
        }

        // Hiển thị trạng thái đang tải
        bacSiSelect.innerHTML = '<option value="">-- Đang tải bác sĩ... --</option>';
        bacSiSelect.disabled = true;
        bacSiSelect.required = false; // Tắt 'required' tạm thời

        // 3. Gọi AJAX đến Controller
        fetch(baseUrl + khoaId)
                .then(response => {
                    if (!response.ok) {
                        // Ném lỗi với thông báo từ server nếu có
                        throw new Error(`Lỗi ${response.status}: ${response.statusText}`);
                    }
                    return response.json(); // Chuyển đổi phản hồi sang JSON
                })
                .then(data => {
                    // 4. Xử lý dữ liệu JSON (danh sách bác sĩ)
                    bacSiSelect.innerHTML = ''; // Xóa sạch

                    if (data.error) {
                        // Nếu server trả về lỗi JSON (ví dụ: { "error": "..." })
                        throw new Error(data.error);
                    }

                    if (data.length === 0) {
                        bacSiSelect.innerHTML = '<option value="">-- Khoa này không có bác sĩ --</option>';
                        bacSiSelect.disabled = true;
                        bacSiSelect.required = false; // Vẫn tắt
                    } else {
                        bacSiSelect.innerHTML = '<option value="">-- Chọn bác sĩ --</option>';
                        data.forEach(bacSi => {
                            const option = document.createElement('option');
                            option.value = bacSi.id;
                            // Sử dụng NhanVienDTO: hoTen và chuyenMon
                            option.textContent = `${bacSi.hoTen} (${bacSi.chuyenMon || 'N/A'})`;
                            bacSiSelect.appendChild(option);
                        });
                        bacSiSelect.disabled = false; // Kích hoạt lại
                        bacSiSelect.required = true; // Bật lại required
                    }
                })
                .catch(error => {
                    console.error('Lỗi AJAX:', error);
                    bacSiSelect.innerHTML = '<option value="">-- Lỗi khi tải dữ liệu --</option>';
                    bacSiSelect.disabled = true;
                    bacSiSelect.required = false; // Tắt required khi có lỗi
                });
    }
});