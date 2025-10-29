<%--
    Document   : formBenhNhan.jsp
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
        <c:set var="isCreating" value="${requestScope.formAction == 'createBenhNhan'}" />
        <title>${isCreating ? 'Thêm Bệnh nhân Mới' : 'Cập nhật Bệnh nhân'}</title>

        <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0/css/all.min.css">
        <link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/css/style.css">

        <%-- Trang này sử dụng các class: .data-form, .form-group, .btn-submit, .btn-cancel --%>
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

            <%-- 
                Form này gửi đến MainController.
                Action sẽ là 'createBenhNhan' hoặc 'updateBenhNhan' 
                (được set bởi BenhNhanController trong biến requestScope.formAction)
            --%>
            <form action="MainController" method="post" class="data-form">

                <input type="hidden" name="action" value="${requestScope.formAction}" />

                <%-- Truyền ID (chỉ khi cập nhật) --%>
                <c:if test="${!isCreating}">
                    <input type="hidden" name="id" value="${requestScope.BENHNHAN_DATA.id}" />
                </c:if>

                <div class="form-group">
                    <label for="maBenhNhan">Mã Bệnh nhân:</label>
                    <%-- Không cho sửa Mã Bệnh nhân khi cập nhật --%>
                    <input type="text" id="maBenhNhan" name="maBenhNhan" 
                           value="<c:out value="${requestScope.BENHNHAN_DATA.maBenhNhan}"/>" 
                           ${!isCreating ? 'readonly' : ''} 
                           required="required">
                </div>

                <div class="form-group">
                    <label for="hoTen">Họ tên Bệnh nhân:</label>
                    <input type="text" id="hoTen" name="hoTen" value="<c:out value="${requestScope.BENHNHAN_DATA.hoTen}"/>" required="required">
                </div>

                <%-- Tài khoản (Có thể chọn khi TẠO MỚI, có thể đổi khi CẬP NHẬT) --%>
                <div class="form-group">
                    <label for="taiKhoanId">Tài khoản liên kết:</label>
                    <select id="taiKhoanId" name="taiKhoanId">
                        <option value="0">-- Không gán tài khoản --</option> <%-- Cho phép bệnh nhân không có tài khoản --%>

                        <%-- 
                           Hiển thị tài khoản HIỆN TẠI của bệnh nhân (khi Edit) 
                           ngay cả khi tài khoản đó đã bị khóa (để biết nó đang được gán)
                        --%>
                        <c:if test="${!isCreating && not empty requestScope.BENHNHAN_DATA.taiKhoanId}">
                            <option value="${requestScope.BENHNHAN_DATA.taiKhoanId}" selected="selected">
                                Tài khoản hiện tại (ID: ${requestScope.BENHNHAN_DATA.taiKhoanId})
                            </option>
                        </c:if>

                        <%-- Lặp qua danh sách tài khoản CHƯA GÁN (vai trò Bệnh Nhân) --%>
                        <c:forEach var="tk" items="${requestScope.LIST_TAIKHOAN}">
                            <option value="${tk.id}" ${requestScope.BENHNHAN_DATA.taiKhoanId == tk.id ? 'selected' : ''}>
                                ${tk.tenDangNhap} (ID: ${tk.id})
                            </option>
                        </c:forEach>
                    </select>
                    <c:if test="${empty requestScope.LIST_TAIKHOAN && isCreating}">
                        <p style="color: #6c757d; font-style: italic;">Không tìm thấy tài khoản (vai trò Bệnh nhân) nào còn trống.</p>
                    </c:if>
                </div>

                <%-- Thêm các trường khác (Ngày sinh, Giới tính, SĐT, Địa chỉ, Tiền sử bệnh, Nhóm máu) --%>

                <div class="form-group">
                    <label for="soDienThoai">Số điện thoại:</label>
                    <input type="text" id="soDienThoai" name="soDienThoai" value="<c:out value="${requestScope.BENHNHAN_DATA.soDienThoai}"/>">
                </div>

                <div class="form-group">
                    <label for="ngaySinh">Ngày sinh:</label>
                    <%-- Lấy giá trị LocalDateTime và format về đúng kiểu input --%>
                    <c:set var="ngaySinhValue" value="${requestScope.BENHNHAN_DATA.ngaySinh}" />
                    <input type="datetime-local" id="ngaySinh" name="ngaySinh" value="${ngaySinhValue}">
                </div>

                <div class="form-group">
                    <label for="gioiTinh">Giới tính:</label>
                    <select id="gioiTinh" name="gioiTinh">
                        <option value="">-- Chọn giới tính --</option>
                        <option value="Nam" ${requestScope.BENHNHAN_DATA.gioiTinh == 'Nam' ? 'selected' : ''}>Nam</option>
                        <option value="Nữ" ${requestScope.BENHNHAN_DATA.gioiTinh == 'Nữ' ? 'selected' : ''}>Nữ</option>
                        <option value="Khác" ${requestScope.BENHNHAN_DATA.gioiTinh == 'Khác' ? 'selected' : ''}>Khác</option>
                    </select>
                </div>

                <div class="form-group">
                    <label for="diaChi">Địa chỉ:</label>
                    <input type="text" id="diaChi" name="diaChi" value="<c:out value="${requestScope.BENHNHAN_DATA.diaChi}"/>">
                </div>

                <div class="form-group">
                    <label for="nhomMau">Nhóm máu:</label>
                    <input type="text" id="nhomMau" name="nhomMau" value="<c:out value="${requestScope.BENHNHAN_DATA.nhomMau}"/>">
                </div>

                <div class="form-group">
                    <label for="tienSuBenh">Tiền sử bệnh:</label>
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