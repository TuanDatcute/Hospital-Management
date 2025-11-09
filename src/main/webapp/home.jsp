<%--
    Document    : home.jsp (Trang chủ Bệnh nhân)
    Created on : Oct 29, 2025
    Author      : ADMIN (Đã cập nhật link Hồ sơ)
--%>

<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>Trang chủ Bệnh nhân</title>
        <link rel="preconnect" href="https://fonts.googleapis.com">
        <link rel="preconnect" href="https://fonts.gstatic.com" crossorigin>
        <link href="https://fonts.googleapis.com/css2?family=Poppins:wght@400;500;600;700&display=swap" rel="stylesheet">

        <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0/css/all.min.css">
        <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/swiper@11/swiper-bundle.min.css" />
        <link href="https://fonts.googleapis.com/css2?family=Be+Vietnam+Pro:wght@400;500;600;700&display=swap" rel="stylesheet">


        <%-- Tăng version CSS --%>
        <link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/css/index.css?v=1.2">
        <link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/css/home.css?v=1.0">
    </head>
    <body>

        <jsp:include page="/WEB-INF/headerDat.jsp" /> 

        <div class="container page-content home-content" style="padding-top: 30px;">

            <h2 class="section-title">Bảng điều khiển</h2>

            <c:if test="${not empty sessionScope.SUCCESS_MESSAGE}">
                <p class="success-message">${sessionScope.SUCCESS_MESSAGE}</p>
                <c:remove var="SUCCESS_MESSAGE" scope="session" />
            </c:if>

            <div class="home-welcome-message">
                Chào mừng bạn trở lại, <strong>${sessionScope.USER.tenDangNhap}</strong>!
                (Vai trò: ${sessionScope.ROLE})
            </div>

            <div class="function-grid">


                <%-- Chức năng Bệnh nhân --%>
                <c:if test="${sessionScope.ROLE == 'BENH_NHAN'}">

                    <a href="MainController?action=showProfile" class="function-card">
                        <div class="icon"><i class="fas fa-id-card"></i></div>
                        <span>Hồ sơ của tôi</span>
                    </a>
                    <a href="#" class="function-card">
                        <div class="icon"><i class="fas fa-history"></i></div>
                        <span>Xem Lịch sử khám</span>
                    </a>
                    <a href="MainController?action=myAppointments" class="function-card">
                        <div class="icon"><i class="fas fa-calendar-plus"></i></div>
                        <span>Đặt Lịch hẹn</span>
                    </a>
                </c:if>

                <%-- Chức năng chung (Vẫn giữ cho Bệnh nhân) --%>
                <a href="MainController?action=showChangePasswordForm" class="function-card">
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