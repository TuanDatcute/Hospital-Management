<%--
    Document    : formKhoa.jsp
    Created on  : Oct 29, 2025
    Author      : ADMIN
    (ĐÃ NÂNG CẤP: Giao diện V2.1 - Tách file admin-form.css)
--%>

<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">

        <%-- Đặt tiêu đề động: Kiểm tra xem 'khoa' có ID chưa --%>
        <c:set var="isCreating" value="${empty requestScope.KHOA_DATA.id}" />
        <title>${isCreating ? 'Thêm Khoa Mới' : 'Cập nhật Khoa'}</title>

        <%-- Nhúng CSS/Font chung --%>
        <link rel="preconnect" href="https://fonts.googleapis.com">
        <link rel="preconnect" href="https://fonts.gstatic.com" crossorigin>
        <link href="https://fonts.googleapis.com/css2?family=Poppins:wght@400;500;600;700&display=swap" rel="stylesheet">

        <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0/css/all.min.css">
        <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/swiper@11/swiper-bundle.min.css" />
        <link href="https://fonts.googleapis.com/css2?family=Be+Vietnam+Pro:wght@400;500;600;700&display=swap" rel="stylesheet">

        <%-- CSS Chung --%>
        <link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/css/index.css?v=1.5">

        <%-- ✨ LIÊN KẾT TỚI FILE CSS CHO FORM MỚI ✨ --%>
        <link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/css/admin-form.css?v=1.0">
    </head>
    <body>

        <jsp:include page="/WEB-INF/headerDat.jsp" /> 

        <div class="container page-content" style="padding-top: 30px;">

            <h2 class="section-title">
                <c:if test="${isCreating}">Thêm Khoa Mới</c:if>
                <c:if test="${!isCreating}">Cập nhật Khoa</c:if>
                </h2>

            <%-- Hiển thị lỗi (ví dụ: Tên khoa bị trùng) --%>
            <c:if test="${not empty requestScope.ERROR_MESSAGE}">
                <p class="error-message">${requestScope.ERROR_MESSAGE}</p>
            </c:if>

            <form action="MainController" method="post" class="data-form">

                <%-- Truyền action (createKhoa/updateKhoa) --%>
                <input type="hidden" name="action" value="${requestScope.formAction}" />

                <%-- Truyền ID (chỉ khi cập nhật) --%>
                <c:if test="${!isCreating}">
                    <input type="hidden" name="id" value="${requestScope.KHOA_DATA.id}" />
                </c:if>

                <div class="form-group">
                    <label for="tenKhoa">Tên Khoa:</label>
                    <input type="text" id="tenKhoa" name="tenKhoa" value="<c:out value="${requestScope.KHOA_DATA.tenKhoa}"/>" required="required">
                </div>

                <div class="form-group">
                    <label for="moTa">Mô tả:</label>
                    <textarea id="moTa" name="moTa" rows="4"><c:out value="${requestScope.KHOA_DATA.moTa}"/></textarea>
                </div>

                <div class="form-actions">
                    <button type="submit" class="btn-submit">
                        <i class="fas fa-save"></i> ${isCreating ? 'Lưu' : 'Cập nhật'}
                    </button>
                    <a href="MainController?action=listKhoa" class="btn-cancel">Hủy</a>
                </div>
            </form>

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