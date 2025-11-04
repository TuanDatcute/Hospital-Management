<%--
    Document   : forgotPassword.jsp
    Created on : Nov 4, 2025
    Author     : ADMIN
    Mô tả      : Form cho phép người dùng nhập email để yêu cầu reset mật khẩu.
--%>

<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<%-- Tạo biến URL động --%>
<c:url var="resetControllerUrl" value="/reset" />

<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <title>Quên Mật khẩu</title>
    <%-- Sử dụng chung file CSS với trang login --%>
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.2/css/all.min.css">
    <link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/css/style.css">
</head>
<body class="login-page-body">
    <div class="login-container" style="max-height: 400px;"> <%-- Đặt kích thước cố định nhỏ hơn --%>
        <div class="form-container sign-in" style="opacity: 1; z-index: 5; width: 100%;">
            
            <%-- Nút quay về Login --%>
            <a href="${pageContext.request.contextPath}/login.jsp" class="home-link" title="Quay về Trang Đăng nhập" style="left: 20px; top: 20px;">
                <i class="fas fa-arrow-left"></i>
            </a>
            
            <form action="${resetControllerUrl}" method="POST" class="login-form" style="padding: 0 40px;">
                <input type="hidden" name="action" value="requestReset">
                
                <h1 class="form-title">Quên Mật khẩu</h1>
                <p style="text-align: center; margin-bottom: 20px; font-size: 14px;">
                    Vui lòng nhập email của bạn. Chúng tôi sẽ gửi một link để đặt lại mật khẩu.
                </p>

                <%-- Hiển thị thông báo (nếu có) --%>
                <c:if test="${not empty requestScope.ERROR_MESSAGE}">
                    <p class="error-message">${requestScope.ERROR_MESSAGE}</p>
                </c:if>
                
                <div class="form-group" style="margin-bottom: 15px;">
                    <input type="email" id="email" name="email" placeholder="Nhập email của bạn..." required>
                </div>

                <div class="form-group">
                    <button type="submit" class="btn-submit">Gửi link Reset</button>
                </div>
            </form>
        </div>
    </div>
</body>
</html>