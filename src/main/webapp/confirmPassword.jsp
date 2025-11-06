<%--
    Document   : confirmPassword.jsp
    Created on : Nov 5, 2025
    Author     : ADMIN
    Mô tả      : Yêu cầu người dùng nhập lại mật khẩu để xác thực.
--%>

<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<%-- Tạo biến URL động --%>
<c:url var="securityControllerUrl" value="/SecurityController" />

<!DOCTYPE html>
<html lang="vi">
    <head>
        <meta charset="UTF-8">
        <title>Xác thực Bảo mật</title>
        <%-- Sử dụng chung file CSS với trang login --%>
        <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.2/css/all.min.css">
        <link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/css/style.css">
    </head>
    <body class="login-page-body">
        <div class="login-container" style="max-height: 420px;"> 
            <div class="form-container sign-in" style="opacity: 1; z-index: 5; width: 100%;">

                <%-- Nút quay về Hồ sơ --%>
                <a href="${pageContext.request.contextPath}/MainController?action=showProfile" class="home-link" title="Quay về Hồ sơ" style="left: 20px; top: 20px;">
                    <i class="fas fa-arrow-left"></i>
                </a>

                <form action="${securityControllerUrl}" method="POST" class="login-form" style="padding: 0 40px;">
                    <input type="hidden" name="action" value="confirmPassword">

                    <input type="hidden" name="nextAction" value="<c:out value='${requestScope.nextAction}'/>">

                    <h1 class="form-title">
                        <i class="fas fa-shield-alt" style="color: #dc3545; margin-bottom: 15px; font-size: 2.5rem;"></i>
                        <br>Yêu cầu Xác thực
                    </h1>
                    <p style="text-align: center; margin-bottom: 20px; font-size: 14px;">
                        Vì lý do bảo mật, vui lòng nhập lại mật khẩu của bạn để tiếp tục.
                    </p>

                    <%-- Hiển thị thông báo (nếu có) --%>
                    <c:if test="${not empty requestScope.ERROR_MESSAGE}">
                        <p class="error-message">${requestScope.ERROR_MESSAGE}</p>
                    </c:if>

                    <div class="form-group" style="margin-bottom: 15px;">
                        <input type="password" id="password" name="password" placeholder="Nhập mật khẩu của bạn..." required>
                    </div>

                    <div class="form-group">
                        <button type="submit" class="btn-submit">Xác nhận</button>
                    </div>
                </form>
            </div>
        </div>
    </body>
</html>