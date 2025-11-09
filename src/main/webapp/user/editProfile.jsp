<%--
    Document   : user/editProfile.jsp
    Mô tả      : (Đã SỬA LỖI + THÊM UPLOAD AVATAR)
--%>

<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<%-- ✨ ĐÃ TRẢ LẠI: Trỏ về MainController theo yêu cầu của bạn ✨ --%>
<c:url var="mainControllerUrl" value="/MainController" /> 
<c:url var="securityControllerUrl" value="/SecurityController" /> 

<!DOCTYPE html>
<html lang="vi">
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>Hồ sơ Cá nhân</title>

        <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0/css/all.min.css">
        <link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/css/style.css">

        <c:set var="isCreateMode" value="${empty requestScope.BENHNHAN_DATA.id or requestScope.BENHNHAN_DATA.id == 0}" />
        <c:set var="hardFieldAccess" value="${isCreateMode ? '' : 'readonly'}" /> 

        <style>
            .form-group label {
                display: flex;
                justify-content: space-between;
                align-items: center;
                width: 100%;
            }
            .form-group .change-link {
                font-size: 0.85em;
                font-weight: 600;
                color: #007bff;
                text-decoration: none;
            }
            .form-group .change-link:hover {
                text-decoration: underline;
            }
            input[readonly] {
                background-color: #f4f4f4;
                color: #333;
                cursor: not-allowed;
            }

            /* CSS cho Avatar */
            .avatar-preview-container {
                margin-top: 15px;
            }
            .avatar-preview {
                max-width: 150px;
                max-height: 150px;
                border-radius: 8px;
                border: 1px solid #ddd;
            }

        </style>
    </head>

    <body class="login-page-body"> 
        <div class="standalone-form-container" style="max-width: 600px;">

            <%-- Tiêu đề động --%>
            <c:if test="${!isCreateMode}">
                <h1 class="form-title">Chỉnh sửa Hồ sơ</h1>
                <p class="form-description">
                    Chỉnh sửa thông tin hồ sơ của bạn.
                    <br>(Để thay đổi thông tin nhạy cảm, vui lòng quay lại trang "Xem Hồ sơ").
                </p>
            </c:if>
            <c:if test="${isCreateMode}">
                <h1 class="form-title">Hoàn tất Hồ sơ</h1>
                <p class="form-description">Chào mừng! Vui lòng điền các thông tin bắt buộc dưới đây.</p>
            </c:if>

            <c:if test="${not empty requestScope.ERROR_MESSAGE}">
                <p class="error-message">${requestScope.ERROR_MESSAGE}</p>
            </c:if>

            <%-- Form action trỏ về MainController --%>
            <form action="${mainControllerUrl}" method="post" class="data-form" id="profile-form">

                <%-- 
                  Action này sẽ được MainController đọc, 
                  và MainController sẽ gọi BenhNhanController
                --%>
                <input type="hidden" name="action" value="saveProfile" />

                <c:if test="${not empty requestScope.BENHNHAN_DATA.id}">
                    <input type="hidden" name="id" value="${requestScope.BENHNHAN_DATA.id}" />
                </c:if>
                <c:if test="${not empty requestScope.BENHNHAN_DATA.maBenhNhan}">
                    <input type="hidden" name="maBenhNhan" value="${requestScope.BENHNHAN_DATA.maBenhNhan}" />
                </c:if>


                <%-- --- CÁC TRƯỜNG BẢO MẬT (Sửa Khó) --- --%>
                <div class="form-group">
                    <label for="hoTen">
                        <span>Họ và Tên (*):</span>
                        <c:if test="${!isCreateMode}">
                            <a href="${securityControllerUrl}?action=showConfirmPassword&next=edit_name" class="change-link">(Thay đổi)</a>
                        </c:if>
                    </label>
                    <input type="text" id="hoTen" name="hoTen" value="<c:out value='${requestScope.BENHNHAN_DATA.hoTen}'/>" required="required" ${hardFieldAccess}>
                </div>
                <div class="form-group">
                    <label for="cccd">
                        <span>Số CCCD/CMND (*):</span>
                        <c:if test="${!isCreateMode}">
                            <a href="${securityControllerUrl}?action=showConfirmPassword&next=edit_cccd" class="change-link">(Thay đổi)</a>
                        </c:if>
                    </label>
                    <input type="text" id="cccd" name="cccd" value="<c:out value='${requestScope.BENHNHAN_DATA.cccd}'/>" required="required" ${hardFieldAccess}>
                </div>
                <div class="form-group">
                    <label for="ngaySinh">
                        <span>Ngày sinh (*):</span>
                        <c:if test="${!isCreateMode}">
                            <a href="${securityControllerUrl}?action=showConfirmPassword&next=edit_dob" class="change-link">(Thay đổi)</a>
                        </c:if>
                    </label>
                    <input type="date" id="ngaySinh" name="ngaySinh" value="${requestScope.BENHNHAN_DATA.ngaySinh}" required="required" ${hardFieldAccess}>
                </div>
                <div class="form-group">
                    <label for="soDienThoai">
                        <span>Số điện thoại (*):</span>
                        <c:if test="${!isCreateMode}">
                            <a href="${securityControllerUrl}?action=showConfirmPassword&next=edit_phone" class="change-link">(Thay đổi)</a>
                        </c:if>
                    </label>
                    <input type="text" id="soDienThoai" name="soDienThoai" value="<c:out value='${requestScope.BENHNHAN_DATA.soDienThoai}'/>" required="required" ${hardFieldAccess}>
                </div>

                <hr class="form-divider">

                <%-- --- CÁC TRƯỜNG HỒ SƠ (Sửa Dễ) -> LUÔN MỞ --- --%>
                <div class="form-group">
                    <label for="gioiTinh"><span>Giới tính (*):</span></label>
                    <select id="gioiTinh" name="gioiTinh" required="required">
                        <option value="">-- Chọn giới tính --</option>
                        <option value="Nam" ${requestScope.BENHNHAN_DATA.gioiTinh == 'Nam' ? 'selected' : ''}>Nam</option>
                        <option value="Nữ" ${requestScope.BENHNHAN_DATA.gioiTinh == 'Nữ' ? 'selected' : ''}>Nữ</option>
                        <option value="Khác" ${requestScope.BENHNHAN_DATA.gioiTinh == 'Khác' ? 'selected' : ''}>Khác</option>
                    </select>
                </div>
                <div class="form-group">
                    <label for="diaChi"><span>Địa chỉ (*):</span></label>
                    <input type="text" id="diaChi" name="diaChi" value="<c:out value='${requestScope.BENHNHAN_DATA.diaChi}'/>" required="required">
                </div>
                <div class="form-group">
                    <label for="nhomMau"><span>Nhóm máu (Tùy chọn):</span></label>
                    <input type="text" id="nhomMau" name="nhomMau" value="<c:out value='${requestScope.BENHNHAN_DATA.nhomMau}'/>" placeholder="Ví dụ: A+, O-">
                </div>
                <div class="form-group">
                    <label for="tienSuBenh"><span>Tiền sử bệnh (Tùy chọn):</span></label>
                    <textarea id="tienSuBenh" name="tienSuBenh" rows="3"><c:out value="${requestScope.BENHNHAN_DATA.tienSuBenh}"/></textarea>
                </div>

                <div class="form-group">
                    <label for="avatarFile">Ảnh đại diện (Tùy chọn):</label>

                    <input type="file" id="avatarFile" accept="image/*" class="form-control" />

                    <input type="hidden" 
                           name="avatarBenhNhanBase64" 
                           id="avatarBase64Hidden" 
                           value="<c:out value='${requestScope.BENHNHAN_DATA.avatarBase64}'/>" />

                    <div class="avatar-preview-container">
                        <img id="avatarPreview" 
                             class="avatar-preview"
                             src="${not empty requestScope.BENHNHAN_DATA.avatarBase64 ? requestScope.BENHNHAN_DATA.avatarBase64 : ''}" 
                             alt="Xem trước ảnh" 
                             style="display: ${not empty requestScope.BENHNHAN_DATA.avatarBase64 ? 'block' : 'none'};">
                    </div>
                </div>
                <div class="form-actions" style="justify-content: center; display: flex; flex-direction: column; gap: 10px;">
                    <button type="submit" class="btn-submit" 
                            style="width: 100%; display: block;" 
                            id="btn-save-profile" 
                            disabled> 
                        <i class="fas fa-save"></i>
                        <c:choose>
                            <c:when test="${isCreateMode}">Lưu và Tiếp tục</c:when>
                            <c:otherwise>Lưu thay đổi</c:otherwise>
                        </c:choose>
                    </button>
                </div>
            </form>
