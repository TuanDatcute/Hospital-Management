<%--
    Document    : danhSachBenhNhan.jsp
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
        <title>Quản lý Bệnh nhân</title>

        <link rel="preconnect" href="https://fonts.googleapis.com">
        <link rel="preconnect" href="https://fonts.gstatic.com" crossorigin>
        <link href="https://fonts.googleapis.com/css2?family=Poppins:wght@400;500;600;700&display=swap" rel="stylesheet">

        <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0/css/all.min.css">
        <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/swiper@11/swiper-bundle.min.css" />
        <link href="https://fonts.googleapis.com/css2?family=Be+Vietnam+Pro:wght@400;500;600;700&display=swap" rel="stylesheet">

        <%-- CSS Chung --%>
        <link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/css/index.css?v=1.5">

        <%-- ✨ CSS MỚI CHO TRANG DANH SÁCH ✨ --%>
        <link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/css/admin-list.css?v=1.0">

        <%-- Khối <style> ... </style> đã được xóa và chuyển vào admin-list.css --%>
    </head>
    <body>

        <jsp:include page="/WEB-INF/headerDat.jsp" />

        <div class="container page-content" style="padding-top: 30px;">

            <h2 class="section-title">Quản lý Bệnh nhân</h2>

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
                <a href="MainController?action=showBenhNhanCreateForm" class="add-new-btn">
                    <i class="fas fa-user-plus"></i> Thêm Bệnh nhân Mới
                </a>

                <div class="search-container">
                    <form action="MainController" method="GET">
                        <input type="hidden" name="action" value="listBenhNhan" />
                        <input type="text" name="keyword" 
                               placeholder="Tìm theo Mã BN, Tên, SĐT, CCCD..." 
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
                        <th>Mã Bệnh nhân</th>
                        <th>Họ Tên</th>
                        <th>Giới tính</th>
                        <th>Số điện thoại</th>
                        <th>Tài khoản?</th>
                        <th style="width: 100px;">Hành động</th>
                    </tr>
                </thead>
                <tbody>
                    <c:forEach var="bn" items="${requestScope.LIST_BENHNHAN}">
                        <tr>
                            <td>${bn.id}</td>
                            <td><c:out value="${bn.maBenhNhan}" /></td>
                            <td><c:out value="${bn.hoTen}" /></td>
                            <td><c:out value="${bn.gioiTinh}" /></td>
                            <td><c:out value="${bn.soDienThoai}" /></td>
                            <td>
                                <c:if test="${not empty bn.taiKhoanId}"><span style="color: green;">Đã liên kết</span> (ID: ${bn.taiKhoanId})</c:if>
                                <c:if test="${empty bn.taiKhoanId}"><span style="color: #888;">Chưa có</span></c:if>
                                </td>
                                <td class="actions">
                                    <a href="MainController?action=showBenhNhanEditForm&id=${bn.id}" class="edit-btn" title="Sửa thông tin">
                                    <i class="fas fa-edit"></i>
                                </a>

                                <%-- === SỬA LỖI (XÓA MỀM & BẢO MẬT) === --%>
                                <c:if test="${empty bn.taiKhoanId || sessionScope.USER.id != bn.taiKhoanId}">
                                    <form action="MainController" method="POST" style="display: inline-block;"
                                          onsubmit="return confirm('Bạn có chắc chắn muốn VÔ HIỆU HÓA bệnh nhân này? \nTài khoản liên kết (nếu có) cũng sẽ bị khóa.');">
                                        <input type="hidden" name="action" value="softDeleteBenhNhan" />
                                        <input type="hidden" name="id" value="${bn.id}" />
                                        <button type="submit" class="delete-btn" title="Vô hiệu hóa bệnh nhân">
                                            <i class="fas fa-user-lock"></i>
                                        </button>
                                    </form>
                                </c:if>
                                <c:if test="${not empty bn.taiKhoanId && sessionScope.USER.id == bn.taiKhoanId}">
                                    (Đây là bạn)
                                </c:if>
                            </td>
                        </tr>
                    </c:forEach>

                    <c:if test="${empty requestScope.LIST_BENHNHAN}">
                        <tr>
                            <td colspan="7" class="empty-cell">
                                <%-- === SỬA (THÊM THÔNG BÁO TÌM KIẾM) === --%>
                                <c:if test="${not empty requestScope.searchKeyword}">
                                    Không tìm thấy bệnh nhân nào với từ khóa "<c:out value='${requestScope.searchKeyword}' />".
                                </c:if>
                                <c:if test="${empty requestScope.searchKeyword}">
                                    Không có dữ liệu bệnh nhân.
                                </c:if>
                            </td>
                        </tr>
                    </c:if>
                </tbody>
            </table>

            <%-- === BẮT ĐẦU SỬA (PHÂN TRANG + TÌM KIẾM) === --%>
            <div class="pagination-container">
                <c:set var="currentPage" value="${requestScope.currentPage}" />
                <c:set var="totalPages" value="${requestScope.totalPages}" />
                <c:set var="searchKeyword" value="${requestScope.searchKeyword}" />

                <c:if test="${totalPages > 1}">
                    <%-- Nút Trang Trước --%>
                    <c:url var="prevPageUrl" value="MainController">
                        <c:param name="action" value="listBenhNhan" />
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
                        <c:param name="action" value="listBenhNhan" />
                        <c:param name="page" value="${currentPage + 1}" />
                        <c:if test="${not empty searchKeyword}"><c:param name="keyword" value="${searchKeyword}" /></c:if>
                    </c:url>
                    <a href="${currentPage < totalPages ? nextPageUrl : '#'}" 
                       class="pagination-btn ${currentPage < totalPages ? '' : 'disabled'}">Trang sau &raquo;</a>
                </c:if>
            </div>
            <%-- === KẾT THÚC THÊM MỚI (PHÂN TRANG) === --%>

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