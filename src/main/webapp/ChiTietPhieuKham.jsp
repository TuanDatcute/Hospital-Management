<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<!DOCTYPE html>
<html lang="vi">
    <head>
        <meta charset="UTF-8">
        <meta name="viewport" content="width=device-width, initial-scale=1.0">
        <title>Chi Tiết Phiếu Khám Bệnh</title>

        <link rel="stylesheet" href="<c:url value='/css/pkb-style.css'/>">
        <link rel="stylesheet" href="<c:url value='/css/ctdt-style.css'/>">
        <link href="https://fonts.googleapis.com/css2?family=Roboto:wght@400;500;700&display=swap" rel="stylesheet">
    </head>
    <body>
        <div class="main-container">

            <div class="header-section">
                <h1>Chi Tiết Phiếu Khám Bệnh</h1>
                <h2>Mã Phiếu Khám: ${phieuKham.maPhieuKham}</h2>
            </div>

            <%-- Thông báo thành công/thất bại --%>
            <c:if test="${not empty sessionScope.ERROR_MESSAGE}">
                <div class="alert alert-danger">${sessionScope.ERROR_MESSAGE}</div>
                <c:remove var="ERROR_MESSAGE" scope="session" />
            </c:if>
            <c:if test="${not empty sessionScope.SUCCESS_MESSAGE}">
                <div class="alert alert-success">${sessionScope.SUCCESS_MESSAGE}</div>
                <c:remove var="SUCCESS_MESSAGE" scope="session" />
            </c:if>

            <div class="info-grid">
                <div class="info-item"><span class="label">Bệnh Nhân:</span><span class="value">${phieuKham.tenBenhNhan}</span></div>
                <div class="info-item"><span class="label">Bác Sĩ Khám:</span><span class="value">${phieuKham.tenBacSi}</span></div>
                <div class="info-item"><span class="label">Thời Gian Khám:</span><span class="value">${phieuKham.thoiGianKhamFormatted}</span></div>
                    <c:if test="${not empty phieuKham.ngayTaiKham}">
                    <div class="info-item"><span class="label">Ngày Tái Khám:</span><span class="value">${phieuKham.ngayTaiKhamFormatted}</span></div>
                    </c:if>
            </div>

            <div class="section">
                <h3>I. Chỉ số sinh tồn</h3>
                <div class="vitals-grid">
                    <div class="vital-item"><span class="label">Nhiệt độ:</span> <span class="value">${phieuKham.nhietDo}°C</span></div>
                    <div class="vital-item"><span class="label">Huyết áp:</span> <span class="value">${phieuKham.huyetAp} mmHg</span></div>
                    <div class="vital-item"><span class="label">Nhịp tim:</span> <span class="value">${phieuKham.nhipTim} lần/phút</span></div>
                    <div class="vital-item"><span class="label">Nhịp thở:</span> <span class="value">${phieuKham.nhipTho} lần/phút</span></div>
                </div>
            </div>

            <div class="section">
                <h3>II. Thông tin lâm sàng</h3>
                <div class="clinical-item"><h4>Triệu chứng:</h4><p>${phieuKham.trieuChung}</p></div>
                <div class="clinical-item"><h4>Chẩn đoán:</h4><p><strong>${phieuKham.chanDoan}</strong></p></div>
                <div class="clinical-item"><h4>Kết luận & Dặn dò:</h4><p>${phieuKham.ketLuan}</p></div>
            </div>

            <div class="sub-grid">
                <div class="section">
                    <h3>III. Dịch vụ đã chỉ định</h3>
                    <div class="add-service-form">
                        <form action="<c:url value='/MainController'/>" method="POST">
                            <input type="hidden" name="action" value="addServiceRequest">
                            <input type="hidden" name="phieuKhamId" value="${phieuKham.id}">
                            <div class="form-group">
                                <label for="dichVuId">Thêm dịch vụ mới:</label>
                                <select id="dichVuId" name="dichVuId" class="form-control" required>
                                    <option value="">-- Chọn dịch vụ --</option>
                                    <c:forEach var="dv" items="${danhSachDichVu}"><option value="${dv.id}">${dv.tenDichVu}</option></c:forEach>
                                    </select>
                                </div>
                                <button type="submit" class="btn btn-primary" style="align-self: end">Thêm</button>

                            </form>
                        </div>

                        <table class="data-table">
                            <thead><tr><th>Tên Dịch Vụ</th><th>Trạng Thái</th><th>Kết Quả</th><th class="text-center">Hành động</th></tr></thead>
                            <tbody id="service-request-body">
                            <c:forEach var="chiDinh" items="${phieuKham.danhSachChiDinh}">
                                <tr>
                                    <td><strong>${chiDinh.tenDichVu}</strong></td>
                                    <td><span class="status status-${chiDinh.trangThai}">${chiDinh.trangThai}</span></td>
                                    <td><c:out value="${chiDinh.ketQua}" default="Chưa có"/></td>
                                    <td class="actions text-center">
                                        <button class="btn btn-edit update-result-btn" data-id="${chiDinh.id}" data-tendichvu="${chiDinh.tenDichVu}" data-ketqua="${chiDinh.ketQua}" data-trangthai="${chiDinh.trangThai}">Cập nhật</button>
                                    </td>
                                </tr>
                            </c:forEach>
                        </tbody>
                    </table>
                </div>

                <div class="section">
                    <h3>IV. Đơn thuốc</h3>
                    <c:choose>
                        <c:when test="${not empty phieuKham.donThuoc}">
                            <p>Đã có đơn thuốc cho lần khám này.</p>
                            <a href="<c:url value='/MainController?action=viewDetails&id=${phieuKham.donThuoc.id}'/>" class="btn btn-primary">Xem & Quản lý Đơn thuốc</a>
                        </c:when>
                        <c:otherwise>
                            <p>Chưa có đơn thuốc nào được kê.</p>
                            <a href="<c:url value='/MainController?action=showCreateDonThuocForm&phieuKhamId=${phieuKham.id}'/>" class="btn btn-success">Kê Đơn Thuốc</a>
                        </c:otherwise>
                    </c:choose>
                </div>
            </div>

            <div class="action-buttons">
                <a href="<c:url value='/MainController?action=listAllEncounters'/>" class="btn btn-secondary">Quay lại danh sách</a>
                <a href="<c:url value='/MainController?action=showUpdateEncounterForm&id=${phieuKham.id}'/>" class="btn btn-edit">Chỉnh sửa Phiếu khám</a>
            </div>
        </div>

        <%-- ===== POPUP (MODAL) ĐỂ CẬP NHẬT KẾT QUẢ ===== --%>
        <div id="modal-overlay" class="modal-overlay">
            <div class="modal-content">
                <div class="modal-header">
                    <h2 id="modal-title">Cập nhật kết quả dịch vụ</h2>
                    <span class="close-button" id="close-button">&times;</span>
                </div>

                <form id="modal-form" action="<c:url value='/MainController'/>" method="POST">
                    <input type="hidden" name="action" value="updateServiceResult">
                    <input type="hidden" name="phieuKhamId" value="${phieuKham.id}">
                    <input type="hidden" id="chiDinhId-input" name="chiDinhId" value="">

                    <div class="modal-body">
                        <div class="form-group">
                            <label for="trangThai-input">Trạng Thái</label>
                            <select id="trangThai-input" name="trangThaiMoi" class="form-control" required>
                                <option value="CHO_THUC_HIEN">Chờ thực hiện</option>
                                <option value="DANG_THUC_HIEN">Đang thực hiện</option>
                                <option value="HOAN_THANH">Hoàn thành</option>
                            </select>
                        </div>
                        <div class="form-group">
                            <label for="ketQua-input">Kết Quả</label>
                            <textarea id="ketQua-input" name="ketQuaMoi" class="form-control" rows="5" placeholder="Nhập kết quả chi tiết..."></textarea>
                        </div>
                    </div>

                    <div class="modal-footer">
                        <button type="button" id="cancel-button" class="btn btn-secondary">Hủy</button>
                        <button type="submit" id="submit-button" class="btn btn-primary">Lưu thay đổi</button>
                    </div>
                </form>
            </div>
        </div>

        <script>
            document.addEventListener('DOMContentLoaded', function () {
                const modalOverlay = document.getElementById('modal-overlay');
                const closeModalButton = document.getElementById('close-button');
                const cancelButton = document.getElementById('cancel-button');
                const tableBody = document.getElementById('service-request-body');

                const modalTitle = document.getElementById('modal-title');
                const chiDinhIdInput = document.getElementById('chiDinhId-input');
                const trangThaiInput = document.getElementById('trangThai-input');
                const ketQuaInput = document.getElementById('ketQua-input');

                function openModal() {
                    modalOverlay.classList.add('active');
                }
                function closeModal() {
                    modalOverlay.classList.remove('active');
                }

                tableBody.addEventListener('click', (event) => {
                    const updateButton = event.target.closest('.update-result-btn');
                    if (!updateButton)
                        return;

                    modalTitle.textContent = `Cập nhật: ${updateButton.dataset.tendichvu}`;
                    chiDinhIdInput.value = updateButton.dataset.id;
                    trangThaiInput.value = updateButton.dataset.trangthai;
                    ketQuaInput.value = updateButton.dataset.ketqua;

                    openModal();
                });

                closeModalButton.addEventListener('click', closeModal);
                cancelButton.addEventListener('click', closeModal);
                modalOverlay.addEventListener('click', (event) => {
                    if (event.target === modalOverlay)
                        closeModal();
                });
            });
        </script>
    </body>
</html>