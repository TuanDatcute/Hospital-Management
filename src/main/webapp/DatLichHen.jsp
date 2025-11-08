<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<html>
<head>
    <title>Đặt lịch hẹn</title>
    <style>
        body { font-family: Arial, sans-serif; margin: 20px; }
        .form-container { border: 1px solid #ccc; padding: 20px; border-radius: 8px; max-width: 500px; }
        .form-group { margin-bottom: 15px; }
        label { display: block; margin-bottom: 5px; font-weight: bold; }
        input[type="datetime-local"], select, textarea {
            width: 100%;
            padding: 8px;
            box-sizing: border-box; /* Quan trọng để padding không làm vỡ layout */
        }
        .error { color: red; font-weight: bold; }
        .load-error { color: orange; }
    </style>
</head>
<body>

    <div class="form-container">
        <h2>Đặt lịch hẹn mới</h2>

        <%-- Hiển thị lỗi nếu có (khi POST thất bại và forward về) --%>
        <c:if test="${not empty ERROR_MESSAGE}">
            <p class="error"><c:out value="${ERROR_MESSAGE}"/></p>
        </c:if>
        <c:if test="${not empty LOAD_FORM_ERROR}">
            <p class="load-error"><c:out value="${LOAD_FORM_ERROR}"/></p>
        </c:if>

        <%-- 
          Form trỏ đến MainController
          Sử dụng ${pageContext.request.contextPath} để đảm bảo URL luôn đúng
        --%>
        <form action="${pageContext.request.contextPath}/MainController" method="POST">
            <%-- Action cho MainController biết cần làm gì --%>
            <input type="hidden" name="action" value="bookAppointment">
            
            <div class="form-group">
                <label for="bacSiId">Chọn Bác sĩ:</label>
                <select name="bacSiId" id="bacSiId" required>
                    <option value="">-- Vui lòng chọn --</option>
                    <%-- 
                      Lặp qua 'bacSiList' (List<NhanVienDTO>) 
                      mà PatientLichHenController đã gửi
                    --%>
                    <c:forEach var="bacSi" items="${bacSiList}">
                        <option value="${bacSi.id}" 
                                <%-- Giữ lại bác sĩ đã chọn nếu có lỗi --%>
                                ${bacSi.id == LICHHEN_DATA.bacSiId ? 'selected' : ''}>
                            
                            <c:out value="${bacSi.hoTen}"/> 
                            <c:if test="${not empty bacSi.chuyenMon}">
                                (<c:out value="${bacSi.chuyenMon}"/>)
                            </c:if>
                        </option>
                    </c:forEach>
                </select>
            </div>
            
            <div class="form-group">
                <label for="thoiGianHen">Thời gian hẹn:</label>
                <%-- 
                  Lưu ý: Input type 'datetime-local' cần giá trị dạng yyyy-MM-ddTHH:mm
                  Việc giữ lại giá trị OffsetDateTime khá phức tạp, 
                  nên ta tạm bỏ qua việc giữ lại giá trị thời gian nếu có lỗi.
                  Nếu bạn muốn giữ lại, bạn cần chuyển đổi LICHHEN_DATA.thoiGianHen
                --%>
                <input type="datetime-local" id="thoiGianHen" name="thoiGianHen" required>
            </div>
            
            <div class="form-group">
                <label for="lyDoKham">Lý do khám:</label>
                <textarea id="lyDoKham" name="lyDoKham" rows="4" required><c:out value="${LICHHEN_DATA.lyDoKham}"/></textarea>
            </div>
            
            <button type="submit">Xác nhận Đặt lịch</button>
        </form>
        
        <hr style="margin-top: 20px;">
        <a href="${pageContext.request.contextPath}/MainController?action=myAppointments">Xem lại Lịch hẹn của tôi</a>
    </div>

</body>
</html>