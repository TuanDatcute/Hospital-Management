<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<html>
<head>
    <title>Sơ đồ Giường bệnh</title>
    <style>
        .status-trong { color: green; }
        .status-dang-su-dung { color: red; }
        .status-bao-tri { color: orange; }
        .status-dang-don-dep { color: blue; } /* Thêm từ DB schema */
    </style>
</head>
<body>
    
    <%-- ============================================= --%>
    <%--        FORM MỚI: Thêm Giường Bệnh            --%>
    <%-- ============================================= --%>
    <h2>Thêm Giường Bệnh Mới</h2>
    <form action="MainController" method="POST">
        <input type="hidden" name="action" value="createBed">
        
        Tên giường (ví dụ: G01, G02): 
        <input type="text" name="tenGiuong" required>
        
        Thuộc phòng:
        <select name="phongBenhId" required>
            <option value="">-- Chọn phòng --</option>
            <%-- Lặp qua 'roomList' được gửi từ BedServlet --%>
            <c:forEach var="room" items="${roomList}">
                <option value="${room.id}">
                    <c:out value="${room.tenPhong}"/> 
                    <%-- Giả sử PhongBenhDTO có lồng KhoaDTO --%>
                    (<c:out value="${room.tenKhoa}"/>)
                </option>
            </c:forEach>
        </select>
        
        <button type="submit">Thêm Giường</button>
    </form>
    
    <hr>

    <h2>Sơ đồ Giường bệnh</h2>
    
    <%-- Hiển thị thông báo (Thêm các thông báo tạo giường) --%>
    <c:if test="${not empty param.createBedSuccess}"> <b style="color:green;">Thêm giường mới thành công!</b> </c:if>
    <c:if test="${not empty param.createBedError}"> <b style="color:red;">Lỗi thêm giường: ${param.createBedError}</b> </c:if>
    <c:if test="${not empty param.assignSuccess}"> <b style="color:green;">Gán giường thành công!</b> </c:if>
    <c:if test="${not empty param.assignError}"> <b style="color:red;">Lỗi gán giường: ${param.assignError}</b> </c:if>
    <c:if test="${not empty param.releaseSuccess}"> <b style="color:blue;">Trả giường thành công!</b> </c:if>
    <c:if test="${not empty param.error}"> <b style="color:red;">${param.error}</b> </c:if> <%-- Lỗi chung --%>


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
            <c:forEach var="bed" items="${bedList}">
                <tr>
                    <td><c:out value="${bed.tenGiuong}"/></td>
                    <td><c:out value="${bed.tenPhong}"/></td>
                    <td>
                        <%-- Dùng class CSS động dựa trên trạng thái --%>
                        <span class="status-${bed.trangThai.toLowerCase()}">
                            ${bed.trangThai}
                        </span>
                    </td>
                    <td>
                        <c:out value="${bed.benhNhanId != null ? bed.tenBenhNhan : '---'}"/>
                    </td>
                    <td>
                        <%-- Form Gán giường (Chỉ hiện khi giường trống) --%>
                        <c:if test="${bed.trangThai == 'TRONG'}">
                            <form action="MainController" method="POST" style="margin:0;">
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

                        <%-- Form Trả giường (Chỉ hiện khi có người) --%>
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