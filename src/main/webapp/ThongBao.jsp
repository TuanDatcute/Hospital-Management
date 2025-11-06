<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>

<html>
<head>
    <title>Quản lý Thông báo</title>
    <style>
        .delete-button { background: none; border: none; color: red; text-decoration: underline; cursor: pointer; padding: 0; font-family: inherit; font-size: inherit; }
        .form-section { border: 1px solid #ccc; padding: 15px; margin-bottom: 20px; background-color: #f9f9f9; }
        label { display: inline-block; width: 100px; margin-bottom: 5px;}
        input[type=text], textarea, select { width: 300px; margin-bottom: 10px; }
        textarea { height: 80px; }
    </style>
</head>
<body>

    <h1>Quản lý Thông báo</h1>

    <%-- ============================================= --%>
    <%--        THÊM MỚI / CẬP NHẬT FORM             --%>
    <%-- ============================================= --%>
    <div class="form-section">
        <c:choose>
            <%-- FORM CẬP NHẬT --%>
            <c:when test="${not empty notificationToUpdate}">
                <h2>Cập nhật Thông báo</h2>
                <form action="MainController" method="POST">
                    <input type="hidden" name="action" value="updateThongBao">
                    <input type="hidden" name="id" value="${notificationToUpdate.id}">
                    
                    <div>
                        <label for="updateTieuDe">Tiêu đề:</label>
                        <input type="text" id="updateTieuDe" name="tieuDe" value="<c:out value='${notificationToUpdate.tieuDe}'/>" required><br/>
                    </div>
                    <div>
                        <label for="updateNoiDung">Nội dung:</label>
                        <textarea id="updateNoiDung" name="noiDung" required><c:out value='${notificationToUpdate.noiDung}'/></textarea><br/>
                    </div>
                    <%-- Không cho sửa người nhận khi cập nhật --%>
                    <div>
                         <label>Người nhận:</label>
                         <span>Tài khoản ID: ${notificationToUpdate.taiKhoanId}</span>
                    </div>

                    <button type="submit">Lưu thay đổi</button>
                    <a href="MainController?action=listNotifications">Hủy</a>
                </form>
                <%-- Hiển thị lỗi nếu update thất bại --%>
                <c:if test="${not empty param.updateError && param.id == notificationToUpdate.id}">
                     <b style="color:red;">Lỗi cập nhật: <c:out value="${param.updateError}"/></b>
                </c:if>
            </c:when>
            
            <%-- FORM THÊM MỚI --%>
            <c:otherwise>
                <h2>Tạo Thông báo Mới</h2>
                <form action="MainController" method="POST" id="createForm">
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

                    <%-- Input thay đổi tùy theo lựa chọn trên --%>
                    <div id="targetValueDiv">
                        <%-- Mặc định ẩn, sẽ hiện khi chọn Vai trò hoặc User --%>
                        <label id="targetValueLabel" for="targetValueInput">Chọn:</label> 
                        <select name="targetValue" id="targetValueInput" style="display:none;">
                            <%-- Options sẽ được thêm bằng Javascript --%>
                        </select>
                    </div>

                    <button type="submit">Gửi Thông báo</button>
                </form>
                 <%-- Hiển thị lỗi nếu create thất bại --%>
                <c:if test="${not empty param.createError}">
                     <b style="color:red;">Lỗi tạo: <c:out value="${param.createError}"/></b>
                </c:if>
            </c:otherwise>
        </c:choose>
    </div>

    <hr>
    <h2>Danh sách Thông báo đang hoạt động</h2>

    <%-- Form Tìm kiếm --%>
    <form action="MainController" method="GET">
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

    <%-- Hiển thị thông báo thành công --%>
    <c:if test="${not empty param.createSuccess}"> <b style="color:green;">Tạo thông báo thành công!</b> </c:if>
    <c:if test="${not empty param.updateSuccess}"> <b style="color:blue;">Cập nhật thông báo thành công!</b> </c:if>
    <c:if test="${not empty param.deleteSuccess}"> <b style="color:green;">Xóa thông báo thành công!</b> </c:if>
    <c:if test="${not empty param.deleteError}"> <b style="color:red;">Lỗi xóa: <c:out value="${param.deleteError}"/></b> </c:if>
    <c:if test="${not empty error}"> <b style="color:red;">Lỗi: ${error}</b> </c:if>

    <%-- Bảng Danh sách --%>
    <table border="1" style="width:100%;">
        <thead>
            <tr>
                <th style="width: 5%">ID</th>
                <th style="width: 20%">Tiêu đề</th>
                <th style="width: 40%">Nội dung</th>
                <th style="width: 10%">Người nhận (ID)</th> <%-- Hiển thị ID người nhận --%>
                <th style="width: 15%">Thời gian gửi</th>
                <th style="width: 10%">Hành động</th>
            </tr>
        </thead>
        <tbody>
            <c:forEach var="noti" items="${notificationList}">
                <tr>
                    <td>${noti.id}</td>
                    <td><c:out value="${noti.tieuDe}"/></td>
                    <td><c:out value="${noti.noiDung}"/></td>
                    <td>${noti.taiKhoanId}</td> 
                    <td><c:out value="${noti.thoiGianGui}"/></td> 
                    <td>
                        <%-- Link Sửa --%>
                        <a href="MainController?action=getThongBaoForUpdate&id=${noti.id}" style="margin-right: 10px; display: inline-block;">Sửa</a>
                        
                        <%-- Form Xóa mềm --%>
                        <form action="MainController" method="POST" style="margin:0; display: inline-block;" 
                              onsubmit="return confirm('Bạn có chắc chắn muốn xóa thông báo này?');">
                            <input type="hidden" name="action" value="deleteThongBao">
                            <input type="hidden" name="id" value="${noti.id}">
                            <button type="submit" class="delete-button">Xóa</button>
                        </form>
                    </td>
                </tr>
            </c:forEach>
        </tbody>
    </table>
    
    <%-- Javascript để xử lý form tạo mới --%>
    <script>
        // Dữ liệu từ Controller (cần được escape đúng cách nếu tên có ký tự đặc biệt)
        const roles = [<c:forEach var="role" items="${roles}" varStatus="loop">'${role}'<c:if test="${!loop.last}">, </c:if></c:forEach>];
        const accounts = [<c:forEach var="acc" items="${accountList}" varStatus="loop">{ id: ${acc.id}, username: '${acc.tenDangNhap}' }<c:if test="${!loop.last}">, </c:if></c:forEach>];

        function toggleTargetValue() {
            const targetType = document.querySelector('input[name="targetType"]:checked').value;
            const targetValueDiv = document.getElementById('targetValueDiv');
            const targetValueLabel = document.getElementById('targetValueLabel');
            const targetValueInput = document.getElementById('targetValueInput'); // Đây là thẻ select

            targetValueInput.innerHTML = ''; // Xóa các option cũ

            if (targetType === 'ALL') {
                targetValueDiv.style.display = 'none'; // Ẩn hoàn toàn div
                targetValueInput.removeAttribute('required'); // Không bắt buộc
                // Tạo một input hidden để gửi giá trị 'ALL'
                 let hiddenInput = document.getElementById('hiddenTargetValue');
                 if (!hiddenInput) {
                     hiddenInput = document.createElement('input');
                     hiddenInput.type = 'hidden';
                     hiddenInput.name = 'targetValue';
                     hiddenInput.id = 'hiddenTargetValue';
                     document.getElementById('createForm').appendChild(hiddenInput);
                 }
                 hiddenInput.value = 'ALL';
                 targetValueInput.name = ''; // Bỏ name của select để không bị gửi đi
            } else {
                 // Xóa input hidden nếu có
                 let hiddenInput = document.getElementById('hiddenTargetValue');
                 if (hiddenInput) {
                     hiddenInput.remove();
                 }
                 targetValueInput.name = 'targetValue'; // Trả lại name cho select

                targetValueDiv.style.display = 'block'; // Hiện div
                targetValueInput.style.display = 'inline-block'; // Hiện select
                targetValueInput.setAttribute('required', 'required'); // Bắt buộc chọn

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
                        option.value = acc.id; // Gửi đi ID
                        option.textContent = acc.username + ' (ID: ' + acc.id + ')';
                        targetValueInput.appendChild(option);
                    });
                }
            }
        }
        // Gọi lần đầu để khởi tạo
        toggleTargetValue();
    </script>

</body>
</html>