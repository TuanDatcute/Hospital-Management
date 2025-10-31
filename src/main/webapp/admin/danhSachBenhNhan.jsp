<%--
    Document   : danhSachBenhNhan.jsp
    Created on : Oct 29, 2025
    Author     : ADMIN
--%>

<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>Quản lý Bệnh nhân</title>

        <%-- Nhúng CSS/Font chung --%>
        <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0/css/all.min.css">
        <link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/css/style.css">

        <%-- 
        LƯU Ý: 
        Trang này tái sử dụng các class từ style.css:
        .data-table, .add-new-btn, .edit-btn, .delete-btn (cho nút khóa)
        --%>
    </head>
    <body>

        <%-- Nhúng Header --%>
        <jsp:include page="/WEB-INF/header.jsp" /> 

        <%-- Nội dung chính --%>
        <div class="container page-content" style="padding-top: 30px;">

            <h2 class="section-title">Quản lý Bệnh nhân</h2>

            <%-- Hiển thị thông báo (nếu có) --%>
            <c:if test="${not empty requestScope.SUCCESS_MESSAGE}">
                <p class="success-message">${requestScope.SUCCESS_MESSAGE}</p>
            </c:if>
            <c:if test="${not empty requestScope.ERROR_MESSAGE}">
                <p class="error-message">${requestScope.ERROR_MESSAGE}</p>
            </c:if>

            <%-- Nút Thêm Mới --%>
            <a href="MainController?action=showBenhNhanCreateForm" class="add-new-btn">
                <i class="fas fa-user-plus"></i> Thêm Bệnh nhân Mới
            </a>

            <%-- Bảng Dữ liệu --%>
            <table class="data-table">
                <thead>
                    <tr>
                        <th>ID</th>
                        <th>Mã Bệnh nhân</th>
                        <th>Họ Tên</th>
                        <th>Giới tính</th>
                        <th>Số điện thoại</th>
                        <th>ID Tài khoản</th>
                        <th style="width: 100px;">Hành động</th>
                    </tr>
                </thead>
                <tbody>
                    <%-- Dùng JSTL để lặp qua danh sách LIST_BENHNHAN --%>
                <c:forEach var="bn" items="${requestScope.LIST_BENHNHAN}">
                    <tr>
                        <td>${bn.id}</td>
                        <td><c:out value="${bn.maBenhNhan}" /></td>
                    <td><c:out value="${bn.hoTen}" /></td>
                    <td><c:out value="${bn.gioiTinh}" /></td>
                    <td><c:out value="${bn.soDienThoai}" /></td>

                    <%-- Kiểm tra xem taiKhoanId có null hay không --%>
                    <td>
                    <c:if test="${not empty bn.taiKhoanId}">${bn.taiKhoanId}</c:if>
                    <c:if test="${empty bn.taiKhoanId}"><span style="color: #888;">N/A</span></c:if>
                    </td>

                    <td class="actions">
                        <%-- Link Sửa (trỏ đến showBenhNhanEditForm) --%>
                        <a href="MainController?action=showBenhNhanEditForm&id=${bn.id}" class="edit-btn" title="Sửa thông tin">
                            <i class="fas fa-edit"></i>
                        </a>

                        <%-- 
                           Link Xóa (Soft Delete) - Chỉ hiển thị nếu BN có tài khoản 
                           và tài khoản đó không phải là admin đang đăng nhập.
                        --%>
                    <c:if test="${not empty bn.taiKhoanId && sessionScope.USER.id != bn.taiKhoanId}">
                        <a href="MainController?action=deleteBenhNhan&id=${bn.id}" class="delete-btn" title="Vô hiệu hóa tài khoản"
                           onclick="return confirm('Bạn có chắc chắn muốn vô hiệu hóa tài khoản của bệnh nhân này?');">
                            <i class="fas fa-user-lock"></i> <%-- Icon khóa user --%>
                        </a>
                    </c:if>
                    </td>
                    </tr>
                </c:forEach>

                <%-- Hiển thị nếu danh sách rỗng --%>
                <c:if test="${empty requestScope.LIST_BENHNHAN}">
                    <tr>
                        <td colspan="7" class="empty-cell">Không có dữ liệu bệnh nhân.</td>
                    </tr>
                </c:if>
                </tbody>
            </table>

        </div> <%-- Kết thúc .container.page-content --%>

        <%-- Nhúng Footer --%>
        <jsp:include page="/WEB-INF/footer.jsp" /> 

    </body>
</html>