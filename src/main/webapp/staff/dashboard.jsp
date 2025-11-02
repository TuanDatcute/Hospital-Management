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
    
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0/css/all.min.css">
    <link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/css/style.css">
</head>
<body>

    <jsp:include page="/WEB-INF/header.jsp" /> 

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
            <a href="${pageContext.request.contextPath}/MainController?action=showCreateForm" class="function-card">
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

    <jsp:include page="/WEB-INF/footer.jsp" /> 

</body>
</html>