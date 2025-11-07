<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<!DOCTYPE html>
<html lang="vi">
    <head>
        <meta charset="UTF-8">
        <meta name="viewport" content="width=device-width, initial-scale=1.0">
        <title>Tạo Lịch Hẹn Mới</title>

        <link rel="stylesheet" href="<c:url value='/css/lichHenDat-style.css'/>">
        <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.5.1/css/all.min.css">
        <link href="https://fonts.googleapis.com/css2?family=Roboto:wght@400;500;700&display=swap" rel="stylesheet">
    </head>
    <body>

        <div class="form-container">
            <div class="theme-switch-wrapper">
                <label class="theme-switch" for="theme-toggle">
                    <input type="checkbox" id="theme-toggle" />
                    <div class="slider">
                        <i class="fas fa-sun sun-icon"></i>
                        <i class="fas fa-moon moon-icon"></i>
                    </div>
                </label>
            </div>

            <h1>Tạo Lịch Hẹn Mới</h1>

            <c:if test="${not empty requestScope.ERROR_MESSAGE}">
                <div class="alert alert-danger">
                    <strong>Lỗi!</strong> ${requestScope.ERROR_MESSAGE}
                </div>
            </c:if>

            <form action="<c:url value='/MainController'/>" method="POST" class="form-grid">
                <input type="hidden" name="action" value="createAppointment">

                <div class="form-group">
                    <label for="benhNhanId">Bệnh Nhân (*)</label>
                    <select id="benhNhanId" name="benhNhanId" class="form-control" required>
                        <option value="">-- Chọn bệnh nhân --</option>
                        <c:forEach var="benhNhan" items="${danhSachBenhNhan}">
                            <option value="${benhNhan.id}">${benhNhan.hoTen} (Mã: ${benhNhan.maBenhNhan})</option>
                        </c:forEach>
                    </select>
                </div>

                <div class="form-group">
                    <label for="khoaId">Chọn Khoa (*)</label>
                    <select id="khoaId" name="khoaId" class="form-control" required>
                        <option value="">-- Vui lòng chọn khoa trước --</option>
                        <c:forEach var="khoa" items="${danhSachKhoa}">
                            <option value="${khoa.id}">${khoa.tenKhoa}</option>
                        </c:forEach>
                    </select>
                </div>

                <div class="form-group">
                    <label for="bacSiId">Bác Sĩ (*)</label>
                    <select id="bacSiId" name="bacSiId" class="form-control" required disabled>
                        <option value="">-- Vui lòng chọn khoa --</option>
                    </select>
                </div>

                <div class="form-group full-width">
                    <label for="thoiGianHen">Thời Gian Hẹn (*)</label>
                    <input type="datetime-local" id="thoiGianHen" name="thoiGianHen" class="form-control" required>
                </div>

                <div class="form-group full-width">
                    <label for="lyDoKham">Lý Do Khám</label>
                    <textarea id="lyDoKham" name="lyDoKham" class="form-control" rows="3" placeholder="Nhập triệu chứng ban đầu của bệnh nhân..."></textarea>
                </div>

                <div class="form-group full-width">
                    <label for="ghiChu">Ghi Chú (nếu có)</label>
                    <textarea id="ghiChu" name="ghiChu" class="form-control" rows="2" placeholder="Ví dụ: Bệnh nhân yêu cầu khám buổi sáng..."></textarea>
                </div>

                <div class="button-group">
                    <a href="<c:url value='/MainController?action=listAllEncounters'/>" class="btn btn-secondary">
                        <i class="fas fa-times"></i> Hủy
                    </a>
                    <button type="submit" class="btn btn-primary">
                        <i class="fas fa-save"></i> Lưu Lịch Hẹn
                    </button>
                </div>
            </form>
        </div>
        <script>
            document.addEventListener('DOMContentLoaded', function () {
                const khoaSelect = document.getElementById('khoaId');
                const bacSiSelect = document.getElementById('bacSiId');
                const baseUrl = '<c:url value="/MainController?action=getDoctorsByKhoa&khoaId="/>';

                khoaSelect.addEventListener('change', function () {
                    const khoaId = this.value;

                    if (!khoaId) {
                        resetBacSiSelect();
                        return;
                    }

                    bacSiSelect.innerHTML = '<option value="">-- Đang tải bác sĩ... --</option>';
                    bacSiSelect.disabled = true;

                    fetch(baseUrl + khoaId)
                            .then(response => {
                                if (!response.ok) {
                                    throw new Error(`HTTP ${response.status}: ${response.statusText}`);
                                }
                                return response.text(); // Dùng text() trước để kiểm tra
                            })
                            .then(text => {
                                // Kiểm tra nếu response là "false"
                                if (text === "false") {
                                    throw new Error("Server trả về false");
                                }

                                try {
                                    const data = JSON.parse(text);
                                    updateBacSiSelect(data);
                                } catch (e) {
                                    console.error('Lỗi parse JSON:', e, 'Response text:', text);
                                    throw new Error("Dữ liệu không hợp lệ từ server");
                                }
                            })
                            .catch(error => {
                                console.error('Lỗi AJAX:', error);
                                showBacSiError('Lỗi khi tải danh sách bác sĩ');
                            });
                });

                function resetBacSiSelect() {
                    bacSiSelect.innerHTML = '<option value="">-- Vui lòng chọn khoa --</option>';
                    bacSiSelect.disabled = true;
                    bacSiSelect.required = false;
                }

                function updateBacSiSelect(data) {
                    bacSiSelect.innerHTML = '';

                    if (!Array.isArray(data) || data.length === 0) {
                        bacSiSelect.innerHTML = '<option value="">-- Khoa này không có bác sĩ --</option>';
                        bacSiSelect.disabled = true;
                        bacSiSelect.required = false;
                    } else {
                        bacSiSelect.innerHTML = '<option value="">-- Chọn bác sĩ --</option>';
                        data.forEach(bacSi => {
                            const option = document.createElement('option');
                            option.value = bacSi.id || '';
                            option.textContent = `${bacSi.hoTen || 'N/A'} (${bacSi.chuyenMon || 'Chưa có chuyên môn'})`;
                            bacSiSelect.appendChild(option);
                        });
                        bacSiSelect.disabled = false;
                        bacSiSelect.required = true;
                    }
                }

                function showBacSiError(message) {
                    bacSiSelect.innerHTML = `<option value="">-- ${message} --</option>`;
                    bacSiSelect.disabled = true;
                    bacSiSelect.required = false;
                }
            });
        </script>
        <script src="<c:url value='/js/darkmode.js'/>"></script>
    </body>
</html>