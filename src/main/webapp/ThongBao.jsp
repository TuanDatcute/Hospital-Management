<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>

<html>
    <head>
        <title>Quản lý Thông báo</title>
        <style>
            /* (CSS giữ nguyên) */
            .form-section {
                border: 1px solid #ccc;
                padding: 15px;
                margin-bottom: 20px;
                background-color: #f9f9f9;
            }
            label {
                display: inline-block;
                width: 100px;
                margin-bottom: 5px;
            }
            input[type=text], textarea, select {
                width: 300px;
                margin-bottom: 10px;
            }
            textarea {
                height: 80px;
            }
        </style>
    </head>
    <body>

        <h1>Quản lý Thông báo</h1>

        <%-- Form Tạo Mới (Giữ nguyên) --%>
        <div class="form-section">
            <h2>Tạo Thông báo Mới</h2>
            <form action="MainController" method="POST" id="createForm">
                <%-- ... (Code form tạo mới giữ nguyên) ... --%>
                <input type="hidden" name="action" value="createThongBao">
                <div>
                    <label for="createTieuDe">Tiêu đề:</label>
                    <input type="text" id="createTieuDe" name="tieuDe" required><br/>
                </div>
                <div>
                    <label for="createNoiDung">Nội dung:</label>
                    <textarea id="createNoiDung" name="noiDung" required></textarea><br/>
                </div>
                <div>
                    <label>Gửi đến:</label>
                    <input type="radio" name="targetType" value="ALL" id="targetAll" checked onchange="toggleTargetValue()"> Tất cả &nbsp;
                    <input type="radio" name="targetType" value="ROLE" id="targetRole" onchange="toggleTargetValue()"> Vai trò &nbsp;
                    <input type="radio" name="targetType" value="USER" id="targetUser" onchange="toggleTargetValue()"> Tài khoản cụ thể
                    <br/>
                </div>
                <div id="targetValueDiv">
                    <label id="targetValueLabel" for="targetValueInput">Chọn:</label> 
                    <select name="targetValue" id="targetValueInput" style="display:none;">
                    </select>
                </div>
                <button type="submit">Gửi Thông báo</button>
            </form>
            <c:if test="${not empty param.createError}">
                <b style="color:red;">Lỗi tạo: <c:out value="${param.createError}"/></b>
            </c:if>
        </div>

        <hr>
        <h2>Danh sách Thông báo đã gửi</h2>

        <%-- Form Tìm kiếm (Giữ nguyên) --%>
        <form action="MainController" method="GET">
            <%-- ... (Code form tìm kiếm giữ nguyên) ... --%>
            <input type="hidden" name="action" value="listNotifications"> 
            Tìm kiếm:
            <input type="text" 
                   name="searchKeyword" 
                   value="<c:out value='${param.searchKeyword}'/>" 
                   placeholder="Nhập tiêu đề, nội dung...">
            <button type="submit">Tìm</button>
            <a href="MainController?action=listNotifications">Xóa lọc</a>
        </form>
        <br> 

        <%-- Hiển thị thông báo (Giữ nguyên) --%>
        <c:if test="${not empty param.createSuccess}"> <b style="color:green;">Tạo thông báo thành công!</b> </c:if>
        <c:if test="${not empty error}"> <b style="color:red;">Lỗi: ${error}</b> </c:if>

        <%-- ============================================= --%>
        <%--        CẬP NHẬT BẢNG DANH SÁCH (5 CỘT)       --%>
        <%-- ============================================= --%>
        <table border="1" style="width:100%;">
            <thead>
                <tr>
                    <th style="width: 20%">Tiêu đề</th>
                    <th style="width: 35%">Nội dung</th>
                    <th style="width: 20%">Gửi đến</th>           <%-- CỘT MỚI (Từ DTO) --%>
                    <th style="width: 10%">Số lượng</th>    <%-- CỘT CŨ (Giữ lại) --%>
                    <th style="width: 15%">Thời gian gửi</th>
                </tr>
            </thead>
            <tbody>
                <c:forEach var="noti" items="${notificationList}">
                    <tr>
                        <td><c:out value="${noti.tieuDe}"/></td>
                        <td><c:out value="${noti.noiDung}"/></td>

                        <%-- CỘT MỚI: Hiển thị "Gửi đến" --%>
                        <td><c:out value="${noti.nguoiNhanDisplay}"/></td> 

                        <%-- CỘT CŨ: Hiển thị "Số lượng nhận" --%>
                        <td>${noti.soLuongNguoiNhan} người</td> 

                        <td>
                            <c:out value="${noti.thoiGianGui}"/>
                        </td>
                    </tr>
                </c:forEach>
                <c:if test="${empty notificationList}">
                    <tr>
                        <%-- Cập nhật colspan="5" --%>
                        <td colspan="5" style="text-align: center;"><i>Không tìm thấy thông báo nào.</i></td>
                    </tr>
                </c:if>
            </tbody>
        </table>

        <%-- Javascript (Giữ nguyên) --%>
        <script>
            // ... (Toàn bộ code Javascript giữ nguyên) ...
            const roles = [<c:forEach var="role" items="${roles}" varStatus="loop">'${role}'<c:if test="${!loop.last}">, </c:if></c:forEach>];
                    const accounts = [<c:forEach var="acc" items="${accountList}" varStatus="loop">{ id: ${acc.id}, username: '${acc.tenDangNhap}' }<c:if test="${!loop.last}">, </c:if></c:forEach>
                    ];
            function toggleTargetValue() {
                const targetType = document.querySelector('input[name="targetType"]:checked').value;
                const targetValueDiv = document.getElementById('targetValueDiv');
                const targetValueLabel = document.getElementById('targetValueLabel');
                const targetValueInput = document.getElementById('targetValueInput');
                targetValueInput.innerHTML = '';
                if (targetType === 'ALL') {
                    targetValueDiv.style.display = 'none';
                    targetValueInput.removeAttribute('required');
                    let hiddenInput = document.getElementById('hiddenTargetValue');
                    if (!hiddenInput) {
                        hiddenInput = document.createElement('input');
                        hiddenInput.type = 'hidden';
                        hiddenInput.name = 'targetValue';
                        hiddenInput.id = 'hiddenTargetValue';
                        document.getElementById('createForm').appendChild(hiddenInput);
                    }
                    hiddenInput.value = 'ALL';
                    targetValueInput.name = '';
                } else {
                    let hiddenInput = document.getElementById('hiddenTargetValue');
                    if (hiddenInput) {
                        hiddenInput.remove();
                    }
                    targetValueInput.name = 'targetValue';
                    targetValueDiv.style.display = 'block';
                    targetValueInput.style.display = 'inline-block';
                    targetValueInput.setAttribute('required', 'required');
                    if (targetType === 'ROLE') {
                        targetValueLabel.textContent = 'Chọn vai trò:';
                        roles.forEach(role => {
                            const option = document.createElement('option');
                            option.value = role;
                            option.textContent = role;
                            targetValueInput.appendChild(option);
                        });
                    } else if (targetType === 'USER') {
                        targetValueLabel.textContent = 'Chọn tài khoản:';
                        const defaultOption = document.createElement('option');
                        defaultOption.value = '';
                        defaultOption.textContent = '-- Chọn tài khoản --';
                        targetValueInput.appendChild(defaultOption);
                        accounts.forEach(acc => {
                            const option = document.createElement('option');
                            option.value = acc.id;
                            option.textContent = acc.username + ' (ID: ' + acc.id + ')';
                            targetValueInput.appendChild(option);
                        });
                    }
                }
            }
            toggleTargetValue();
        </script>

    </body>
</html>