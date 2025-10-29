<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>

<!DOCTYPE html>
<html lang="vi">
    <head>
        <meta charset="UTF-8">
        <title>Danh Sách Phiếu Khám Bệnh</title>

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
                    <label class="theme-switch" for="theme-toggle">
                        <input type="checkbox" id="theme-toggle" />
                        <div class="slider round">

                            <span class="sun-icon"><i class="fas fa-sun"></i></span>
                            <span class="moon-icon"><i class="fas fa-moon"></i></span>
                        </div>
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
            document.addEventListener('DOMContentLoaded', function () {
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