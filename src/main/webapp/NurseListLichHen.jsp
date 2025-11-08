<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>

<!DOCTYPE html>
<html lang="vi">
    <head>
        <meta charset="UTF-8">
        <title>Quản lý Lịch Hẹn</title>

        <link rel="stylesheet" href="<c:url value='/css/_sidebar.css?v=1.1'/>">
        <link rel="stylesheet" href="<c:url value='/css/NurseListLichHen.css?v=1.0'/>">

        <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.5.1/css/all.min.css">
        <link href="https://fonts.googleapis.com/css2?family=Roboto:wght@400;500;700&display=swap" rel="stylesheet">
        <link href="https://fonts.googleapis.com/css2?family=Be+Vietnam+Pro:wght@400;500;600;700&display=swap" rel="stylesheet">

        <script>
            (function () {
                var themeKey = 'theme-preference';
                var theme = localStorage.getItem(themeKey);
                if (theme === 'dark') {
                    document.documentElement.classList.add('dark-mode');
                }
            })();
        </script>
    </head>
    <body>

        <jsp:include page="_sidebar.jsp" />

        <div class="main-content">

            <div class="main-container">



                <h1>Quản lý Lịch Hẹn</h1>

                <c:if test="${not empty sessionScope.ERROR_MESSAGE}"><div class="alert alert-danger">${sessionScope.ERROR_MESSAGE}</div><c:remove var="ERROR_MESSAGE" scope="session" /></c:if>
                <c:if test="${not empty sessionScope.SUCCESS_MESSAGE}"><div class="alert alert-success">${sessionScope.SUCCESS_MESSAGE}</div><c:remove var="SUCCESS_MESSAGE" scope="session" /></c:if>

                    <div class="header-controls">
                        <form action="<c:url value='/MainController'/>" method="GET" class="search-form">
                        <input type="hidden" name="action" value="listLichHenNurse">
                        <i class="fas fa-search search-icon"></i>
                        <input type="text" name="keyword" class="form-control" placeholder="Tìm tên BN, Bác sĩ, lý do..." value="${requestScope.searchKeyword}">
                        <button type="submit" class="btn btn-primary"><i class="fas fa-search"></i> Tìm kiếm</button>
                        <c:if test="${not empty requestScope.searchKeyword}">
                            <a href="<c:url value='/MainController?action=listLichHenNurse'/>" class="btn btn-secondary">Xem tất cả</a>
                        </c:if>
                    </form>
                    <a href="<c:url value='/MainController?action=showCreateAppointmentForm'/>" class="btn btn-success"><i class="fas fa-plus"></i> Tạo Lịch Hẹn Mới</a>
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

                <div class="table-responsive">
                    <table class="data-table">
                        <thead>
                            <tr>
                                <th class="text-center">STT</th>
                                <th>Thời Gian Hẹn</th>
                                <th>Bệnh Nhân</th>
                                <th>Bác Sĩ</th>
                                <th>Lý Do Khám</th>
                                <th>Trạng Thái</th>                             
                            </tr>
                        </thead>
                        <tbody>
                            <c:choose>
                                <c:when test="${not empty danhSachLichHen}">
                                    <c:forEach var="lh" items="${danhSachLichHen}">
                                        <tr>
                                            <td class="text-center"><strong>${lh.stt}</strong></td>
                                            <td>${lh.thoiGianHenFormatted}</td>
                                            <td>${lh.tenBenhNhan}</td>
                                            <td>${lh.tenBacSi}</td>
                                            <td>${lh.lyDoKham}</td>

                                            <td>
                                                <c:choose>
                                                    <%-- Nếu đã hoàn thành/hủy, chỉ hiển thị text --%>
                                                    <c:when test="${lh.trangThai eq 'HOAN_THANH' or lh.trangThai eq 'DA_HUY' or lh.trangThai eq 'DA_DEN_KHAM'}">
                                                        <span class="status status-${lh.trangThai}">${lh.trangThaiDisplay}</span>
                                                    </c:when>
                                                    <%-- Ngược lại, hiển thị dropdown --%>
                                                    <c:otherwise>
                                                        <form action="<c:url value='/MainController'/>" method="POST" class="status-form">
                                                            <input type="hidden" name="action" value="updateAppointmentStatus">
                                                            <input type="hidden" name="id" value="${lh.id}">

                                                            <select name="trangThaiMoi" class="status-select status-${lh.trangThai}" 
                                                                    onchange="this.form.submit()">
                                                                <option value="CHO_XAC_NHAN" ${lh.trangThai == 'CHO_XAC_NHAN' ? 'selected' : ''}>
                                                                    Chờ xác nhận
                                                                </option>
                                                                <option value="DA_XAC_NHAN" ${lh.trangThai == 'DA_XAC_NHAN' ? 'selected' : ''}>
                                                                    Đã xác nhận
                                                                </option>
                                                                <option value="DA_HUY" ${lh.trangThai == 'DA_HUY' ? 'selected' : ''}>
                                                                    Đã hủy
                                                                </option>
                                                            </select>
                                                        </form>
                                                    </c:otherwise>
                                                </c:choose>
                                            </td>                                           
                                        </tr>
                                    </c:forEach>
                                </c:when>
                                <c:otherwise>
                                    <tr>
                                        <td colspan="7" class="no-results">Không tìm thấy lịch hẹn nào.</td>
                                    </tr>
                                </c:otherwise>
                            </c:choose>
                        </tbody>
                    </table>
                </div>
            </div> </div>
        <script src="<c:url value='/js/darkmode.js'/>"></script>
        <script src="<c:url value='/js/theme.js'/>"></script>
    </body>
</html>