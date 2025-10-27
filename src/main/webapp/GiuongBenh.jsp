<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %> <%-- Nên giữ lại fmt --%>

<html>
<head>
    <title>Sơ đồ Giường bệnh</title>
    <%-- (style giữ nguyên) --%>
    <style>
        .status-trong { color: green; }
        .status-dang-su-dung { color: red; }
        .status-bao-tri { color: orange; }
        .status-dang-don-dep { color: blue; }
        .delete-button {
            background: none; border: none; color: red;
            text-decoration: underline; cursor: pointer; padding: 0;
            font-family: inherit; font-size: inherit;
        }
    </style>
</head>
<body>

    <%-- ============================================= --%>
    <%--        THÊM MỚI / CẬP NHẬT FORM             --%>
    <%-- ============================================= --%>
    <c:choose>
        <%-- TRƯỜNG HỢP 1: CẬP NHẬT (có bedToUpdate) --%>
        <c:when test="${not empty bedToUpdate}">
            <h2>Cập nhật Giường Bệnh: <c:out value="${bedToUpdate.tenGiuong}"/></h2>
            <form action="${pageContext.request.contextPath}/MainController" method="POST">
                <input type="hidden" name="action" value="updateBed">
                <input type="hidden" name="bedId" value="${bedToUpdate.id}">
                
                Tên giường: 
                <%-- Dùng c:out để an toàn hơn --%>
                <input type="text" name="tenGiuong" value="<c:out value='${bedToUpdate.tenGiuong}'/>" required>
                
                Thuộc phòng:
                <select name="phongBenhId" required>
                    <option value="">-- Chọn phòng --</option>
                    <c:forEach var="room" items="${roomList}">
                        <%-- Giữ nguyên logic chọn selected --%>
                        <option value="${room.id}" ${room.id == bedToUpdate.phongBenhId ? 'selected' : ''}>
                            <c:out value="${room.tenPhong}"/> (<c:out value="${room.tenKhoa}"/>)
                        </option>
                    </c:forEach>
                </select>
                
                <button type="submit">Cập nhật Giường</button>
                <a href="MainController?action=listBeds">Hủy</a> <%-- Link hủy quay về list --%>
            </form>
            <%-- Hiển thị lỗi nếu update thất bại VÀ đó là lỗi của giường này --%>
            <c:if test="${not empty param.updateError && param.bedId == bedToUpdate.id}">
                 <b style="color:red;">Lỗi cập nhật: <c:out value="${param.updateError}"/></b>
            </c:if>
        </c:when>
        
        <%-- TRƯỜNG HỢP 2: THÊM MỚI (mặc định) --%>
        <c:otherwise>
            <h2>Thêm Giường Bệnh Mới</h2>
            <form action="${pageContext.request.contextPath}/MainController" method="POST">
                <input type="hidden" name="action" value="createBed">
                Tên giường: 
                <input type="text" name="tenGiuong" required>
                Thuộc phòng:
                <select name="phongBenhId" required>
                    <option value="">-- Chọn phòng --</option>
                    <c:forEach var="room" items="${roomList}">
                        <option value="${room.id}">
                            <c:out value="${room.tenPhong}"/> (<c:out value="${room.tenKhoa}"/>)
                        </option>
                    </c:forEach>
                </select>
                <button type="submit">Thêm Giường</button>
            </form>
        </c:otherwise>
    </c:choose>
    
    <hr>
    <h2>Sơ đồ Giường bệnh</h2>

    <%-- Form Tìm kiếm (Giữ nguyên) --%>
    <form action="MainController" method="GET">
         <input type="hidden" name="action" value="listBeds"> 
         Tìm kiếm:
         <input type="text" 
                name="searchKeyword" 
                value="<c:out value='${param.searchKeyword}'/>" 
                placeholder="Nhập tên giường, phòng, bệnh nhân...">
         <button type="submit">Tìm</button>
         <a href="MainController?action=listBeds">Xóa tìm kiếm</a>
    </form>
    <br> 

    <%-- Hiển thị thông báo (Thêm thông báo update) --%>
    <%-- === THÊM MỚI: Thông báo Update === --%>
    <c:if test="${not empty param.updateSuccess}"> <b style="color:blue;">Cập nhật giường thành công!</b> </c:if>
    <%-- Lỗi update đã hiển thị ở form trên, không cần hiển thị lại ở đây --%>
    <%-- === KẾT THÚC THÊM MỚI === --%>
    <c:if test="${not empty param.deleteSuccess}"> <b style="color:green;">Xóa giường thành công!</b> </c:if>
    <c:if test="${not empty param.deleteError}"> <b style="color:red;">Lỗi xóa giường: <c:out value="${param.deleteError}"/></b> </c:if>
    <c:if test="${not empty param.createBedSuccess}"> <b style="color:green;">Thêm giường mới thành công!</b> </c:if>
    <c:if test="${not empty param.createBedError}"> <b style="color:red;">Lỗi thêm giường: <c:out value="${param.createBedError}"/></b> </c:if>
    <c:if test="${not empty param.assignSuccess}"> <b style="color:green;">Gán giường thành công!</b> </c:if>
    <c:if test="${not empty param.assignError}"> <b style="color:red;">Lỗi gán giường: <c:out value="${param.assignError}"/></b> </c:if>
    <c:if test="${not empty param.releaseSuccess}"> <b style="color:blue;">Trả giường thành công!</b> </c:if>
    <c:if test="${not empty param.error}"> <b style="color:red;">Lỗi: ${param.error}</b> </c:if>


    <%-- Bảng Sơ đồ giường (Thêm nút Sửa) --%>
    <table border="1" style="width:100%;">
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
                    
                    <%-- CỘT HÀNH ĐỘNG ĐÃ CẬP NHẬT --%>
                    <td>
                        <%-- === THÊM MỚI: Nút Sửa === --%>
                        <%-- Chỉ cho sửa nếu giường không đang sử dụng --%>
                        <c:if test="${bed.trangThai != 'DANG_SU_DUNG'}">
                            <a href="MainController?action=getBedForUpdate&bedId=${bed.id}" style="margin-right: 10px; display: inline-block;">Sửa</a>
                        </c:if>
                        <%-- === KẾT THÚC THÊM MỚI === --%>
                        
                        <%-- Form Gán/Trả (giữ nguyên, chỉ thêm style) --%>
                        <c:if test="${bed.trangThai == 'TRONG'}">
                            <form action="${pageContext.request.contextPath}/MainController" method="POST" style="margin:0; display: inline-block;">
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
                            <form action="MainController" method="POST" style="margin:0; display: inline-block;">
                                <input type="hidden" name="action" value="releaseBed">
                                <input type="hidden" name="bedId" value="${bed.id}">
                                <button type="submit">Trả giường</button>
                            </form>
                        </c:if>

                        <%-- Form Xóa mềm (giữ nguyên, chỉ thêm style) --%>
                        <c:if test="${bed.trangThai != 'DANG_SU_DUNG'}">
                            <form action="MainController" method="POST" style="margin:0; display: inline-block;" 
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