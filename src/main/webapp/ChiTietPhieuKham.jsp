<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>

<!DOCTYPE html>
<html lang="vi">
    <head>
        <meta charset="UTF-8">
        <meta name="viewport" content="width=device-width, initial-scale=1.0">
        <title>Bệnh Án - ${phieuKham.maPhieuKham}</title>

        <%-- Sử dụng c:url để đảm bảo đường dẫn luôn đúng --%>
        <link rel="stylesheet" href="<c:url value='/css/ctdt-style.css'/>">
        <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.5.1/css/all.min.css">
        <link rel="preconnect" href="https://fonts.googleapis.com">
        <link rel="preconnect" href="https://fonts.gstatic.com" crossorigin>
        <link href="https://fonts.googleapis.com/css2?family=Be+Vietnam+Pro:wght@400;500;600;700&display=swap" rel="stylesheet">
    </head>
    <body>
        <div class="dashboard-container">

            <%--  nút gạt  --%>
            <div class="theme-switch-wrapper">
                <label class="theme-switch" for="theme-toggle">
                    <input type="checkbox" id="theme-toggle" />
                    <div class="slider round">

                        <span class="sun-icon"><i class="fas fa-sun"></i></span>
                        <span class="moon-icon"><i class="fas fa-moon"></i></span>
                    </div>
                </label>
            </div>
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
                    <a href="<c:url value='/MainController?action=listAllEncounters'/>" class="btn btn-secondary"><i class="fas fa-arrow-left"></i> Quay lại DS</a>
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

        <script>
            // Thay thế toàn bộ nội dung trong thẻ <script> bằng đoạn mã này

            document.addEventListener('DOMContentLoaded', function () {



                // --- Xử lý Modal ---
                const modalOverlay = document.getElementById('modal-overlay');
                const updateButtons = document.querySelectorAll('.update-result-btn');

                if (modalOverlay && updateButtons.length > 0) {
                    const closeModalButton = document.getElementById('close-button');
                    const cancelButton = document.getElementById('cancel-button');
                    const modalTitle = document.getElementById('modal-title');
                    const chiDinhIdInput = document.getElementById('chiDinhId-input');
                    const trangThaiInput = document.getElementById('trangThai-input');
                    const ketQuaInput = document.getElementById('ketQua-input');

                    const openModal = () => modalOverlay.classList.add('active');
                    const closeModal = () => modalOverlay.classList.remove('active');

                    updateButtons.forEach(button => {
                        button.addEventListener('click', () => {
                            modalTitle.textContent = `Cập nhật: ${button.dataset.tendichvu}`;
                            chiDinhIdInput.value = button.dataset.id;
                            trangThaiInput.value = button.dataset.trangthai;
                            ketQuaInput.value = (button.dataset.ketqua === 'null' || button.dataset.ketqua === 'undefined') ? '' : button.dataset.ketqua;
                            openModal();
                        });
                    });

                    closeModalButton.addEventListener('click', closeModal);
                    cancelButton.addEventListener('click', closeModal);
                    modalOverlay.addEventListener('click', (event) => {
                        if (event.target === modalOverlay)
                            closeModal();
                    });
                }

                // --- Cập nhật thanh chỉ số sinh tồn động ---
                function updateVitalGauges() {
                    const gauges = document.querySelectorAll('.vital-gauge');

                    // Định nghĩa ngưỡng giá trị bình thường, cảnh báo và nguy hiểm
                    const thresholds = {
                        temp: {normal: [36.5, 37.5], warning: [37.6, 38.5], range: [35, 42]},
                        bp: {normal: [90, 120], warning: [121, 139], range: [70, 180]}, // Huyết áp tâm thu
                        hr: {normal: [60, 100], warning: [101, 120], range: [40, 180]},
                        rr: {normal: [16, 20], warning: [21, 24], range: [10, 30]}
                    };

                    gauges.forEach(gauge => {
                        const type = gauge.dataset.type;
                        const rawValue = gauge.dataset.value.split('/')[0];
                        const value = parseFloat(rawValue);

                        if (isNaN(value) || !thresholds[type])
                            return;

                        const config = thresholds[type];
                        const [minRange, maxRange] = config.range;
                        const fillElement = gauge.querySelector('.gauge-fill');
                        const valueElement = gauge.querySelector('.value');

                        let percentage = (value - minRange) / (maxRange - minRange) * 100;
                        percentage = Math.max(0, Math.min(100, percentage));
                        fillElement.style.width = `${percentage}%`;

                        gauge.classList.remove('status-normal', 'status-warning', 'status-high');
                        valueElement.classList.remove('status-normal', 'status-warning', 'status-high');

                        if (value >= config.normal[0] && value <= config.normal[1]) {
                            gauge.classList.add('status-normal');
                            valueElement.classList.add('status-normal');
                        } else if (value > config.normal[1] && value <= config.warning[1] || (value < config.normal[0] && value >= 80)) {
                            gauge.classList.add('status-warning');
                            valueElement.classList.add('status-warning');
                        } else {
                            gauge.classList.add('status-high');
                            valueElement.classList.add('status-high');
                        }
                    });
                }

                updateVitalGauges();
                
                
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
            });
        </script>
    </body>
</html>