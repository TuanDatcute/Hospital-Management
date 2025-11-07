<%--
    Document   : confirmProfile.jsp
    Mô tả      : (ĐÃ THIẾT KẾ LẠI) 
                 Trang xác nhận, khớp với style của các trang 'edit...'.
--%>
<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<%-- Tạo biến URL động --%>
<c:url var="bnControllerUrl" value="/BenhNhanController" />

<!DOCTYPE html>
<html lang="vi">
    <head>
        <meta charset="UTF-8">
        <title>Xác nhận thông tin</title>
        <%-- Nhúng CSS/Font chung --%>
        <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0/css/all.min.css">
        <link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/css/style.css">

        <%-- CSS nhỏ để hiển thị thông tin --%>
        <style>
            /* * Style này mô phỏng 'form-group' 
             * nhưng dùng để hiển thị text (label ở trên, value ở dưới)
             */
            .view-group {
                background-color: #f4f4f4; /* Giống màu của readonly/disabled */
                border: 1px solid #ddd;
                border-radius: 8px;
                padding: 10px 15px;
                margin-bottom: 15px;
                text-align: left;
            }
            .view-group label {
                font-weight: 600;
                color: #555;
                font-size: 0.9em;
                display: block;
                margin-bottom: 5px;
            }
            .view-group span {
                font-size: 1em;
                color: #000;
                word-break: break-word;
            }

            /* Ghi đè cho nút (để 2 nút nằm cạnh nhau) */
            .form-actions {
                display: flex;
                flex-direction: row; /* Nằm ngang */
                justify-content: space-between;
                gap: 10px;
                width: 100%;
            }
            .form-actions form {
                flex: 1; /* Chia đều không gian */
            }
            .form-actions button {
                width: 100%; /* Nút chiếm đầy form */
            }
        </style>
    </head>
    <body class="login-page-body">
        <%-- Dùng lại 'login-container' để có style hộp trắng --%>
        <%-- Tăng 'max-height' để chứa đủ thông tin --%>
        <div class="login-container" style="max-height: 720px; overflow-y: auto;"> 
            <div class="form-container sign-in" style="opacity: 1; z-index: 5; width: 100%;">

                <%-- Nút quay về trang Sửa Hồ sơ --%>
                <a href="${pageContext.request.contextPath}/MainController?action=showEditProfile" class="home-link" title="Quay lại" style="left: 20px; top: 20px;">
                    <i class="fas fa-arrow-left"></i>
                </a>

                <%-- Dùng 'login-form' để có padding nhất quán --%>
                <div class="login-form" style="padding: 0 40px;">

                    <h1 class="form-title">
                        <i class="fas fa-user-check" style="color: #007bff; margin-bottom: 15px; font-size: 2.5rem;"></i>
                        <br>Chào mừng trở lại!
                    </h1>
                    <p style="text-align: center; margin-bottom: 20px; font-size: 14px;">
                        Chúng tôi tìm thấy hồ sơ y tế này với CCCD bạn đã nhập.
                    </p>

                    <%-- Hiển thị thông tin tìm được --%>
                    <div class="view-group">
                        <label>Họ tên:</label>
                        <span><c:out value="${EXISTING_PATIENT.hoTen}"/></span>
                    </div>
                    <div class="view-group">
                        <label>CCCD:</label>
                        <span><c:out value="${EXISTING_PATIENT.cccd}"/></span>
                    </div>
                    <div class="view-group">
                        <label>Ngày sinh:</label>
                        <span>${EXISTING_PATIENT.ngaySinh}</span>
                    </div>
                    <div class="view-group">
                        <label>Giới tính:</label>
                        <span><c:out value="${EXISTING_PATIENT.gioiTinh}"/></span>
                    </div>
                    <div class="view-group">
                        <label>Số điện thoại:</label>
                        <span><c:out value="${EXISTING_PATIENT.soDienThoai}"/></span>
                    </div>
                    <div class="view-group">
                        <label>Địa chỉ:</label>
                        <span><c:out value="${EXISTING_PATIENT.diaChi}"/></span>
                    </div>

                    <p style="margin-top: 20px; color: #333; font-weight: 600; text-align: center;">
                        Đây có phải là thông tin của bạn?
                    </p>

                    <div class="form-actions">
                        <%-- Nút "Cần cập nhật" (Màu xám) --%>
                        <form action="${bnControllerUrl}" method="GET" style="display: inline-block;">
                            <input type="hidden" name="action" value="showEditProfileWithExisting">
                            <input type="hidden" name="patientId" value="${EXISTING_PATIENT.id}">
                            <button type="submit" class="btn-submit btn-secondary"> <%-- Dùng class btn-secondary --%>
                                <i class="fas fa-edit"></i> Cần cập nhật
                            </button>
                        </form>

                        <%-- Nút "Đúng, liên kết" (Màu xanh) --%>
                        <form action="${bnControllerUrl}" method="POST" style="display: inline-block;">
                            <input type="hidden" name="action" value="confirmAndLink">
                            <input type="hidden" name="patientId" value="${EXISTING_PATIENT.id}">
                            <button type="submit" class="btn-submit"> <%-- Dùng class btn-submit (mặc định) --%>
                                <i class="fas fa-check"></i> Liên kết ngay
                            </button>
                        </form>
                    </div>

                </div>
            </div>
        </div>
    </body>
</html>