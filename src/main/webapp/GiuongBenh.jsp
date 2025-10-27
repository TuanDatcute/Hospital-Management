<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<html>
    <head>
        <title>Sơ đồ Giường bệnh</title>
        <style>
            .status-trong {
                color: green;
            }
            .status-dang-su-dung {
                color: red;
            }
            .status-bao-tri {
                color: orange;
            }
            .status-dang-don-dep {
                color: blue;
            }
        </style>
    </head>
    <body>

        <%-- Form Thêm Giường Bệnh (Giữ nguyên) --%>
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

        <hr>

        <h2>Sơ đồ Giường bệnh</h2>

        <%-- ============================================= --%>
        <%--        MỚI: FORM TÌM KIẾM                   --%>
        <%-- ============================================= --%>
        <form action="MainController" method="GET">
            <%-- Luôn gửi kèm action để MainController biết --%>
            <input type="hidden" name="action" value="listBeds"> 

            Tìm kiếm:
            <%-- 
              Hiển thị lại từ khóa đã tìm kiếm.
              Dùng <c:out> để tránh lỗi XSS.
            --%>
            <input type="text" 
                   name="searchKeyword" 
                   value="<c:out value='${param.searchKeyword}'/>" 
                   placeholder="Nhập tên giường, phòng, bệnh nhân...">

            <button type="submit">Tìm</button>

            <%-- Link này để xóa bộ lọc tìm kiếm --%>
            <a href="MainController?action=listBeds">Xóa tìm kiếm</a>
        </form>
        <br> <%-- Thêm một khoảng ngắt --%>

        <%-- Hiển thị thông báo (Giữ nguyên) --%>
        <c:if test="${not empty param.createBedSuccess}"> <b style="color:green;">Thêm giường mới thành công!</b> </c:if>
        <c:if test="${not empty param.createBedError}"> <b style="color:red;">Lỗi thêm giường: <c:out value="${param.createBedError}"/></b> </c:if>
        <c:if test="${not empty param.assignSuccess}"> <b style="color:green;">Gán giường thành công!</b> </c:if>
        <c:if test="${not empty param.assignError}"> <b style="color:red;">Lỗi gán giường: <c:out value="${param.assignError}"/></b> </c:if>
        <c:if test="${not empty param.releaseSuccess}"> <b style="color:blue;">Trả giường thành công!</b> </c:if>
        <c:if test="${not empty param.error}"> <b style="color:red;">${param.error}</b> </c:if>


        <%-- Bảng Sơ đồ giường (Giữ nguyên) --%>
        <table border="1" style="width:100%;">
            <thead>
                <tr>
                    <th>Giường</th>
                    <th>Phòng</th>
                    <th>Trạng thái</th>
                    <th>Bệnh nhân</th>
                    <th>Hành động</th>
                </tr>
            </thead>
            <tbody>

            <%-- Vòng lặp này bây giờ sẽ hiển thị danh sách đã được lọc --%>
            <c:forEach var="bed" items="${bedList}">
                <tr>
                    <td><c:out value="${bed.tenGiuong}"/></td>
                    <td><c:out value="${bed.tenPhong}"/></td>
                    <td>
                        <span class="status-${bed.trangThai.toLowerCase()}">
                            ${bed.trangThai}
                        </span>
                    </td>
                    <td>
                        <c:out value="${bed.benhNhanId != null ? bed.tenBenhNhan : '---'}"/>
                    </td>
                    <td>
                        <%-- (Các form Gán/Trả giường giữ nguyên) --%>
                        <c:if test="${bed.trangThai == 'TRONG'}">
                            <form action="${pageContext.request.contextPath}/MainController" method="POST" style="margin:0;">
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
                            <form action="MainController" method="POST" style="margin:0;">
                                <input type="hidden" name="action" value="releaseBed">
                                <input type="hidden" name="bedId" value="${bed.id}">
                                <button type="submit">Trả giường</button>
                            </form>
                        </c:if>
                    </td>
                </tr>
            </c:forEach>
        </tbody>
    </table>
</body>
</html>