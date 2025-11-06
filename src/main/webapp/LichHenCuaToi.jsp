<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>

<html>
    <head>
        <title>Lịch hẹn của tôi</title>
        <style>
            /* (CSS cũ giữ nguyên) */
            .delete-button {
                background: none;
                border: none;
                color: red;
                text-decoration: underline;
                cursor: pointer;
                padding: 0;
                font-family: inherit;
                font-size: 0.9em;
            }
        </style>
    </head>
    <body>
        <h2>Lịch hẹn của tôi</h2>

        <%-- Thông báo (Thêm 'cancelSuccess') --%>
        <c:if test="${not empty param.bookSuccess}"><p class="success">Bạn đã đặt lịch hẹn thành công!</p></c:if>
        <c:if test="${not empty param.cancelSuccess}"><p class="success">Bạn đã hủy lịch hẹn thành công.</p></c:if>
        <c:if test="${not empty ERROR_MESSAGE}"><p class="error"><c:out value="${ERROR_MESSAGE}"/></p></c:if>
        <c:if test="${not empty param.error}"><p class="error"><c:out value="${param.error}"/></p></c:if>

            <p>
                <a href="${pageContext.request.contextPath}/MainController?action=showPatientBookingForm">
                <button type="button">+ Đặt lịch hẹn mới</button>
            </a>
        </p>

        <%-- === THÊM MỚI: Form Tìm kiếm === --%>
        <form action="${pageContext.request.contextPath}/MainController" method="GET">
            <input type="hidden" name="action" value="myAppointments">
            Tìm kiếm:
            <input type="text" name="searchKeyword" value="<c:out value='${param.searchKeyword}'/>" 
                   placeholder="Nhập tên bác sĩ, lý do, trạng thái...">
            <button type="submit">Tìm</button>
            <a href="${pageContext.request.contextPath}/MainController?action=myAppointments">Xóa lọc</a>
        </form>
        <br>
        <%-- ================================ --%>

        <table border="1" style="width:100%">
            <thead>
                <tr>
                    <th>STT</th>
                    <th>Thời gian hẹn</th>
                    <th>Bác sĩ</th>
                    <th>Lý do khám</th>
                    <th>Trạng thái</th>
                    <th>Hành động</th> <%-- THÊM CỘT MỚI --%>
                </tr>
            </thead>
            <tbody>
                <c:forEach var="lich" items="${lichHenList}">
                    <tr>
                        <td>${lich.stt}</td>
                        <td>
                            <c:out value="${lich.thoiGianHen.toInstant()}"/>
                        </td>
                        <td>
                            <c:out value="${lich.tenBacSi}"/>
                        </td>
                        <td><c:out value="${lich.lyDoKham}"/></td>
                        <td>
                            <span class="status-${lich.trangThai}">
                                <c:out value="${lich.trangThai}"/>
                            </span>
                        </td>
                        <%-- === THÊM MỚI: Nút Hủy === --%>
                        <td>
                            <%-- Chỉ cho hủy nếu đang chờ hoặc đã xác nhận --%>
                            <c:if test="${lich.trangThai == 'CHO_XAC_NHAN' || lich.trangThai == 'DA_XAC_NHAN'}">
                                <form action="${pageContext.request.contextPath}/MainController" method="POST"
                                      onsubmit="return confirm('Bạn có chắc chắn muốn hủy lịch hẹn này?');">
                                    <input type="hidden" name="action" value="cancelAppointment">
                                    <input type="hidden" name="id" value="${lich.id}">
                                    <button type="submit" class="delete-button">Hủy lịch</button>
                                </form>
                            </c:if>
                        </td>
                        <%-- ======================== --%>
                    </tr>
                </c:forEach>
            </tbody>
        </table>

    </body>
</html>