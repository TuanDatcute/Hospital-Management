<%--
    Document    : formNhanVien.jsp (Đã sửa Chuyên môn thành Dropdown)
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

        <c:set var="isCreating" value="${requestScope.formAction == 'createNhanVien'}" />
        <title>${isCreating ? 'Thêm Nhân viên' : 'Cập nhật Nhân viên'}</title>

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
                <c:if test="${isCreating}">Thêm Nhân viên Mới</c:if>
                <c:if test="${!isCreating}">Cập nhật Thông tin Nhân viên</c:if>
                </h2>

            <c:if test="${not empty requestScope.ERROR_MESSAGE}">
                <p class="error-message">${requestScope.ERROR_MESSAGE}</p>
            </c:if>
            <c:if test="${not empty requestScope.LOAD_FORM_ERROR}">
                <p class="error-message">${requestScope.LOAD_FORM_ERROR}</p>
            </c:if>

            <form action="MainController" method="post" class="data-form">

                <input type="hidden" name="action" value="${requestScope.formAction}" />

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
                        <c:otherwise>
                            <input type="text" value="ID: ${requestScope.NHANVIEN_DATA.taiKhoanId} (Không thể thay đổi)" readonly="readonly" class="disabled-input">
                        </c:otherwise>
                    </c:choose>
                </div>

                <%-- *** BẮT ĐẦU SỬA LỖI: Chuyển 'chuyenMon' thành <select> *** --%>
                <div class="form-group">
                    <label for="chuyenMon">Chuyên môn (Vai trò):</label>
                    <select id="chuyenMon" name="chuyenMon" required="required">
                        <option value="">-- Chọn chuyên môn --</option>
                        <option value="Bác sĩ" ${requestScope.NHANVIEN_DATA.chuyenMon == 'Bác sĩ' ? 'selected' : ''}>Bác sĩ</option>
                        <option value="Y tá" ${requestScope.NHANVIEN_DATA.chuyenMon == 'Y tá' ? 'selected' : ''}>Y tá</option>
                        <option value="Lễ tân" ${requestScope.NHANVIEN_DATA.chuyenMon == 'Lễ tân' ? 'selected' : ''}>Lễ tân</option>
                        <option value="Dược sĩ" ${requestScope.NHANVIEN_DATA.chuyenMon == 'Dược sĩ' ? 'selected' : ''}>Dược sĩ</option>
                        <option value="Kỹ thuật viên" ${requestScope.NHANVIEN_DATA.chuyenMon == 'Kỹ thuật viên' ? 'selected' : ''}>Kỹ thuật viên</option>
                        <option value="Khác" ${requestScope.NHANVIEN_DATA.chuyenMon == 'Khác' ? 'selected' : ''}>Khác</option>
                    </select>
                </div>
                <%-- *** KẾT THÚC SỬA LỖI *** --%>

                <div class="form-group">
                    <label for="khoaId">Khoa:</label>
                    <select id="khoaId" name="khoaId">
                        <option value="0">-- Không thuộc khoa nào --</option>
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

                <div class="form-group">
                    <label for="ngaySinh">Ngày sinh:</label>
                    <c:set var="ngaySinhValue" value="${requestScope.NHANVIEN_DATA.ngaySinh}" />
                    <input type="datetime-local" id="ngaySinh" name="ngaySinh" value="${ngaySinhValue}">
                </div>

                <div class="form-group">
                    <label for="gioiTinh">Giới tính:</label>
                    <select id="gioiTinh" name="gioiTinh">
                        <option value="Nam" ${requestScope.NHANVIEN_DATA.gioiTinh == 'Nam' ? 'selected' : ''}>Nam</option>
                        <option value="Nữ" ${requestScope.NHANVIEN_DATA.gioiTinh == 'Nữ' ? 'selected' : ''}>Nữ</option>
                        <option value="Khác" ${requestScope.NHANVIEN_DATA.gioiTinh == 'Khác' ? 'selected' : ''}>Khác</option>
                    </select>
                </div>

                <div class="form-group">
                    <label for="bangCap">Bằng cấp:</label>
                    <input type="text" id="bangCap" name="bangCap" value="<c:out value="${requestScope.NHANVIEN_DATA.bangCap}"/>" placeholder="Ví dụ: Thạc sĩ Y khoa">
                </div>

                <div class="form-group">
                    <label for="diaChi">Địa chỉ:</label>
                    <input type="text" id="diaChi" name="diaChi" value="<c:out value="${requestScope.NHANVIEN_DATA.diaChi}"/>">
                </div>

                <div class="form-actions">
                    <button type="submit" class="btn-submit">
                        <i class="fas fa-save"></i> ${isCreating ? 'Tạo mới' : 'Cập nhật'}
                    </button>
                    <a href="${pageContext.request.contextPath}/MainController?action=listNhanVien" class="btn-cancel">Hủy</a>
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