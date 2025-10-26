<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<html>
<head>
    <title>Quản lý Phòng Bệnh</title>
</head>
<body>

    <%-- Form Thêm/Sửa (Giữ nguyên như cũ) --%>
    <c:choose>
        <c:when test="${not empty roomToUpdate}">
            <h2>Cập nhật Phòng Bệnh: ${roomToUpdate.tenPhong}</h2>
            <form action="MainController" method="POST">
                <input type="hidden" name="action" value="updateRoom">
                <input type="hidden" name="roomId" value="${roomToUpdate.id}">
                Tên phòng: 
                <input type="text" name="tenPhong" value="${roomToUpdate.tenPhong}" required> <br/>
                Loại phòng: 
                <input type="text" name="loaiPhong" value="${roomToUpdate.loaiPhong}"> <br/>
                Sức chứa: 
                <input type="number" name="sucChua" value="${roomToUpdate.sucChua}" required> <br/>
                Khoa: 
                <select name="khoaId" required>
                    <c:forEach var="khoa" items="${khoaList}">
                        <%-- Giả sử DTO của bạn có khoaId --%>
                        <option value="${khoa.id}" ${khoa.id == roomToUpdate.khoaId ? 'selected' : ''}>
                            <c:out value="${khoa.tenKhoa}"/>
                        </option>
                    </c:forEach>
                </select> <br/>
                <button type="submit">Cập nhật</button>
                <a href="MainController?action=listRooms">Hủy</a>
            </form>
        </c:when>
        
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

    <%-- Thông báo (Giữ nguyên) --%>
    <c:if test="${not empty param.createSuccess}"> <b style="color:green;">Thêm phòng thành công!</b> </c:if>
    <c:if test="${not empty param.createError}"> <b style="color:red;">Lỗi khi thêm: ${param.createError}</b> </c:if>
    <c:if test="${not empty param.updateSuccess}"> <b style="color:blue;">Cập nhật phòng thành công!</b> </c:if>
    <c:if test="${not empty param.updateError}"> <b style="color:red;">Lỗi khi cập nhật: ${param.updateError}</b> </c:if>

    <h2>Danh sách Phòng Bệnh</h2>

    <%-- ============================================= --%>
    <%--               FORM TÌM KIẾM                   --%>
    <%-- ============================================= --%>
    <form action="MainController" method="GET">
        <input type="hidden" name="action" value="listRooms"> 
        Tìm kiếm:
        <input type="text" 
               name="searchKeyword" 
               value="<c:out value='${param.searchKeyword}'/>" 
               placeholder="Nhập tên phòng, loại, tên khoa...">
        
        <button type="submit">Tìm</button>
        <a href="MainController?action=listRooms">Xóa lọc</a>
    </form>
    <br>

    <table border="1">
        <thead>
            <tr>
                <th>ID</th>
                <th>Tên Phòng</th>
                <th>Loại Phòng</th>
                <th>Sức chứa</th>
                <th>Thuộc Khoa</th> <%-- Giả sử DTO có tenKhoa --%>
                <th>Hành động</th>
            </tr>
        </thead>
        <tbody>
            <c:forEach var="room" items="${roomList}">
                <tr>
                    <td>${room.id}</td>
                    <td><c:out value="${room.tenPhong}"/></td>
                    <td><c:out value="${room.loaiPhong}"/></td>
                    <td>${room.sucChua}</td>
                    <%-- Giả sử DTO của bạn có tenKhoa (được service xử lý) --%>
                    <td><c:out value="${room.tenKhoa}"/></td> 
                    <td>
                        <a href="MainController?action=getRoomForUpdate&roomId=${room.id}">Sửa</a>
                    </td>
                </tr>
            </c:forEach>
        </tbody>
    </table>
</body>
</html>