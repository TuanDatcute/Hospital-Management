<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>

<!DOCTYPE html>
<html lang="vi">
    <head>
        <meta charset="UTF-8">
        <title>Danh Sách Phiếu Khám Bệnh</title>
        <link rel="stylesheet" href="<c:url value='/css/danhSach-style.css'/>">
        <link rel="stylesheet" href="<c:url value='/css/_sidebar.css?v=1.1'/>">
        <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.2/css/all.min.css">
        <link href="https://fonts.googleapis.com/css2?family=Roboto:wght@400;500;700&display=swap" rel="stylesheet">   
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
    </head>
    <body>


        <jsp:include page="_sidebar.jsp" />

        <div class="main-content">
            <div class="container">

                <%-- Các thông báo --%>
                <c:if test="${not empty sessionScope.ERROR_MESSAGE}">
                    <div class="alert alert-danger">${sessionScope.ERROR_MESSAGE}</div>
                    <c:remove var="ERROR_MESSAGE" scope="session" />
                </c:if>
                <c:if test="${not empty sessionScope.SUCCESS_MESSAGE}">
                    <div class="alert alert-success">${sessionScope.SUCCESS_MESSAGE}</div>
                    <c:remove var="SUCCESS_MESSAGE" scope="session" />
                </c:if>

                <h1>Danh Sách Phiếu Khám Bệnh</h1>

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

                        <c:if test="${not empty requestScope.searchKeyword}">
                            <a href="<c:url value='/MainController?action=listAllEncounters'/>" class="btn btn-clear-search">
                                <i class="fas fa-times"></i> Xem tất cả
                            </a>
                        </c:if>
                    </div>

                    <div class="view-toggle">
                        <button id="grid-view-btn" class="view-btn active" title="Xem dạng lưới"><i class="fas fa-th-large"></i></button>
                        <button id="list-view-btn" class="view-btn" title="Xem dạng danh sách"><i class="fas fa-bars"></i></button>
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

                <%-- DANH SÁCH CARD GRID --%>
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
        </div> 
        <script src="<c:url value='/js/darkmode.js'/>"></script>
        <script src="<c:url value='/js/theme.js'/>"></script>
        <script src="<c:url value='/js/danhSachPhieuKham.js'/>"></script>
    </body>
</html>