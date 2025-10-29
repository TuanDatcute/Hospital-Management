<%--
    Document   : danhSachKhoa.jsp
    Created on : Oct 29, 2025
    Author     : ADMIN
--%>

<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>Quản lý Khoa</title>

        <%-- Nhúng CSS/Font chung --%>
        <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0/css/all.min.css">
        <link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/css/style.css">
    </head>
    <body>

        <%-- Nhúng Header --%>
        <jsp:include page="/WEB-INF/header.jsp" /> 

        <%-- Nội dung chính --%>
        <div class="container page-content" style="padding-top: 30px;">

            <h2 class="section-title">Quản lý Khoa</h2>

            <%-- Hiển thị thông báo (nếu có) --%>
            <c:if test="${not empty requestScope.SUCCESS_MESSAGE}">
                <p class="success-message">${requestScope.SUCCESS_MESSAGE}</p>
            </c:if>
            <c:if test="${not empty requestScope.ERROR_MESSAGE}">
                <p class="error-message">${requestScope.ERROR_MESSAGE}</p>
            </c:if>

            <%-- Nút Thêm Mới --%>
            <a href="MainController?action=showKhoaCreateForm" class="add-new-btn">
                <i class="fas fa-plus"></i> Thêm Khoa Mới
            </a>

            <%-- Bảng Dữ liệu --%>
            <table class="data-table">
                <thead>
                    <tr>
                        <th>ID</th>
                        <th>Tên Khoa</th>
                        <th>Mô tả</th>
                        <th style="width: 100px;">Hành động</th>
                    </tr>
                </thead>
                <tbody>
                    <%-- Dùng JSTL để lặp qua danh sách LIST_KHOA --%>
                    <c:forEach var="khoa" items="${requestScope.LIST_KHOA}">
                        <tr>
                            <td>${khoa.id}</td>
                            <td><c:out value="${khoa.tenKhoa}" /></td>
                            <td><c:out value="${khoa.moTa}" /></td>
                            <td class="actions">
                                <%-- Link Sửa --%>
                                <a href="MainController?action=showKhoaEditForm&id=${khoa.id}" class="edit-btn" title="Sửa">
                                    <i class="fas fa-edit"></i>
                                </a>
                                <%-- Link Xóa --%>
                                <a href="MainController?action=deleteKhoa&id=${khoa.id}" class="delete-btn" title="Xóa"
                                   onclick="return confirm('Bạn có chắc chắn muốn xóa khoa này? \n(Nhân viên thuộc khoa này sẽ bị set về NULL)');">
                                    <i class="fas fa-trash-alt"></i>
                                </a>
                            </td>
                        </tr>
                    </c:forEach>

                    <%-- Hiển thị nếu danh sách rỗng --%>
                    <c:if test="${empty requestScope.LIST_KHOA}">
                        <tr>
                            <td colspan="4" class="empty-cell">Không có dữ liệu khoa.</td>
                        </tr>
                    </c:if>
                </tbody>
            </table>

        </div> <%-- Kết thúc .container.page-content --%>

        <%-- Nhúng Footer --%>
        <jsp:include page="/WEB-INF/footer.jsp" /> 

    </body>
</html>