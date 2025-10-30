function toggleQrCode() {
    var methodSelect = document.getElementById("phuongThuc");
    var qrContainer = document.getElementById("qrCodeContainer");
    var soTienInput = document.getElementById("soTien");
    var qrAmountSpan = document.getElementById("qrAmount");
    var maHoaDon = "${invoice.maHoaDon}"; // Lấy mã hóa đơn từ JSTL
    var qrContentSpan = document.getElementById("qrContent");

    if (methodSelect.value === "CHUYEN_KHOAN") {
        // 1. Hiển thị khối QR
        qrContainer.style.display = "block";

        // 2. Cập nhật số tiền (lấy từ ô input, phòng trường hợp người dùng sửa)
        var soTien = parseFloat(soTienInput.value) || 0;
        // Dùng hàm của trình duyệt để format tiền tệ (hiện đại hơn JSTL)
        qrAmountSpan.innerText = soTien.toLocaleString('vi-VN', {style: 'currency', currency: 'VND'});

        // 3. Cập nhật nội dung chuyển khoản
        qrContentSpan.innerText = "Thanh toan HD " + maHoaDon;

    } else {
        // Ẩn khối QR
        qrContainer.style.display = "none";
    }
}

// Bổ sung: Thêm sự kiện 'input' cho ô số tiền
// Để nếu người dùng sửa số tiền thì số tiền trên QR code cũng cập nhật theo
document.getElementById('soTien').addEventListener('input', function () {
    // Chỉ cập nhật nếu QR code đang được hiển thị
    if (document.getElementById('phuongThuc').value === 'CHUYEN_KHOAN') {
        var soTien = parseFloat(this.value) || 0;
        document.getElementById('qrAmount').innerText = soTien.toLocaleString('vi-VN', {style: 'currency', currency: 'VND'});
    }
});