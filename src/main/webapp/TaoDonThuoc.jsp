<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<!DOCTYPE html>
<html lang="vi">
    <head>
        <meta charset="UTF-8">
        <title>Kê Đơn Thuốc Mới</title>
        <link rel="stylesheet" href="<c:url value='/css/taoDonThuoc-style.css'/>"> 

        <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.5.1/css/all.min.css">
        <script>
            (function () {
                // Key này (theme-preference) phải khớp với key trong theme.js
                var themeKey = 'theme-preference';
                var theme = localStorage.getItem(themeKey);

                if (theme === 'dark') {
                    // ✨ SỬA 3: Không đổi màu nền, mà thêm class vào <html>
                    document.documentElement.classList.add('dark-mode');
                }
            })();
        </script>
        <script src="<c:url value='/js/darkmode.js'/>"></script>
    </head>
    <body>

        <div class="theme-switch-wrapper">
            <label class="theme-switch" for="theme-toggle">
                <input type="checkbox" id="theme-toggle" />
                <div class="slider round">

                    <span class="sun-icon"><i class="fas fa-sun"></i></span>
                    <span class="moon-icon"><i class="fas fa-moon"></i></span>
                </div>
            </label>
        </div>



        <div class="form-container">
            <h1>Kê Đơn Thuốc Mới</h1>
            <p>Cho Phiếu Khám: <strong>#${param.phieuKhamId}</strong></p>

            <c:if test="${not empty requestScope.ERROR_MESSAGE}">
                <div class="alert alert-danger">${requestScope.ERROR_MESSAGE}</div>
            </c:if>

            <form action="<c:url value='MainController'/>" method="POST">
                <input type="hidden" name="action" value="createPrescription">
                <input type="hidden" name="phieuKhamId" value="${param.phieuKhamId}">

                <div class="form-group full-width">
                    <label for="loiDan">Lời Dặn Chung</label>
                    <textarea id="loiDan" name="loiDan" class="form-control" rows="3" placeholder="Ví dụ: Uống thuốc đúng giờ, tái khám sau 7 ngày..."></textarea>
                </div>

                <hr>
                <h3>Chi Tiết Đơn Thuốc</h3>
                <div id="medication-list">
                    <%-- JavaScript sẽ thêm các dòng thuốc vào đây --%>
                </div>

                <button type="button" id="add-med-btn" class="btn btn-secondary">
                    <i class="fas fa-plus"></i> Thêm thuốc
                </button>

                <div class="button-group">
                    <a href="javascript:history.back()" class="btn btn-secondary">
                        <i class="fas fa-arrow-left"></i> Quay lại
                    </a>
                    <button type="submit" class="btn btn-primary"><i class="fas fa-save"></i> Lưu Đơn Thuốc</button>
                </div>

            </form>
        </div>

        <div id="medication-row-template" style="display: none;">
            <div class="medication-row">
                <div class="form-group ten-thuoc">
                    <label>Tên Thuốc</label>
                    <select name="thuocId" class="form-control" required>
                        <option value="">-- Chọn thuốc --</option>
                        <c:forEach var="thuoc" items="${danhSachThuoc}">
                            <option value="${thuoc.id}">${thuoc.tenThuoc} (Tồn: ${thuoc.soLuongTonKho})</option>
                        </c:forEach>
                    </select>
                </div>
                <div class="form-group so-luong">
                    <label>Số Lượng</label>
                    <input type="number" name="soLuong" class="form-control" min="1" required>
                </div>
                <div class="form-group lieu-dung">
                    <label>Liều Dùng</label>
                    <input type="text" name="lieuDung" class="form-control" placeholder="Ví dụ: Sáng 1v, tối 1v" required>
                </div>
                <button type="button" class="btn btn-delete remove-btn">
                    <i class="fas fa-trash"></i>
                </button>
            </div>
        </div>

        <script>
            document.addEventListener('DOMContentLoaded', function () {

                // ===================================================
                // LOGIC THÊM/XÓA ĐƠN THUỐC CỦA BẠN
                // ===================================================
                const addBtn = document.getElementById('add-med-btn');
                const listContainer = document.getElementById('medication-list');
                const template = document.getElementById('medication-row-template');

                function addMedicationRow() {
                    const newRow = template.firstElementChild.cloneNode(true);
                    listContainer.appendChild(newRow);
                }

                addBtn.addEventListener('click', addMedicationRow);

                // Dùng event delegation để xử lý nút xóa
                listContainer.addEventListener('click', function (event) {
                    // Tìm nút xóa gần nhất mà người dùng click
                    const removeButton = event.target.closest('.remove-btn');
                    if (removeButton) {
                        removeButton.closest('.medication-row').remove();
                    }
                });

                // Tự động thêm một dòng khi tải trang
                addMedicationRow();
            });
        </script>
    </body>
</html>