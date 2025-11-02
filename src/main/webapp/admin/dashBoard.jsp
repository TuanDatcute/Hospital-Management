<%--
    Document   : dashboard.jsp (Trang chủ Admin)
    Created on : Oct 29, 2025
    Author     : ADMIN
--%>

<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>Bảng điều khiển Admin</title>

        <%-- Nhúng CSS/Font chung --%>
        <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0/css/all.min.css">
        <link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/css/style.css">
    </head>
    <body>

        <%-- Nhúng Header --%>
        <jsp:include page="/WEB-INF/header.jsp" /> 

        <div class="container page-content" style="padding-top: 30px;">

            <h2 class="section-title">Bảng điều khiển Quản trị</h2>

            <%-- Hiển thị thông báo thành công (ví dụ: đổi mật khẩu thành công) --%>
            <c:if test="${not empty sessionScope.SUCCESS_MESSAGE}">
                <p class="success-message">${sessionScope.SUCCESS_MESSAGE}</p>
                <c:remove var="SUCCESS_MESSAGE" scope="session" />
            </c:if>

            <div class="home-welcome-message">
                <%-- Dòng "Chào mừng Admin !" giống trong ảnh --%>
                Chào mừng ${sessionScope.USER.tenDangNhap} ! 
            </div>

            <%-- Lưới chức năng (Tái sử dụng class từ style.css) --%>
            <div class="function-grid">

                <%-- 1. Link đến Quản lý Tài khoản --%>
                <a href="${pageContext.request.contextPath}/MainController?action=listUsers" class="function-card">
                    <div class="icon"><i class="fas fa-users-cog"></i></div>
                    <span>Quản lý Tài khoản</span>
                </a>

                <%-- 2. Link đến Quản lý Khoa --%>
                <a href="${pageContext.request.contextPath}/MainController?action=listKhoa" class="function-card">
                    <div class="icon"><i class="fas fa-hospital-symbol"></i></div>
                    <span>Quản lý Khoa</span>
                </a>

                <%-- 3. Link đến Quản lý Nhân viên --%>
                <a href="${pageContext.request.contextPath}/MainController?action=listNhanVien" class="function-card">
                    <div class="icon"><i class="fas fa-user-nurse"></i></div>
                    <span>Quản lý Nhân viên</span>
                </a>

                <%-- 4. Link đến Quản lý Bệnh nhân --%>
                <a href="${pageContext.request.contextPath}/MainController?action=listBenhNhan" class="function-card">
                    <div class="icon"><i class="fas fa-procedures"></i></div>
                    <span>Quản lý Bệnh nhân</span>
                </a>

                <%-- 5. Link đến Quản lý Lịch hẹn --%>
                <a href="${pageContext.request.contextPath}/MainController?action=listLichHen" class="function-card">
                    <div class="icon"><i class="fas fa-calendar-alt"></i></div>
                    <span>Quản lý Lịch hẹn</span>
                </a>

                <%-- 6. Link đến Đổi mật khẩu --%>
                <a href="${pageContext.request.contextPath}/MainController?action=showChangePasswordForm" class="function-card">
                    <div class="icon"><i class="fas fa-key"></i></div>
                    <span>Đổi mật khẩu</span>
                </a>

            </div> <%-- End function-grid --%>
        </div> <%-- End container --%>

        <%-- Nhúng Footer --%>
        <jsp:include page="/WEB-INF/footer.jsp" /> 

    </body>
</html>