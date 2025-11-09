<%--
    Document    : danhSachLichHen.jsp
    Created on  : Oct 29, 2025
    Author      : ADMIN
    (ĐÃ NÂNG CẤP: Giao diện V2.1 + Tách file admin-list.css)
--%>

<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>

<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>Quản lý Lịch hẹn</title>

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

            <h2 class="section-title">Quản lý Lịch hẹn</h2>

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

            <%-- ✨ BẮT ĐẦU TOOLBAR MỚI ✨ --%>
            <div class="toolbar">
                <%-- Trang này thường không có nút "Thêm mới" từ phía Admin --%>
                <%-- Bọc thanh tìm kiếm vào div và đẩy sang phải --%>
                <div class="search-container" style="margin-left: auto;">
                    <form action="MainController" method="GET">
                        <input type="hidden" name="action" value="listLichHen" />
                        <input type="text" name="keyword" 
                               placeholder="Tìm theo Tên BN, Bác sĩ, Trạng thái..." 
                               value="<c:out value='${requestScope.searchKeyword}' />" />
                        <button type="submit"><i class="fas fa-search"></i></button>
                    </form>
                </div>
            </div>
            <%-- ✨ KẾT THÚC TOOLBAR MỚI ✨ --%>

            <table class="data-table">
                <thead>
                    <tr>
                        <th>STT</th>
                        <th>Thời gian hẹn</th>
                        <th>Bệnh nhân</th>
                        <th>Bác sĩ</th>
                        <th>Trạng thái</th>
                    </tr>
                </thead>
                <tbody>
                    <c:forEach var="lh" items="${requestScope.LIST_LICHHEN}">
                        <tr>
                            <td>${lh.stt}</td>
                            <td>
                                <fmt:parseDate value="${lh.thoiGianHen}" pattern="yyyy-MM-dd'T'HH:mmXXX" var="parsedDateTime" type="both" />
                                <fmt:formatDate value="${parsedDateTime}" pattern="dd/MM/yyyy HH:mm" />
                            </td>
                            <td><c:out value="${lh.tenBenhNhan}" /></td>
                            <td><c:out value="${lh.tenBacSi}" /></td>
                            <td>
                                <%-- ✨ SỬ DỤNG CLASS THAY CHO STYLE INLINE ✨ --%>
                                <c:choose>
                                    <c:when test="${lh.trangThai == 'HOAN_THANH'}"><span class="status status-completed">Hoàn thành</span></c:when>
                                    <c:when test="${lh.trangThai == 'DA_XAC_NHAN'}"><span class="status status-confirmed">Đã xác nhận</span></c:when>
                                    <c:when test="${lh.trangThai == 'DA_HUY'}"><span class="status status-cancelled">Đã hủy</span></c:when>
                                    <c:when test="${lh.trangThai == 'CHO_XAC_NHAN'}"><span class="status status-pending">Chờ xác nhận</span></c:when>
                                    <c:otherwise><c:out value="${lh.trangThai}"/></c:otherwise>
                                </c:choose>
                            </td>
                        </tr>
                    </c:forEach>

                    <c:if test="${empty requestScope.LIST_LICHHEN}">
                        <tr>
                            <td colspan="5" class="empty-cell">
                                <c:if test="${not empty requestScope.searchKeyword}">
                                    Không tìm thấy lịch hẹn nào với từ khóa "<c:out value='${requestScope.searchKeyword}' />".
                                </c:if>
                                <c:if test="${empty requestScope.searchKeyword}">
                                    Không có dữ liệu lịch hẹn.
                                </c:if>
                            </td>
                        </tr>
                    </c:if>
                </tbody>
            </table>

            <%-- ✨ NÂNG CẤP PHÂN TRANG (Giống hệt 2 file kia) ✨ --%>
            <div class="pagination-container">
                <c:set var="currentPage" value="${requestScope.currentPage}" />
                <c:set var="totalPages" value="${requestScope.totalPages}" />
                <c:set var="searchKeyword" value="${requestScope.searchKeyword}" />

                <c:if test="${totalPages > 1}">
                    <%-- Nút Trang Trước --%>
                    <c:url var="prevPageUrl" value="MainController">
                        <c:param name="action" value="listLichHen" />
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
                        <c:param name="action" value="listLichHen" />
                        <c:param name="page" value="${currentPage + 1}" />
                        <c:if test="${not empty searchKeyword}"><c:param name="keyword" value="${searchKeyword}" /></c:if>
                    </c:url>
                    <a href="${currentPage < totalPages ? nextPageUrl : '#'}" 
                       class="pagination-btn ${currentPage < totalPages ? '' : 'disabled'}">Trang sau &raquo;</a>
                </c:if>
            </div>

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