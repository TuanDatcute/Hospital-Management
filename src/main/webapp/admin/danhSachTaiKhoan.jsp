<%--
    Document   : danhSachTaiKhoan.jsp
    Created on : Oct 29, 2025
    Author     : ADMIN
--%>

<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>Quản lý Tài khoản</title>

        <%-- Nhúng CSS/Font chung --%>
        <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0/css/all.min.css">
        <link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/css/style.css">
    </head>
    <body>

        <%-- Nhúng Header --%>
        <jsp:include page="/WEB-INF/header.jsp" /> 

        <%-- Nội dung chính --%>
        <div class="container page-content" style="padding-top: 30px;">

            <h2 class="section-title">Quản lý Tài khoản</h2>

            <%-- Hiển thị thông báo (nếu có) --%>
            <c:if test="${not empty requestScope.SUCCESS_MESSAGE}">
                <p class="success-message">${requestScope.SUCCESS_MESSAGE}</p>
            </c:if>
            <c:if test="${not empty requestScope.ERROR_MESSAGE}">
                <p class="error-message">${requestScope.ERROR_MESSAGE}</p>
            </c:if>

            <%-- Nút Thêm Mới --%>
            <a href="MainController?action=showUserCreateForm" class="add-new-btn">
                <i class="fas fa-user-plus"></i> Thêm Tài khoản Mới
            </a>

            <%-- Bảng Dữ liệu --%>
            <table class="data-table">
                <thead>
                    <tr>
                        <th>ID</th>
                        <th>Tên đăng nhập</th>
                        <th>Email</th>
                        <th>Vai trò</th>
                        <th>Trạng thái</th>
                        <th style="width: 120px;">Hành động</th>
                    </tr>
                </thead>
                <tbody>
                    <%-- Dùng JSTL để lặp qua danh sách LIST_TAIKHOAN --%>
                    <c:forEach var="tk" items="${requestScope.LIST_TAIKHOAN}">
                        <tr>
                            <td>${tk.id}</td>
                            <td><c:out value="${tk.tenDangNhap}" /></td>
                            <td><c:out value="${tk.email}" /></td>
                            <td><c:out value="${tk.vaiTro}" /></td>

                            <td>
                                <c:if test="${tk.trangThai == 'HOAT_DONG'}">
                                    <span style="color: green; font-weight: bold;">Hoạt động</span>
                                </c:if>
                                <c:if test="${tk.trangThai == 'BI_KHOA'}">
                                    <span style="color: red; font-weight: bold;">Đã khóa</span>
                                </c:if>
                            </td>

                            <td class="actions">
                                <%-- Không cho admin tự khóa chính mình --%>
                                <c:if test="${sessionScope.USER.id != tk.id}">
                                    <%-- Link Sửa --%>
                                    <a href="MainController?action=showUserEditForm&id=${tk.id}" class="edit-btn" title="Sửa Trạng thái/Vai trò">
                                        <i class="fas fa-edit"></i>
                                    </a>

                                    <%-- Nút Khóa --%>
                                    <c:if test="${tk.trangThai == 'HOAT_DONG'}">
                                        <a href="MainController?action=updateUserStatus&id=${tk.id}&trangThai=BI_KHOA" class="delete-btn" title="Khóa tài khoản"
                                           onclick="return confirm('Bạn có chắc chắn muốn KHÓA tài khoản này?');">
                                            <i class="fas fa-lock"></i>
                                        </a>
                                    </c:if>

                                    <%-- Nút Mở khóa --%>
                                    <c:if test="${tk.trangThai == 'BI_KHOA'}">
                                        <a href="MainController?action=updateUserStatus&id=${tk.id}&trangThai=HOAT_DONG" class="edit-btn" title="Mở khóa tài khoản"
                                           onclick="return confirm('Bạn có chắc chắn muốn MỞ KHÓA tài khoản này?');">
                                            <i class="fas fa-lock-open"></i>
                                        </a>
                                    </c:if>
                                </c:if>

                                <c:if test="${sessionScope.USER.id == tk.id}">
                                    (Đây là bạn)
                                </c:if>
                            </td>
                        </tr>
                    </c:forEach>

                    <c:if test="${empty requestScope.LIST_TAIKHOAN}">
                        <tr>
                            <td colspan="6" class="empty-cell">Không có dữ liệu tài khoản.</td>
                        </tr>
                    </c:if>
                </tbody>
            </table>

        </div> <%-- Kết thúc .container.page-content --%>

        <%-- Nhúng Footer --%>
        <jsp:include page="/WEB-INF/footer.jsp" /> 

    </body>
</html>