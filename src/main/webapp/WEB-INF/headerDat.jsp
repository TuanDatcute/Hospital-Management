<%-- /WEB-INF/headerDat.jsp (Đã cập nhật) --%>
<%@ page pageEncoding="UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>

<div class="header-wrapper" id="headerWrapper">

    <%-- Thanh Header trên cùng (Ẩn) --%>
    <div class="header-top">
        <%-- ... (Nội dung không đổi) ... --%>
    </div>

    <%-- Khu vực Header chính (Sticky) --%>
    <div class="header-main">
        <div class="header-left">
            <div class="logo">
                <a href="${pageContext.request.contextPath}/index.jsp"><img src="${pageContext.request.contextPath}/images/logo.png" alt="Logo Bệnh viện"></a>
            </div>

            <%-- NAV CHO DESKTOP (Giữ nguyên) --%>
            <nav class="desktop-nav">
                <ul>
                    <%-- Lấy action hiện tại (param.action) và đường dẫn JSP (servletPath) --%>
                    <c:set var="currentAction" value="${param.action}" />
                    <c:set var="servletPath" value="${pageContext.request.servletPath}" />

                    <c:choose>
                        <%-- 1. NẾU LÀ ADMIN --%>
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
                            <%-- ✨ SỬA LỖI: Đổi 'home.jsp' thành 'index.jsp' --%>
                            <c:set var="isHome" value="${fn:endsWith(servletPath, '/index.jsp')}" /> 
                            <li><a href="${pageContext.request.contextPath}/index.jsp" 
                                   class="${isHome ? 'active' : ''}">Trang chủ</a></li>
                            <li><a href="${pageContext.request.contextPath}/MainController?action=myAppointments" 
                                   class="${fn:contains(currentAction, 'LichHen') ? 'active' : ''}">Đặt lịch hẹn</a></li>

                            <%-- ✨ ĐÃ SỬA LỖI: Thêm class="active" --%>
                            <li><a href="${pageContext.request.contextPath}/MainController?action=viewMyHistory" 
                                   class="${currentAction == 'viewMyHistory' ? 'active' : ''}">Lịch sử khám bệnh</a></li>

                            <li>
                                <a href="${pageContext.request.contextPath}/MainController?action=showProfile" 
                                   class="${currentAction == 'showProfile' ? 'active' : ''}">
                                    Hồ sơ của tôi
                                </a>
                            </li>
                            <li><a href="${pageContext.request.contextPath}/MainController?action=viewMyNotifications"
                                   class="${fn:contains(currentAction, 'ThongBao') ? 'active' : ''}">Thông Báo</a>
                            </li>                 
                        </c:when>

                        <%-- 3. NẾU LÀ BÁC SĨ / LỄ TÂN --%>
                        <c:when test="${sessionScope.ROLE == 'BAC_SI' || sessionScope.ROLE == 'LE_TAN'}">
                            <c:set var="isStaffDashboard" value="${fn:endsWith(servletPath, '/dashboard.jsp')}" />
                            <li><a href="${pageContext.request.contextPath}/staff/dashboard.jsp" 
                                   class="${isStaffDashboard ? 'active' : ''}">Bảng điều khiển</a></li>
                            <li><a href="${pageContext.request.contextPath}/MainController?action=listLichHen" 
                                   class="${fn:contains(currentAction, 'LichHen') ? 'active' : ''}">QL Lịch hẹn</a></li>
                            <li><a href="${pageContext.request.contextPath}/MainController?action=showCreateForm" 
                                   class="${fn:contains(currentAction, 'CreateForm') ? 'active' : ''}">Tạo Phiếu khám</a></li>
                            <li><a href="${pageContext.request.contextPath}/MainController?action=viewMyNotifications"
                                   class="${fn:contains(currentAction, 'ThongBao') ? 'active' : ''}">Thông Báo</a></li>     
                            </c:when>

                        <%-- 4. NẾU CHƯA ĐĂNG NHẬP (Khách) --%>
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

        <%-- Khu vực Tài khoản và Nút Mobile --%>
        <div class="header-right">
            <div class="top-actions">
                <%-- ... (Logic Tài khoản/Đăng nhập giữ nguyên) ... --%>
                <c:if test="${empty sessionScope.USER}">
                    <a href="${pageContext.request.contextPath}/login.jsp" class="btn btn-account">
                        <i class="fas fa-user"></i> Tài khoản
                    </a>
                </c:if>
                <c:if test="${not empty sessionScope.USER}">
                    <c:choose>
                        <c:when test="${sessionScope.ROLE == 'QUAN_TRI'}">
                            <a href="${pageContext.request.contextPath}/admin/dashboard.jsp" class="btn btn-account">
                                <i class="fas fa-user-shield"></i> ${sessionScope.USER.tenDangNhap}
                            </a>
                        </c:when>
                        <c:when test="${sessionScope.ROLE == 'BAC_SI' || sessionScope.ROLE == 'LE_TAN'}">
                            <a href="${pageContext.request.contextPath}/staff/dashboard.jsp" class="btn btn-account">
                                <i class="fas fa-user-md"></i> ${sessionScope.USER.tenDangNhap}
                            </a>
                        </c:when>
                        <c:otherwise>
                            <a href="${pageContext.request.contextPath}/home.jsp" class="btn btn-account">
                                <i class="fas fa-user"></i> ${sessionScope.USER.tenDangNhap}
                            </a>
                        </c:otherwise>
                    </c:choose>
                    <span style="margin-left: 15px; color: #0056b3; font-weight: 600; font-size: 0.9em;">
                        <c:choose>
                            <c:when test="${sessionScope.ROLE == 'BENH_NHAN'}">
                                Bệnh Nhân
                            </c:when>
                            <c:when test="${not empty sessionScope.ROLE}">
                                ${fn:replace(sessionScope.ROLE, '_', ' ')}
                            </c:when>
                        </c:choose>
                    </span>
                    <a href="${pageContext.request.contextPath}/MainController?action=logout" style="margin-left: 15px; color: #dc3545; font-weight: 600;">Đăng xuất</a>
                </c:if>
            </div>

            <%-- NÚT BẤM 3 GẠCH (Giữ nguyên) --%>
            <button class="mobile-nav-toggle" id="mobileNavToggle" aria-label="Toggle menu">
                <i class="fas fa-bars"></i>
            </button>
        </div>
    </div>

    <%-- ✨ HTML CHO MENU MOBILE (Thêm mới) --%>
    <div class="mobile-nav-overlay" id="mobileNavOverlay">
        <button class="mobile-nav-close" id="mobileNavClose" aria-label="Close menu">
            <i class="fas fa-times"></i>
        </button>
        <nav class="mobile-nav">
            <ul>
                <%-- SAO CHÉP LOGIC TỪ NAV DESKTOP VÀO ĐÂY --%>
                <c:set var="currentAction" value="${param.action}" />
                <c:set var="servletPath" value="${pageContext.request.servletPath}" />

                <c:choose>
                    <%-- 1. NẾU LÀ ADMIN --%>
                    <c:when test="${sessionScope.ROLE == 'QUAN_TRI'}">
                        <c:set var="isDashboard" value="${fn:endsWith(servletPath, '/dashboard.jsp')}" />
                        <li><a href="${pageContext.request.contextPath}/admin/dashboard.jsp" class="${isDashboard ? 'active' : ''}">Bảng điều khiển</a></li>
                        <li><a href="${pageContext.request.contextPath}/MainController?action=listKhoa" class="${fn:contains(currentAction, 'Khoa') ? 'active' : ''}">Quản lý Khoa</a></li>
                        <li><a href="${pageContext.request.contextPath}/MainController?action=listNhanVien" class="${fn:contains(currentAction, 'NhanVien') ? 'active' : ''}">Nhân viên</a></li>
                        <li><a href="${pageContext.request.contextPath}/MainController?action=listBenhNhan" class="${fn:contains(currentAction, 'BenhNhan') ? 'active' : ''}">Bệnh nhân</a></li>
                        <li><a href="${pageContext.request.contextPath}/MainController?action=listUsers" class="${fn:contains(currentAction, 'User') ? 'active' : ''}">Tài khoản</a></li>
                        <li><a href="${pageContext.request.contextPath}/MainController?action=listLichHen" class="${fn:contains(currentAction, 'LichHen') ? 'active' : ''}">Lịch hẹn</a></li>
                        <li><a href="${pageContext.request.contextPath}/MainController?action=listNotifications" class="${fn:contains(currentAction, 'ThongBao') ? 'active' : ''}">Thông Báo</a></li>     
                        </c:when>


                    <%-- 2. NẾU LÀ BỆNH NHÂN --%>
                    <%-- 2. NẾU LÀ BỆNH NHÂN --%>
                    <c:when test="${sessionScope.ROLE == 'BENH_NHAN'}">
                        <%-- ✨ SỬA LỖI: Đổi 'home.jsp' thành 'index.jsp' --%>
                        <c:set var="isHome" value="${fn:endsWith(servletPath, '/index.jsp')}" />
                        <li><a href="${pageContext.request.contextPath}/index.jsp" class="${isHome ? 'active' : ''}">Trang chủ</a></li>
                        <li><a href="${pageContext.request.contextPath}/MainController?action=showLichHenCreateForm" class="${fn:contains(currentAction, 'LichHen') ? 'active' : ''}">Đặt lịch hẹn</a></li>
                        <li><a href="${pageContext.request.contextPath}/MainController?action=viewMyHistory" 
                               class="${currentAction == 'viewMyHistory' ? 'active' : ''}">Lịch sử khám bệnh</a></li>

                        <li><a href="${pageContext.request.contextPath}/MainController?action=showProfile" class="${currentAction == 'showProfile' ? 'active' : ''}">Hồ sơ của tôi</a></li>
                        <li><a href="${pageContext.request.contextPath}/MainController?action=viewMyNotifications" class="${fn:contains(currentAction, 'ThongBao') ? 'active' : ''}">Thông Báo</a></li>                 
                        </c:when>

                    <%-- 3. NẾU LÀ BÁC SĨ / LỄ TÂN --%>
                    <c:when test="${sessionScope.ROLE == 'BAC_SI' || sessionScope.ROLE == 'LE_TAN'}">
                        <c:set var="isStaffDashboard" value="${fn:endsWith(servletPath, '/dashboard.jsp')}" />
                        <li><a href="${pageContext.request.contextPath}/staff/dashboard.jsp" class="${isStaffDashboard ? 'active' : ''}">Bảng điều khiển</a></li>
                        <li><a href="${pageContext.request.contextPath}/MainController?action=listLichHen" class="${fn:contains(currentAction, 'LichHen') ? 'active' : ''}">QL Lịch hẹn</a></li>
                        <li><a href="${pageContext.request.contextPath}/MainController?action=showCreateForm" class="${fn:contains(currentAction, 'CreateForm') ? 'active' : ''}">Tạo Phiếu khám</a></li>
                        <li><a href="${pageContext.request.contextPath}/MainController?action=viewMyNotifications" class="${fn:contains(currentAction, 'ThongBao') ? 'active' : ''}">Thông Báo</a></li>     
                        </c:when>

                    <%-- 4. NẾU CHƯA ĐĂNG NHẬP (Khách) --%>
                    <c:otherwise>
                        <c:set var="isIndex" value="${fn:endsWith(servletPath, '/index.jsp')}" />
                        <li><a href="${pageContext.request.contextPath}/index.jsp" class="${isIndex ? 'active' : ''}">Cơ sở y tế</a></li>
                        <li><a href="#">Dịch vụ y tế</a></li>
                        <li><a href="#">Khám sức khỏe DN</a></li>
                        <li><a href="#">Tin tức</a></li>
                        <li><a href="#">Hướng dẫn</a></li>
                        <li><a href="#">Liên hệ</a></li>
                        </c:otherwise>
                    </c:choose>
            </ul>
        </nav>
    </div>
</div>