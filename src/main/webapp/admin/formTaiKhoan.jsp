<%--
    Document    : formTaiKhoan.jsp (Đã đơn giản hóa cho Admin)
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

        <c:set var="isCreating" value="${requestScope.formAction == 'createUser'}" />
        <title>${isCreating ? 'Thêm Tài khoản' : 'Cập nhật Tài khoản'}</title>

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
                <c:if test="${isCreating}">Thêm Tài khoản Nhân viên</c:if>
                <c:if test="${!isCreating}">Cập nhật Trạng thái Tài khoản</c:if>
                </h2>

            <c:if test="${not empty requestScope.ERROR_MESSAGE}">
                <p class="error-message">${requestScope.ERROR_MESSAGE}</p>
            </c:if>

            <form action="MainController" method="post" class="data-form">

                <%-- **MERGE:** Lấy các trường 'hidden' (Rất quan trọng) --%>
                <input type="hidden" name="action" value="${requestScope.formAction}" />
                <c:if test="${!isCreating}">
                    <input type="hidden" name="id" value="${requestScope.USER_DATA.id}" />
                </c:if>
                <%-- **KẾT THÚC MERGE** --%>

                <div class="form-group">
                    <label for="tenDangNhap">Tên đăng nhập:</label>
                    <input type="text" id="tenDangNhap" name="tenDangNhap" 
                           value="<c:out value="${requestScope.USER_DATA.tenDangNhap}"/>" 
                           ${!isCreating ? 'readonly class="disabled-input"' : ''} 
                           required="required">
                </div>

                <%-- **MERGE:** Giữ lại ô Email (Tùy chọn) --%>
                <div class="form-group">
                    <label for="email">Email (Tùy chọn):</label>
                    <input type="email" id="email" name="email" 
                           value="<c:out value="${requestScope.USER_DATA.email}"/>" 
                           ${!isCreating ? 'readonly class="disabled-input"' : ''}>
                </div>

                <%-- Chỉ hiển thị ô nhập mật khẩu KHI TẠO MỚI --%>
                <c:if test="${isCreating}">
                    <div class="form-group">
                        <label for="password">Mật khẩu tạm thời:</label>
                        <input type="password" id="password" name="password" required="required">
                    </div>
                </c:if>

                <div class="form-group">
                    <label for="vaiTro">Vai trò:</label>
                    <%-- **MERGE:** Giữ logic 'disabled' khi CẬP NHẬT --%>
                    <select id="vaiTro" name="vaiTro" ${!isCreating ? 'disabled' : ''} class="${!isCreating ? 'disabled-input' : ''}">
                        <%-- Giới hạn lại vai trò --%>
                        <option value="BAC_SI" ${requestScope.USER_DATA.vaiTro == 'BAC_SI' ? 'selected' : ''}>Bác sĩ</option>
                        <option value="LE_TAN" ${requestScope.USER_DATA.vaiTro == 'LE_TAN' ? 'selected' : ''}>Lễ tân</option>
                    </select>
                </div>

                <%-- Chỉ hiển thị ô Trạng thái KHI CẬP NHẬT --%>
                <c:if test="${!isCreating}">
                    <div class="form-group">
                        <label for="trangThai">Trạng thái:</label>
                        <select id="trangThai" name="trangThai">
                            <option value="HOAT_DONG" ${requestScope.USER_DATA.trangThai == 'HOAT_DONG' ? 'selected' : ''}>Hoạt động</option>
                            <option value="BI_KHOA" ${requestScope.USER_DATA.trangThai == 'BI_KHOA' ? 'selected' : ''}>Bị khóa</option>
                        </select>
                    </div>
                </c:if>


                <div class="form-actions">
                    <button type="submit" class="btn-submit">
                        <i class="fas fa-save"></i> ${isCreating ? 'Tạo mới' : 'Cập nhật'}
                    </button>
                    <a href="MainController?action=listUsers" class="btn-cancel">Hủy</a>
                </div>
            </form>

        </div>

        <%-- Footer --%>
        <footer class="main-footer">
            <div class="container">
                <jsp:include page="/WEB-INF/footer.jsp" /> 
            </div>
        </footer>

        <%-- ✨ 3. THÊM THƯ VIỆN SWIPER.JS (Bắt buộc) --%>
        <script src="https://cdn.jsdelivr.net/npm/swiper@11/swiper-bundle.min.js"></script>

        <%-- ✨ 4. LINK TỚI FILE JS (Sẽ cập nhật ở Bước 3) --%>
        <script src="<c:url value='/js/index.js'/>"></script>

    </body>
</html>