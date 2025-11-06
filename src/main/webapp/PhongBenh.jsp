<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %> 

<html>
    <head>
        <title>Quản lý Phòng Bệnh</title>
        <%-- === THÊM MỚI: Liên kết CSS === --%>
        <link rel="stylesheet" href="css/PhongBenh.css">
    </head>
    <body>

        <%-- Form Thêm/Sửa (Đã cập nhật cấu trúc) --%>
        <c:choose>
            <c:when test="${not empty roomToUpdate}">
                <h2>Cập nhật Phòng Bệnh: ${roomToUpdate.tenPhong}</h2>
                <form action="MainController" method="POST">
                    <input type="hidden" name="action" value="updateRoom">
                    <input type="hidden" name="roomId" value="${roomToUpdate.id}">
                    
                    <p>
                        <label for="tenPhong">Tên phòng:</label>
                        <input type="text" id="tenPhong" name="tenPhong" value="${roomToUpdate.tenPhong}" required>
                    </p>
                    <p>
                        <label for="loaiPhong">Loại phòng:</label>
                        <input type="text" id="loaiPhong" name="loaiPhong" value="${roomToUpdate.loaiPhong}">
                    </p>
                    <p>
                        <label for="sucChua">Sức chứa:</label>
                        <input type="number" id="sucChua" name="sucChua" value="${roomToUpdate.sucChua}" required>
                    </p>
                    <p>
                        <label for="khoaId">Khoa:</label>
                        <select name="khoaId" id="khoaId" required>
                            <c:forEach var="khoa" items="${khoaList}">
                                <option value="${khoa.id}" ${khoa.id == roomToUpdate.khoaId ? 'selected' : ''}>
                                    <c:out value="${khoa.tenKhoa}"/>
                                </option>
                            </c:forEach>
                        </select>
                    </p>
                    <button type="submit">Cập nhật</button>
                    <a href="MainController?action=listRooms">Hủy</a>
                </form>
            </c:when>

            <c:otherwise>
                <h2>Thêm Phòng Bệnh Mới</h2>
                <form action="MainController" method="POST">
                    <input type="hidden" name="action" value="createRoom">
                    <p>
                        <label for="tenPhong">Tên phòng:</label>
                        <input type="text" id="tenPhong" name="tenPhong" required>
                    </p>
                    <p>
                        <label for="loaiPhong">Loại phòng:</label>
                        <input type="text" id="loaiPhong" name="loaiPhong">
                    </p>
                    <p>
                        <label for="sucChua">Sức chứa:</label>
                        <input type="number" id="sucChua" name="sucChua" required>
                    </p>
                    <p>
                        <label for="khoaId">Khoa:</label>
                        <select name="khoaId" id="khoaId" required>
                            <option value="">-- Chọn khoa --</option>
                            <c:forEach var="khoa" items="${khoaList}">
                                <option value="${khoa.id}">${khoa.tenKhoa}</option>
                            </c:forEach>
                        </select>
                    </p>
                    <button type="submit">Thêm Mới</button>
                </form>
            </c:otherwise>
        </c:choose>

        <hr>

        <%-- Thông báo (Đã cập nhật dùng class) --%>
        <c:if test="${not empty param.deleteSuccess}"> 
            <div class="message success">Xóa phòng thành công!</div> 
        </c:if>
        <c:if test="${not empty param.deleteError}"> 
            <div class="message error">Lỗi xóa phòng: <c:out value="${param.deleteError}"/></div> 
        </c:if>
        <c:if test="${not empty param.createSuccess}"> 
            <div class="message success">Thêm phòng thành công!</div> 
        </c:if>
        <c:if test="${not empty param.createError}"> 
            <div class="message error">Lỗi khi thêm: ${param.createError}</div> 
        </c:if>
        <c:if test="${not empty param.updateSuccess}"> 
            <div class="message info">Cập nhật phòng thành công!</div> 
        </c:if>
        <c:if test="${not empty param.updateError}"> 
            <div class="message error">Lỗi khi cập nhật: ${param.updateError}</div> 
        </c:if>
        <c:if test="${not empty error}"> 
            <div class="message error">Lỗi: ${error}</div> 
        </c:if>
        
        <h2>Danh sách Phòng Bệnh</h2>

        <%-- Form Tìm kiếm (Đã cập nhật) --%>
        <form action="MainController" method="GET">
            <input type="hidden" name="action" value="listRooms"> 
            <label for="searchKeyword">Tìm kiếm:</label>
            <input type="text" 
                   id="searchKeyword"
                   name="searchKeyword" 
                   value="<c:out value='${param.searchKeyword}'/>" 
                   placeholder="Nhập tên phòng, loại, tên khoa...">

            <button type="submit">Tìm</button>
            <a href="MainController?action=listRooms">Xóa lọc</a>
        </form>
        <br>

        <%-- Bảng Danh sách Phòng (Đã xóa style inline) --%>
        <table>
            <thead>
                <tr>
                    <th>ID</th>
                    <th>Tên Phòng</th>
                    <th>Loại Phòng</th>
                    <th>Sức chứa</th>
                    <th>Thuộc Khoa</th> 
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
                        <td><c:out value="${room.tenKhoa}"/></td> 

                        <%-- CỘT HÀNH ĐỘNG (Đã xóa style inline) --%>
                        <td>
                            <%-- Link Sửa --%>
                            <a href="MainController?action=getRoomForUpdate&roomId=${room.id}">Sửa</a>

                            <%-- Form Xóa --%>
                            <form action="MainController" method="POST" 
                                  onsubmit="return confirm('Bạn có chắc chắn muốn xóa phòng [${room.tenPhong}]?\nTẤT CẢ giường trống trong phòng này cũng sẽ bị xóa.');">

                                <input type="hidden" name="action" value="deleteRoom">
                                <input type="hidden" name="roomId" value="${room.id}">
                                <button type="submit" class="delete-button">Xóa</button>
                            </form>
                        </td>
                    </tr>
                </c:forEach>
            </tbody>
        </table>
    </body>
</html>