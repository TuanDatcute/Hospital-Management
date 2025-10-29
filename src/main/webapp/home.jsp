<%--
    Document   : home.jsp (Trang chủ Bệnh nhân / User chung)
    Created on : Oct 29, 2025
    Author     : ADMIN
--%>

<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>Trang chủ - Quản lý Bệnh viện</title>
        <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0/css/all.min.css">
        <link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/css/style.css">
    </head>
    <body>

        <jsp:include page="/WEB-INF/header.jsp" /> 

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

                <%-- Chức năng Bác sĩ / Lễ tân --%>
                <c:if test="${sessionScope.ROLE == 'BAC_SI' || sessionScope.ROLE == 'LE_TAN'}">
                    <a href="MainController?action=listLichHen" class="function-card">
                        <div class="icon"><i class="fas fa-calendar-check"></i></div>
                        <span>Quản lý Lịch hẹn</span>
                    </a>
                    <a href="MainController?action=showCreateForm" class="function-card">
                        <div class="icon"><i class="fas fa-file-medical"></i></div>
                        <span>Tạo Phiếu khám</span>
                    </a>
                    <a href="MainController?action=listBenhNhan" class="function-card">
                        <div class="icon"><i class="fas fa-user-injured"></i></div>
                        <span>Danh sách Bệnh nhân</span>
                    </a>
                </c:if>

                <%-- Chức năng Bệnh nhân --%>
                <c:if test="${sessionScope.ROLE == 'BENH_NHAN'}">
                    <a href="#" class="function-card">
                        <div class="icon"><i class="fas fa-history"></i></div>
                        <span>Xem Lịch sử khám</span>
                    </a>
                    <a href="MainController?action=showLichHenCreateForm" class="function-card">
                        <div class="icon"><i class="fas fa-calendar-plus"></i></div>
                        <span>Đặt Lịch hẹn</span>
                    </a>
                </c:if>

                <%-- Chức năng chung --%>
                <a href="MainController?action=showChangePasswordForm" class="function-card">
                    <div class="icon"><i class="fas fa-key"></i></div>
                    <span>Đổi mật khẩu</span>
                </a>

            </div> <%-- End function-grid --%>
        </div> 

        <jsp:include page="/WEB-INF/footer.jsp" /> 

    </body>
</html>