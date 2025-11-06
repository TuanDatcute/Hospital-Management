<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<!DOCTYPE html>
<html lang="vi">
    <head>
        <meta charset="UTF-8">
        <meta name="viewport" content="width=device-width, initial-scale=1.0">
        <title>Chi Tiết Đơn Thuốc</title>
        <link rel="stylesheet" href="<c:url value='/css/qldt-style.css'/>">
        <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.5.1/css/all.min.css">

        <link href="https://fonts.googleapis.com/css2?family=Be+Vietnam+Pro:wght@400;500;600;700&display=swap" rel="stylesheet">
        <link href="https://fonts.googleapis.com/css2?family=Roboto:wght@400;500;700&display=swap" rel="stylesheet">
    </head>
    <body>

        <div class="main-container">
            <div class="theme-switch-wrapper">
                <label class="theme-switch" for="theme-toggle">
                    <input type="checkbox" id="theme-toggle" />
                    <div class="slider round">
                        <span class="sun-icon"><i class="fas fa-sun"></i></span>
                        <span class="moon-icon"><i class="fas fa-moon"></i></span>
                    </div>
                </label>
            </div>
            <div class="header-section">

                <h1>Chi Tiết Đơn Thuốc #${donThuoc.id}</h1>
                <div class="prescription-info">
                    <p><strong>Ngày kê đơn:</strong> ${donThuoc.ngayKeDonFormatted}</p>
                    <p><strong>Tên bệnh nhân:</strong> ${donThuoc.tenBenhNhan}</p>
                    <p><strong>Phiếu khám:</strong> <a href="<c:url value='MainController?action=viewEncounterDetails&id=${donThuoc.phieuKhamId}'/>">#${donThuoc.phieuKhamId}</a></p>
                    <p><strong>Lời dặn:</strong> ${donThuoc.loiDan}</p>
                </div>
            </div>

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
                    <button id="add-new-btn" class="btn btn-success"><i class="fas fa-plus"></i> Thêm thuốc vào đơn</button>
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
                                                data-tenthuoc="${chiTiet.tenThuoc}">
                                            <i class="fas fa-pencil-alt"></i> Sửa
                                        </button>
                                        <form action="<c:url value='MainController'/>" method="POST" style="display:inline;" onsubmit="return confirm('Bạn có chắc muốn xóa thuốc \'${chiTiet.tenThuoc}\' khỏi đơn?');">
                                            <input type="hidden" name="action" value="deleteDetail">
                                            <input type="hidden" name="donThuocId" value="${donThuoc.id}">
                                            <input type="hidden" name="chiTietId" value="${chiTiet.id}">
                                            <button type="submit" class="btn btn-delete">
                                                <i class="fas fa-trash"></i> Xóa
                                            </button>
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

            <div class="footer-controls">
                <a href="<c:url value='MainController?action=viewEncounterDetails&id=${donThuoc.phieuKhamId}'/>" class="btn btn-secondary">
                    <i class="fas fa-arrow-left"></i> Quay lại Phiếu khám
                </a>
                <a href="<c:url value='MainController?action=listAll'/>" class="btn btn-secondary">
                    <i class="fas fa-list"></i> DS Đơn thuốc
                </a>
            </div>
        </div>

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

        <script>
            document.addEventListener('DOMContentLoaded', function () {
                // ===================================================
                // LOGIC CHUNG CHO CHẾ ĐỘ TỐI (DARK MODE)
                // ===================================================
                // 1. Lấy ra các đối tượng cần thiết từ DOM
                const themeToggle = document.getElementById('theme-toggle');
                const body = document.body;

                // Tên key để lưu trong localStorage
                const themeKey = 'theme-preference';

                // 2. Hàm để áp dụng theme được lưu
                const applyTheme = (theme) => {
                    if (theme === 'dark') {
                        // Thêm class 'dark-mode' vào body
                        body.classList.add('dark-mode');
                        // Đánh dấu check cho nút gạt
                        themeToggle.checked = true;
                    } else {
                        // Xóa class 'dark-mode' khỏi body
                        body.classList.remove('dark-mode');
                        // Bỏ check cho nút gạt
                        themeToggle.checked = false;
                    }
                };

                // 3. Lấy theme đã lưu từ localStorage khi tải trang
                const savedTheme = localStorage.getItem(themeKey);

                // Mặc định là 'light' nếu chưa có gì được lưu
                const currentTheme = savedTheme ? savedTheme : 'light';
                applyTheme(currentTheme);


                // 4. Lắng nghe sự kiện 'change' trên nút gạt
                themeToggle.addEventListener('change', () => {
                    let newTheme;
                    // Nếu nút gạt được check, theme mới là 'dark'
                    if (themeToggle.checked) {
                        newTheme = 'dark';
                    } else {
                        // Nếu không, theme mới là 'light'
                        newTheme = 'light';
                    }

                    // Lưu lựa chọn mới vào localStorage
                    localStorage.setItem(themeKey, newTheme);
                    // Áp dụng theme mới ngay lập tức
                    applyTheme(newTheme);
                });

                // ===================================================
                // LOGIC CỦA TRANG CHI TIẾT ĐƠN THUỐC
                // ===================================================
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

                function openModal() {
                    modalOverlay.classList.add('active');
                }
                function closeModal() {
                    modalOverlay.classList.remove('active');
                }

                // Chỉ thêm sự kiện cho nút "Thêm" nếu nó tồn tại
                if (addNewButton) {
                    addNewButton.addEventListener('click', () => {
                        modalForm.reset();
                        modalTitle.textContent = 'Thêm Thuốc vào Đơn';
                        formAction.value = 'addDetail';
                        submitButton.textContent = 'Thêm Thuốc';
                        chiTietIdInput.value = '';
                        thuocSelectGroup.style.display = 'block';
                        thuocSelectInput.required = true;
                        openModal();
                    });
                }

                if (tableBody) {
                    tableBody.addEventListener('click', (event) => {
                        const editButton = event.target.closest('.edit-btn');
                        if (!editButton)
                            return;
                        const id = editButton.dataset.id;
                        const soLuong = editButton.dataset.soluong;
                        const lieuDung = editButton.dataset.lieudung;
                        const tenThuoc = editButton.dataset.tenthuoc;
                        modalTitle.textContent = `Chỉnh Sửa: ${tenThuoc}`;
                        formAction.value = 'updateDetail';
                        submitButton.textContent = 'Cập Nhật';
                        chiTietIdInput.value = id;
                        soLuongInput.value = soLuong;
                        lieuDungInput.value = lieuDung;
                        thuocSelectGroup.style.display = 'none';
                        thuocSelectInput.required = false;
                        openModal();
                    });
                }

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