<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>

<!DOCTYPE html>
<html lang="vi">
    <head>
        <meta charset="UTF-8">
        <title>Danh Sách Phiếu Khám Bệnh</title>
        <%-- ĐỔI TÊN FILE CSS SANG MỘT FILE MỚI, VÍ DỤ: danhSach-style-cards.css --%>
        <link rel="stylesheet" href="<c:url value='/css/danhSach-style.css'/>">
        <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.2/css/all.min.css">
        <link href="https://fonts.googleapis.com/css2?family=Roboto:wght@400;500;700&display=swap" rel="stylesheet">    </head>
    <body>
        <div class="container">

            <%-- Các thông báo vẫn giữ nguyên --%>
            <c:if test="${not empty sessionScope.ERROR_MESSAGE}">
                <div class="alert alert-danger">${sessionScope.ERROR_MESSAGE}</div>
                <c:remove var="ERROR_MESSAGE" scope="session" />
            </c:if>
            <c:if test="${not empty sessionScope.SUCCESS_MESSAGE}">
                <div class="alert alert-success">${sessionScope.SUCCESS_MESSAGE}</div>
                <c:remove var="SUCCESS_MESSAGE" scope="session" />
            </c:if>

            <h1>Danh Sách Phiếu Khám Bệnh</h1>

            <%-- Khu vực tìm kiếm vẫn giữ nguyên --%>
            <div class="page-header">

                <a href="<c:url value='/MainController?action=showCreateEncounterForm'/>" class="btn btn-create-new">Tạo Phiếu Khám</a>

                <div class="search-container">
                    <form action="<c:url value='/MainController'/>" method="GET" class="search-form">
                        <i class="fas fa-search search-icon-left"></i>

                        <input type="hidden" name="action" value="listAllEncounters">
                        <input type="text" name="keyword" class="form-control" placeholder="Tìm kiếm..." value="${requestScope.searchKeyword}">

                        <button type="submit" class="search-button" aria-label="Tìm kiếm">
                            <i class="fas fa-search"></i>
                        </button>
                    </form>
                </div>

                <div class="theme-switch-wrapper">
                    <label class="theme-switch" for="checkbox">
                        <input type="checkbox" id="checkbox" />
                        <div class="slider"></div>
                    </label>
                </div>

            </div>
            <%-- THAY THẾ CẤU TRÚC TABLE BẰNG CARD GRID --%>
            <div class="card-grid">
                <c:choose>
                    <c:when test="${not empty danhSachPhieuKham}">
                        <c:forEach var="pkb" items="${danhSachPhieuKham}">
                            <div class="encounter-card">
                                <div class="card-header">
                                    <div class="card-id">#${pkb.maPhieuKham}</div>
                                    <div class="card-status">
                                        <c:choose>
                                            <c:when test="${pkb.trangThai == 'HOAN_THANH'}">
                                                <span class="status status-HOAN_THANH">Hoàn thành</span>
                                            </c:when>
                                            <c:otherwise>
                                                <span class="status status-CHUA_HOAN_THANH">Chưa hoàn thành</span>
                                            </c:otherwise>
                                        </c:choose>
                                    </div>
                                </div>

                                <div class="card-body">
                                    <h3 class="patient-name">${pkb.tenBenhNhan}</h3>
                                    <p class="doctor-name">BS. ${pkb.tenBacSi}</p>
                                    <div class="card-info">
                                        <div class="info-item">
                                            <span class="label">Thời gian:</span>
                                            <span>${pkb.thoiGianKhamFormatted}</span>
                                        </div>
                                        <div class="info-item">
                                            <span class="label">Chẩn đoán:</span>
                                            <span>${pkb.chanDoan}</span>
                                        </div>
                                    </div>
                                </div>

                                <div class="card-footer">
                                    <a href="<c:url value='MainController?action=viewEncounterDetails&id=${pkb.id}'/>" class="btn btn-primary btn-details">Xem Chi Tiết</a>
                                </div>
                            </div>
                        </c:forEach>
                    </c:when>
                    <c:otherwise>
                        <div class="no-results">
                            <c:choose>
                                <c:when test="${not empty requestScope.searchKeyword}">
                                    Không tìm thấy phiếu khám nào khớp với từ khóa "${requestScope.searchKeyword}".
                                </c:when>
                                <c:otherwise>
                                    Chưa có phiếu khám nào trong hệ thống.
                                </c:otherwise>
                            </c:choose>
                        </div>
                    </c:otherwise>
                </c:choose>
            </div>
        </div>
        <script>
            const toggleSwitch = document.querySelector('#checkbox');
            const currentTheme = localStorage.getItem('theme');

            // Hàm để áp dụng theme
            function applyTheme(theme) {
                if (theme === 'dark') {
                    document.body.classList.add('dark-mode');
                    toggleSwitch.checked = true;
                } else {
                    document.body.classList.remove('dark-mode');
                    toggleSwitch.checked = false;
                }
            }

            // Kiểm tra theme đã lưu khi tải trang
            if (currentTheme) {
                applyTheme(currentTheme);
            }

            // Lắng nghe sự kiện click vào nút gạt
            toggleSwitch.addEventListener('change', function () {
                let theme = 'light';
                if (this.checked) {
                    theme = 'dark';
                }
                document.body.classList.toggle('dark-mode', this.checked);
                localStorage.setItem('theme', theme);
            });
        </script>
    </body>
</html>