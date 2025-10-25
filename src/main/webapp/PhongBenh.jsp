<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<html>
<head>
    <title>Quản lý Phòng Bệnh</title>
</head>
<body>

    <%-- 
      Sử dụng <c:choose> để quyết định hiển thị form Thêm mới hay Cập nhật
      Kiểm tra xem 'roomToUpdate' (được gửi từ Servlet) có tồn tại không
    --%>
    <c:choose>
        <%-- TRƯỜNG HỢP 1: CẬP NHẬT (roomToUpdate tồn tại) --%>
        <c:when test="${not empty roomToUpdate}">
            <h2>Cập nhật Phòng Bệnh: ${roomToUpdate.tenPhong}</h2>
            <form action="MainController" method="POST">
                <input type="hidden" name="action" value="updateRoom">
                <%-- Thêm trường hidden để giữ ID --%>
                <input type="hidden" name="roomId" value="${roomToUpdate.id}">
                
                Tên phòng: 
                <input type="text" name="tenPhong" value="${roomToUpdate.tenPhong}" required> <br/>
                Loại phòng: 
                <input type="text" name="loaiPhong" value="${roomToUpdate.loaiPhong}"> <br/>
                Sức chứa: 
                <input type="number" name="sucChua" value="${roomToUpdate.sucChua}" required> <br/>
                Khoa: 
                <select name="khoaId" required>
                    <option value="">-- Chọn khoa --</option>
                    <c:forEach var="khoa" items="${khoaList}">
                        <%-- Thêm 'selected' nếu khoaId khớp với khoa của phòng --%>
                        <option value="${khoa.id}" ${khoa.id == roomToUpdate.khoa.id ? 'selected' : ''}>
                            <c:out value="${khoa.tenKhoa}"/>
                        </option>
                    </c:forEach>
                </select> <br/>
                <button type="submit">Cập nhật</button>
                <%-- Thêm link "Hủy" để quay về trạng thái Thêm mới --%>
                <a href="MainController?action=listRooms">Hủy</a>
            </form>
        </c:when>
        
        <%-- TRƯỜNG HỢP 2: THÊM MỚI (mặc định) --%>
        <c:otherwise>
            <h2>Thêm Phòng Bệnh Mới</h2>
            <form action="MainController" method="POST">
                <input type="hidden" name="action" value="createRoom">
                Tên phòng: <input type="text" name="tenPhong" required> <br/>
                Loại phòng: <input type="text" name="loaiPhong"> <br/>
                Sức chứa: <input type="number" name="sucChua" required> <br/>
                Khoa: 
                <select name="khoaId" required>
                    <option value="">-- Chọn khoa --</option>
                    <c:forEach var="khoa" items="${khoaList}">
                        <option value="${khoa.id}">${khoa.tenKhoa}</option>
                    </c:forEach>
                </select> <br/>
                <button type="submit">Thêm Mới</button>
            </form>
        </c:otherwise>
    </c:choose>
    
    <hr>

    <%-- Hiển thị thông báo (nếu có) từ redirect --%>
    <c:if test="${not empty param.createSuccess}"> <b style="color:green;">Thêm phòng thành công!</b> </c:if>
    <c:if test="${not empty param.createError}"> <b style="color:red;">Lỗi khi thêm: ${param.createError}</b> </c:if>
    <c:if test="${not empty param.updateSuccess}"> <b style="color:blue;">Cập nhật phòng thành công!</b> </c:if>
    <c:if test="${not empty param.updateError}"> <b style="color:red;">Lỗi khi cập nhật: ${param.updateError}</b> </c:if>

    <h2>Danh sách Phòng Bệnh</h2>
    <table border="1">
        <thead>
            <tr>
                <th>ID</th>
                <th>Tên Phòng</th>
                <th>Loại Phòng</th>
                <th>Sức chứa</th>
                <th>Thuộc Khoa</th>
                <th>Hành động</th> <%-- Thêm cột Hành động --%>
            </tr>
        </thead>
        <tbody>
            <c:forEach var="room" items="${roomList}">
                <tr>
                    <td>${room.id}</td>
                    <td><c:out value="${room.tenPhong}"/></td>
                    <td><c:out value="${room.loaiPhong}"/></td>
                    <td>${room.sucChua}</td>
                    <td><c:out value="${room.khoa.tenKhoa}"/></td>
                    <td>
                        <%-- Link Sửa: Trỏ về MainController với action 'getRoomForUpdate' --%>
                        <a href="MainController?action=getRoomForUpdate&roomId=${room.id}">Sửa</a>
                        <%-- (Bạn có thể thêm link Xóa ở đây) --%>
                    </td>
                </tr>
            </c:forEach>
        </tbody>
    </table>
</body>
</html>