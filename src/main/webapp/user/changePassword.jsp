<%--
    Document   : changePassword.jsp
    Created on : Oct 29, 2025
    Author     : ADMIN
--%>

<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>Đổi mật khẩu</title>

        <%-- Nhúng CSS/Font chung --%>
        <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0/css/all.min.css">
        <link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/css/style.css">
    </head>
    <body class="login-page-body"> <%-- Tái sử dụng class nền và căn giữa --%>

        <div class="login-container" style="width: 500px;"> <%-- Tăng độ rộng 1 chút --%>

            <%-- *** BẮT ĐẦU SỬA LỖI NÚT HOME *** --%>
            <%-- Nút Home giờ sẽ kiểm tra vai trò (ROLE) --%>
            <c:choose>
                <%-- 1. Nếu là Admin, link về Dashboard --%>
                <c:when test="${sessionScope.ROLE == 'QUAN_TRI'}">
                    <a href="${pageContext.request.contextPath}/admin/dashboard.jsp" class="home-link" title="Quay về Bảng điều khiển">
                        <i class="fas fa-home"></i>
                    </a>
                </c:when>
                <%-- 2. Nếu là vai trò khác (đã đăng nhập), link về home.jsp --%>
                <c:when test="${not empty sessionScope.USER}">
                    <a href="${pageContext.request.contextPath}/home.jsp" class="home-link" title="Quay về Trang chủ">
                        <i class="fas fa-home"></i>
                    </a>
                </c:when>
                <%-- 3. Mặc định (chưa đăng nhập), link về index.jsp --%>
                <c:otherwise>
                    <a href="${pageContext.request.contextPath}/index.jsp" class="home-link" title="Quay về Trang giới thiệu">
                        <i class="fas fa-home"></i>
                    </a>
                </c:otherwise>
            </c:choose>
            <%-- *** KẾT THÚC SỬA LỖI NÚT HOME *** --%>

            <h2 class="section-title" style="margin-bottom: 25px;">Đổi mật khẩu</h2>

            <c:if test="${not empty requestScope.ERROR_MESSAGE}">
                <p class="error-message">${requestScope.ERROR_MESSAGE}</p>
            </c:if>

            <form action="MainController" method="post" class="data-form" style="margin-top: 0; box-shadow: none; border: none; padding: 0;">

                <input type="hidden" name="action" value="changePassword" />

                <div class="form-group">
                    <label for="oldPassword">Mật khẩu cũ:</label>
                    <input type="password" id="oldPassword" name="oldPassword" required="required">
                </div>

                <div class="form-group">
                    <label for="newPassword">Mật khẩu mới:</label>
                    <input type="password" id="newPassword" name="newPassword" required="required">
                </div>

                <div class="form-group">
                    <label for="confirmPassword">Xác nhận mật khẩu mới:</label>
                    <input type="password" id="confirmPassword" name="confirmPassword" required="required">
                </div>

                <div class="form-actions">
                    <button type="submit" class="btn-submit">
                        <i class="fas fa-save"></i> Cập nhật Mật khẩu
                    </button>

                    <%-- Nút Hủy (Đã có logic đúng) --%>
                    <c:choose>
                        <c:when test="${sessionScope.ROLE == 'QUAN_TRI'}">
                            <a href="${pageContext.request.contextPath}/admin/dashboard.jsp" class="btn-cancel">Hủy</a>
                        </c:when>
                        <c:otherwise>
                            <a href="${pageContext.request.contextPath}/home.jsp" class="btn-cancel">Hủy</a>
                        </c:otherwise>
                    </c:choose>
                </div>
            </form>

        </div> <%-- Kết thúc .login-container --%>

    </body>
</html>