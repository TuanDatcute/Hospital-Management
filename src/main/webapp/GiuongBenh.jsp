<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %> 

<html>
<head>
    <title>Sơ đồ Giường bệnh</title>
    <%-- THÊM MỚI: Liên kết CSS bên ngoài --%>
    <link rel="stylesheet" href="css/GiuongBenh.css">
</head>
<body>

    <%-- ============================================= --%>
    <%--         THÊM MỚI / CẬP NHẬT FORM (Đã cập nhật)  --%>
    <%-- ============================================= --%>
    <c:choose>
        <%-- TRƯỜNG HỢP 1: CẬP NHẬT --%>
        <c:when test="${not empty bedToUpdate}">
            <h2>Cập nhật Giường Bệnh: <c:out value="${bedToUpdate.tenGiuong}"/></h2>
            <form action="${pageContext.request.contextPath}/MainController" method="POST">
                <input type="hidden" name="action" value="updateBed">
                <input type="hidden" name="bedId" value="${bedToUpdate.id}">
                
                <p>
                    <label for="tenGiuong">Tên giường:</label>
                    <input type="text" id="tenGiuong" name="tenGiuong" value="<c:out value='${bedToUpdate.tenGiuong}'/>" required>
                </p>
                <p>
                    <label for="phongBenhId">Thuộc phòng:</label>
                    <select id="phongBenhId" name="phongBenhId" required>
                        <option value="">-- Chọn phòng --</option>
                        <c:forEach var="room" items="${roomList}">
                            <option value="${room.id}" ${room.id == bedToUpdate.phongBenhId ? 'selected' : ''}>
                                <c:out value="${room.tenPhong}"/> (<c:out value="${room.tenKhoa}"/>)
                            </option>
                        </c:forEach>
                    </select>
                </p>
                
                <button type="submit">Cập nhật Giường</button>
                <a href="MainController?action=listBeds">Hủy</a>
            </form>
            
            <%-- Hiển thị lỗi (Đã cập nhật dùng class) --%>
            <c:if test="${not empty param.updateError && param.bedId == bedToUpdate.id}">
                 <div class="message error">Lỗi cập nhật: <c:out value="${param.updateError}"/></div>
            </c:if>
        </c:when>
        
        <%-- TRƯỜNG HỢP 2: THÊM MỚI (Đã cập nhật) --%>
        <c:otherwise>
            <h2>Thêm Giường Bệnh Mới</h2>
            <form action="${pageContext.request.contextPath}/MainController" method="POST">
                <input type="hidden" name="action" value="createBed">
                <p>
                    <label for="tenGiuong">Tên giường:</label>
                    <input type="text" id="tenGiuong" name="tenGiuong" required>
                </p>
                <p>
                    <label for="phongBenhId">Thuộc phòng:</label>
                    <select id="phongBenhId" name="phongBenhId" required>
                        <option value="">-- Chọn phòng --</option>
                        <c:forEach var="room" items="${roomList}">
                            <option value="${room.id}">
                                <c:out value="${room.tenPhong}"/> (<c:out value="${room.tenKhoa}"/>)
                            </option>
                        </c:forEach>
                    </select>
                </p>
                <button type="submit">Thêm Giường</button>
            </form>
        </c:otherwise>
    </c:choose>
    
    <hr>
    <h2>Sơ đồ Giường bệnh</h2>

    <%-- Form Tìm kiếm (Đã cập nhật) --%>
    <form action="MainController" method="GET">
         <input type="hidden" name="action" value="listBeds"> 
         <label for="searchKeyword">Tìm kiếm:</label>
         <input type="text" 
                id="searchKeyword"
                name="searchKeyword" 
                value="<c:out value='${param.searchKeyword}'/>" 
                placeholder="Nhập tên giường, phòng, bệnh nhân...">
         <button type="submit">Tìm</button>
         <a href="MainController?action=listBeds">Xóa tìm kiếm</a>
    </form>
    <br> 

    <%-- Hiển thị thông báo (Đã cập nhật dùng class) --%>
    <c:if test="${not empty param.updateSuccess}"> 
        <div class="message info">Cập nhật giường thành công!</div> 
    </c:if>
    <c:if test="${not empty param.deleteSuccess}"> 
        <div class="message success">Xóa giường thành công!</div> 
    </c:if>
    <c:if test="${not empty param.deleteError}"> 
        <div class="message error">Lỗi xóa giường: <c:out value="${param.deleteError}"/></div> 
    </c:if>
    <c:if test="${not empty param.createBedSuccess}"> 
        <div class="message success">Thêm giường mới thành công!</div> 
    </c:if>
    <c:if test="${not empty param.createBedError}"> 
        <div class="message error">Lỗi thêm giường: <c:out value="${param.createBedError}"/></div> 
    </c:if>
    <c:if test="${not empty param.assignSuccess}"> 
        <div class="message success">Gán giường thành công!</div> 
    </c:if>
    <c:if test="${not empty param.assignError}"> 
        <div class="message error">Lỗi gán giường: <c:out value="${param.assignError}"/></div> 
    </c:if>
    <c:if test="${not empty param.releaseSuccess}"> 
        <div class="message info">Trả giường thành công!</div> 
    </c:if>
    <c:if test="${not empty param.error}"> 
        <div class="message error">Lỗi: ${param.error}</div> 
    </c:if>


    <%-- Bảng Sơ đồ giường (Đã xóa style inline) --%>
    <table>
        <thead>
            <tr>
                <th>Giường</th> <th>Phòng</th> <th>Trạng thái</th> <th>Bệnh nhân</th> <th>Hành động</th>
            </tr>
        </thead>
        <tbody>
            <c:forEach var="bed" items="${bedList}">
                <tr>
                    <td><c:out value="${bed.tenGiuong}"/></td>
                    <td><c:out value="${bed.tenPhong}"/></td> 
                    <td>
                        <span class="status-${bed.trangThai.toLowerCase()}"> ${bed.trangThai} </span>
                    </td>
                    <td>
                        <c:out value="${bed.benhNhanId != null ? bed.tenBenhNhan : '---'}"/>
                    </td>
                    
                    <%-- CỘT HÀNH ĐỘNG (Đã xóa style inline) --%>
                    <td>
                        <%-- Nút Sửa --%>
                        <c:if test="${bed.trangThai != 'DANG_SU_DUNG'}">
                            <a href="MainController?action=getBedForUpdate&bedId=${bed.id}">Sửa</a>
                        </c:if>
                        
                        <%-- Form Gán/Trả --%>
                        <c:if test="${bed.trangThai == 'TRONG'}">
                            <form action="${pageContext.request.contextPath}/MainController" method="POST">
                                <input type="hidden" name="action" value="assignBed">
                                <input type="hidden" name="bedId" value="${bed.id}">
                                <select name="patientId">
                                    <option value="">-- Chọn bệnh nhân --</option>
                                    <c:forEach var="patient" items="${patientList}">
                                        <option value="${patient.id}">
                                            <c:out value="${patient.hoTen}"/>
                                        </option>
                                    </c:forEach>
                                </select>
                                <button type="submit">Gán</button>
                            </form>
                        </c:if>
                        <c:if test="${bed.trangThai == 'DANG_SU_DUNG'}">
                            <form action="MainController" method="POST">
                                <input type="hidden" name="action" value="releaseBed">
                                <input type="hidden" name="bedId" value="${bed.id}">
                                <button type="submit">Trả giường</button>
                            </form>
                        </c:if>

                        <%-- Form Xóa mềm --%>
                        <c:if test="${bed.trangThai != 'DANG_SU_DUNG'}">
                            <form action="MainController" method="POST" 
                                  onsubmit="return confirm('Bạn có chắc chắn muốn xóa giường [${bed.tenGiuong}] không? Giường sẽ bị ẩn đi.');">
                                <input type="hidden" name="action" value="deleteBed">
                                <input type="hidden" name="bedId" value="${bed.id}">
                                <button type="submit" class="delete-button">Xóa</button>
                            </form>
                        </c:if>
                    </td>
                </tr>
            </c:forEach>
        </tbody>
    </table>
</body>
</html>