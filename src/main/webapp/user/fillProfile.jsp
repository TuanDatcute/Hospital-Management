<%--
    Document   : fillProfile.jsp
    (Đã cập nhật để gửi ID bệnh nhân nếu có)
--%>

<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<!DOCTYPE html>
<html lang="vi">
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>Hoàn tất Hồ sơ Cá nhân</title>

        <%-- Nhúng CSS/Font chung --%>
        <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0/css/all.min.css">
        <link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/css/style.css">
    </head>
    <body class="login-page-body"> 

        <div class="standalone-form-container" style="max-width: 600px;">

            <h1 class="form-title">Hoàn tất Hồ sơ</h1>
            <p class="form-description">
                Chào mừng bạn! Vui lòng cập nhật thông tin cá nhân bắt buộc để tiếp tục.
            </p>

            <%-- Hiển thị lỗi validation --%>
            <c:if test="${not empty requestScope.ERROR_MESSAGE}">
                <p class="error-message">${requestScope.ERROR_MESSAGE}</p>
            </c:if>

            <form action="${pageContext.request.contextPath}/MainController" method="post" class="data-form">

                <%-- Action đã sửa từ bước trước --%>
                <input type="hidden" name="action" value="saveProfile" />

                <c:if test="${not empty requestScope.BENHNHAN_DATA.id}">
                    <input type="hidden" name="id" value="${requestScope.BENHNHAN_DATA.id}" />
                </c:if>
                <div class="form-group">
                    <label for="hoTen">Họ và Tên (*):</label>
                    <input type="text" id="hoTen" name="hoTen" value="<c:out value='${requestScope.BENHNHAN_DATA.hoTen}'/>" required="required">
                </div>

                <div class="form-group">
                    <label for="cccd">Số CCCD/CMND (*):</label>
                    <input type="text" id="cccd" name="cccd" value="<c:out value='${requestScope.BENHNHAN_DATA.cccd}'/>" required="required">
                </div>

                <div class="form-group">
                    <label for="ngaySinh">Ngày sinh (*):</label>
                    <input type="date" id="ngaySinh" name="ngaySinh" value="${requestScope.BENHNHAN_DATA.ngaySinh}" required="required">
                </div>

                <div class="form-group">
                    <label for="gioiTinh">Giới tính (*):</label>
                    <select id="gioiTinh" name="gioiTinh" required="required">
                        <option value="">-- Chọn giới tính --</option>
                        <option value="Nam" ${requestScope.BENHNHAN_DATA.gioiTinh == 'Nam' ? 'selected' : ''}>Nam</option>
                        <option value="Nữ" ${requestScope.BENHNHAN_DATA.gioiTinh == 'Nữ' ? 'selected' : ''}>Nữ</option>
                        <option value="Khác" ${requestScope.BENHNHAN_DATA.gioiTinh == 'Khác' ? 'selected' : ''}>Khác</option>
                    </select>
                </div>

                <div class="form-group">
                    <label for="soDienThoai">Số điện thoại (*):</label>
                    <input type="text" id="soDienThoai" name="soDienThoai" value="<c:out value='${requestScope.BENHNHAN_DATA.soDienThoai}'/>" required="required">
                </div>

                <div class="form-group">
                    <label for="diaChi">Địa chỉ (*):</label>
                    <input type="text" id="diaChi" name="diaChi" value="<c:out value='${requestScope.BENHNHAN_DATA.diaChi}'/>" required="required">
                </div>

                <hr class="form-divider">

                <div class="form-group">
                    <label for="nhomMau">Nhóm máu (Tùy chọn):</label>
                    <input type="text" id="nhomMau" name="nhomMau" value="<c:out value='${requestScope.BENHNHAN_DATA.nhomMau}'/>" placeholder="Ví dụ: A+">
                </div>

                <div class="form-group">
                    <label for="tienSuBenh">Tiền sử bệnh (Tùy chọn):</label>
                    <textarea id="tienSuBenh" name="tienSuBenh" rows="3"><c:out value="${requestScope.BENHNHAN_DATA.tienSuBenh}"/></textarea>
                </div>

                <div class="form-actions" style="justify-content: center;">
                    <button type="submit" class="btn-submit" style="width: 100%;">
                        <i class="fas fa-save"></i> Lưu và Tiếp tục
                    </button>
                </div>
            </form>

        </div>
    </body>
</html>