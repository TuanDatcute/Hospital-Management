<%--
    Document   : formKhoa.jsp
    Created on : Oct 29, 2025
    Author     : ADMIN
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
        <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0/css/all.min.css">
        <link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/css/style.css">

        <%-- 
            LƯU Ý: 
            Trang này sử dụng các class từ style.css:
            .data-form, .form-group, .btn-submit, .btn-cancel
        --%>
    </head>
    <body>

        <jsp:include page="/WEB-INF/header.jsp" /> 

        <div class="container page-content" style="padding-top: 30px;">

            <h2 class="section-title">
                <c:if test="${isCreating}">Thêm Khoa Mới</c:if>
                <c:if test="${!isCreating}">Cập nhật Khoa</c:if>
                </h2>

            <%-- Hiển thị lỗi (ví dụ: Tên khoa bị trùng) --%>
            <c:if test="${not empty requestScope.ERROR_MESSAGE}">
                <p class="error-message">${requestScope.ERROR_MESSAGE}</p>
            </c:if>

            <%-- 
                Form này sẽ gửi đến MainController.
                Action sẽ là 'createKhoa' (nếu tạo mới) hoặc 'updateKhoa' (nếu cập nhật).
                Biến 'formAction' được KhoaController set ở doGet.
            --%>
            <form action="MainController" method="post" class="data-form">

                <%-- Truyền action (createKhoa/updateKhoa) --%>
                <input type="hidden" name="action" value="${requestScope.formAction}" />

                <%-- Truyền ID (chỉ khi cập nhật) --%>
                <c:if test="${!isCreating}">
                    <input type="hidden" name="id" value="${requestScope.KHOA_DATA.id}" />
                </c:if>

                <div class="form-group">
                    <label for="tenKhoa">Tên Khoa:</label>
                    <%-- 
                        Dùng <c:out> để điền lại dữ liệu cũ 
                        (khi sửa, hoặc khi tạo mới bị lỗi validation)
                    --%>
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

        <jsp:include page="/WEB-INF/footer.jsp" /> 

    </body>
</html>