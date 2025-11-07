<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<html>
    <head>
        <title>Đặt lịch hẹn</title>
        <%-- THÊM CSS MỚI --%>
        <link rel="stylesheet" href="${pageContext.request.contextPath}/css/LichHenQuang.css">
        <%-- Xóa thẻ <style> cũ --%>
    </head>
    <body>

        <div class="form-container">
            <h2>Đặt lịch hẹn mới</h2>

            <c:if test="${not empty ERROR_MESSAGE}">
                <p class="error"><c:out value="${ERROR_MESSAGE}"/></p>
            </c:if>
            <c:if test="${not empty LOAD_FORM_ERROR}">
                <p class="load-error"><c:out value="${LOAD_FORM_ERROR}"/></p>
            </c:if>

            <%-- FORM (Giữ nguyên) --%>
            <form action="${pageContext.request.contextPath}/MainController" method="POST">
                <input type="hidden" name="action" value="bookAppointment">

                <div class="form-group">
                    <label for="khoaId">Chọn Khoa:</label>
                    <%-- GỌI HÀM loadBacSi TỪ TỆP app.js --%>
                    <select name="khoaId" id="khoaId" required onchange="loadBacSi(this)">
                        <option value="">-- Vui lòng chọn khoa --</option>
                        <c:forEach var="khoa" items="${khoaList}">
                            <option value="${khoa.id}">
                                <c:out value="${khoa.tenKhoa}"/>
                            </option>
                        </c:forEach>
                    </select>
                </div>

                <div class="form-group">
                    <label for="bacSiId">Chọn Bác sĩ:</label>
                    <select name="bacSiId" id="bacSiId" required>
                        <option value="">-- Vui lòng chọn khoa trước --</option>
                    </select>
                    <small id="bacSiLoading" style="color: #007bff; display: none;">Đang tải bác sĩ...</small>
                </div>

                <div class="form-group">
                    <label for="thoiGianHen">Thời gian hẹn:</label>
                    <input type="datetime-local" id="thoiGianHen" name="thoiGianHen" required 
                           value="${LICHHEN_DATA.thoiGianHen.toLocalDateTime()}">
                </div>

                <div class="form-group">
                    <label for="lyDoKham">Lý do khám:</label>
                    <textarea id="lyDoKham" name="lyDoKham" rows="4" required><c:out value="${LICHHEN_DATA.lyDoKham}"/></textarea>
                </div>

                <button type="submit">Xác nhận Đặt lịch</button>
            </form>

            <p><a href="MainController?action=myAppointments">Xem lịch hẹn của tôi</a></p>
        </div>

        <%-- THÊM JAVASCRIPT MỚI --%>
        <script src="${pageContext.request.contextPath}/js/LichHenQuang.js"></script>
        <%-- Xóa thẻ <script> cũ --%>

    </body>
</html>