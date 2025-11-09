<%-- /WEB-INF/header.jsp (Đã cập nhật cấu trúc) --%>
<%@ page pageEncoding="UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>

<div class="header-wrapper" id="headerWrapper">

    <%-- Thanh Header trên cùng (Vẫn giữ logic ở đây nhưng CSS sẽ ẩn nó) --%>
    <div class="header-top">
        <div class="social-links">
            <a href="#" title="Tiktok"><i class="fab fa-tiktok"></i></a>
            <a href="#" title="Facebook"><i class="fab fa-facebook-f"></i></a>
            <a href="#" title="Zalo"><i class="fa-solid fa-comment-dots"></i></a>
            <a href="#" title="Youtube"><i class="fab fa-youtube"></i></a>
        </div>
        <div class="top-actions-placeholder">
            <%-- Khu vực này sẽ được di chuyển xuống header-main --%>
        </div>
    </div>

    <%-- Khu vực Header chính (Sticky) --%>
    <div class="header-main">
        <div class="header-left">
            <div class="logo">
                <a href="${pageContext.request.contextPath}/index.jsp"><img src="${pageContext.request.contextPath}/images/your-logo.png" alt="Logo Bệnh viện"></a>
            </div>

            <%-- LOGIC MENU CHÍNH (Giữ nguyên) --%>
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
                            <li><a href="${pageContext.request.contextPath}/MainController?action=listNotifications"
                                   class="${fn:contains(currentAction, 'ThongBao') ? 'active' : ''}">Thông Báo</a></li>     
                            </c:when>

                        <%-- 2. NẾU LÀ BỆNH NHÂN --%>
                        <c:when test="${sessionScope.ROLE == 'BENH_NHAN'}">
                            <c:set var="isHome" value="${fn:endsWith(servletPath, '/home.jsp')}" />
                            <li><a href="${pageContext.request.contextPath}/home.jsp" 
                                   class="${isHome ? 'active' : ''}">Bảng điều khiển</a></li>
                            <li><a href="${pageContext.request.contextPath}/MainController?action=myAppointments" 
                                   class="${fn:contains(currentAction, 'LichHen') ? 'active' : ''}">Đặt lịch hẹn</a></li>
                            <li><a href="#">Lịch sử khám bệnh</a></li>
                            <li>
                                <a href="${pageContext.request.contextPath}/MainController?action=showProfile" 
                                   class="${currentAction == 'showProfile' ? 'active' : ''}">
                                    Hồ sơ của tôi
                                </a>
                            </c:when>
                            <%-- Link cho Nhân viên (BS/LT) --%>
                            <c:when test="${sessionScope.ROLE == 'BAC_SI' || sessionScope.ROLE == 'LE_TAN'}">
                                <a href="${pageContext.request.contextPath}/staff/dashboard.jsp" class="btn btn-account">
                                    <i class="fas fa-user-md"></i> ${sessionScope.USER.tenDangNhap}
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
                            (${sessionScope.ROLE})
                        </span>

                        <%-- Nút Đăng xuất --%>
                        <a href="${pageContext.request.contextPath}/MainController?action=logout" style="margin-left: 15px; color: #dc3545; font-weight: 600;">Đăng xuất</a>
                        </div>

                        <%-- Nút menu cho mobile --%>
                        <button class="mobile-nav-toggle" aria-label="Toggle menu">
                            <i class="fas fa-bars"></i>
                        </button>
                        </div>

                        </div>

                        <%-- Dòng chữ chạy (Vẫn giữ logic ở đây nhưng CSS sẽ ẩn nó) --%>
                        <div class="marquee-banner">
                            <marquee behavior="scroll" direction="left" scrollamount="5">
                                Hãy liên hệ với chúng tôi qua hotline 1900 0000 để được tư vấn và hỗ trợ một cách tốt nhất! 🏥 Chúc bạn ngày mới tốt lành!
                            </marquee>
                        </div>
                        </div>