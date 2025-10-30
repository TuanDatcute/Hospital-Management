<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>

<!DOCTYPE html>
<html lang="vi">
    <head>
        <meta charset="UTF-8">
        <title>Bệnh Án - ${phieuKham.maPhieuKham}</title>
        <link rel="stylesheet" href="<c:url value='/css/InBenhAn.css'/>">
        <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.5.1/css/all.min.css">
        <link href="https://fonts.googleapis.com/css2?family=Be+Vietnam+Pro:wght@400;500;600;700&display=swap" rel="stylesheet">
    </head>
    <body>

        <div class="page-controls">
            <div class="action-buttons">
                <a href="javascript:history.back()" class="btn btn-secondary"><i class="fas fa-arrow-left"></i> Quay Lại</a>
                <button onclick="window.print()" class="btn btn-primary"><i class="fas fa-print"></i> In Bệnh Án</button>
            </div>
            <div class="theme-switch-wrapper">
                <label class="theme-switch" for="theme-toggle">
                    <input type="checkbox" id="theme-toggle" />
                    <div class="slider round">

                        <span class="sun-icon"><i class="fas fa-sun"></i></span>
                        <span class="moon-icon"><i class="fas fa-moon"></i></span>
                    </div>
                </label>
            </div>      
        </div>


        <div class="page-container">
            <div class="header">
                <h1>Bệnh Án Điện Tử</h1>
                <h2>Mã Phiếu Khám: ${phieuKham.maPhieuKham}</h2>
            </div>

            <div class="section">
                <h3><i class="fas fa-user-circle"></i> I. THÔNG TIN HÀNH CHÍNH</h3>
                <div class="info-grid">
                    <div class="info-item"><span class="label">Họ tên bệnh nhân:</span><span>${phieuKham.tenBenhNhan}</span></div>
                    <div class="info-item"><span class="label">Bác sĩ khám:</span><span>${phieuKham.tenBacSi}</span></div>
                    <div class="info-item"><span class="label">Thời gian khám:</span><span>${phieuKham.thoiGianKhamFormatted}</span></div>
                    <c:if test="${not empty phieuKham.ngayTaiKham}">
                        <div class="info-item"><span class="label">Ngày tái khám:</span><span>${phieuKham.ngayTaiKhamFormatted}</span></div>
                    </c:if>
                </div>
            </div>

            <div class="section">
                <h3><i class="fas fa-stethoscope"></i> II. KHÁM LÂM SÀNG</h3>
                <div class="clinical-content">
                    <h4>1. Chỉ số sinh tồn</h4>
                    <div class="vitals-grid">
                        <div class="info-item"><span class="label">Nhiệt độ:</span><span>${phieuKham.nhietDo}°C</span></div>
                        <div class="info-item"><span class="label">Huyết áp:</span><span>${phieuKham.huyetAp} mmHg</span></div>
                        <div class="info-item"><span class="label">Nhịp tim:</span><span>${phieuKham.nhipTim} lần/phút</span></div>
                        <div class="info-item"><span class="label">Nhịp thở:</span><span>${phieuKham.nhipTho} lần/phút</span></div>
                    </div>
                </div>
                <div class="clinical-content"><h4>2. Triệu chứng:</h4><p>${phieuKham.trieuChung}</p></div>
                <div class="clinical-content"><h4>3. Chẩn đoán:</h4><p class="diagnosis">${phieuKham.chanDoan}</p></div>
            </div>

            <div class="section">
                <h3><i class="fas fa-vials"></i> III. CHỈ ĐỊNH CẬN LÂM SÀNG</h3>
                <table class="data-table">
                    <thead><tr><th>Tên Dịch Vụ</th><th>Trạng Thái</th><th>Kết Quả</th></tr></thead>
                    <tbody>
                        <c:choose>
                            <c:when test="${not empty phieuKham.danhSachChiDinh}">
                                <c:forEach var="chiDinh" items="${phieuKham.danhSachChiDinh}">
                                    <tr>
                                        <td>${chiDinh.tenDichVu}</td>
                                        <td>${fn:replace(chiDinh.trangThai, '_', ' ')}</td>
                                        <td><c:out value="${chiDinh.ketQua}" default="Chưa có"/></td>
                                    </tr>
                                </c:forEach>
                            </c:when>
                            <c:otherwise>
                                <tr><td colspan="3" style="text-align: center;">Không có chỉ định.</td></tr>
                            </c:otherwise>
                        </c:choose>
                    </tbody>
                </table>
            </div>

            <div class="section">
                <h3><i class="fas fa-pills"></i> IV. ĐƠN THUỐC</h3>
                <c:choose>
                    <c:when test="${not empty phieuKham.donThuoc}">
                        <div class="clinical-content"><h4>Lời dặn:</h4><p>${phieuKham.donThuoc.loiDan}</p></div>
                        <table class="data-table">
                            <thead><tr><th>Tên Thuốc</th><th style="width: 15%;">Số Lượng</th><th>Liều Dùng</th></tr></thead>
                            <tbody>
                                <c:forEach var="chiTiet" items="${phieuKham.donThuoc.chiTietDonThuoc}">
                                    <tr><td><strong>${chiTiet.tenThuoc}</strong></td><td style="text-align: center;">${chiTiet.soLuong}</td><td>${chiTiet.lieuDung}</td></tr>
                                        </c:forEach>
                            </tbody>
                        </table>
                    </c:when>
                    <c:otherwise>
                        <p style="text-align: center;">Không có đơn thuốc.</p>
                    </c:otherwise>
                </c:choose>
            </div>

            <div class="section">
                <h3><i class="fas fa-file-medical-alt"></i> V. KẾT LUẬN & DẶN DÒ</h3>
                <p>${phieuKham.ketLuan}</p>
            </div>

            <div class="footer">
                <div class="signature sig1">
                    <p class="title">Bệnh nhân</p>
                    <p class="instruction">(Ký và ghi rõ họ tên)</p>
                </div>
                <div class="signature">
                    <p class="date">Ngày ${phieuKham.thoiGianKham.dayOfMonth} tháng ${phieuKham.thoiGianKham.monthValue} năm ${phieuKham.thoiGianKham.year}</p>
                    <p class="title">Bác sĩ khám</p>
                    <p class="instruction">(Ký và ghi rõ họ tên)</p>
                    <br><br><br>
                    <p><strong>${phieuKham.tenBacSi}</strong></p>
                </div>
            </div>
        </div>

        <script>
            // ✨ BỌC TOÀN BỘ CODE BÊN TRONG SỰ KIỆN NÀY ✨
            document.addEventListener('DOMContentLoaded', function () {

                // 1. Lấy ra các đối tượng cần thiết từ DOM
                const themeToggle = document.getElementById('theme-toggle');
                const body = document.body;

                // Nếu không tìm thấy nút gạt, dừng lại để tránh lỗi
                if (!themeToggle) {
                    return;
                }

                // Tên key để lưu trong localStorage
                const themeKey = 'theme-preference';

                // 2. Hàm để áp dụng theme được lưu
                const applyTheme = (theme) => {
                    if (theme === 'dark') {
                        body.classList.add('dark-mode');
                        themeToggle.checked = true;
                    } else {
                        body.classList.remove('dark-mode');
                        themeToggle.checked = false;
                    }
                };

                // 3. Lấy theme đã lưu từ localStorage khi tải trang
                const savedTheme = localStorage.getItem(themeKey);
                const currentTheme = savedTheme ? savedTheme : 'light'; // Mặc định là 'light'
                applyTheme(currentTheme);

                // 4. Lắng nghe sự kiện 'change' trên nút gạt
                themeToggle.addEventListener('change', () => {
                    let newTheme = themeToggle.checked ? 'dark' : 'light';

                    // Lưu lựa chọn mới vào localStorage
                    localStorage.setItem(themeKey, newTheme);

                    // Áp dụng theme mới ngay lập tức
                    applyTheme(newTheme);
                });

                // ✨ (Tùy chọn) Thêm cả code xử lý gauge vào đây để đảm bảo nó cũng chạy đúng
                // function updateVitalGauges() { ... }
                // updateVitalGauges();
            });
        </script>
    </body>
</html>