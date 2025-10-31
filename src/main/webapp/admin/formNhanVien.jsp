<%--
    Document   : formNhanVien.jsp
    Created on : Oct 29, 2025
    Author     : ADMIN
--%>

<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">

        <%-- Đặt tiêu đề động --%>
        <c:set var="isCreating" value="${requestScope.formAction == 'createNhanVien'}" />
        <title>${isCreating ? 'Thêm Nhân viên' : 'Cập nhật Nhân viên'}</title>

        <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0/css/all.min.css">
        <link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/css/style.css">

        <%-- Trang này sử dụng các class: .data-form, .form-group, .btn-submit, .btn-cancel --%>
    </head>
    <body>

        <jsp:include page="/WEB-INF/header.jsp" /> 

        <div class="container page-content" style="padding-top: 30px;">

            <h2 class="section-title">
                <c:if test="${isCreating}">Thêm Nhân viên Mới</c:if>
                <c:if test="${!isCreating}">Cập nhật Thông tin Nhân viên</c:if>
                </h2>

            <%-- Hiển thị lỗi (ví dụ: validation thất bại) --%>
            <c:if test="${not empty requestScope.ERROR_MESSAGE}">
                <p class="error-message">${requestScope.ERROR_MESSAGE}</p>
            </c:if>
            <%-- Hiển thị lỗi tải dữ liệu (nếu không tải được Khoa/Tài khoản) --%>
            <c:if test="${not empty requestScope.LOAD_FORM_ERROR}">
                <p class="error-message">${requestScope.LOAD_FORM_ERROR}</p>
            </c:if>

            <%-- 
                Form này gửi đến MainController.
                Action sẽ là 'createNhanVien' hoặc 'updateNhanVien' 
                (được set bởi NhanVienController trong biến requestScope.formAction)
            --%>
            <form action="MainController" method="post" class="data-form">

                <input type="hidden" name="action" value="${requestScope.formAction}" />

                <%-- Truyền ID (chỉ khi cập nhật) --%>
                <c:if test="${!isCreating}">
                    <input type="hidden" name="id" value="${requestScope.NHANVIEN_DATA.id}" />
                </c:if>

                <div class="form-group">
                    <label for="hoTen">Họ tên Nhân viên:</label>
                    <input type="text" id="hoTen" name="hoTen" value="<c:out value="${requestScope.NHANVIEN_DATA.hoTen}"/>" required="required">
                </div>

                <%-- Tài khoản (Chỉ chọn được khi TẠO MỚI) --%>
                <div class="form-group">
                    <label for="taiKhoanId">Tài khoản liên kết:</label>
                    <c:choose>
                        <%-- Khi TẠO MỚI: Hiển thị dropdown --%>
                        <c:when test="${isCreating}">
                            <select id="taiKhoanId" name="taiKhoanId" required="required">
                                <option value="">-- Chọn tài khoản chưa gán --</option>
                                <c:forEach var="tk" items="${requestScope.LIST_TAIKHOAN}">
                                    <option value="${tk.id}" ${requestScope.NHANVIEN_DATA.taiKhoanId == tk.id ? 'selected' : ''}>
                                        ${tk.tenDangNhap} (ID: ${tk.id}, Email: ${tk.email})
                                    </option>
                                </c:forEach>
                            </select>
                            <c:if test="${empty requestScope.LIST_TAIKHOAN}">
                                <p style="color: red;">Không tìm thấy tài khoản hoạt động nào chưa được gán.</p>
                            </c:if>
                        </c:when>
                        <%-- Khi CẬP NHẬT: Hiển thị dạng text (không cho sửa) --%>
                        <c:otherwise>
                            <input type="text" value="ID: ${requestScope.NHANVIEN_DATA.taiKhoanId} (Không thể thay đổi)" readonly="readonly" class="disabled-input">
                        </c:otherwise>
                    </c:choose>
                </div>

                <div class="form-group">
                    <label for="chuyenMon">Chuyên môn (Vai trò):</label>
                    <input type="text" id="chuyenMon" name="chuyenMon" value="<c:out value="${requestScope.NHANVIEN_DATA.chuyenMon}"/>" placeholder="Ví dụ: Bác sĩ, Y tá, Lễ tân...">
                </div>

                <div class="form-group">
                    <label for="khoaId">Khoa:</label>
                    <select id="khoaId" name="khoaId">
                        <option value="0">-- Không thuộc khoa nào --</option> <%-- Cho phép nhân viên (ví dụ: lễ tân) không có khoa --%>
                        <c:forEach var="khoa" items="${requestScope.LIST_KHOA}">
                            <option value="${khoa.id}" ${requestScope.NHANVIEN_DATA.khoaId == khoa.id ? 'selected' : ''}>
                                <c:out value="${khoa.tenKhoa}" />
                            </option>
                        </c:forEach>
                    </select>
                </div>

                <div class="form-group">
                    <label for="soDienThoai">Số điện thoại:</label>
                    <input type="text" id="soDienThoai" name="soDienThoai" value="<c:out value="${requestScope.NHANVIEN_DATA.soDienThoai}"/>">
                </div>

                <%-- Thêm các trường khác (Ngày sinh, Giới tính, Địa chỉ, Bằng cấp) nếu bạn muốn --%>
                <%-- Ví dụ: --%>
                <%-- 
                <div class="form-group">
                    <label for="ngaySinh">Ngày sinh:</label>
                    <input type="datetime-local" id="ngaySinh" name="ngaySinh" value="${requestScope.NHANVIEN_DATA.ngaySinh}">
                </div>
                <div class="form-group">
                    <label for="gioiTinh">Giới tính:</label>
                    <select id="gioiTinh" name="gioiTinh">
                        <option value="Nam" ${requestScope.NHANVIEN_DATA.gioiTinh == 'Nam' ? 'selected' : ''}>Nam</option>
                        <option value="Nữ" ${requestScope.NHANVIEN_DATA.gioiTinh == 'Nữ' ? 'selected' : ''}>Nữ</option>
                        <option value="Khác" ${requestScope.NHANVIEN_DATA.gioiTinh == 'Khác' ? 'selected' : ''}>Khác</option>
                    </select>
                </div>
                --%>

                <div class="form-actions">
                    <button type="submit" class="btn-submit">
                        <i class="fas fa-save"></i> ${isCreating ? 'Tạo mới' : 'Cập nhật'}
                    </button>
                    <a href="MainController?action=listNhanVien" class="btn-cancel">Hủy</a>
                </div>
            </form>

        </div>

        <jsp:include page="/WEB-INF/footer.jsp" /> 

    </body>
</html>