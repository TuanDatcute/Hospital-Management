<%--
    Document   : dashboard.jsp (Trang chủ Bác sĩ/Lễ tân)
    Created on : Oct 30, 2025
    Author     : ADMIN
--%>

<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>Bảng điều khiển Nhân viên</title>

        <link rel="preconnect" href="https://fonts.googleapis.com">
        <link rel="preconnect" href="https://fonts.gstatic.com" crossorigin>
        <link href="https://fonts.googleapis.com/css2?family=Poppins:wght@400;500;600;700&display=swap" rel="stylesheet">

        <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0/css/all.min.css">
        <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/swiper@11/swiper-bundle.min.css" />
        <link href="https://fonts.googleapis.com/css2?family=Be+Vietnam+Pro:wght@400;500;600;700&display=swap" rel="stylesheet">


        <%-- Tăng version CSS --%>
        <link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/css/index.css?v=1.2">
    <body>

        <jsp:include page="/WEB-INF/headerDat.jsp" /> 

        <div class="container page-content" style="padding-top: 30px;">

            <h2 class="section-title">Bảng điều khiển Nhân viên</h2>

            <c:if test="${not empty sessionScope.SUCCESS_MESSAGE}">
                <p class="success-message">${sessionScope.SUCCESS_MESSAGE}</p>
                <c:remove var="SUCCESS_MESSAGE" scope="session" />
            </c:if>

            <div class="home-welcome-message">
                Chào mừng <strong>${sessionScope.USER.tenDangNhap}</strong>!
                (Vai trò: ${sessionScope.ROLE})
            </div>

            <%-- Lưới chức năng (ĐÃ SỬA LỖI HREF) --%>
            <div class="function-grid">

                <a href="${pageContext.request.contextPath}/MainController?action=listLichHen" class="function-card">
                    <div class="icon"><i class="fas fa-calendar-check"></i></div>
                    <span>Quản lý Lịch hẹn</span>
                </a>
                <a href="${pageContext.request.contextPath}/MainController?action=showCreateEncounterForm" class="function-card">
                    <div class="icon"><i class="fas fa-file-medical"></i></div>
                    <span>Tạo Phiếu khám</span>
                </a>
                <a href="${pageContext.request.contextPath}/MainController?action=listBenhNhan" class="function-card">
                    <div class="icon"><i class="fas fa-user-injured"></i></div>
                    <span>Danh sách Bệnh nhân</span>
                </a>

                <a href="${pageContext.request.contextPath}/MainController?action=showChangePasswordForm" class="function-card">
                    <div class="icon"><i class="fas fa-key"></i></div>
                    <span>Đổi mật khẩu</span>
                </a>

            </div> <%-- End function-grid --%>
        </div> 

        <footer class="main-footer">
            <div class="container">
                <jsp:include page="/WEB-INF/footer.jsp" /> 
            </div>
        </footer>

        <%-- ✨ 3. THÊM THƯ VIỆN SWIPER.JS (Bắt buộc) --%>
        <script src="https://cdn.jsdelivr.net/npm/swiper@11/swiper-bundle.min.js"></script>

        <%-- ✨ 4. LINK TỚI FILE JS (Sẽ cập nhật ở Bước 3) --%>
        <script src="<c:url value='/js/index.js'/>"></script> 

    </body>
</html>