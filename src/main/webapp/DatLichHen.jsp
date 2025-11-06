<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<html>
    <head>
        <title>Đặt lịch hẹn</title>
        <style>
            /* (CSS cũ giữ nguyên) */
            .form-container {
                border: 1px solid #ccc;
                padding: 20px;
                max-width: 500px;
            }
            .form-group {
                margin-bottom: 15px;
            }
            label {
                display: block;
                margin-bottom: 5px;
                font-weight: bold;
            }
            input[type="datetime-local"], select, textarea {
                width: 100%;
                padding: 8px;
                box-sizing: border-box;
            }
            .error {
                color: red;
                font-weight: bold;
            }
            .load-error {
                color: orange;
            }
        </style>
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

            <form action="${pageContext.request.contextPath}/MainController" method="POST">
                <input type="hidden" name="action" value="bookAppointment">

                <%-- === THÊM MỚI: CHỌN KHOA === --%>
                <div class="form-group">
                    <label for="khoaId">Chọn Khoa:</label>
                    <select name="khoaId" id="khoaId" required onchange="loadBacSi()">
                        <option value="">-- Vui lòng chọn khoa --</option>
                        <%-- Lặp qua 'khoaList' (List<KhoaDTO>) --%>
                        <c:forEach var="khoa" items="${khoaList}">
                            <option value="${khoa.id}">
                                <c:out value="${khoa.tenKhoa}"/>
                            </option>
                        </c:forEach>
                    </select>
                </div>
                <%-- =========================== --%>

                <div class="form-group">
                    <label for="bacSiId">Chọn Bác sĩ:</label>
                    <%-- SỬA: Thêm id "bacSiLoading" --%>
                    <select name="bacSiId" id="bacSiId" required>
                        <option value="">-- Vui lòng chọn khoa trước --</option>
                    </select>
                    <small id="bacSiLoading" style="color: blue; display: none;">Đang tải bác sĩ...</small>
                </div>

                <div class="form-group">
                    <label for="thoiGianHen">Thời gian hẹn:</label>
                    <input type="datetime-local" id="thoiGianHen" name="thoiGianHen" required 
                           value="${LICHHEN_DATA.thoiGianHen.toLocalDateTime()}"> <%-- Giữ lại giá trị --%>
                </div>

                <div class="form-group">
                    <label for="lyDoKham">Lý do khám:</label>
                    <textarea id="lyDoKham" name="lyDoKham" rows="4" required><c:out value="${LICHHEN_DATA.lyDoKham}"/></textarea>
                </div>

                <button type="submit">Xác nhận Đặt lịch</button>
            </form>

            <p><a href="${pageContext.request.contextPath}/MainController?action=myAppointments">Xem lịch hẹn của tôi</a></p>
        </div>

        <%-- === THÊM MỚI: JAVASCRIPT CHO AJAX === --%>
        <script>
        const contextPath = '${pageContext.request.contextPath}';
        const khoaSelect = document.getElementById('khoaId');
        const bacSiSelect = document.getElementById('bacSiId');
        const bacSiLoading = document.getElementById('bacSiLoading');

        async function loadBacSi() {
            const khoaId = khoaSelect.value;
            bacSiSelect.innerHTML = ''; // Xóa option cũ

            if (!khoaId) {
                bacSiSelect.innerHTML = '<option value="">-- Vui lòng chọn khoa trước --</option>';
                return;
            }

            bacSiLoading.style.display = 'block'; // Hiển thị "Đang tải..."
            bacSiSelect.disabled = true;

            try {
                // Gọi Controller (action 'getBacSiByKhoa')
                const response = await fetch(`${contextPath}/MainController?action=getBacSiByKhoa&khoaId=${khoaId}`);

                            if (!response.ok) {
                                throw new Error('Lỗi server khi tải bác sĩ.');
                            }

                            const bacSiList = await response.json(); // Nhận List<NhanVienDTO>

                            if (bacSiList.length === 0) {
                                bacSiSelect.innerHTML = '<option value="">-- Khoa này không có bác sĩ --</option>';
                            } else {
                                bacSiSelect.innerHTML = '<option value="">-- Chọn bác sĩ --</option>';
                                bacSiList.forEach(bacSi => {
                                    const option = document.createElement('option');
                                    option.value = bacSi.id;
                                    option.textContent = `${bacSi.hoTen} (${bacSi.chuyenMon || 'Chưa cập nhật'})`;
                                    bacSiSelect.appendChild(option);
                                });
                            }

                        } catch (error) {
                            console.error('Lỗi AJAX:', error);
                            bacSiSelect.innerHTML = '<option value="">-- Lỗi tải danh sách --</option>';
                        } finally {
                            bacSiLoading.style.display = 'none'; // Ẩn "Đang tải..."
                            bacSiSelect.disabled = false;
                        }
                    }
        </script>
        <%-- =================================== --%>
    </body>
</html>