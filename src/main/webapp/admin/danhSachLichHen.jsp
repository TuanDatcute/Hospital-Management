<%--
    Document   : danhSachLichHen.jsp
    Created on : Oct 29, 2025
    Author     : ADMIN
--%>

<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %> <%-- Thêm thư viện Format (fmt) để định dạng ngày giờ --%>

<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>Quản lý Lịch hẹn</title>

        <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0/css/all.min.css">
        <link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/css/style.css">
    </head>
    <body>

        <%-- Nhúng Header --%>
        <jsp:include page="/WEB-INF/header.jsp" /> 

        <div class="container page-content" style="padding-top: 30px;">

            <h2 class="section-title">Quản lý Lịch hẹn</h2>

            <%-- Hiển thị thông báo (nếu có) --%>
            <c:if test="${not empty requestScope.SUCCESS_MESSAGE}">
                <p class="success-message">${requestScope.SUCCESS_MESSAGE}</p>
            </c:if>
            <c:if test="${not empty requestScope.ERROR_MESSAGE}">
                <p class="error-message">${requestScope.ERROR_MESSAGE}</p>
            </c:if>

            <%-- Nút Thêm Mới --%>
            <a href="MainController?action=showLichHenCreateForm" class="add-new-btn">
                <i class="fas fa-calendar-plus"></i> Tạo Lịch hẹn Mới
            </a>

            <%-- TODO: Thêm bộ lọc (filter) theo ngày, bác sĩ, trạng thái ở đây --%>

            <%-- Bảng Dữ liệu --%>
            <table class="data-table">
                <thead>
                    <tr>
                        <th>STT</th>
                        <th>Thời gian hẹn</th>
                        <th>Bệnh nhân</th>
                        <th>Bác sĩ</th>
                        <th>Trạng thái</th>
                        <th style="width: 250px;">Cập nhật Trạng thái</th>
                    </tr>
                </thead>
                <tbody>
                <c:forEach var="lh" items="${requestScope.LIST_LICHHEN}">
                    <tr>
                        <td>${lh.stt}</td>
                        <td>
                            <%-- Định dạng OffsetDateTime (ví dụ: 29/10/2025 14:30) --%>
                    <fmt:parseDate value="${lh.thoiGianHen}" pattern="yyyy-MM-dd'T'HH:mmXXX" var="parsedDateTime" type="both" />
                    <fmt:formatDate value="${parsedDateTime}" pattern="dd/MM/yyyy HH:mm" />
                    </td>

                    <%-- 
                       LƯU Ý: Giả định LichHenService (hàm getAllLichHen) 
                       đã trả về DTO có tên Bác sĩ và Bệnh nhân.
                       Nếu DTO chỉ có ID, bạn cần sửa Service để nó gán tên.
                       Tạm thời, chúng ta sẽ hiển thị ID.
                    --%>
                    <td>(ID: ${lh.benhNhanId})</td> <%-- TODO: Hiển thị tên Bệnh nhân --%>
                    <td>(ID: ${lh.bacSiId})</td>   <%-- TODO: Hiển thị tên Bác sĩ --%>

                    <td>
                        <%-- Hiển thị trạng thái với màu sắc --%>
                    <c:choose>
                        <c:when test="${lh.trangThai == 'HOAN_THANH'}"><span style="color: green; font-weight: bold;">Hoàn thành</span></c:when>
                        <c:when test="${lh.trangThai == 'DA_XAC_NHAN'}"><span style="color: blue; font-weight: bold;">Đã xác nhận</span></c:when>
                        <c:when test="${lh.trangThai == 'DA_HUY'}"><span style="color: red; font-weight: bold;">Đã hủy</span></c:when>
                        <c:when test="${lh.trangThai == 'CHO_XAC_NHAN'}"><span style="color: orange; font-weight: bold;">Chờ xác nhận</span></c:when>
                        <c:otherwise><c:out value="${lh.trangThai}"/></c:otherwise>
                    </c:choose>
                    </td>

                    <td class="actions">
                        <%-- Chỉ cho phép cập nhật nếu chưa Hoàn thành hoặc Hủy --%>
                    <c:if test="${lh.trangThai != 'HOAN_THANH' && lh.trangThai != 'DA_HUY'}">
                        <form action="MainController" method="post" class="table-form">
                            <input type="hidden" name="action" value="updateLichHenStatus" />
                            <input type="hidden" name="id" value="${lh.id}" />

                            <select name="trangThai" class="form-control-sm">
                                <option value="DA_XAC_NHAN" ${lh.trangThai == 'DA_XAC_NHAN' ? 'selected' : ''}>Đã xác nhận</option>
                                <option value="DA_DEN_KHAM" ${lh.trangThai == 'DA_DEN_KHAM' ? 'selected' : ''}>Đã đến khám</option>
                                <option value="HOAN_THANH" ${lh.trangThai == 'HOAN_THANH' ? 'selected' : ''}>Hoàn thành</option>
                                <option value="DA_HUY" ${lh.trangThai == 'DA_HUY' ? 'selected' : ''}>Hủy lịch</option>
                            </select>

                            <button type="submit" class="btn-sm" title="Cập nhật">
                                <i class="fas fa-check"></i>
                            </button>
                        </form>
                    </c:if>
                    </td>
                    </tr>
                </c:forEach>

                <c:if test="${empty requestScope.LIST_LICHHEN}">
                    <tr>
                        <td colspan="6" class="empty-cell">Không có dữ liệu lịch hẹn.</td>
                    </tr>
                </c:if>
                </tbody>
            </table>
        </div>

        <jsp:include page="/WEB-INF/footer.jsp" /> 

    </body>
</html>