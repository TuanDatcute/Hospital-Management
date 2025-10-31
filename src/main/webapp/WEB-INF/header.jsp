<%-- /WEB-INF/header.jsp --%>
<%@ page pageEncoding="UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %> <%-- Bắt buộc phải có cho menu động --%>

<%-- 
    KHÔNG CHỨA: <html>, <head>, <body>, <link>, <style>, <title>
    File này chỉ chứa code HTML của phần header.
--%>

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
            
            <%-- 1. KHI CHƯA ĐĂNG NHẬP --%>
            <c:if test="${empty sessionScope.USER}">
                <a href="${pageContext.request.contextPath}/login.jsp" class="btn btn-account"><i class="fas fa-user"></i> Tài khoản</a>
                <%-- Link Đăng ký trỏ đến login.jsp với #register (để mở panel Đăng ký) --%>
                <a href="${pageContext.request.contextPath}/login.jsp#register" style="margin-left: 10px; color: #0056b3; font-weight: 600;">Đăng ký</a>
            </c:if>

            <%-- 2. KHI ĐÃ ĐĂNG NHẬP --%>
            <c:if test="${not empty sessionScope.USER}">
                <%-- Dùng c:choose để link về đúng trang chủ (Dashboard hoặc Home) --%>
                <c:choose>
                    <%-- Link cho Admin --%>
                    <c:when test="${sessionScope.ROLE == 'QUAN_TRI'}">
                        <a href="${pageContext.request.contextPath}/admin/dashboard.jsp" class="btn btn-account">
                            <i class="fas fa-user"></i> ${sessionScope.USER.tenDangNhap}
                        </a>
                    </c:when>
                    <%-- Link cho Nhân viên (BS/LT) --%>
                    <c:when test="${sessionScope.ROLE == 'BAC_SI' || sessionScope.ROLE == 'LE_TAN'}">
                         <a href="${pageContext.request.contextPath}/staff/dashboard.jsp" class="btn btn-account">
                            <i class="fas fa-user"></i> ${sessionScope.USER.tenDangNhap}
                        </a>
                    </c:when>
                    <%-- Link cho Bệnh nhân (và vai trò khác) --%>
                    <c:otherwise>
                        <a href="${pageContext.request.contextPath}/home.jsp" class="btn btn-account">
                            <i class="fas fa-user"></i> ${sessionScope.USER.tenDangNhap}
                        </a>
                    </c:otherwise>
                </c:choose>

                <%-- Hiển thị Vai trò --%>
                <span style="margin-left: 15px; color: #0056b3; font-weight: 600; font-size: 0.9em;">
                    (Vai trò: ${sessionScope.ROLE})
                </span>
                
                <%-- Nút Đăng xuất --%>
                <a href="${pageContext.request.contextPath}/MainController?action=logout" style="margin-left: 15px; color: #dc3545; font-weight: 600;">Đăng xuất</a>
            </c:if>

            <%-- Lá cờ --%>
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

        <%-- LOGIC MENU CHÍNH (Đã sửa) --%>
        <nav>
            <ul>
                <%-- Lấy action hiện tại (param.action) và đường dẫn JSP (servletPath) --%>
                <c:set var="currentAction" value="${param.action}" />
                <c:set var="servletPath" value="${pageContext.request.servletPath}" />
                
                <c:choose>
                    <%-- 1. NẾU LÀ ADMIN -> Hiển thị Menu Admin --%>
                    <c:when test="${sessionScope.ROLE == 'QUAN_TRI'}">
                        <c:set var="isDashboard" value="${fn:endsWith(servletPath, '/dashboard.jsp')}" />
                        <li><a href="${pageContext.request.contextPath}/admin/dashboard.jsp" 
                               class="${isDashboard ? 'active' : ''}">Bảng điều khiển</a></li>
                        <li><a href="${pageContext.request.contextPath}/MainController?action=listKhoa" 
                               class="${fn:contains(currentAction, 'Khoa') ? 'active' : ''}">Quản lý Khoa</a></li>
                        <li><a href="${pageContext.request.contextPath}/MainController?action=listNhanVien"
                               class="${fn:contains(currentAction, 'NhanVien') ? 'active' : ''}">Nhân viên</a></li>
                        <li><a href="${pageContext.request.contextPath}/MainController?action=listBenhNhan"
                               class="${fn:contains(currentAction, 'BenhNhan') ? 'active' : ''}">Bệnh nhân</a></li>
                        <li><a href="${pageContext.request.contextPath}/MainController?action=listUsers"
                               class="${fn:contains(currentAction, 'User') ? 'active' : ''}">Tài khoản</a></li>
                        <li><a href="${pageContext.request.contextPath}/MainController?action=listLichHen"
                               class="${fn:contains(currentAction, 'LichHen') ? 'active' : ''}">Lịch hẹn</a></li>
                    </c:when>
                    
                    <%-- 2. NẾU LÀ BỆNH NHÂN --%>
                    <c:when test="${sessionScope.ROLE == 'BENH_NHAN'}">
                        <c:set var="isHome" value="${fn:endsWith(servletPath, '/home.jsp')}" />
                        <li><a href="${pageContext.request.contextPath}/home.jsp" class="${isHome ? 'active' : ''}">Trang chủ</a></li>
                        <li><a href="${pageContext.request.contextPath}/MainController?action=showLichHenCreateForm" class="${fn:contains(currentAction, 'LichHen') ? 'active' : ''}">Đặt lịch hẹn</a></li>
                        <li><a href="#">Lịch sử khám bệnh</a></li>
                        <li><a href="#">Hồ sơ của tôi</a></li>
                    </c:when>

                    <%-- 3. NẾU LÀ BÁC SĨ / LỄ TÂN --%>
                    <c:when test="${sessionScope.ROLE == 'BAC_SI' || sessionScope.ROLE == 'LE_TAN'}">
                         <c:set var="isStaffDashboard" value="${fn:endsWith(servletPath, '/dashboard.jsp')}" />
                        <li><a href="${pageContext.request.contextPath}/staff/dashboard.jsp" class="${isStaffDashboard ? 'active' : ''}">Bảng điều khiển</a></li>
                        <li><a href="${pageContext.request.contextPath}/MainController?action=listLichHen" class="${fn:contains(currentAction, 'LichHen') ? 'active' : ''}">QL Lịch hẹn</a></li>
                        <li><a href="${pageContext.request.contextPath}/MainController?action=showCreateForm" class="${fn:contains(currentAction, 'CreateForm') ? 'active' : ''}">Tạo Phiếu khám</a></li>
                    </c:when>
                    
                    <%-- 4. NẾU CHƯA ĐĂNG NHẬP (Khách) -> Hiển thị Menu Public --%>
                    <c:otherwise>
                        <c:set var="isIndex" value="${fn:endsWith(servletPath, '/index.jsp')}" />
                        <li><a href="${pageContext.request.contextPath}/index.jsp" class="${isIndex ? 'active' : ''}">Cơ sở y tế <span class="dropdown-icon">▼</span></a></li>
                        <li><a href="#">Dịch vụ y tế <span class="dropdown-icon">▼</span></a></li>
                        <li><a href="#">Khám sức khỏe DN</a></li>
                        <li><a href="#">Tin tức <span class="dropdown-icon">▼</span></a></li>
                        <li><a href="#">Hướng dẫn <span class="dropdown-icon">▼</span></a></li>
                        <li><a href="#">Liên hệ <span class="dropdown-icon">▼</span></a></li>
                    </c:otherwise>
                </c:choose>
            </ul>
        </nav>
    </div>
    
    <%-- Dòng chữ chạy (Sticky - Nằm trong Wrapper) --%>
    <div class="marquee-banner">
        <marquee behavior="scroll" direction="left" scrollamount="5">
            Hãy liên hệ với chúng tôi qua hotline 1900 2115 để được tư vấn và hỗ trợ một cách tốt nhất! 🏥 Chúc bạn ngày mới tốt lành!
        </marquee>
    </div>
</div>