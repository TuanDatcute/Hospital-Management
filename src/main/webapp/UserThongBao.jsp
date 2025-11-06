<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>

<html>
    <head>
        <title>Thông báo của tôi</title>
        <%-- (CSS giữ nguyên) --%>
        <style>
            .notification-list {
                list-style-type: none;
                padding: 0;
            }
            .notification-item {
                border: 1px solid #ddd;
                margin-bottom: 10px;
                padding: 15px;
                border-radius: 5px;
                position: relative;
            }
            .notification-item.unread {
                background-color: #f8f9fa;
                border-left: 5px solid #007bff;
                font-weight: bold;
            }
            .notification-item h4 {
                margin-top: 0;
            }
            .notification-item p {
                font-weight: normal;
            }
            .notification-time {
                font-size: 0.85em;
                color: #6c757d;
                font-weight: normal;
            }
            .actions {
                margin-top: 10px;
                font-weight: normal;
            }
            .actions form {
                display: inline-block;
                margin-right: 10px;
            }
            .delete-button {
                background: none;
                border: none;
                color: red;
                text-decoration: underline;
                cursor: pointer;
                padding: 0;
                font-family: inherit;
                font-size: 0.9em;
            }
            .read-button {
                background: none;
                border: none;
                color: green;
                text-decoration: underline;
                cursor: pointer;
                padding: 0;
                font-family: inherit;
                font-size: 0.9em;
            }
        </style>
    </head>
    <body>
        <h1>Thông báo của tôi</h1>

        <%-- Form Tìm kiếm: Sửa action trỏ về MainController --%>
        <form action="MainController" method="GET"> <%-- SỬA --%>
            <%-- SỬA: Thêm action GET --%>
            <input type="hidden" name="action" value="viewMyNotifications"> 

            Tìm kiếm:
            <input type="text" 
                   name="searchKeyword" 
                   value="<c:out value='${param.searchKeyword}'/>" 
                   placeholder="Nhập tiêu đề, nội dung...">
            <button type="submit">Tìm</button>

            <%-- Link Xóa lọc: Sửa trỏ về MainController --%>
            <a href="MainController?action=viewMyNotifications">Xóa lọc</a> <%-- SỬA --%>
        </form>
        <br>

        <%-- (Thông báo lỗi/thành công giữ nguyên) --%>
        <c:if test="${not empty param.deleteSuccess}"> <b style="color:green;">Đã xóa thông báo.</b> </c:if>
        <c:if test="${not empty param.error}"> <b style="color:red;">Lỗi: <c:out value="${param.error}"/></b> </c:if>
        <c:if test="${not empty error}"> <b style="color:red;">Lỗi: ${error}</b> </c:if>

            <ul class="notification-list">
            <c:forEach var="noti" items="${notificationList}">
                <li class="notification-item ${noti.daDoc ? '' : 'unread'}">

                    <h4><c:out value="${noti.tieuDe}"/></h4>
                    <p><c:out value="${noti.noiDung}"/></p>

                    <span class="notification-time">
                        <c:out value="${noti.thoiGianGui}"/>
                    </span>

                    <div class="actions">
                        <%-- Nút Đánh dấu đã đọc: Sửa action trỏ về MainController --%>
                        <c:if test="${!noti.daDoc}">
                            <form action="MainController" method="POST" style="margin:0;"> <%-- SỬA --%>
                                <input type="hidden" name="action" value="markNotificationAsRead"> <%-- SỬA --%>
                                <input type="hidden" name="id" value="${noti.id}">
                                <button type="submit" class="read-button">Đánh dấu đã đọc</button>
                            </form>
                        </c:if>

                        <%-- Nút Xóa mềm: Sửa action trỏ về MainController --%>
                        <form action="MainController" method="POST" style="margin:0;" <%-- SỬA --%>
                              onsubmit="return confirm('Bạn có chắc chắn muốn xóa thông báo này?');">
                            <input type="hidden" name="action" value="deleteMyNotification"> <%-- SỬA --%>
                            <input type="hidden" name="id" value="${noti.id}">
                            <button type="submit" class="delete-button">Xóa</button>
                        </form>
                    </div>
                </li>
            </c:forEach>

            <c:if test="${empty notificationList}">
                <li class="notification-item" style="font-weight: normal;"><i>Bạn không có thông báo nào.</i></li>
                </c:if>
        </ul>

    </body>
</html>