<%--
    Document   : editDOB.jsp
    Created on : Nov 5, 2025
    Author     : ADMIN
    Mô tả      : Form cho phép người dùng sửa Ngày sinh sau khi đã xác thực.
--%>

<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<c:url var="securityControllerUrl" value="/SecurityController" />

<!DOCTYPE html>
<html lang="vi">
    <head>
        <meta charset="UTF-8">
        <title>Thay đổi Ngày sinh</title>
        <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.2/css/all.min.css">
        <link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/css/style.css">
    </head>
    <body class="login-page-body">
        <div class="login-container" style="max-height: 450px;"> 
            <div class="form-container sign-in" style="opacity: 1; z-index: 5; width: 100%;">

                <a href="${pageContext.request.contextPath}/MainController?action=showProfile" class="home-link" title="Quay về Hồ sơ" style="left: 20px; top: 20px;">
                    <i class="fas fa-arrow-left"></i>
                </a>

                <form action="${securityControllerUrl}" method="POST" class="login-form" style="padding: 0 40px;">
                    <input type="hidden" name="action" value="saveDOB">

                    <h1 class="form-title">
                        <i class="fas fa-calendar-day" style="color: #007bff; margin-bottom: 15px; font-size: 2.5rem;"></i>
                        <br>Thay đổi Ngày sinh
                    </h1>
                    <p style="text-align: center; margin-bottom: 20px; font-size: 14px;">
                        Nhập ngày sinh mới của bạn.
                    </p>

                    <c:if test="${not empty requestScope.ERROR_MESSAGE}">
                        <p class="error-message">${requestScope.ERROR_MESSAGE}</p>
                    </c:if>

                    <div class="form-group" style="margin-bottom: 15px;">
                        <label style="text-align: left; font-size: 0.9em; margin-bottom: 5px;">Ngày sinh hiện tại:</label>
                        <input type="date" value="${requestScope.currentDOB}" disabled 
                               style="background-color: #f4f4f4; color: #555;">
                    </div>

                    <div class="form-group" style="margin-bottom: 15px;">
                        <label style="text-align: left; font-size: 0.9em; margin-bottom: 5px;">Ngày sinh mới (*):</label>
                        <input type="date" id="newDOB" name="newDOB" required>
                    </div>

                    <div class="form-group">
                        <button type="submit" class="btn-submit">Lưu thay đổi</button>
                    </div>
                </form>
            </div>
        </div>
    </body>
</html>