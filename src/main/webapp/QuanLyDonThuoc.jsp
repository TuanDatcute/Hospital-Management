<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<!DOCTYPE html>
<html lang="vi">
    <head>
        <meta charset="UTF-8">
        <meta name="viewport" content="width=device-width, initial-scale=1.0">
        <title>Chi Tiết Đơn Thuốc</title>

        <%-- Link đến các file CSS --%>
        <link rel="stylesheet" href="<c:url value='/css/danhSach-style.css'/>">
        <link rel="stylesheet" href="<c:url value='/css/qldt-style.css'/>">

        <%-- (Tùy chọn) Thêm font từ Google Fonts cho đẹp hơn --%>
        <link href="https://fonts.googleapis.com/css2?family=Roboto:wght@400;500;700&display=swap" rel="stylesheet">
    </head>
    <body>

        <%-- ===== PHẦN TRANG CHÍNH ===== --%>
        <div class="main-container">
            <div class="header-section">
                <h1>Chi Tiết Đơn Thuốc #${donThuoc.id}</h1>
                <div class="prescription-info">
                    <p><strong>Ngày kê đơn:</strong> ${donThuoc.ngayKeDonFormatted}</p>
                    <p><strong>Tên bệnh nhân:</strong> ${donThuoc.tenBenhNhan}</p>
                    <p><strong>Phiếu khám liên quan:</strong> <a style="text-decoration: none" href="<c:url value='MainController?action=viewEncounterDetails&id=${donThuoc.phieuKhamId}'/> ">ID: ${donThuoc.phieuKhamId}</a></p>
                    <p><strong>Lời dặn:</strong> ${donThuoc.loiDan}</p>
                </div>
            </div>

            <%-- Hiển thị thông báo (nếu có) --%>
            <c:if test="${not empty sessionScope.ERROR_MESSAGE}">
                <div class="alert alert-danger">${sessionScope.ERROR_MESSAGE}</div>
                <c:remove var="ERROR_MESSAGE" scope="session" />
            </c:if>
            <c:if test="${not empty sessionScope.SUCCESS_MESSAGE}">
                <div class="alert alert-success">${sessionScope.SUCCESS_MESSAGE}</div>
                <c:remove var="SUCCESS_MESSAGE" scope="session" />
            </c:if>

            <c:if test="${trangThaiPhieuKham ne 'HOAN_THANH'}">
                <div class="controls">
                    <button id="add-new-btn" class="btn btn-success">Thêm thuốc vào đơn</button>
                </div>
            </c:if>

            <div class="table-section">
                <h2>Các thuốc đã kê</h2>
                <table class="data-table">
                    <thead>
                        <tr>
                            <th>Tên Thuốc</th>
                            <th class="text-right">Số Lượng</th>
                            <th>Liều Dùng</th>
                            <th class="text-center">Hành động</th>
                        </tr>
                    </thead>
                    <tbody id="prescription-details-body"> 
                        <c:forEach var="chiTiet" items="${donThuoc.chiTietDonThuoc}">
                            <tr>
                                <td><strong>${chiTiet.tenThuoc}</strong></td>
                                <td class="text-right">${chiTiet.soLuong}</td>
                                <td>${chiTiet.lieuDung}</td>


                                <td class="actions text-center">
                                    <c:if test="${trangThaiPhieuKham ne 'HOAN_THANH'}">
                                        <button class="btn btn-edit edit-btn" 
                                                data-id="${chiTiet.id}"
                                                data-soluong="${chiTiet.soLuong}"
                                                data-lieudung="${chiTiet.lieuDung}"
                                                data-tenthuoc="${chiTiet.tenThuoc}">Sửa</button>

                                        <form action="<c:url value='MainController'/>" method="POST" style="display:inline;" onsubmit="return confirm('Bạn có chắc muốn xóa thuốc \'${chiTiet.tenThuoc}\' khỏi đơn?');">
                                            <input type="hidden" name="action" value="deleteDetail">
                                            <input type="hidden" name="donThuocId" value="${donThuoc.id}">
                                            <input type="hidden" name="chiTietId" value="${chiTiet.id}">
                                            <button type="submit" class="btn btn-delete">Xóa</button>
                                        </form>
                                    </c:if>
                                </td>
                            </tr>
                        </c:forEach>
                        <c:if test="${empty donThuoc.chiTietDonThuoc}">
                            <tr><td colspan="4" class="no-results">Chưa có thuốc nào được kê trong đơn này.</td></tr>
                        </c:if>
                    </tbody>
                </table>
            </div>
        </div>
        <div class="controls">
            <button id="add-new-btn" class="btn btn-success"><a style="text-decoration: none" href="<c:url value='MainController?action=viewEncounterDetails&id=${donThuoc.phieuKhamId}'/> ">Quay lại</a></button>
        </div>
        <div class="controls">
            <button id="add-new-btn" class="btn btn-success"><a style="text-decoration: none" href="<c:url value='MainController?action=listAll'/> ">Danh sách đơn thuốc</a></button>
        </div>

        <%-- ===== POPUP (MODAL) ĐA NĂNG (ẨN MẶC ĐỊNH) ===== --%>
        <div id="modal-overlay" class="modal-overlay">
            <div class="modal-content">
                <div class="modal-header">
                    <h2 id="modal-title">Thêm Thuốc vào Đơn</h2>
                    <span class="close-button" id="close-button">&times;</span>
                </div>

                <form id="modal-form" action="<c:url value='MainController'/>" method="POST">
                    <input type="hidden" name="donThuocId" value="${donThuoc.id}">
                    <input type="hidden" id="form-action" name="action" value="addDetail">
                    <input type="hidden" id="chiTietId-input" name="chiTietId" value="">

                    <div class="modal-body">
                        <div id="thuoc-select-group" class="form-group">
                            <label for="thuocId-input">Chọn Thuốc</label>
                            <select id="thuocId-input" name="thuocId" class="form-control" required>
                                <option value="">-- Chọn một loại thuốc --</option>
                                <c:forEach var="thuoc" items="${danhSachThuoc}">
                                    <option value="${thuoc.id}">${thuoc.tenThuoc} (Tồn kho: ${thuoc.soLuongTonKho})</option>
                                </c:forEach>
                            </select>
                        </div>

                        <div class="form-group">
                            <label for="soLuong-input">Số Lượng</label>
                            <input type="number" id="soLuong-input" name="soLuong" class="form-control" min="1" required>
                        </div>

                        <div class="form-group">
                            <label for="lieuDung-input">Liều Dùng</label>
                            <input type="text" id="lieuDung-input" name="lieuDung" class="form-control" placeholder="Ví dụ: Ngày 2 lần, sau ăn" required>
                        </div>
                    </div>

                    <div class="modal-footer">
                        <button type="button" id="cancel-button" class="btn btn-secondary">Hủy</button>
                        <button type="submit" id="submit-button" class="btn btn-primary">Thêm Thuốc</button>
                    </div>
                </form>
            </div>
        </div>

        <%-- ===== JAVASCRIPT ĐỂ ĐIỀU KHIỂN POPUP (SỬ DỤNG EVENT DELEGATION) ===== --%>
        <script>
            document.addEventListener('DOMContentLoaded', function () {
                // Lấy các phần tử DOM
                const thuocSelectInput = document.getElementById('thuocId-input');
                const modalOverlay = document.getElementById('modal-overlay');
                const closeModalButton = document.getElementById('close-button');
                const cancelButton = document.getElementById('cancel-button');
                const addNewButton = document.getElementById('add-new-btn');
                const tableBody = document.getElementById('prescription-details-body');

                const modalTitle = document.getElementById('modal-title');
                const modalForm = document.getElementById('modal-form');
                const formAction = document.getElementById('form-action');
                const chiTietIdInput = document.getElementById('chiTietId-input');
                const thuocSelectGroup = document.getElementById('thuoc-select-group');
                const soLuongInput = document.getElementById('soLuong-input');
                const lieuDungInput = document.getElementById('lieuDung-input');
                const submitButton = document.getElementById('submit-button');

                // Hàm mở modal
                function openModal() {
                    modalOverlay.classList.add('active');
                }

                // Hàm đóng modal
                function closeModal() {
                    modalOverlay.classList.remove('active');
                }

                // Sự kiện khi nhấn nút "Thêm Thuốc"
                addNewButton.addEventListener('click', () => {
                    modalForm.reset();
                    modalTitle.textContent = 'Thêm Thuốc vào Đơn';
                    formAction.value = 'addDetail';
                    submitButton.textContent = 'Thêm Thuốc';
                    chiTietIdInput.value = '';

                    thuocSelectGroup.style.display = 'block'; // Hiện ô chọn thuốc
                    thuocSelectInput.required = true; // ✨ BẬT yêu cầu bắt buộc

                    openModal();
                });

                // Sự kiện khi nhấn nút "Sửa"
                tableBody.addEventListener('click', (event) => {
                    const editButton = event.target.closest('.edit-btn');
                    if (!editButton)
                        return;

                    // Lấy dữ liệu từ thuộc tính data-* của nút
                    const id = editButton.dataset.id;
                    const soLuong = editButton.dataset.soluong;
                    const lieuDung = editButton.dataset.lieudung;
                    const tenThuoc = editButton.dataset.tenthuoc;

                    // Cập nhật form cho chế độ sửa
                    modalTitle.textContent = `Chỉnh Sửa Thuốc: ${tenThuoc}`;
                    formAction.value = 'updateDetail';
                    submitButton.textContent = 'Cập Nhật';
                    chiTietIdInput.value = id;
                    soLuongInput.value = soLuong;
                    lieuDungInput.value = lieuDung;
                    thuocSelectGroup.style.display = 'none'; // Ẩn ô chọn thuốc vì không thể đổi thuốc
                    thuocSelectInput.required = false; // ✨ TẮT yêu cầu bắt buộc
                    openModal();
                });

                // Sự kiện đóng modal
                closeModalButton.addEventListener('click', closeModal);
                cancelButton.addEventListener('click', closeModal);
                modalOverlay.addEventListener('click', (event) => {
                    if (event.target === modalOverlay) {
                        closeModal();
                    }
                });
            });
        </script>
    </body>
</html>