<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %> <%-- Thêm thư viện format ngày tháng --%>

<!DOCTYPE html>
<html lang="vi">
    <head>
        <meta charset="UTF-8">
        <title>Tạo Phiếu Khám Bệnh Mới</title>
        <style>
            /* (Giữ nguyên phần CSS của bạn) */
            body {
                font-family: Arial, sans-serif;
                background-color: #f4f4f9;
                display: flex;
                justify-content: center;
                align-items: center;
                padding: 20px;
            }
            .container {
                background-color: #ffffff;
                padding: 25px 40px;
                border-radius: 8px;
                box-shadow: 0 4px 8px rgba(0, 0, 0, 0.1);
                width: 100%;
                max-width: 800px;
            }
            h1 {
                text-align: center;
                color: #333;
                margin-bottom: 20px;
            }
            .form-grid {
                display: grid;
                grid-template-columns: 1fr 1fr;
                gap: 20px;
            }
            .form-group {
                display: flex;
                flex-direction: column;
            }
            .full-width {
                grid-column: 1 / -1;
            }
            label {
                margin-bottom: 5px;
                font-weight: bold;
                color: #555;
            }
            input[type="text"], input[type="number"], input[type="datetime-local"], select, textarea {
                padding: 10px;
                border: 1px solid #ccc;
                border-radius: 4px;
                font-size: 16px;
                width: 100%;
                box-sizing: border-box;
            }
            textarea {
                resize: vertical;
                min-height: 80px;
            }
            .button-group {
                grid-column: 1 / -1;
                display: flex;
                justify-content: flex-end;
                gap: 10px;
                margin-top: 20px;
            }
            button {
                padding: 10px 20px;
                border: none;
                border-radius: 4px;
                font-size: 16px;
                cursor: pointer;
            }
            .btn-submit {
                background-color: #007bff;
                color: white;
            }
            .btn-reset {
                background-color: #6c757d;
                color: white;
            }
            .error-box {
                padding: 15px;
                background-color: #f8d7da;
                color: #721c24;
                border: 1px solid #f5c6cb;
                border-radius: 5px;
                margin-bottom: 20px;
                grid-column: 1 / -1;
            }
        </style>
    </head>
    <body>

        <div class="container">
            <h1>Phiếu Khám Bệnh</h1>

            <%-- Hiển thị thông báo lỗi (nếu có) ngay trên đầu form --%>
            <c:if test="${not empty requestScope.ERROR_MESSAGE}">
                <div class="error-box">
                    <strong>Lỗi!</strong> ${requestScope.ERROR_MESSAGE}
                </div>
            </c:if>

            <form action="MainController" method="POST">
                <input type="hidden" name="action" value="createEncounter">

                <div class="form-grid">
                    <div class="form-group">
                        <label for="maPhieuKham">Mã Phiếu Khám</label>
                        <%-- Giữ lại giá trị đã nhập nếu có lỗi --%>
                        <input type="text" id="maPhieuKham" name="maPhieuKham" value="${ENCOUNTER_DATA.maPhieuKham}" required>
                    </div>

                    <div class="form-group">
                        <label for="thoiGianKham">Thời Gian Khám</label>
                        <%-- Định dạng lại ngày tháng để hiển thị đúng --%>
                        <fmt:formatDate value="${ENCOUNTER_DATA.thoiGianKham}" pattern="yyyy-MM-dd'T'HH:mm" var="formattedThoiGianKham" />
                        <input type="datetime-local" id="thoiGianKham" name="thoiGianKham" value="${formattedThoiGianKham}" required>
                    </div>

                    <%-- ✨ NÂNG CẤP: Dùng dropdown thay vì nhập ID --%>
                    <div class="form-group">
                        <label for="benhNhanId">Bệnh Nhân</label>
                        <select id="benhNhanId" name="benhNhanId" required>
                            <option value="">-- Chọn bệnh nhân --</option>
                            <c:forEach var="benhNhan" items="${danhSachBenhNhan}">
                                <option value="${benhNhan.id}" ${ENCOUNTER_DATA.benhNhanId == benhNhan.id ? 'selected' : ''}>
                                    ${benhNhan.hoTen} (Mã: ${benhNhan.maBenhNhan})
                                </option>
                            </c:forEach>
                        </select>
                    </div>

                    <%-- ✨ NÂNG CẤP & SỬA LỖI: Dùng dropdown và name="bacSiId" --%>
                    <div class="form-group">
                        <label for="bacSiId">Bác sĩ</label>
                        <select id="bacSiId" name="bacSiId" required>
                            <option value="">-- Chọn bác sĩ --</option>
                            <c:forEach var="bacSi" items="${danhSachBacSi}">
                                <option value="${bacSi.id}" ${ENCOUNTER_DATA.bacSiId == bacSi.id ? 'selected' : ''}>
                                    ${bacSi.hoTen} (${bacSi.chuyenMon})
                                </option>
                            </c:forEach>
                        </select>
                    </div>

                    <%-- Giữ lại giá trị đã nhập cho các textarea và input còn lại --%>
                    <div class="form-group full-width">
                        <label for="trieuChung">Triệu Chứng</label>
                        <textarea id="trieuChung" name="trieuChung" rows="3">${ENCOUNTER_DATA.trieuChung}</textarea>
                    </div>

                    <div class="form-group">
                        <label for="nhietDo">Nhiệt Độ (°C)</label>
                        <input type="number" id="nhietDo" name="nhietDo" step="0.1" value="${ENCOUNTER_DATA.nhietDo}" placeholder="Ví dụ: 37.5">
                    </div>

                    <div class="form-group">
                        <label for="huyetAp">Huyết Áp (mmHg)</label>
                        <input type="text" id="huyetAp" name="huyetAp" value="${ENCOUNTER_DATA.huyetAp}" placeholder="Ví dụ: 120/80">
                    </div>

                    <div class="form-group">
                        <label for="nhipTim">Nhịp Tim (lần/phút)</label>
                        <input type="number" id="nhipTim" name="nhipTim" value="${ENCOUNTER_DATA.nhipTim}" placeholder="Ví dụ: 80">
                    </div>

                    <div class="form-group">
                        <label for="nhipTho">Nhịp Thở (lần/phút)</label>
                        <input type="number" id="nhipTho" name="nhipTho" value="${ENCOUNTER_DATA.nhipTho}" placeholder="Ví dụ: 18">
                    </div>

                    <div class="form-group full-width">
                        <label for="chanDoan">Chẩn Đoán</label>
                        <textarea id="chanDoan" name="chanDoan" rows="4">${ENCOUNTER_DATA.chanDoan}</textarea>
                    </div>

                    <div class="form-group full-width">
                        <label for="ketLuan">Kết Luận & Dặn Dò</label>
                        <textarea id="ketLuan" name="ketLuan" rows="4">${ENCOUNTER_DATA.ketLuan}</textarea>
                    </div>

                    <div class="form-group">
                        <label for="ngayTaiKham">Ngày Tái Khám</label>
                        <fmt:formatDate value="${ENCOUNTER_DATA.ngayTaiKham}" pattern="yyyy-MM-dd'T'HH:mm" var="formattedNgayTaiKham" />
                        <input type="datetime-local" id="ngayTaiKham" name="ngayTaiKham" value="${formattedNgayTaiKham}">
                    </div>

                    <div class="form-group">
                        <label for="lichHenId">ID Lịch Hẹn (nếu có)</label>
                        <input type="number" id="lichHenId" name="lichHenId" value="${ENCOUNTER_DATA.lichHenId}">
                    </div>

                    <div class="button-group">
                        <button type="reset" class="btn-reset">Làm lại</button>
                        <button type="submit" class="btn-submit">Lưu Phiếu Khám</button>
                    </div>
                </div>
            </form>
        </div>
    </body>
</html>