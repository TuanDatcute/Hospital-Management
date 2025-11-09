<%--
    Document    : danhSachKhoa.jsp
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
        <title>Quản lý Khoa</title>

        <link rel="preconnect" href="https://fonts.googleapis.com">
        <link rel="preconnect" href="https://fonts.gstatic.com" crossorigin>
        <link href="https://fonts.googleapis.com/css2?family=Poppins:wght@400;500;600;700&display=swap" rel="stylesheet">

        <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0/css/all.min.css">
        <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/swiper@11/swiper-bundle.min.css" />
        <link href="https://fonts.googleapis.com/css2?family=Be+Vietnam+Pro:wght@400;500;600;700&display=swap" rel="stylesheet">

        <%-- ✨ CSS Chung --%>
        <link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/css/index.css?v=1.5">

        <%-- ✨ SỬ DỤNG CHUNG CSS DANH SÁCH ✨ --%>
        <link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/css/admin-list.css?v=1.0">

        <%-- Khối <style> ... </style> đã được xóa --%>
    </head>
    <body>

        <jsp:include page="/WEB-INF/headerDat.jsp" /> 

        <div class="container page-content" style="padding-top: 30px;">

            <h2 class="section-title">Quản lý Khoa</h2>

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
                <a href="MainController?action=showKhoaCreateForm" class="add-new-btn">
                    <i class="fas fa-plus"></i> Thêm Khoa Mới
                </a>

                <div class="search-container">
                    <form action="MainController" method="GET">
                        <input type="hidden" name="action" value="listKhoa" />
                        <input type="text" name="keyword" 
                               placeholder="Tìm theo Tên Khoa, Mô Tả..." 
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
                        <th>Tên Khoa</th>
                        <th>Mô tả</th>
                        <th style="width: 100px;">Hành động</th>
                    </tr>
                </thead>
                <tbody>
                    <c:forEach var="khoa" items="${requestScope.LIST_KHOA}">
                        <tr>
                            <td>${khoa.id}</td>
                            <td><c:out value="${khoa.tenKhoa}" /></td>
                            <td><c:out value="${khoa.moTa}" /></td>
                            <td class="actions">
                                <a href="MainController?action=showKhoaEditForm&id=${khoa.id}" class="edit-btn" title="Sửa">
                                    <i class="fas fa-edit"></i>
                                </a>

                                <form action="MainController" method="POST" style="display: inline-block;"
                                      onsubmit="return confirm('Bạn có chắc chắn muốn VÔ HIỆU HÓA khoa này? \n(Khoa sẽ bị ẩn khỏi các danh sách chọn.)');">
                                    <input type="hidden" name="action" value="softDeleteKhoa" />
                                    <input type="hidden" name="id" value="${khoa.id}" />
                                    <button type="submit" class="delete-btn" title="Vô hiệu hóa">
                                        <i class="fas fa-archive"></i>
                                    </button>
                                </form>
                            </td>
                        </tr>
                    </c:forEach>

                    <c:if test="${empty requestScope.LIST_KHOA}">
                        <tr>
                            <td colspan="4" class="empty-cell">
                                <c:if test="${not empty requestScope.searchKeyword}">
                                    Không tìm thấy khoa nào với từ khóa "<c:out value='${requestScope.searchKeyword}' />".
                                </c:if>
                                <c:if test="${empty requestScope.searchKeyword}">
                                    Không có dữ liệu khoa.
                                </c:if>
                            </td>
                        </tr>
                    </c:if>
                </tbody>
            </table>

            <%-- === PHÂN TRANG (Đã sửa để giữ keyword) === --%>
            <div class="pagination-container">
                <c:set var="currentPage" value="${requestScope.currentPage}" />
                <c:set var="totalPages" value="${requestScope.totalPages}" />
                <c:set var="searchKeyword" value="${requestScope.searchKeyword}" />

                <c:if test="${totalPages > 1}">
                    <%-- Nút Trang Trước --%>
                    <c:url var="prevPageUrl" value="MainController">
                        <c:param name="action" value="listKhoa" />
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
                        <c:param name="action" value="listKhoa" />
                        <c:param name="page" value="${currentPage + 1}" />
                        <c:if test="${not empty searchKeyword}"><c:param name="keyword" value="${searchKeyword}" /></c:if>
                    </c:url>
                    <a href="${currentPage < totalPages ? nextPageUrl : '#'}" 
                       class="pagination-btn ${currentPage < totalPages ? '' : 'disabled'}">Trang sau &raquo;</a>
                </c:if>
            </div>
            <%-- === KẾT THÚC PHÂN TRANG === --%>

        </div> <%-- Kết thúc .container.page-content --%>

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