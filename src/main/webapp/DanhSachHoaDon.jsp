<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>

<!DOCTYPE html>
<html lang="vi">
    <head>
        <meta charset="UTF-8">
        <title>Quản lý Hóa đơn & Thanh toán</title>
        <%-- Sử dụng chung file CSS với trang danh sách phiếu khám --%>
        <link rel="stylesheet" href="<c:url value='/css/StyleChungCuaQuang.css'/>">
        <link rel="stylesheet" href="<c:url value='/css/_sidebar.css'/>">

        <%-- Thêm Font Awesome (cần cho các icon) --%>
        <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.2/css/all.min.css">
        <link href="https://fonts.googleapis.com/css2?family=Roboto:wght@400;500;700&display=swap" rel="stylesheet">    
        <%-- Các file JS (cần thiết cho dark mode và chuyển đổi view) --%>
        <script src="<c:url value='/js/darkmodeQuang.js'/>"></script>
        <script src="<c:url value='/js/danhSachPhieuKham.js'/>"></script>
        <script src="<c:url value='/js/theme.js'/>"></script>
    </head>
    <body>
        <jsp:include page="_sidebar.jsp" />
        <div class="main-content">
            <div class="container">

                <%-- Các thông báo (dùng class 'alert' từ file CSS mới) --%>
                <c:if test="${not empty param.genSuccess}">
                    <div class="alert alert-success">
                        Đã tạo hóa đơn mới thành công! (ID: ${param.genSuccess})
                    </div>
                </c:if>
                <c:if test="${not empty param.genError}">
                    <div class="alert alert-danger">
                        Lỗi tạo hóa đơn: <c:out value="${param.genError}"/>
                    </div>
                </c:if>
                <%-- Thêm các thông báo khác nếu cần, ví dụ: --%>
                <c:if test="${not empty sessionScope.SUCCESS_MESSAGE}">
                    <div class="alert alert-success">${sessionScope.SUCCESS_MESSAGE}</div>
                    <c:remove var="SUCCESS_MESSAGE" scope="session" />
                </c:if>
                <c:if test="${not empty sessionScope.ERROR_MESSAGE}">
                    <div class="alert alert-danger">${sessionScope.ERROR_MESSAGE}</div>
                    <c:remove var="ERROR_MESSAGE" scope="session" />
                </c:if>


                <h1>Quản lý Hóa đơn & Thanh toán</h1>

                <%-- Header trang với các nút chức năng --%>
                <div class="page-header">

                    <%-- Thanh tìm kiếm (đã nâng cấp) --%>
                    <div class="search-container">
                        <form action="<c:url value='/MainController'/>" method="GET" class="search-form">
                            <i class="fas fa-search search-icon-left"></i>
                            <input type="hidden" name="action" value="listInvoices">
                            <input type="text" name="searchKeyword" class="form-control" placeholder="Tìm mã HĐ, tên bệnh nhân, trạng thái..." value="${param.searchKeyword}">
                            <button type="submit" class="search-button" aria-label="Tìm kiếm">
                                <i class="fas fa-search"></i>
                            </button>
                        </form>

                        <c:if test="${not empty param.searchKeyword}">
                            <a href="<c:url value='/MainController?action=listInvoices'/>" class="btn btn-clear-search">
                                <i class="fas fa-times"></i> Xem tất cả
                            </a>
                        </c:if>
                    </div>

                    <%-- Nút chuyển đổi Grid/List --%>
                    <div class="view-toggle">
                        <button id="grid-view-btn" class="view-btn active" title="Xem dạng lưới"><i class="fas fa-th-large"></i></button>
                        <button id="list-view-btn" class="view-btn" title="Xem dạng danh sách"><i class="fas fa-bars"></i></button>
                    </div>

                    <%-- Nút chuyển đổi Dark Mode --%>
                    <div class="theme-switch-wrapper">
                        <label class="theme-switch" for="theme-toggle">
                            <input type="checkbox" id="theme-toggle" />
                            <div class="slider round">
                                <span class="sun-icon"><i class="fas fa-sun"></i></span>
                                <span class="moon-icon"><i class="fas fa-moon"></i></span>
                            </div>
                        </label>
                    </div>
                </div>

                <%-- ============================================= --%>
                <%--  DANH SÁCH HÓA ĐƠN (DẠNG CARD)               --%>
                <%-- ============================================= --%>
                <h3>Danh sách Hóa đơn (Đã lập)</h3>
                <div class="card-grid">
                    <c:choose>
                        <c:when test="${not empty invoiceList}">
                            <c:forEach var="invoice" items="${invoiceList}">
                                <div class="encounter-card"> <%-- Tái sử dụng class .encounter-card --%>
                                    <div class="card-header">
                                        <div class="card-id">#${invoice.maHoaDon}</div>
                                        <div class="card-status">
                                            <%-- Giả định trạng thái: CHUA_THANH_TOAN và DA_THANH_TOAN --%>
                                            <c:choose>
                                                <c:when test="${invoice.trangThai == 'DA_THANH_TOAN'}">
                                                    <span class="status status-HOAN_THANH">Đã thanh toán</span>
                                                </c:when>
                                                <c:otherwise>
                                                    <span class="status status-CHUA_HOAN_THANH">${invoice.trangThai}</span>
                                                </c:otherwise>
                                            </c:choose>
                                        </div>
                                    </div>
                                    <div class="card-body">
                                        <h3 class="patient-name">${invoice.hoTenBenhNhan}</h3>

                                        <%-- Hiển thị tổng tiền ở vị trí tên bác sĩ cũ --%>
                                        <p class="doctor-name" style="color: var(--danger-color); font-weight: 700; font-size: 1.2rem;">
                                            <fmt:formatNumber value="${invoice.tongTien}" pattern="#,##0 '₫'"/>
                                        </p>

                                        <div class="card-info">
                                            <div class="info-item">
                                                <span class="label">Ngày tạo:</span>
                                                <%-- Định dạng lại ngày tháng nếu cần --%>
                                                <span>${invoice.ngayTao}</span>
                                            </div>
                                        </div>
                                    </div>
                                    <div class="card-footer">
                                        <a href="<c:url value='MainController?action=viewInvoice&id=${invoice.id}'/>" class="btn btn-primary btn-details">Xem Chi Tiết</a>
                                    </div>
                                </div>
                            </c:forEach>
                        </c:when>
                        <c:otherwise>
                            <div class="no-results">
                                <c:choose>
                                    <c:when test="${not empty param.searchKeyword}">
                                        Không tìm thấy hóa đơn nào khớp với từ khóa "${param.searchKeyword}".
                                    </c:when>
                                    <c:otherwise>
                                        Chưa có hóa đơn nào trong hệ thống.
                                    </c:otherwise>
                                </c:choose>
                            </div>
                        </c:otherwise>
                    </c:choose>
                </div>

                <hr>

                <%-- ============================================= --%>
                <%--  Bảng Chờ lập hóa đơn (VẪN DÙNG TABLE)       --%>
                <%-- ============================================= --%>
                <h3>Danh sách Phiếu khám (Chờ lập hóa đơn)</h3>

                <%-- Bọc bảng trong .table-responsive để cuộn ngang --%>
                <div class="table-responsive">
                    <table>
                        <thead>
                            <tr>
                                <th>Mã Phiếu Khám</th>
                                <th>Bệnh nhân</th>
                                <th>Bác sĩ</th>
                                <th>Thời gian khám</th>
                                <th>Hành động</th>
                            </tr>
                        </thead>
                        <tbody>
                            <c:forEach var="pk" items="${pendingEncounterList}">
                                <tr>
                                    <td><c:out value="${pk.maPhieuKham}"/></td>
                                    <td><c:out value="${pk.tenBenhNhan}"/></td>
                                    <td><c:out value="${pk.tenBacSi}"/></td>
                                    <td><c:out value="${pk.thoiGianKham}"/></td>
                                    <td>
                                        <form action="MainController" method="POST">
                                            <input type="hidden" name="action" value="generateInvoice">
                                            <input type="hidden" name="phieuKhamId" value="${pk.id}">
                                            <button type="submit">Lập Hóa đơn</button>
                                        </form>
                                    </td>
                                </tr>
                            </c:forEach>
                            <c:if test="${empty pendingEncounterList}">
                                <tr>
                                    <td colspan="5">Không có phiếu khám nào chờ lập hóa đơn.</td>
                                </tr>
                            </c:if>
                        </tbody>
                    </table>
                </div>

            </div> 
        </div>
    </body>
</html>