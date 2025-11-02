<%--
    Document   : formTaiKhoan.jsp (Đã đơn giản hóa cho Admin)
    Created on : Oct 29, 2025
    Author     : ADMIN
--%>

<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<!DOCTYPE html>
<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    
    <c:set var="isCreating" value="${requestScope.formAction == 'createUser'}" />
    <title>${isCreating ? 'Thêm Tài khoản' : 'Cập nhật Tài khoản'}</title>
    
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0/css/all.min.css">
    <link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/css/style.css">
</head>
<body>

    <jsp:include page="/WEB-INF/header.jsp" /> 

    <div class="container page-content" style="padding-top: 30px;">
        
        <h2 class="section-title">
            <c:if test="${isCreating}">Thêm Tài khoản Nhân viên</c:if>
            <c:if test="${!isCreating}">Cập nhật Trạng thái Tài khoản</c:if>
        </h2>

        <c:if test="${not empty requestScope.ERROR_MESSAGE}">
            <p class="error-message">${requestScope.ERROR_MESSAGE}</p>
        </c:if>

        <form action="MainController" method="post" class="data-form">
            
            <input type="hidden" name="action" value="${requestScope.formAction}" />
            
            <c:if test="${!isCreating}">
                <input type="hidden" name="id" value="${requestScope.USER_DATA.id}" />
            </c:if>

            <div class="form-group">
                <label for="tenDangNhap">Tên đăng nhập:</label>
                <input type="text" id="tenDangNhap" name="tenDangNhap" 
                       value="<c:out value="${requestScope.USER_DATA.tenDangNhap}"/>" 
                       ${!isCreating ? 'readonly' : ''} 
                       required="required">
            </div>

            <%-- **ĐÃ XÓA Ô EMAIL** --%>
            <%-- (Bạn có thể thêm lại nếu muốn, nhưng bỏ 'required="required"') --%>
            <%--
            <div class="form-group">
                <label for="email">Email (Tùy chọn):</label>
                <input type="email" id="email" name="email" 
                       value="<c:out value="${requestScope.USER_DATA.email}"/>" 
                       ${!isCreating ? 'readonly' : ''}>
            </div>
            --%>

            <%-- Chỉ hiển thị ô nhập mật khẩu KHI TẠO MỚI --%>
            <c:if test="${isCreating}">
                <div class="form-group">
                    <label for="password">Mật khẩu tạm thời:</label>
                    <input type="password" id="password" name="password" required="required">
                </div>
                <%-- **ĐÃ XÓA Ô XÁC NHẬN MẬT KHẨU** --%>
            </c:if>

            <div class="form-group">
                <label for="vaiTro">Vai trò:</label>
                <select id="vaiTro" name="vaiTro" ${!isCreating ? 'disabled' : ''}>
                    <%-- **ĐÃ GIỚI HẠN LẠI VAI TRÒ** --%>
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

    <jsp:include page="/WEB-INF/footer.jsp" /> 

</body>
</html>