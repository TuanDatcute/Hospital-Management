<%@ page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<!DOCTYPE html>
<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <title>Thông Báo</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.1.3/dist/css/bootstrap.min.css" rel="stylesheet">
    
    <style>
        .notification-container { max-width: 800px; margin: 40px auto; }
        .notification-item {
            border-left: 5px solid #ccc;
            padding: 15px;
            margin-bottom: 15px;
            cursor: pointer;
            transition: background-color 0.3s;
        }
        .notification-item.unread {
            font-weight: bold;
            border-left-color: #0d6efd; /* Màu xanh cho thông báo chưa đọc */
            background-color: #f8f9fa;
        }
        .notification-item:hover { background-color: #e9ecef; }
        .notification-item.read { background-color: #fff; border-left-color: #ccc; }
        .notification-time { font-size: 0.8em; color: #6c757d; }
    </style>
</head>
<body>

    <div class="notification-container">
        <h1 class="mb-4">🔔 Hộp Thư Thông Báo</h1>

        <c:if test="${empty notifications}">
            <div class="alert alert-secondary">Bạn không có thông báo nào.</div>
        </c:if>

        <div id="notificationList">
            <c:forEach var="tb" items="${notifications}">
                <div class="notification-item ${tb.daDoc ? 'read' : 'unread'}" 
                     data-id="${tb.thongBaoId}" 
                     onclick="markAsRead(this, ${tb.thongBaoId})">
                    
                    <h5>
                        ${tb.tieuDe}
                        <c:if test="${!tb.daDoc}">
                            <span class="badge bg-primary float-end">Mới</span>
                        </c:if>
                    </h5>
                    <p class="mb-1">${tb.noiDung}</p>
                    <small class="notification-time">
                        <fmt:formatDate value="${tb.thoiGianGui}" pattern="HH:mm 'ngày' dd/MM/yyyy"/>
                    </small>
                </div>
            </c:forEach>
        </div>
    </div>

    <script>
        function markAsRead(element, notificationId) {
            // Chỉ gửi yêu cầu nếu thông báo đó chưa đọc
            if (!element.classList.contains('unread')) {
                return; // Không làm gì nếu đã đọc rồi
            }
            
            // URL phải trỏ đến MainController
            const url = 'MainController?action=markNotificationAsRead';

            fetch(url, {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/x-www-form-urlencoded',
                },
                body: 'notificationId=' + notificationId
            })
            .then(response => response.json())
            .then(data => {
                if (data.status === 'success') {
                    // Cập nhật giao diện mà không cần tải lại trang
                    element.classList.remove('unread');
                    element.classList.add('read');
                    const newBadge = element.querySelector('.badge');
                    if (newBadge) {
                        newBadge.style.display = 'none'; // Ẩn tag "Mới"
                    }
                    console.log("Đã đánh dấu thông báo " + notificationId + " là đã đọc.");
                } else {
                    console.error("Lỗi khi đánh dấu đã đọc.");
                }
            })
            .catch(error => console.error('Lỗi Fetch:', error));
        }
    </script>
</body>
</html>