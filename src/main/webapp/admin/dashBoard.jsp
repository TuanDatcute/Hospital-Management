<%--
    Document    : dashboard.jsp (Trang chủ Admin)
    Created on  : Oct 29, 2025
    Author      : ADMIN
    (ĐÃ NÂNG CẤP: Giao diện V2.1 - Đồng bộ với home.jsp)
--%>

<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>Bảng điều khiển Admin</title>

        <%-- Nhúng CSS/Font chung --%>
        <link rel="preconnect" href="https://fonts.googleapis.com">
        <link rel="preconnect" href="https://fonts.gstatic.com" crossorigin>
        <link href="https://fonts.googleapis.com/css2?family=Poppins:wght@400;500;600;700&display=swap" rel="stylesheet">
        <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0/css/all.min.css">
        <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/swiper@11/swiper-bundle.min.css" />
        <link href="https://fonts.googleapis.com/css2?family=Be+Vietnam+Pro:wght@400;500;600;700&display=swap" rel="stylesheet">

        <%-- CSS Chung --%>
        <link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/css/index.css?v=1.5">

        <%-- ✨ LIÊN KẾT TỚI FILE CSS CỦA TRANG HOME (Rất quan trọng) ✨ --%>
        <link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/css/home.css?v=2.2">
    </head>
    <body>

        <%-- Nhúng Header --%>
        <jsp:include page="/WEB-INF/headerDat.jsp" /> 

        <%-- ✨ SỬ DỤNG CLASS "home-content" TỪ home.css ✨ --%>
        <div class="home-content">

            <h1 class="section-title">Bảng điều khiển Quản trị</h1>

            <%-- Hiển thị thông báo thành công (ví dụ: đổi mật khẩu thành công) --%>
            <c:if test="${not empty sessionScope.SUCCESS_MESSAGE}">
                <p class="success-message">${sessionScope.SUCCESS_MESSAGE}</p>
                <c:remove var="SUCCESS_MESSAGE" scope="session" />
            </c:if>

            <div class="home-welcome-message">
                Chào mừng <strong>${sessionScope.USER.tenDangNhap}</strong>! (Vai trò: ${sessionScope.ROLE})
            </div>

            <%-- Lưới chức năng (Đã nâng cấp HTML) --%>
            <div class="function-grid">

                <%-- 1. Quản lý Tài khoản --%>
                <a href="${pageContext.request.contextPath}/MainController?action=listUsers" class="function-card color-admin-user">
                    <div class="icon"><i class="fas fa-users-cog"></i></div>
                    <h4>Quản lý Tài khoản</h4>
                    <p>Thêm mới, phân quyền và khóa/mở tài khoản.</p>
                </a>

                <%-- 2. Quản lý Khoa --%>
                <a href="${pageContext.request.contextPath}/MainController?action=listKhoa" class="function-card color-admin-khoa">
                    <div class="icon"><i class="fas fa-hospital-symbol"></i></div>
                    <h4>Quản lý Khoa</h4>
                    <p>Quản lý danh sách các khoa và phòng ban.</p>
                </a>

                <%-- 3. Quản lý Nhân viên --%>
                <a href="${pageContext.request.contextPath}/MainController?action=listNhanVien" class="function-card color-admin-nhanvien">
                    <div class="icon"><i class="fas fa-user-nurse"></i></div>
                    <h4>Quản lý Nhân viên</h4>
                    <p>Quản lý hồ sơ bác sĩ và các nhân viên khác.</p>
                </a>

                <%-- 4. Quản lý Bệnh nhân --%>
                <a href="${pageContext.request.contextPath}/MainController?action=listBenhNhan" class="function-card color-admin-benhnhan">
                    <div class="icon"><i class="fas fa-procedures"></i></div>
                    <h4>Quản lý Bệnh nhân</h4>
                    <p>Tra cứu và quản lý hồ sơ bệnh nhân toàn viện.</p>
                </a>

                <%-- 5. Quản lý Lịch hẹn --%>
                <a href="${pageContext.request.contextPath}/MainController?action=listLichHen" class="function-card color-admin-lichhen">
                    <div class="icon"><i class="fas fa-calendar-alt"></i></div>
                    <h4>Quản lý Lịch hẹn</h4>
                    <p>Xem, xác nhận hoặc hủy các lịch hẹn đã đặt.</p>
                </a>

                <%-- 6. Đổi mật khẩu --%>
                <a href="${pageContext.request.contextPath}/MainController?action=showChangePasswordForm" class="function-card color-admin-security">
                    <div class="icon"><i class="fas fa-key"></i></div>
                    <h4>Đổi mật khẩu</h4>
                    <p>Thay đổi mật khẩu quản trị của chính bạn.</p>
                </a>

            </div> <%-- End function-grid --%>
        </div> <%-- End home-content --%>

        <%-- Footer --%>
        <footer class="main-footer">
            <div class="container">
                <jsp:include page="/WEB-INF/footer.jsp" /> 
            </div>
        </footer>

    

        <script src="https://cdn.jsdelivr.net/npm/swiper@11/swiper-bundle.min.js"></script>

        <script src="<c:url value='/js/index.js'/>"></script>

    </body>
</html>