<%--
    Document   : user/viewProfile.jsp
    Mô tả      : (File MỚI) Trang "Chỉ Xem" hồ sơ.
--%>

<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<%-- Tạo biến URL động --%>
<c:url var="mainControllerUrl" value="/MainController" />
<c:url var="securityControllerUrl" value="/SecurityController" /> 

<!DOCTYPE html>
<html lang="vi">
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>Xem Hồ sơ Cá nhân</title>

        <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0/css/all.min.css">
        <link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/css/style.css">

        <%-- CSS riêng cho trang "Chỉ Xem" --%>
        <style>
            .view-profile-container {
                background: #fff;
                padding: 30px 40px;
                border-radius: 8px;
                box-shadow: 0 4px 12px rgba(0,0,0,0.05);
                max-width: 600px;
                margin: 40px auto;
            }
            .view-profile-item {
                display: flex;
                justify-content: space-between;
                padding: 15px 0;
                border-bottom: 1px solid #eee;
            }
            .view-profile-item:last-child {
                border-bottom: none;
            }
            .view-profile-item label {
                font-weight: 600;
                color: #555;
                flex-basis: 30%; /* 30% cho nhãn */
            }
            .view-profile-item span {
                flex-basis: 70%; /* 70% cho giá trị */
                color: #000;
                word-break: break-word;
            }
            .view-profile-item .change-link {
                font-size: 0.9em;
                font-weight: 600;
                color: #007bff;
                text-decoration: none;
                margin-left: 10px;
            }
            .view-profile-item .change-link:hover {
                text-decoration: underline;
            }
            .profile-actions {
                display: flex;
                justify-content: center;
                padding-top: 20px;
                margin-top: 20px;
                border-top: 1px solid #eee;
            }
        </style>
    </head>

    <body class="login-page-body"> 
        <div class="view-profile-container">

            <h1 class="form-title">Hồ sơ Cá nhân</h1>
            <p class="form-description">
                Đây là thông tin hồ sơ y tế của bạn tại bệnh viện.
            </p>

            <%-- Hiển thị thông báo (ví dụ: "Cập nhật SĐT thành công!") --%>
            <c:if test="${not empty sessionScope.SUCCESS_MESSAGE}">
                <p class="success-message">${sessionScope.SUCCESS_MESSAGE}</p>
                <c:remove var="SUCCESS_MESSAGE" scope="session" />
            </c:if>

            <%-- --- PHẦN BẢO MẬT (Sửa Khó) --- --%>
            <div class="view-profile-item">
                <label>Họ và Tên:</label>
                <span>
                    <c:out value="${requestScope.BENHNHAN_DATA.hoTen}"/>
                    <a href="${securityControllerUrl}?action=showConfirmPassword&next=edit_name" class="change-link">(Thay đổi)</a>
                </span>
            </div>
            <div class="view-profile-item">
                <label>Số CCCD/CMND:</label>
                <span>
                    <c:out value="${requestScope.BENHNHAN_DATA.cccd}"/>
                    <a href="${securityControllerUrl}?action=showConfirmPassword&next=edit_cccd" class="change-link">(Thay đổi)</a>
                </span>
            </div>
            <div class="view-profile-item">
                <label>Ngày sinh:</label>
                <span>
                    <c:out value="${requestScope.BENHNHAN_DATA.ngaySinh}"/>
                    <a href="${securityControllerUrl}?action=showConfirmPassword&next=edit_dob" class="change-link">(Thay đổi)</a>
                </span>
            </div>
            <div class="view-profile-item">
                <label>Số điện thoại:</label>
                <span>
                    <c:out value="${requestScope.BENHNHAN_DATA.soDienThoai}"/>
                    <a href="${securityControllerUrl}?action=showConfirmPassword&next=edit_phone" class="change-link">(Thay đổi)</a>
                </span>
            </div>

            <%-- --- PHẦN HỒ SƠ (Sửa Dễ) --- --%>
            <div class="view-profile-item">
                <label>Giới tính:</label>
                <span><c:out value="${requestScope.BENHNHAN_DATA.gioiTinh}"/></span>
            </div>
            <div class="view-profile-item">
                <label>Địa chỉ:</label>
                <span><c:out value="${requestScope.BENHNHAN_DATA.diaChi}"/></span>
            </div>
            <div class="view-profile-item">
                <label>Nhóm máu:</label>
                <span><c:out value="${not empty requestScope.BENHNHAN_DATA.nhomMau ? requestScope.BENHNHAN_DATA.nhomMau : 'Chưa cập nhật'}"/></span>
            </div>
            <div class="view-profile-item">
                <label>Tiền sử bệnh:</label>
                <span><c:out value="${not empty requestScope.BENHNHAN_DATA.tienSuBenh ? requestScope.BENHNHAN_DATA.tienSuBenh : 'Chưa cập nhật'}"/></span>
            </div>

            <%-- Nút bấm --%>
            <div class="profile-actions">
                <%-- Link này sẽ trỏ đến action "showEditProfile" MỚI --%>
                <a href="${mainControllerUrl}?action=showEditProfile" class="btn-submit btn-secondary">
                    <i class="fas fa-edit"></i> Chỉnh sửa thông tin hồ sơ
                </a>
            </div>

        </div>
    </body>
</html>