<%--
    Document    : danhSachTaiKhoan.jsp
    Created on  : Oct 29, 2025
    Author      : ADMIN
    (ĐÃ NÂNG CẤP: Giao diện V2.1 + Tách file admin-list.css)
--%>

<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>Quản lý Tài khoản</title>

        <link rel="preconnect" href="https://fonts.googleapis.com">
        <link rel="preconnect" href="https://fonts.gstatic.com" crossorigin>
        <link href="https://fonts.googleapis.com/css2?family=Poppins:wght@400;500;600;700&display=swap" rel="stylesheet">

        <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0/css/all.min.css">
        <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/swiper@11/swiper-bundle.min.css" />
        <link href="https://fonts.googleapis.com/css2?family=Be+Vietnam+Pro:wght@400;500;600;700&display=swap" rel="stylesheet">

        <%-- CSS Chung --%>
        <link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/css/index.css?v=1.5">

        <%-- ✨ SỬ DỤNG CHUNG CSS DANH SÁCH ✨ --%>
        <link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/css/admin-list.css?v=1.0">

        <%-- Khối <style> ... </style> đã được xóa --%>
    </head>
    <body>

        <jsp:include page="/WEB-INF/headerDat.jsp" /> 

        <div class="container page-content" style="padding-top: 30px;">

            <h2 class="section-title">Quản lý Tài khoản</h2>

            <%-- === SỬA LỖI PRG (Post-Redirect-Get) === --%>
            <c:if test="${not empty sessionScope.SUCCESS_MESSAGE}">
                <p class="success-message">${sessionScope.SUCCESS_MESSAGE}</p>
                <c:remove var="SUCCESS_MESSAGE" scope="session" />
            </c:if>
            <c:if test="${not empty sessionScope.ERROR_MESSAGE}">
                <p class="error-message">${sessionScope.ERROR_MESSAGE}</p>
                <c:remove var="ERROR_MESSAGE" scope="session" />
            </c:if>
            <%-- === KẾT THÚC SỬA LỖI PRG === --%>

            <%-- ✨ BẮT ĐẦU TOOLBAR MỚI (Đã bọc lại) ✨ --%>
            <div class="toolbar">
                <a href="${pageContext.request.contextPath}/MainController?action=showUserCreateForm" class="add-new-btn">
                    <i class="fas fa-user-plus"></i> Thêm Tài khoản Mới
                </a>

                <div class="search-container">
                    <form action="MainController" method="GET">
                        <input type="hidden" name="action" value="listUsers" />
                        <input type="text" name="keyword" 
                               placeholder="Tìm theo Tên đăng nhập, Email..." 
                               value="<c:out value='${requestScope.searchKeyword}' />" />
                        <button type="submit"><i class="fas fa-search"></i></button>
                    </form>
                </div>
            </div>
            <%-- ✨ KẾT THÚC TOOLBAR MỚI ✨ --%>

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
                                <%-- ✨ SỬ DỤNG CLASS THAY CHO STYLE INLINE ✨ --%>
                                <c:if test="${tk.trangThai == 'HOAT_DONG'}">
                                    <span class="status status-completed">Hoạt động</span>
                                </c:if>
                                <c:if test="${tk.trangThai == 'BI_KHOA'}">
                                    <span class="status status-cancelled">Đã khóa</span>
                                </c:if>
                            </td>

                            <td class="actions">
                                <c:if test="${sessionScope.USER.id != tk.id}">
                                    <a href="${pageContext.request.contextPath}/MainController?action=showUserEditForm&id=${tk.id}" class="edit-btn" title="Sửa Trạng thái">
                                        <i class="fas fa-edit"></i>
                                    </a>

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
                            <td colspan="6" class="empty-cell">
                                <c:if test="${not empty requestScope.searchKeyword}">
                                    Không tìm thấy tài khoản nào với từ khóa "<c:out value='${requestScope.searchKeyword}' />".
                                </c:if>
                                <c:if test="${empty requestScope.searchKeyword}">
                                    Không có dữ liệu tài khoản.
                                </c:if>
                            </td>
                        </tr>
                    </c:if>
                </tbody>
            </table>

            <%-- === BẮT ĐẦU THÊM MỚI (PHÂN TRANG + TÌM KIẾM) === --%>
            <div class="pagination-container">
                <c:set var="currentPage" value="${requestScope.currentPage}" />
                <c:set var="totalPages" value="${requestScope.totalPages}" />
                <c:set var="searchKeyword" value="${requestScope.searchKeyword}" />

                <c:if test="${totalPages > 1}">
                    <%-- Nút Trang Trước --%>
                    <c:url var="prevPageUrl" value="MainController">
                        <c:param name="action" value="listUsers" />
                        <c:param name="page" value="${currentPage - 1}" />
                        <c:if test="${not empty searchKeyword}"><c:param name="keyword" value="${searchKeyword}" /></c:if>
                    </c:url>
                    <a href="${currentPage > 1 ? prevPageUrl : '#'}" 
                       class="pagination-btn ${currentPage > 1 ? '' : 'disabled'}">&laquo; Trang trước</a>

                    <span class="pagination-info">
                        Trang ${currentPage} / ${totalPages}
                    </span>

                    <%-- Nút Trang Sau --%>
                    <c:url var="nextPageUrl" value="MainController">
                        <c:param name="action" value="listUsers" />
                        <c:param name="page" value="${currentPage + 1}" />
                        <c:if test="${not empty searchKeyword}"><c:param name="keyword" value="${searchKeyword}" /></c:if>
                    </c:url>
                    <a href="${currentPage < totalPages ? nextPageUrl : '#'}" 
                       class="pagination-btn ${currentPage < totalPages ? '' : 'disabled'}">Trang sau &raquo;</a>
                </c:if>
            </div>
            <%-- === KẾT THÚC THÊM MỚI (PHÂN TRANG + TÌM KIẾM) === --%>

        </div> 

        <%-- Footer --%>
        <footer class="main-footer">
            <div class="container">
                <jsp:include page="/WEB-INF/footer.jsp" /> 
            </div>
        </footer>

        <%-- (Tôi đã xóa bớt 1 thẻ </div> thừa ở đây so với file gốc của bạn) --%>

        <%-- ✨ 3. THÊM THƯ VIỆN SWIPER.JS (Bắt buộc) --%>
        <script src="https://cdn.jsdelivr.net/npm/swiper@11/swiper-bundle.min.js"></script>

        <%-- ✨ 4. LINK TỚI FILE JS (Sẽ cập nhật ở Bước 3) --%>
        <script src="<c:url value='/js/index.js'/>"></script> 

    </body>
</html>