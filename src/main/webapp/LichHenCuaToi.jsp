<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>

<html>
    <head>
        <title>Lịch hẹn của tôi</title>

        <%-- THÊM CSS MỚI --%>
        <link rel="stylesheet" href="${pageContext.request.contextPath}/css/LichHenQuang.css">
        <link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/css/style.css">

        <style>
            body {
                font-family: Arial, sans-serif;
                margin: 20px;
            }
            table {
                width: 100%;
                border-collapse: collapse;
                margin-top: 15px;
            }
            th, td {
                border: 1px solid #ddd;
                padding: 8px;
                text-align: left;
            }
            th {
                background-color: #f2f2f2;
            }
            .success {
                color: green;
                font-weight: bold;
            }
            .error {
                color: red;
                font-weight: bold;
            }
            .status-CHO_XAC_NHAN {
                color: orange;
                font-weight: bold;
            }
            .status-DA_XAC_NHAN {
                color: blue;
                font-weight: bold;
            }
            .status-DA_HUY {
                color: red;
                text-decoration: line-through;
            }
            .status-HOAN_THANH {
                color: green;
            }
        </style>
    </head>
    <body>
        <jsp:include page="/WEB-INF/header.jsp" /> 
        <h2>Lịch hẹn của tôi</h2>

        <%-- Hiển thị thông báo (khi đặt lịch thành công hoặc khi tải trang bị lỗi) --%>
        <c:if test="${not empty param.bookSuccess}">
            <p class="success">Bạn đã đặt lịch hẹn thành công!</p>
        </c:if>
        <c:if test="${not empty ERROR_MESSAGE}">
            <p class="error"><c:out value="${ERROR_MESSAGE}"/></p>
        </c:if>

        <p>
            <a href="${pageContext.request.contextPath}/MainController?action=showPatientBookingForm">
                <button type="button">+ Đặt lịch hẹn mới</button>
            </a>
        </p>

        <table border="1">
            <thead>
                <tr>
                    <th>STT</th>
                    <th>Thời gian hẹn</th>
                    <th>Bác sĩ</th>
                    <th>Lý do khám</th>
                    <th>Trạng thái</th>
                </tr>
            </thead>
            <tbody>
                <c:forEach var="lich" items="${lichHenList}">
                    <tr>
                        <td>${lich.stt}</td>
                        <td>
                            <%-- 
                              Định dạng OffsetDateTime.
                              Giả sử múi giờ của bạn là +7 (Việt Nam) 
                            --%>
                            <c:out value="${lich.thoiGianHen.toInstant()}"/>
                        </td>
                        <td>
                            <%-- Hiển thị tên Bác sĩ (LichHenDTO đã được làm phẳng) --%>
                            <c:out value="${lich.tenBacSi}"/>
                        </td>
                        <td><c:out value="${lich.lyDoKham}"/></td>
                        <td>
                            <%-- Thêm style cho trạng thái --%>
                            <span class="status-${lich.trangThai}">
                                <c:out value="${lich.trangThai}"/>
                            </span>
                        </td>
                    </tr>
                </c:forEach>
                <c:if test="${empty lichHenList}">
                    <tr>
                        <td colspan="5" style="text-align: center;"><i>Bạn chưa có lịch hẹn nào.</i></td>
                    </tr>
                </c:if>
            </tbody>
        </table>
        <jsp:include page="/WEB-INF/footer.jsp" /> 
    </body>
</html>