<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>

<!DOCTYPE html>
<html lang="vi">
    <head>
        <meta charset="UTF-8">
        <meta name="viewport" content="width=device-width, initial-scale=1.0">
        <title>Dashboard Bệnh Án - ${phieuKham.maPhieuKham}</title>

        <link rel="stylesheet" href="<c:url value='/css/ctdt-style.css'/>">
        <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.5.1/css/all.min.css">

        <script>
            (function () {
                var themeKey = 'theme-preference'; // Key này phải khớp với theme.js
                var theme = localStorage.getItem(themeKey);
                if (theme === 'dark') {
                    document.documentElement.classList.add('dark-mode');
                }
            })();
        </script>

        <link rel="preconnect" href="https://fonts.googleapis.com">
        <link rel="preconnect" href="https://fonts.gstatic.com" crossorigin>
        <link href="https://fonts.googleapis.com/css2?family=Be+Vietnam+Pro:wght@400;500;600;700&display=swap" rel="stylesheet">
    </head>
    <body>
        <div class="dashboard-container">

            <%-- Nút gạt --%>
            <div class="theme-switch-wrapper">
                <label class="theme-switch" for="theme-toggle">
                    <input type="checkbox" id="theme-toggle" />
                    <div class="slider round">
                        <span class="sun-icon"><i class="fas fa-sun"></i></span>
                        <span class="moon-icon"><i class="fas fa-moon"></i></span>
                    </div>
                </label>
            </div>

            <%-- Thông báo --%>
            <c:if test="${not empty sessionScope.ERROR_MESSAGE}">
                <div class="alert alert-danger">${sessionScope.ERROR_MESSAGE}</div>
                <c:remove var="ERROR_MESSAGE" scope="session" />
            </c:if>
            <c:if test="${not empty sessionScope.SUCCESS_MESSAGE}">
                <div class="alert alert-success">${sessionScope.SUCCESS_MESSAGE}</div>
                <c:remove var="SUCCESS_MESSAGE" scope="session" />
            </c:if>

            <%-- Card Thông tin Bệnh nhân --%>
            <div class="card profile-card">
                <h1 title="${phieuKham.tenBenhNhan}">${phieuKham.tenBenhNhan}</h1>
                <p class="patient-id">Mã Phiếu: #${phieuKham.maPhieuKham}</p>
                <span class="status status-${phieuKham.trangThai}">${phieuKham.trangThai.replace('_', ' ')}</span>
                <div class="info-list">
                    <div class="info-item"><span><i class="fa-solid fa-user-doctor"></i> Bác sĩ</span><strong>${phieuKham.tenBacSi}</strong></div>
                    <div class="info-item"><span><i class="fa-solid fa-clock"></i> Thời gian</span><strong>${phieuKham.thoiGianKhamFormatted}</strong></div>
                                <c:if test="${not empty phieuKham.ngayTaiKham}">
                        <div class="info-item"><span><i class="fa-solid fa-calendar-check"></i> Tái khám</span><strong>${phieuKham.ngayTaiKhamFormatted}</strong></div>
                                </c:if>
                </div>
            </div>

            <%-- Card Hành động nhanh --%>
            <div class="card actions-card">          
                <div class="card-header"><h3><i class="fa-solid fa-bolt"></i> Hành động</h3></div>
                <div class="card-body action-buttons">
                    <c:if test="${phieuKham.trangThai ne 'HOAN_THANH'}">
                        <a href="<c:url value='/MainController?action=showUpdateEncounterForm&id=${phieuKham.id}'/>" class="btn btn-edit"><i class="fas fa-pencil-alt"></i> Chỉnh sửa</a>
                        <form action="<c:url value='/MainController'/>" method="POST" onsubmit="return confirm('Bạn có chắc chắn muốn hoàn thành phiếu khám này?');">
                            <input type="hidden" name="action" value="completeEncounter">
                            <input type="hidden" name="phieuKhamId" value="${phieuKham.id}">
                            <button type="submit" class="btn btn-success"><i class="fas fa-check-circle"></i> Hoàn thành</button>
                        </form>
                    </c:if>
                    <a href="<c:url value='/MainController?action=printEncounter&id=${phieuKham.id}'/>" 
                       target="_blank" class="btn btn-primary">
                        <i class="fas fa-print"></i> In Bệnh Án
                    </a>
                    <a href="javascript:history.back()" class="btn btn-secondary">
                        <i class="fas fa-arrow-left"></i> Quay lại
                    </a>
                </div>
            </div>

            <%-- Card Chỉ số sinh tồn (Vitals) --%>
            <div class="card vitals-dashboard-card">
                <div class="card-header"><h3><i class="fas fa-heart-pulse"></i> Chỉ số Sinh tồn</h3></div>
                <div class="card-body vitals-grid">
                    <div class="vital-gauge" data-value="${phieuKham.nhietDo}" data-type="temp">
                        <div class="gauge-header">
                            <span><i class="fa-solid fa-temperature-half"></i> Nhiệt độ (°C)</span>
                            <strong class="value">${phieuKham.nhietDo}</strong>
                        </div>
                        <div class="gauge-bar"><div class="gauge-fill"></div></div>
                    </div>
                    <div class="vital-gauge" data-value="${phieuKham.huyetAp}" data-type="bp">
                        <div class="gauge-header">
                            <span><i class="fa-solid fa-stethoscope"></i> Huyết áp (mmHg)</span>
                            <strong class="value">${phieuKham.huyetAp}</strong>
                        </div>
                        <div class="gauge-bar"><div class="gauge-fill"></div></div>
                    </div>
                    <div class="vital-gauge" data-value="${phieuKham.nhipTim}" data-type="hr">
                        <div class="gauge-header">
                            <span><i class="fa-solid fa-heart-pulse"></i> Nhịp tim (bpm)</span>
                            <strong class="value">${phieuKham.nhipTim}</strong>
                        </div>
                        <div class="gauge-bar"><div class="gauge-fill"></div></div>
                    </div>
                    <div class="vital-gauge" data-value="${phieuKham.nhipTho}" data-type="rr">
                        <div class="gauge-header">
                            <span><i class="fa-solid fa-lungs"></i> Nhịp thở (/p)</span>
                            <strong class="value">${phieuKham.nhipTho}</strong>
                        </div>
                        <div class="gauge-bar"><div class="gauge-fill"></div></div>
                    </div>
                </div>
            </div>

            <%-- Card Ghi chú lâm sàng --%>
            <div class="card clinical-card">
                <div class="card-header"><h3><i class="fas fa-notes-medical"></i> Ghi chú lâm sàng</h3></div>
                <div class="card-body">
                    <div class="clinical-item">
                        <h4>Triệu chứng</h4><p>${phieuKham.trieuChung}</p>
                    </div>
                    <div class="clinical-item">
                        <h4>Chẩn đoán</h4><p class="diagnosis">${phieuKham.chanDoan}</p>
                    </div>
                    <div class="clinical-item">
                        <h4>Kết luận & Dặn dò</h4><p>${phieuKham.ketLuan}</p>
                    </div>
                </div>
            </div>

            <%-- Card Dịch vụ chỉ định --%>
            <div class="card services-card">
                <div class="card-header"><h3><i class="fas fa-vials"></i> Dịch vụ chỉ định</h3></div>
                <div class="card-body">
                    <c:if test="${phieuKham.trangThai ne 'HOAN_THANH'}">
                        <div class="add-service-form">
                            <form action="<c:url value='/MainController'/>" method="POST">
                                <input type="hidden" name="action" value="addServiceRequest">
                                <input type="hidden" name="phieuKhamId" value="${phieuKham.id}">
                                <select name="dichVuId" class="form-control" required><option value="">-- Chọn dịch vụ --</option><c:forEach var="dv" items="${danhSachDichVu}"><option value="${dv.id}">${dv.tenDichVu}</option></c:forEach></select>
                                    <button type="submit" class="btn btn-primary add-btn" aria-label="Thêm dịch vụ"><i class="fas fa-plus"></i></button>
                                </form>
                            </div>
                    </c:if>
                    <div class="table-wrapper">
                        <table class="data-table">
                            <tbody>
                                <c:choose>
                                    <c:when test="${not empty phieuKham.danhSachChiDinh}">
                                        <c:forEach var="chiDinh" items="${phieuKham.danhSachChiDinh}">
                                            <tr>
                                                <td><strong>${chiDinh.tenDichVu}</strong><small><c:out value="${chiDinh.ketQua}" default="Chưa có kết quả"/></small></td>
                                                <td><span class="status status-${chiDinh.trangThai}">${chiDinh.trangThai.replace('_', ' ')}</span></td>
                                                <td class="actions">
                                                    <c:if test="${phieuKham.trangThai ne 'HOAN_THANH'}">
                                                        <button class="btn-icon update-result-btn" data-id="${chiDinh.id}" data-tendichvu="${chiDinh.tenDichVu}" data-ketqua="${chiDinh.ketQua}" data-trangthai="${chiDinh.trangThai}" title="Cập nhật kết quả">
                                                            <i class="fa-solid fa-pen-to-square"></i>
                                                        </button>
                                                    </c:if>
                                                </td>
                                            </tr>
                                        </c:forEach>
                                    </c:when>
                                    <c:otherwise>
                                        <tr><td colspan="3">
                                                <div class="empty-state">
                                                    <i class="fa-solid fa-flask-vial"></i>
                                                    <p>Chưa có dịch vụ nào được chỉ định.</p>
                                                </div>
                                            </td></tr>
                                        </c:otherwise>
                                    </c:choose>
                            </tbody>
                        </table>
                    </div>
                </div>
            </div>

            <%-- Card Đơn thuốc --%>
            <div class="card prescription-card">
                <div class="card-header"><h3><i class="fas fa-prescription"></i> Đơn thuốc</h3></div>
                <div class="card-body">
                    <c:choose>
                        <c:when test="${not empty phieuKham.donThuoc}">
                            <div class="prescription-note">${phieuKham.donThuoc.loiDan}</div>
                            <div class="table-wrapper">
                                <table class="data-table">
                                    <thead><tr><th>Tên Thuốc</th><th>SL</th><th>Liều Dùng</th></tr></thead>
                                    <tbody>
                                        <c:forEach var="chiTiet" items="${phieuKham.donThuoc.chiTietDonThuoc}">
                                            <tr>
                                                <td><strong>${chiTiet.tenThuoc}</strong></td>
                                                <td class="text-center">${chiTiet.soLuong}</td>
                                                <td>${chiTiet.lieuDung}</td>
                                            </tr>
                                        </c:forEach>
                                    </tbody>
                                </table>
                            </div>
                            <a href="<c:url value='/MainController?action=viewDetails&id=${phieuKham.donThuoc.id}'/>" class="btn btn-outline-primary full-width" style="margin-top: 15px;">Quản lý chi tiết</a>
                        </c:when>
                        <c:otherwise>
                            <div class="empty-state">
                                <i class="fa-solid fa-pills"></i>
                                <p>Chưa có đơn thuốc.</p>
                                <c:if test="${phieuKham.trangThai ne 'HOAN_THANH'}">
                                    <a href="<c:url value='/MainController?action=showCreateDonThuocForm&phieuKhamId=${phieuKham.id}'/>" class="btn btn-primary">
                                        <i class="fa-solid fa-plus"></i> Kê Đơn
                                    </a>
                                </c:if>
                            </div>
                        </c:otherwise>
                    </c:choose>
                </div>
            </div>
        </div>

        <%-- MODAL (POPUP) Cập nhật kết quả dịch vụ --%>
        <div id="modal-overlay" class="modal-overlay">
            <div class="modal-content">
                <div class="modal-header">
                    <h2 id="modal-title">Cập nhật kết quả</h2>
                    <button class="close-button" id="close-button" aria-label="Đóng">&times;</button>
                </div>
                <form id="modal-form" action="<c:url value='/MainController'/>" method="POST">
                    <input type="hidden" name="action" value="updateServiceResult">
                    <input type="hidden" name="phieuKhamId" value="${phieuKham.id}">
                    <input type="hidden" id="chiDinhId-input" name="chiDinhId">
                    <div class="modal-body">
                        <div class="form-group">
                            <label for="trangThai-input">Trạng Thái</label>
                            <select id="trangThai-input" name="trangThaiMoi" class="form-control" required>
                                <option value="CHO_THUC_HIEN">Chờ thực hiện</option>
                                <option value="DANG_THUC_HIEN">Đang thực hiện</option>
                                <option value="HOAN_THANH">Hoàn thành</option>
                                <option value="DA_HUY">Đã Hủy</option>
                            </select>
                        </div>
                        <div class="form-group">
                            <label for="ketQua-input">Kết Quả</label>
                            <textarea id="ketQua-input" name="ketQuaMoi" class="form-control" rows="5" placeholder="Nhập kết quả chi tiết..."></textarea>
                        </div>
                    </div>
                    <div class="modal-footer">
                        <button type="button" id="cancel-button" class="btn btn-secondary">Hủy</button>
                        <button type="submit" id="submit-button" class="btn btn-primary">Lưu</button>
                    </div>
                </form>
            </div>
        </div>

        <script src="<c:url value='/js/darkmode.js'/>"></script>
        <script src="<c:url value='/js/ctdt-dashboard.js'/>"></script>
    </body>
</html>