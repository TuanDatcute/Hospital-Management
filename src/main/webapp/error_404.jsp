<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<html>
    <head>
        <title>Truy cập bị cấm (403)</title>
        <style>
            body {
                font-family: Arial, sans-serif;
                text-align: center;
                margin-top: 50px;
            }
            .container {
                padding: 20px;
                border: 1px solid #ddd;
                display: inline-block;
            }
            h1 {
                color: #dc3545;
            }
            .message {
                font-size: 1.2em;
                margin: 20px 0;
            }
        </style>
    </head>
    <body>
        <div class="container">
            <h1>Lỗi 403 - Truy cập bị cấm</h1>
            <div class="message">
                <p>Rất tiếc, vai trò của bạn không có quyền thực hiện chức năng này.</p>
                <c:if test="${not empty ERROR_MESSAGE}">
                    <p><b>Lý do:</b> <c:out value="${ERROR_MESSAGE}"/></p>
                </c:if>
            </div>
            <p>
                <%-- Sửa 'home' thành action trang chủ của bạn --%>
                <a href="${pageContext.request.contextPath}/main?action=home">Quay về Trang chủ</a> 
                |
                <a href="${pageContext.request.contextPath}/main?action=logout">Đăng xuất</a>
            </p>
        </div>
    </body>
</html>