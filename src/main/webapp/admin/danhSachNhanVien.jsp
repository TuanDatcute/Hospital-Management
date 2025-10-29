<%--
    Document   : danhSachNhanVien.jsp
    Created on : Oct 29, 2025
    Author     : ADMIN
--%>

<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>Quản lý Nhân viên</title>

        <%-- Nhúng CSS/Font chung --%>
        <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0/css/all.min.css">
        <link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/css/style.css">

        <%-- 
        LƯU Ý: 
        Trang này tái sử dụng các class từ style.css:
        .data-table, .add-new-btn, .edit-btn, .delete-btn
        --%>
    </head>
    <body>

        <%-- Nhúng Header --%>
        <jsp:include page="/WEB-INF/header.jsp" /> 

        <%-- Nội dung chính --%>
        <div class="container page-content" style="padding-top: 30px;">

            <h2 class="section-title">Quản lý Nhân viên</h2>

            <%-- Hiển thị thông báo (nếu có) --%>
            <c:if test="${not empty requestScope.SUCCESS_MESSAGE}">
                <p class="success-message">${requestScope.SUCCESS_MESSAGE}</p>
            </c:if>
            <c:if test="${not empty requestScope.ERROR_MESSAGE}">
                <p class="error-message">${requestScope.ERROR_MESSAGE}</p>
            </c:if>

            <%-- Nút Thêm Mới --%>
            <a href="MainController?action=showNhanVienCreateForm" class="add-new-btn">
                <i class="fas fa-user-plus"></i> Thêm Nhân viên Mới
            </a>

            <%-- Bảng Dữ liệu --%>
            <table class="data-table">
                <thead>
                    <tr>
                        <th>ID</th>
                        <th>Họ Tên</th>
                        <th>Chuyên môn</th>
                        <th>Số điện thoại</th>
                        <th>ID Khoa</th> <%-- Hiển thị ID Khoa, vì DTO chỉ có ID --%>
                        <th style="width: 100px;">Hành động</th>
                    </tr>
                </thead>
                <tbody>
                    <%-- Dùng JSTL để lặp qua danh sách LIST_NHANVIEN --%>
                    <c:forEach var="nv" items="${requestScope.LIST_NHANVIEN}">
                        <tr>
                            <td>${nv.id}</td>
                            <td><c:out value="${nv.hoTen}" /></td>
                            <td><c:out value="${nv.chuyenMon}" /></td>
                            <td><c:out value="${nv.soDienThoai}" /></td>

                            <%-- Kiểm tra xem khoaId có null hay không --%>
                            <td>
                                <c:if test="${not empty nv.khoaId}">${nv.khoaId}</c:if>
                                <c:if test="${empty nv.khoaId}"><span style="color: #888;">N/A</span></c:if>
                                </td>

                                <td class="actions">
                                <%-- Không cho admin tự vô hiệu hóa chính mình --%>
                                <c:if test="${sessionScope.USER.id != nv.taiKhoanId}">
                                    <%-- Link Sửa --%>
                                    <a href="MainController?action=showNhanVienEditForm&id=${nv.id}" class="edit-btn" title="Sửa thông tin">
                                        <i class="fas fa-edit"></i>
                                    </a>
                                    <%-- Link Xóa (Soft Delete) --%>
                                    <a href="MainController?action=deleteNhanVien&id=${nv.id}" class="delete-btn" title="Vô hiệu hóa"
                                       onclick="return confirm('Bạn có chắc chắn muốn vô hiệu hóa nhân viên này? \n(Tài khoản liên kết sẽ bị khóa)');">
                                        <i class="fas fa-user-lock"></i> <%-- Icon khóa user --%>
                                    </a>
                                </c:if>

                                <c:if test="${sessionScope.USER.id == nv.taiKhoanId}">
                                    (Đây là bạn)
                                </c:if>
                            </td>
                        </tr>
                    </c:forEach>

                    <%-- Hiển thị nếu danh sách rỗng --%>
                    <c:if test="${empty requestScope.LIST_NHANVIEN}">
                        <tr>
                            <td colspan="6" class="empty-cell">Không có dữ liệu nhân viên.</td>
                        </tr>
                    </c:if>
                </tbody>
            </table>

        </div> <%-- Kết thúc .container.page-content --%>

        <%-- Nhúng Footer --%>
        <jsp:include page="/WEB-INF/footer.jsp" /> 

    </body>
</html>