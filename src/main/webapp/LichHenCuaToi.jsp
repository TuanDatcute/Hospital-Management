<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<html>
    <head>
        <title>Lịch hẹn của tôi</title>
        <%-- Đảm bảo đường dẫn CSS chính xác --%>


        <%-- CẢNH BÁO: file index.css này có thể GHI ĐÈ style của LichHenQuang.css nếu nó được tải sau --%>
        <link rel="stylesheet" href="<c:url value='/css/index.css'/>">
        <script src="<c:url value='/js/index.js'/>"></script>
        <link rel="stylesheet" href="${pageContext.request.contextPath}/css/LichHenQuang.css">

    </head>
    <body>
        <jsp:include page="/WEB-INF/headerDat.jsp" />


        <div class="page-container">
            <h2>Lịch hẹn của tôi</h2>

            <%-- Thông báo --%>
            <c:if test="${not empty param.bookSuccess}"><p class="success">Bạn đã đặt lịch hẹn thành công!</p></c:if>
            <c:if test="${not empty param.cancelSuccess}"><p class="success">Bạn đã hủy lịch hẹn thành công.</p></c:if>
            <c:if test="${not empty ERROR_MESSAGE}"><p class="error"><c:out value="${ERROR_MESSAGE}"/></p></c:if>
            <c:if test="${not empty param.error}"><p class="error"><c:out value="${param.error}"/></p></c:if>

            <%-- SỬA 1: Nút Đặt lịch hẹn mới --%>
            <p>
                <a href="${pageContext.request.contextPath}/MainController?action=showPatientBookingForm" 
                   class="btn btn-primary">
                    + Đặt lịch hẹn mới
                </a>
            </p>

            <%-- SỬA 2: Form Tìm kiếm --%>
            <form action="${pageContext.request.contextPath}/MainController" method="GET" class="search-controls">
                <input type="hidden" name="action" value="myAppointments">

                <label for="search">Tìm kiếm:</label>
                <input type="text" id="search" name="searchKeyword" value="<c:out value='${param.searchKeyword}'/>"
                       placeholder="Nhập tên bác sĩ, lý do, trạng thái...">

                <div class="button-group">
                    <button type="submit" class="btn btn-primary">Tìm</button>
                    <a href="${pageContext.request.contextPath}/MainController?action=myAppointments" class="btn-link">Xóa lọc</a>
                </div>
            </form>
            <br>

            <%-- SỬA 3: Bảng (Table) --%>
            <div class="table-responsive-wrapper">
                <table> <thead>
                        <tr>
                            <th>STT</th>
                            <th>Thời gian hẹn</th>
                            <th>Bác sĩ</th>
                            <th>Lý do khám</th>
                            <th>Trạng thái</th>
                            <th>Hành động</th> 
                        </tr>
                    </thead>
                    <tbody>
                        <c:forEach var="lich" items="${lichHenList}">
                            <tr>
                                <td>${lich.stt}</td>
                                <td>
                                    <%-- CẢI TIẾN: Format lại ngày giờ cho đẹp --%>
                                  
                                    <c:out value="${lich.thoiGianHen}"/> 
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
                                <td>
                                    <c:if test="${lich.trangThai == 'CHO_XAC_NHAN' || lich.trangThai == 'DA_XAC_NHAN'}">
                                        <form action="${pageContext.request.contextPath}/MainController" method="POST"
                                              onsubmit="return confirm('Bạn có chắc chắn muốn hủy lịch hẹn này?');">
                                            <input type="hidden" name="action" value="cancelAppointment">
                                            <input type="hidden" name="id" value="${lich.id}">

                                            <%-- SỬA 4: Nút Hủy lịch --%>
                                            <button type="submit" class="btn-danger-link">Hủy lịch</button>
                                        </form>
                                    </c:if>
                                </td>
                            </tr>
                        </c:forEach>
                    </tbody>
                </table>
            </div>
        </div>
        <%-- Footer --%>
        <footer class="main-footer">
            <div class="container">
                <jsp:include page="/WEB-INF/footer.jsp" /> 
            </div>
        </footer>

        <%-- THÊM THƯ VIỆN SWIPER.JS (Bắt buộc) --%>
        <script src="https://cdn.jsdelivr.net/npm/swiper@11/swiper-bundle.min.js"></script>

        <%-- LINK TỚI FILE JS --%>
        <script src="<c:url value='/js/index.js'/>"></script>
    </body>
</html>