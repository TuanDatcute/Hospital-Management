<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<!DOCTYPE html>
<html lang="vi">
    <head>
        <meta charset="UTF-8">
        <title>Kê Đơn Thuốc Mới</title>
        <link rel="stylesheet" href="<c:url value='/css/style.css'/>">
        <style>
            .medication-row {
                display: flex;
                gap: 10px;
                align-items: center;
                margin-bottom: 10px;
            }
            .medication-row .form-group {
                flex-grow: 1;
            }
            .medication-row .remove-btn {
                margin-top: 20px;
            }
        </style>
    </head>
    <body>


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

                <button type="button" id="add-med-btn" class="btn btn-secondary">Thêm thuốc</button>

                <div class="button-group">
                    <button type="submit" class="btn btn-primary">Lưu Đơn Thuốc</button>
                </div>
            </form>
        </div>

        <div id="medication-row-template" style="display: none;">
            <div class="medication-row">
                <div class="form-group">
                    <label>Tên Thuốc</label>
                    <select name="thuocId" class="form-control" required>
                        <option value="">-- Chọn thuốc --</option>
                        <c:forEach var="thuoc" items="${danhSachThuoc}">
                            <option value="${thuoc.id}">${thuoc.tenThuoc} (Tồn: ${thuoc.soLuongTonKho})</option>
                        </c:forEach>
                    </select>
                </div>
                <div class="form-group" style="flex-basis: 120px; flex-grow: 0;">
                    <label>Số Lượng</label>
                    <input type="number" name="soLuong" class="form-control" min="1" required>
                </div>
                <div class="form-group">
                    <label>Liều Dùng</label>
                    <input type="text" name="lieuDung" class="form-control" placeholder="Ví dụ: Sáng 1 viên, tối 1 viên" required>
                </div>
                <button type="button" class="btn btn-delete remove-btn">Xóa</button>
            </div>
        </div>

        <script>
            document.addEventListener('DOMContentLoaded', function () {
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
                    if (event.target.classList.contains('remove-btn')) {
                        event.target.closest('.medication-row').remove();
                    }
                });

                // Tự động thêm một dòng khi tải trang
                addMedicationRow();
            });
        </script>
    </body>
</html>