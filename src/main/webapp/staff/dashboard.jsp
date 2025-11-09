<%--
    Document    : dashboard.jsp (Trang chủ Bác sĩ/Lễ tân)
    Created on  : Oct 30, 2025
    Author      : ADMIN
    (ĐÃ NÂNG CẤP: Giao diện V2.1 + Sửa lỗi cấu trúc Footer)
--%>

<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>Bảng điều khiển Nhân viên</title>

        <link rel="preconnect" href="https://fonts.googleapis.com">
        <link rel="preconnect" href="https://fonts.gstatic.com" crossorigin>
        <link href="https://fonts.googleapis.com/css2?family=Poppins:wght@400;500;600;700&display=swap" rel="stylesheet">

        <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0/css/all.min.css">
        <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/swiper@11/swiper-bundle.min.css" />
        <link href="https://fonts.googleapis.com/css2?family=Be+Vietnam+Pro:wght@400;500;600;700&display=swap" rel="stylesheet">

        <%-- CSS Chung --%>
        <link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/css/index.css?v=1.5">

        <%-- ✨ LIÊN KẾT TỚI FILE CSS CỦA TRANG HOME ✨ --%>
        <link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/css/home.css?v=2.2">
    </head>
    <body>

        <jsp:include page="/WEB-INF/headerDat.jsp" /> 

        <%-- ✨ SỬ DỤNG CLASS "home-content" TỪ home.css ✨ --%>
        <div class="home-content">

            <h1 class="section-title">Bảng điều khiển Nhân viên</h1>

            <c:if test="${not empty sessionScope.SUCCESS_MESSAGE}">
                <p class="success-message">${sessionScope.SUCCESS_MESSAGE}</p>
                <c:remove var="SUCCESS_MESSAGE" scope="session" />
            </c:if>

            <div class="home-welcome-message">
                Chào mừng <strong>${sessionScope.USER.tenDangNhap}</strong>!
                (Vai trò: ${sessionScope.ROLE})
            </div>

            <%-- Lưới chức năng (ĐÃ NÂNG CẤP HTML & CLASS MÀU) --%>
            <div class="function-grid">

                <a href="${pageContext.request.contextPath}/MainController?action=showAppointmentForm_Staff" class="function-card color-appoint">
                    <div class="icon"><i class="fas fa-calendar-plus"></i></div>
                    <h4>Tạo Lịch hẹn Mới</h4>
                    <p>Tạo lịch hẹn cho bệnh nhân (gọi điện/trực tiếp).</p>
                </a>

                <a href="${pageContext.request.contextPath}/MainController?action=listLichHen" class="function-card color-staff-lichhen">
                    <div class="icon"><i class="fas fa-calendar-check"></i></div>
                    <h4>Quản lý Lịch hẹn</h4>
                    <p>Xem, xác nhận hoặc hủy các lịch hẹn trong ngày.</p>
                </a>

                <a href="${pageContext.request.contextPath}/MainController?action=showCreateEncounterForm" class="function-card color-staff-phieukham">
                    <div class="icon"><i class="fas fa-file-medical"></i></div>
                    <h4>Tạo Phiếu khám</h4>
                    <p>Tạo phiếu khám mới cho bệnh nhân đến khám.</p>
                </a>

                <a href="${pageContext.request.contextPath}/MainController?action=listBenhNhan" class="function-card color-staff-benhnhan">
                    <div class="icon"><i class="fas fa-user-injured"></i></div>
                    <h4>Danh sách Bệnh nhân</h4>
                    <p>Tra cứu và xem lại thông tin hồ sơ bệnh nhân.</p>
                </a>

                <%-- Tái sử dụng class màu Xám của Admin --%>
                <a href="${pageContext.request.contextPath}/MainController?action=showChangePasswordForm" class="function-card color-admin-security">
                    <div class="icon"><i class="fas fa-key"></i></div>
                    <h4>Đổi mật khẩu</h4>
                    <p>Thay đổi mật khẩu cá nhân của bạn.</p>
                </a>

            </div> <%-- End function-grid --%>

            <%-- ✨ LỖI LÀ Ở ĐÂY: Thẻ div "home-content" phải được đóng LẠI TRƯỚC footer --%>
        </div> <%-- End home-content --%>


        <%-- ✨ FOOTER ĐÃ ĐƯỢC DI CHUYỂN RA NGOÀI ✨ --%>
        <footer class="main-footer">
            <div class="container">
                <jsp:include page="/WEB-INF/footer.jsp" /> 
            </div>
        </footer>

        <%-- ✨ SCRIPTS CŨNG PHẢI NẰM BÊN NGOÀI "home-content" ✨ --%>
        <%-- ✨ 3. THÊM THƯ VIỆN SWIPER.JS (Bắt buộc) --%>
        <script src="https://cdn.jsdelivr.net/npm/swiper@11/swiper-bundle.min.js"></script>

        <%-- ✨ 4. LINK TỚI FILE JS (Sẽ cập nhật ở Bước 3) --%>
        <script src="<c:url value='/js/index.js'/>"></script> 

    </body>
</html>