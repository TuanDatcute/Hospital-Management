<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<html lang="vi">
    <head>
        <meta charset="UTF-8">
        <title>Tạo Phiếu Khám Bệnh Mới</title>
        <link rel="stylesheet" href="<c:url value='/css/pkb-style.css'/>">    </head>
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
                        <input type="text" id="maPhieuKham" name="maPhieuKham" value="${ENCOUNTER_DATA.maPhieuKham}" required>
                    </div>

                    <div class="form-group">
                        <label for="thoiGianKham">Thời Gian Khám</label>
                        <input type="datetime-local" id="thoiGianKham" name="thoiGianKham" value="${ENCOUNTER_DATA.formattedThoiGianKham}" required>
                    </div>

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
                        <input type="datetime-local" id="ngayTaiKham" name="ngayTaiKham" value="${ENCOUNTER_DATA.formattedNgayTaiKham}">
                    </div>


                    <div class="form-group">
                        <label for="lichHenId">ID Lịch Hẹn (nếu có)</label>
                        <select id="lichHenId" name="lichHenId" >
                            <option value="">-- Chọn --</option>
                            <c:forEach var="lichHen" items="${danhSachLichHen}">
                                <option value="${lichHen.id}" ${ENCOUNTER_DATA.lichHenId == lichHen.id ? 'selected' : ''}>
                                    ${lichHen.stt} (${lichHen.lyDoKham})
                                </option>
                            </c:forEach>
                        </select>         
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