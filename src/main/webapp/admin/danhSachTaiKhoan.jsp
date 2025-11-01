<%--
    Document   : danhSachTaiKhoan.jsp (Đã sửa lỗi GET/POST)
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

        <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0/css/all.min.css">
        <link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/css/style.css">
    </head>
    <body>

        <jsp:include page="/WEB-INF/header.jsp" /> 

        <div class="container page-content" style="padding-top: 30px;">

            <h2 class="section-title">Quản lý Tài khoản</h2>

            <c:if test="${not empty requestScope.SUCCESS_MESSAGE}">
                <p class="success-message">${requestScope.SUCCESS_MESSAGE}</p>
            </c:if>
            <c:if test="${not empty requestScope.ERROR_MESSAGE}">
                <p class="error-message">${requestScope.ERROR_MESSAGE}</p>
            </c:if>

            <a href="${pageContext.request.contextPath}/MainController?action=showUserCreateForm" class="add-new-btn">
                <i class="fas fa-user-plus"></i> Thêm Tài khoản Mới
            </a>

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
                                <%-- Không cho admin tự khóa/sửa chính mình --%>
                                <c:if test="${sessionScope.USER.id != tk.id}">

                                    <%-- Link Sửa (vẫn là GET vì nó chỉ HIỂN THỊ form) --%>
                                    <a href="${pageContext.request.contextPath}/MainController?action=showUserEditForm&id=${tk.id}" class="edit-btn" title="Sửa Trạng thái">
                                        <i class="fas fa-edit"></i>
                                    </a>

                                    <%-- **SỬA LỖI: Dùng Form POST cho Nút Khóa** --%>
                                    <c:if test="${tk.trangThai == 'HOAT_DONG'}">
                                        <form action="${pageContext.request.contextPath}/MainController" method="post" class="table-form" 
                                              onsubmit="return confirm('Bạn có chắc chắn muốn KHÓA tài khoản này?');">

                                            <input type="hidden" name="action" value="updateUserStatus" />
                                            <input type="hidden" name="id" value="${tk.id}" />
                                            <input type="hidden" name="trangThai" value="BI_KHOA" />

                                            <button type="submit" class="delete-btn" title="Khóa tài khoản">
                                                <i class="fas fa-lock"></i>
                                            </button>
                                        </form>
                                    </c:if>

                                    <%-- **SỬA LỖI: Dùng Form POST cho Nút Mở khóa** --%>
                                    <c:if test="${tk.trangThai == 'BI_KHOA'}">
                                        <form action="${pageContext.request.contextPath}/MainController" method="post" class="table-form" 
                                              onsubmit="return confirm('Bạn có chắc chắn muốn MỞ KHÓA tài khoản này?');">

                                            <input type="hidden" name="action" value="updateUserStatus" />
                                            <input type="hidden" name="id" value="${tk.id}" />
                                            <input type="hidden" name="trangThai" value="HOAT_DONG" />

                                            <button type="submit" class="edit-btn" title="Mở khóa tài khoản">
                                                <i class="fas fa-lock-open"></i>
                                            </button>
                                        </form>
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

        </div> 

        <jsp:include page="/WEB-INF/footer.jsp" /> 

    </body>
</html>