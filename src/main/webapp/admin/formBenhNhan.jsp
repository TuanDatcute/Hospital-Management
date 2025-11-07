<%--
    Document    : formBenhNhan.jsp
    Created on  : Oct 29, 2025
    Author      : ADMIN
    (ĐÃ NÂNG CẤP: Mã Bệnh nhân tự động & an toàn, Thêm CCCD, Sửa lỗi Ngày sinh)
--%>

<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">

        <%-- Đặt tiêu đề động --%>
        <c:set var="isCreating" value="${requestScope.formAction == 'createBenhNhan'}" />
        <title>${isCreating ? 'Thêm Bệnh nhân Mới' : 'Cập nhật Bệnh nhân'}</title>

        <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0/css/all.min.css">
        <link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/css/style.css">
    </head>
    <body>

        <jsp:include page="/WEB-INF/header.jsp" /> 

        <div class="container page-content" style="padding-top: 30px;">

            <h2 class="section-title">
                <c:if test="${isCreating}">Thêm Bệnh nhân Mới</c:if>
                <c:if test="${!isCreating}">Cập nhật Thông tin Bệnh nhân</c:if>
                </h2>

            <%-- Hiển thị lỗi (ví dụ: validation thất bại) --%>
            <c:if test="${not empty requestScope.ERROR_MESSAGE}">
                <p class="error-message">${requestScope.ERROR_MESSAGE}</p>
            </c:if>
            <c:if test="${not empty requestScope.LOAD_FORM_ERROR}">
                <p class="error-message">${requestScope.LOAD_FORM_ERROR}</p>
            </c:if>

            <form action="MainController" method="post" class="data-form">

                <input type="hidden" name="action" value="${requestScope.formAction}" />

                <c:if test="${!isCreating}">
                    <input type="hidden" name="id" value="${requestScope.BENHNHAN_DATA.id}" />
                </c:if>

                <%-- === BẮT ĐẦU SỬA (HIỂN THỊ MÃ DỰ KIẾN) === --%>
                <div class="form-group">
                    <label for="maBenhNhan">Mã Bệnh nhân:</label>

                    <%-- Nếu là "Sửa" (Update), hiển thị mã và khóa lại --%>
                    <c:if test="${!isCreating}">
                        <input type="text" id="maBenhNhan" name="maBenhNhan" 
                               value="<c:out value="${requestScope.BENHNHAN_DATA.maBenhNhan}"/>" readonly class="disabled-input">
                    </c:if>

                    <%-- Nếu là "Tạo mới" (Create), hiển thị mã dự kiến nhưng VÔ HIỆU HÓA nó --%>
                    <c:if test="${isCreating}">
                        <input type="text" id="maBenhNhan" name="maBenhNhan"
                               value="<c:out value="${requestScope.BENHNHAN_DATA.maBenhNhan}"/>" 
                               placeholder="<Tự động tạo...>" disabled class="disabled-input">

                        <%-- Ghi chú cho Admin biết đây là mã dự kiến --%>
                        <small>Mã dự kiến: <c:out value="${requestScope.BENHNHAN_DATA.maBenhNhan}" /> (Sẽ được gán tự động khi lưu).</small>
                    </c:if>
                </div>
                <%-- === KẾT THÚC SỬA === --%>

                <div class="form-group">
                    <label for="hoTen">Họ tên Bệnh nhân:</label>
                    <input type="text" id="hoTen" name="hoTen" value="<c:out value="${requestScope.BENHNHAN_DATA.hoTen}"/>" required="required">
                </div>

                <div class="form-group">
                    <label for="cccd">Số CCCD (Bắt buộc):</label>
                    <input type="text" id="cccd" name="cccd" 
                           value="<c:out value='${requestScope.BENHNHAN_DATA.cccd}'/>" 
                           required="required"
                           placeholder="Nhập 9 hoặc 12 số CCCD"
                           ${!isCreating ? 'readonly' : ''}>
                    <c:if test="${isCreating}">
                        <small>Đây sẽ là khóa để bệnh nhân liên kết tài khoản của họ sau này.</small>
                    </c:if>
                </div>

                <%-- Khối div "Tài khoản liên kết" đã được xóa --%>

                <div class="form-group">
                    <label for="soDienThoai">Số điện thoại:</label>
                    <input type="text" id="soDienThoai" name="soDienThoai" value="<c:out value="${requestScope.BENHNHAN_DATA.soDienThoai}"/>" required="required">
                </div>

                <div class="form-group">
                    <label for="ngaySinh">Ngày sinh:</label>
                    <input type="date" id="ngaySinh" name="ngaySinh" value="${requestScope.BENHNHAN_DATA.ngaySinh}" required="required">
                </div>

                <div class="form-group">
                    <label for="gioiTinh">Giới tính:</label>
                    <select id="gioiTinh" name="gioiTinh" required="required">
                        <option value="">-- Chọn giới tính --</option>
                        <option value="Nam" ${requestScope.BENHNHAN_DATA.gioiTinh == 'Nam' ? 'selected' : ''}>Nam</option>
                        <option value="Nữ" ${requestScope.BENHNHAN_DATA.gioiTinh == 'Nữ' ? 'selected' : ''}>Nữ</option>
                        <option value="Khác" ${requestScope.BENHNHAN_DATA.gioiTinh == 'Khác' ? 'selected' : ''}>Khác</option>
                    </select>
                </div>

                <div class="form-group">
                    <label for="diaChi">Địa chỉ:</label>
                    <input type="text" id="diaChi" name="diaChi" value="<c:out value="${requestScope.BENHNHAN_DATA.diaChi}"/>" required="required">
                </div>

                <div class="form-group">
                    <label for="nhomMau">Nhóm máu (Tùy chọn):</label>
                    <input type="text" id="nhomMau" name="nhomMau" value="<c:out value="${requestScope.BENHNHAN_DATA.nhomMau}"/>" placeholder="Ví dụ: O+ hoặc AB-">
                </div>

                <div class="form-group">
                    <label for="tienSuBenh">Tiền sử bệnh (Tùy chọn):</label>
                    <textarea id="tienSuBenh" name="tienSuBenh" rows="4"><c:out value="${requestScope.BENHNHAN_DATA.tienSuBenh}"/></textarea>
                </div>

                <div class="form-actions">
                    <button type="submit" class="btn-submit">
                        <i class="fas fa-save"></i> ${isCreating ? 'Tạo mới' : 'Cập nhật'}
                    </button>
                    <a href="MainController?action=listBenhNhan" class="btn-cancel">Hủy</a>
                </div>
            </form>

        </div>

        <jsp:include page="/WEB-INF/footer.jsp" /> 

    </body>
</html>