
document.addEventListener('DOMContentLoaded', function () {

    // 1. Lấy các phần tử DOM đã được cập nhật
    const lichHenSelect = document.getElementById('lichHenId');
    const benhNhanSelect = document.getElementById('benhNhanId_select'); // ✨ Sửa ID
    const benhNhanHidden = document.getElementById('benhNhanId_hidden'); // ✨ Lấy input ẩn

// 2. Gán sự kiện 'change' cho dropdown Lịch Hẹn
    lichHenSelect.addEventListener('change', function () {
        const selectedOption = this.options[this.selectedIndex];
        const patientId = selectedOption.dataset.patientId;

        if (patientId) {
            // 3. Tự động chọn đúng bệnh nhân (chỉ để hiển thị)
            benhNhanSelect.value = patientId;

            // 4. Khóa dropdown Bệnh Nhân lại
            benhNhanSelect.disabled = true;

            // 5. ✨ GÁN GIÁ TRỊ CHO INPUT ẨN (Quan trọng nhất) ✨
            // Giá trị này sẽ được gửi đến server
            benhNhanHidden.value = patientId;

        } else {
            // Nếu chọn "-- Không có --", mở lại dropdown Bệnh Nhân
            benhNhanSelect.disabled = false;
            // Và xóa giá trị của input ẩn
            benhNhanHidden.value = "";
            // Tự động chọn lại giá trị cho input ẩn
            benhNhanHidden.value = benhNhanSelect.value;
        }
    });

// ✨ THÊM: Xử lý trường hợp người dùng không chọn lịch hẹn
// Đảm bảo input ẩn luôn có giá trị đúng
    if (benhNhanSelect) {
        benhNhanSelect.addEventListener('change', function () {
            if (!this.disabled) {
                benhNhanHidden.value = this.value;
            }
        });
    }
}

);
