<%--
    Document   : login.jsp
    Created on : Oct 27, 2025
    Author     : ADMIN
--%>

<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<!DOCTYPE html>
<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <title>Đăng nhập - Quản lý Bệnh viện</title>
    <link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/css/style.css">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0/css/all.min.css">
</head>
<body class="login-page-body">

    <div class="login-container">
    
        <%-- **THÊM NÚT HOME VÀO ĐÂY** --%>
        <a href="${pageContext.request.contextPath}/index.jsp" class="home-link" title="Quay về Trang chủ">
            <i class="fas fa-home"></i>
        </a>
        
        <h1>Đăng nhập Hệ thống</h1>

        <%-- Hiển thị thông báo đăng ký thành công (nếu có) --%>
        <c:if test="${not empty sessionScope.SUCCESS_MESSAGE}">
            <%-- Dùng class .success-message từ CSS chung --%>
            <p class="success-message">
                ${sessionScope.SUCCESS_MESSAGE}
            </p>
            <c:remove var="SUCCESS_MESSAGE" scope="session" />
        </c:if>
        
        <%-- Hiển thị thông báo lỗi đăng nhập --%>
        <c:if test="${not empty ERROR_MESSAGE}">
            <div class="error-message">${ERROR_MESSAGE}</div>
        </c:if>
            <div class="error-message">${ERROR_MESSAGE}</div>

        <form action="MainController" method="post">
            <input type="hidden" name="action" value="login" />
            <table>
                <tr>
                    <td>Tên đăng nhập:</td>
                    <td><input type="text" name="username" required="required" /></td>
                </tr>
                <tr>
                    <td>Mật khẩu:</td>
                    <td><input type="password" name="password" required="required" /></td>
                </tr>
                <tr>
                    <td colspan="2" style="text-align: center; padding-top: 20px;">
                        <input type="submit" value="Đăng nhập" />
                        
                        <%-- **SỬA LẠI ONCLICK CHO ĐÚNG ACTION** --%>
                        <button type="button" 
                                onclick="window.location.href='${pageContext.request.contextPath}/MainController?action=showUserRegister'">
                            Đăng ký
                        </button>
                    </td>
                </tr>
            </table>
        </form>
    </div>
</body>
</html>