<!--            <a href="${pageContext.request.contextPath}/index.jsp" class="home-link" title="Quay về Trang chủ">
                <i class="fas fa-home"></i>-->
            </a> 
        </div>


        <script>
            document.addEventListener("DOMContentLoaded", function () {

                const form = document.getElementById('profile-form');
                const saveButton = document.getElementById('btn-save-profile');

                // --- ✨ JAVASCRIPT UPLOAD AVATAR ✨ ---
                const avatarFileInput = document.getElementById('avatarFile');
                const avatarHiddenInput = document.getElementById('avatarBase64Hidden');
                const avatarPreviewImg = document.getElementById('avatarPreview');

                avatarFileInput.addEventListener('change', function () {
                    const file = this.files[0];
                    if (!file)
                        return;

                    const reader = new FileReader();

                    reader.onload = function (e) {
                        const base64String = e.target.result;
                        avatarHiddenInput.value = base64String;
                        avatarPreviewImg.src = base64String;
                        avatarPreviewImg.style.display = 'block';
                    };

                    reader.readAsDataURL(file);
                });
                // --- ✨ KẾT THÚC JS AVATAR ✨ ---


                // --- LOGIC KÍCH HOẠT NÚT LƯU (Giữ nguyên) ---
                function enableSaveButton() {
                    saveButton.disabled = false;
                    form.removeEventListener('input', enableSaveButton);
                    form.removeEventListener('change', enableSaveButton);
                }
                form.addEventListener('input', enableSaveButton);
                form.addEventListener('change', enableSaveButton);

            });
        </script>

    </body>
</html>