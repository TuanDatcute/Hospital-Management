<%-- /WEB-INF/header.jsp --%>
<%@ page pageEncoding="UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<%-- KHÔNG CHỨA HTML, HEAD, BODY, STYLE, HAY LINK CSS/FA --%>

<div class="header-wrapper" id="headerWrapper">
    <%-- Thanh Header trên cùng --%>
    <div class="header-top">
        <div class="social-links">
            <a href="#" title="Tiktok"><i class="fab fa-tiktok"></i></a>
            <a href="#" title="Facebook"><i class="fab fa-facebook-f"></i></a>
            <a href="#" title="Zalo"><i class="fa-solid fa-comment-dots"></i></a>
            <a href="#" title="Youtube"><i class="fab fa-youtube"></i></a>
        </div>
        <div class="top-actions">
            <c:if test="${empty sessionScope.USER}">
                <a href="${pageContext.request.contextPath}/login.jsp" class="btn btn-account"><i class="fas fa-user"></i> Tài khoản</a>
            </c:if>

            <c:if test="${not empty sessionScope.USER}">
                <%-- Dùng c:choose để link về đúng trang chủ (Dashboard hoặc Home) --%>
                <c:choose>
                    <c:when test="${sessionScope.ROLE == 'QUAN_TRI'}">
                        <a href="${pageContext.request.contextPath}/admin/dashboard.jsp" class="btn btn-account">
                            <i class="fas fa-user"></i> ${sessionScope.USER.tenDangNhap}
                        </a>
                    </c:when>
                    <c:otherwise>
                        <a href="${pageContext.request.contextPath}/home.jsp" class="btn btn-account">
                            <i class="fas fa-user"></i> ${sessionScope.USER.tenDangNhap}
                        </a>
                    </c:otherwise>
                </c:choose>

                <span style="margin-left: 15px; color: #0056b3; font-weight: 600; font-size: 0.9em;">
                    (Vai trò: ${sessionScope.ROLE})
                </span>
                <a href="${pageContext.request.contextPath}/MainController?action=logout" style="margin-left: 15px; color: #dc3545; font-weight: 600;">Đăng xuất</a>
            </c:if>

            <div class="language-selector">
                <img src="${pageContext.request.contextPath}/images/vn-flag.png" alt="VN Flag">
            </div>
        </div>
    </div>

    <%-- Khu vực Header chính (Sticky) --%>
    <div class="header-main">
        <div class="logo">
            <a href="${pageContext.request.contextPath}/index.jsp"><img src="${pageContext.request.contextPath}/images/your-logo.png" alt="Logo Bệnh viện"></a>
        </div>
        <div class="support-info">
            <i class="fas fa-headset"></i>
            <div class="text">
                Hỗ trợ đặt khám
                <span class="phone-number">1900 2115</span>
            </div>
        </div>

        <%-- *** BẮT ĐẦU SỬA LOGIC MENU CHÍNH *** --%>
        <nav>
            <ul>
                <c:choose>
                    <%-- 1. NẾU LÀ ADMIN -> Hiển thị Menu Admin --%>
                    <c:when test="${sessionScope.ROLE == 'QUAN_TRI'}">
                        <li><a href="${pageContext.request.contextPath}/admin/dashboard.jsp" class="active">Bảng điều khiển</a></li>
                        <li><a href="${pageContext.request.contextPath}/MainController?action=listKhoa">Quản lý Khoa</a></li>
                        <li><a href="${pageContext.request.contextPath}/MainController?action=listNhanVien">Nhân viên</a></li>
                        <li><a href="${pageContext.request.contextPath}/MainController?action=listBenhNhan">Bệnh nhân</a></li>
                        <li><a href="${pageContext.request.contextPath}/MainController?action=listUsers">Tài khoản</a></li>
                        <li><a href="${pageContext.request.contextPath}/MainController?action=listLichHen">Lịch hẹn</a></li>
                        </c:when>

                    <%-- 2. NẾU LÀ BỆNH NHÂN -> Hiển thị Menu Bệnh nhân --%>
                    <c:when test="${sessionScope.ROLE == 'BENH_NHAN'}">
                        <li><a href="${pageContext.request.contextPath}/home.jsp" class="active">Trang chủ</a></li>
                        <li><a href="${pageContext.request.contextPath}/MainController?action=showLichHenCreateForm">Đặt lịch hẹn</a></li>
                        <li><a href="#">Lịch sử khám bệnh</a></li>
                        <li><a href="#">Hồ sơ của tôi</a></li>
                        </c:when>

                    <%-- 3. NẾU LÀ BÁC SĨ / LỄ TÂN -> Hiển thị Menu BS/LT (Tùy chỉnh) --%>
                    <c:when test="${sessionScope.ROLE == 'BAC_SI' || sessionScope.ROLE == 'LE_TAN'}">
                        <li><a href="${pageContext.request.contextPath}/home.jsp" class="active">Trang chủ</a></li>
                        <li><a href="${pageContext.request.contextPath}/MainController?action=listLichHen">QL Lịch hẹn</a></li>
                        <li><a href="${pageContext.request.contextPath}/MainController?action=showCreateForm">Tạo Phiếu khám</a></li>
                        </c:when>

                    <%-- 4. NẾU CHƯA ĐĂNG NHẬP (Khách) -> Hiển thị Menu Public --%>
                    <c:otherwise>
                        <li><a href="#" class="active">Cơ sở y tế <span class="dropdown-icon">▼</span></a></li>
                        <li><a href="#">Dịch vụ y tế <span class="dropdown-icon">▼</span></a></li>
                        <li><a href="#">Khám sức khỏe DN<span class="dropdown-icon">▼</span></a></li>
                        <li><a href="#">Tin tức <span class="dropdown-icon">▼</span></a></li>
                        <li><a href="#">Hướng dẫn <span class="dropdown-icon">▼</span></a></li>
                        <li><a href="#">Liên hệ <span class="dropdown-icon">▼</span></a></li>
                        </c:otherwise>
                    </c:choose>
            </ul>
        </nav>
        <%-- *** KẾT THÚC SỬA LOGIC MENU CHÍNH *** --%>

    </div>

    <%-- Dòng chữ chạy (Sticky - Nằm trong Wrapper) --%>
    <div class="marquee-banner">
        <marquee behavior="scroll" direction="left" scrollamount="5">
            Hãy liên hệ với chúng tôi qua hotline 1900 2115 để được tư vấn và hỗ trợ một cách tốt nhất! 🏥 Chúc bạn ngày mới tốt lành!
        </marquee>
    </div>
</div>