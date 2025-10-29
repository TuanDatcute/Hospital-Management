<%--
    Document   : register.jsp
    Created on : Oct 29, 2025
    Author     : ADMIN
--%>

<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>Đăng ký - Quản lý Bệnh viện</title>

        <%-- 1. Nhúng CSS/Font chung (Tái sử dụng style của trang login) --%>
        <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0/css/all.min.css">
        <link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/css/style.css">
    </head>
    <body class="login-page-body"> <%-- Tái sử dụng class nền và căn giữa --%>

        <div class="login-container" style="width: 400px;"> <%-- Tăng độ rộng 1 chút --%>

            <%-- **THÊM NÚT HOME VÀO ĐÂY** --%>
            <a href="${pageContext.request.contextPath}/index.jsp" class="home-link" title="Quay về Trang chủ">
                <i class="fas fa-home"></i> <%-- Icon ngôi nhà --%>
            </a>

            <h1>Đăng ký Tài khoản Bệnh nhân</h1>

            <%-- Hiển thị thông báo lỗi nếu có (ví dụ: mật khẩu không khớp) --%>
            <c:if test="${not empty requestScope.ERROR_MESSAGE}">
                <div class="error-message">${requestScope.ERROR_MESSAGE}</div>
            </c:if>

            <form action="MainController" method="post">
                <%-- Action này sẽ được MainController chuyển đến UserController (doPost) --%>
                <input type="hidden" name="action" value="register" /> 
                <table>
                    <tr>
                        <td>Tên đăng nhập:</td>
                        <%-- Dùng EL để giữ lại giá trị nếu đăng ký lỗi --%>
                        <td><input type="text" name="username" required="required" value="<c:out value="${requestScope.username_register}"/>" /></td>
                    </tr>
                    <tr>
                        <td>Email:</td>
                        <td><input type="email" name="email" required="required" value="<c:out value="${requestScope.email_register}"/>" /></td>
                    </tr>
                    <tr>
                        <td>Mật khẩu:</td>
                        <td><input type="password" name="password" required="required" /></td>
                    </tr>
                    <tr>
                        <td>Xác nhận mật khẩu:</td>
                        <td><input type="password" name="confirmPassword" required="required" /></td>
                    </tr>
                    <tr>
                        <td colspan="2" style="text-align: center; padding-top: 20px;">
                            <input type="submit" value="Đăng ký" />
                        </td>
                    </tr>
                    <tr>
                        <td colspan="2" style="text-align: center; padding-top: 10px;">
                            Đã có tài khoản? <a href="login.jsp">Đăng nhập ngay</a>
                        </td>
                    </tr>
                </table>
            </form>
        </div>

    </body>
</html>