<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<!DOCTYPE html>
<html lang="vi">
    <head>
        <meta charset="UTF-8">
        <meta name="viewport" content="width=device-width, initial-scale=1.0">
        <title>Tạo Lịch Hẹn Mới</title>

        <link rel="stylesheet" href="<c:url value='/css/style.css'/>"> <%-- Link CSS chung --%>
        <link rel="stylesheet" href="<c:url value='/css/form-style.css'/>"> <%-- Link CSS cho form --%>
        <link href="https://fonts.googleapis.com/css2?family=Roboto:wght@400;500;700&display=swap" rel="stylesheet">
    </head>
    <body>
        <div class="form-container">
            <h1>Tạo Lịch Hẹn Mới</h1>

            <%-- Hiển thị thông báo lỗi (nếu có) --%>
            <c:if test="${not empty requestScope.ERROR_MESSAGE}">
                <div class="alert alert-danger">
                    <strong>Lỗi!</strong> ${requestScope.ERROR_MESSAGE}
                </div>
            </c:if>

            <%-- Form gửi dữ liệu đến MainController --%>
            <form action="<c:url value='/MainController'/>" method="POST" class="form-grid">
                <input type="hidden" name="action" value="createAppointment">

                <%-- Dropdown chọn Bệnh Nhân --%>
                <div class="form-group">
                    <label for="benhNhanId">Bệnh Nhân (*)</label>
                    <select id="benhNhanId" name="benhNhanId" class="form-control" required>
                        <option value="">-- Chọn bệnh nhân --</option>
                        <c:forEach var="benhNhan" items="${danhSachBenhNhan}">
                            <option value="${benhNhan.id}">${benhNhan.hoTen} (Mã: ${benhNhan.maBenhNhan})</option>
                        </c:forEach>
                    </select>
                </div>

                <%-- Dropdown chọn Bác Sĩ --%>
                <div class="form-group">
                    <label for="bacSiId">Bác Sĩ (*)</label>
                    <select id="bacSiId" name="bacSiId" class="form-control" required>
                        <option value="">-- Chọn bác sĩ --</option>
                        <c:forEach var="bacSi" items="${danhSachBacSi}">
                            <option value="${bacSi.id}">${bacSi.hoTen} (${bacSi.chuyenMon})</option>
                        </c:forEach>
                    </select>
                </div>

                <%-- Chọn Ngày Giờ Hẹn --%>
                <div class="form-group full-width">
                    <label for="thoiGianHen">Thời Gian Hẹn (*)</label>
                    <input type="datetime-local" id="thoiGianHen" name="thoiGianHen" class="form-control" required>
                </div>

                <%-- Lý do khám --%>
                <div class="form-group full-width">
                    <label for="lyDoKham">Lý Do Khám</label>
                    <textarea id="lyDoKham" name="lyDoKham" class="form-control" rows="3" placeholder="Nhập triệu chứng ban đầu của bệnh nhân..."></textarea>
                </div>

                <%-- Ghi chú --%>
                <div class="form-group full-width">
                    <label for="ghiChu">Ghi Chú (nếu có)</label>
                    <textarea id="ghiChu" name="ghiChu" class="form-control" rows="2" placeholder="Ví dụ: Bệnh nhân yêu cầu khám buổi sáng..."></textarea>
                </div>

                <div class="button-group">
                    <a href="<c:url value='/MainController?action=listAllEncounters'/>" class="btn btn-secondary">Hủy</a>
                    <button type="submit" class="btn btn-primary">Lưu Lịch Hẹn</button>
                </div>
            </form>
        </div>
    </body>
</html>