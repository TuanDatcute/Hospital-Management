<%--
    Document    : home.jsp (Trang chủ Bệnh nhân)
    Author      : ADMIN (Đã NÂNG CẤP LÊN Dashboard V2)
--%>

<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>

<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>Trang chủ Bệnh nhân</title>

        <link rel="preconnect" href="https://fonts.googleapis.com">
        <link rel="preconnect" href="https://fonts.gstatic.com" crossorigin>
        <link href="https://fonts.googleapis.com/css2?family=Be+Vietnam+Pro:wght@400;500;600;700&display=swap" rel="stylesheet">
        <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0/css/all.min.css">

        <link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/css/index.css?v=1.2">

        <link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/css/home.css?v=2.1">
    </head>
    <body>

        <jsp:include page="/WEB-INF/headerDat.jsp" />   

        <div class="home-content">

            <h1 class="section-title">Chào mừng trở lại!</h1>
            <p class="home-welcome-message">
                Xin chào <strong>
                    <c:choose>
                        <c:when test="${not empty sessionScope.USER.tenDangNhap}">
                            <c:out value="${sessionScope.USER.tenDangNhap}"/>
                        </c:when>
                        <c:otherwise>
                            Bệnh nhân
                        </c:otherwise>
                    </c:choose>
                </strong>, 
                chúc bạn một ngày tốt lành.
            </p>

            <div class="function-grid">

                <%-- Chức năng Bệnh nhân --%>
                <c:if test="${sessionScope.ROLE == 'BENH_NHAN'}">

                    <a href="BenhNhanController?action=showProfile" class="function-card color-profile">
                        <div class="icon"><i class="fas fa-id-card"></i></div>
                        <h4>Hồ sơ của tôi</h4>
                        <p>Xem và cập nhật thông tin cá nhân của bạn.</p>
                    </a>

                    <a href="#" class="function-card color-history">
                        <div class="icon"><i class="fas fa-history"></i></div>
                        <h4>Xem Lịch sử khám</h4>
                        <p>Tra cứu các lần khám bệnh và đơn thuốc trước đây.</p>
                    </a>

                    <a href="AppointmentController?action=showAppointmentForm" class="function-card color-appoint">
                        <div class="icon"><i class="fas fa-calendar-plus"></i></div>
                        <h4>Đặt Lịch hẹn</h4>
                        <p>Sắp xếp lịch hẹn khám mới với bác sĩ.</p>
                    </a>
                </c:if>

                <a href="UserController?action=showChangePasswordForm" class="function-card color-security">
                    <div class="icon"><i class="fas fa-key"></i></div>
                    <h4>Đổi mật khẩu</h4>
                    <p>Tăng cường bảo mật cho tài khoản của bạn.</p>
                </a>

            </div> <%-- End function-grid --%>
        </div> <%-- End home-content --%>
        <footer class="main-footer">
            <div class="container">
                <jsp:include page="/WEB-INF/footer.jsp" /> 
            </div>
        </footer>

    </body>
</html>