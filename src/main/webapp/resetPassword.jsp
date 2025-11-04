<%--
    Document   : resetPassword.jsp
    Created on : Nov 4, 2025
    Author     : ADMIN
    Mô tả      : Form cho phép người dùng nhập mật khẩu mới sau khi click link.
--%>

<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<%-- Tạo biến URL động --%>
<c:url var="resetControllerUrl" value="/reset" />

<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <title>Đặt lại Mật khẩu</title>
    <%-- Sử dụng chung file CSS với trang login --%>
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.2/css/all.min.css">
    <link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/css/style.css">
</head>
<body class="login-page-body">
    <div class="login-container" style="max-height: 450px;">
        <div class="form-container sign-in" style="opacity: 1; z-index: 5; width: 100%;">
            
            <form action="${resetControllerUrl}" method="POST" class="login-form" style="padding: 0 40px;">
                <input type="hidden" name="action" value="performReset">
                <input type="hidden" name="token" value="<c:out value='${requestScope.token}'/>">
                
                <h1 class="form-title">Đặt lại Mật khẩu</h1>
                <p style="text-align: center; margin-bottom: 20px; font-size: 14px;">
                    Vui lòng nhập mật khẩu mới của bạn.
                </p>

                <%-- Hiển thị lỗi (nếu có) --%>
                <c:if test="${not empty requestScope.ERROR_MESSAGE}">
                    <p class="error-message">${requestScope.ERROR_MESSAGE}</p>
                </c:if>

                <div class="form-group" style="margin-bottom: 15px;">
                    <input type="password" id="newPassword" name="newPassword" placeholder="Mật khẩu mới..." required>
                </div>
                
                <div class="form-group" style="margin-bottom: 15px;">
                    <input type="password" id="confirmPassword" name="confirmPassword" placeholder="Xác nhận mật khẩu mới..." required>
                </div>

                <div class="form-group">
                    <button type="submit" class="btn-submit">Lưu Mật khẩu</button>
                </div>
            </form>
        </div>
    </div>
</body>
</html>