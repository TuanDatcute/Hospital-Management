<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<!DOCTYPE html>
<html lang="vi">
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>Form Phòng Bệnh</title>
        <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.5.1/css/all.min.css">
        <link rel="stylesheet" href="css/PhongBenh.css">
        <link rel="stylesheet" href="<c:url value='/css/_sidebar.css'/>">
        <script src="<c:url value='/js/darkmodeQuang.js'/>" defer></script>
        <script src="<c:url value='/js/theme.js'/>"></script>
    </head>
    <body>
        <jsp:include page="_sidebar.jsp" />
        <div class="main-content">
            <%-- Bọc toàn bộ nội dung trong .container --%>
            <div class="container">

                <%-- Đặt nút Dark Mode ở đầu (trong container) --%>
                <div class="theme-switch-wrapper" style="position: absolute; top: 30px; right: 30px;">
                    <label class="theme-switch" for="theme-toggle">
                        <input type="checkbox" id="theme-toggle" />
                        <span class="slider">
                            <i class="fa-solid fa-sun sun-icon"></i>
                            <i class="fa-solid fa-moon moon-icon"></i>
                        </span>
                    </label>
                </div>

                <%-- Cập nhật thông báo lỗi (dùng class .alert) --%>
                <c:if test="${not empty param.createError}">
                    <div class="alert alert-danger">Lỗi khi thêm: ${param.createError}</div>
                </c:if>
                <c:if test="${not empty param.updateError}">
                    <div class="alert alert-danger">Lỗi khi cập nhật: ${param.updateError}</div>
                </c:if>
                <c:if test="${not empty error}">
                    <div class="alert alert-danger">Lỗi: ${error}</div>
                </c:if>

                <%-- Form Thêm/Sửa (Đã cập nhật cấu trúc) --%>
                <c:choose>
                    <c:when test="${not empty roomToUpdate}">
                        <%-- Dùng <h3> thay vì <h2> để khớp style CSS --%>
                        <h3>Cập nhật Phòng Bệnh: ${roomToUpdate.tenPhong}</h3>

                        <form action="MainController" method="POST">
                            <input type="hidden" name="action" value="updateRoom">
                            <input type="hidden" name="roomId" value="${roomToUpdate.id}">

                            <%-- Dùng .form-group và .form-control --%>
                            <div class="form-group">
                                <label for="tenPhong">Tên phòng:</label>
                                <input type="text" id="tenPhong" name="tenPhong" value="${roomToUpdate.tenPhong}" class="form-control" required>
                            </div>

                            <div class="form-group">
                                <label for="loaiPhong">Loại phòng:</label>
                                <input type="text" id="loaiPhong" name="loaiPhong" value="${roomToUpdate.loaiPhong}" class="form-control">
                            </div>

                            <div class="form-group">
                                <label for="sucChua">Sức chứa:</label>
                                <input type="number" id="sucChua" min="1" max="20" name="sucChua" value="${roomToUpdate.sucChua}" class="form-control" required>
                            </div>

                            <div class="form-group">
                                <label for="khoaId">Khoa:</label>
                                <select name="khoaId" id="khoaId" class="form-control" required>
                                    <c:forEach var="khoa" items="${khoaList}">
                                        <option value="${khoa.id}" ${khoa.id == roomToUpdate.khoaId ? 'selected' : ''}>
                                            <c:out value="${khoa.tenKhoa}"/>
                                        </option>
                                    </c:forEach>
                                </select>
                            </div>

                            <%-- Cập nhật style cho các nút bấm --%>
                            <div class="form-actions">
                                <button type="submit" class="btn btn-primary">Cập nhật</button>
                                <a href="MainController?action=listRooms" class="btn btn-secondary">Hủy</a>
                            </div>
                        </form>
                    </c:when>

                    <c:otherwise>
                        <%-- Dùng <h3> thay vì <h2> --%>
                        <h3>Thêm Phòng Bệnh Mới</h3>

                        <form action="MainController" method="POST">
                            <input type="hidden" name="action" value="createRoom">

                            <div class="form-group">
                                <label for="tenPhong">Tên phòng:</label>
                                <input type="text" id="tenPhong" name="tenPhong" class="form-control" required>
                            </div>

                            <div class="form-group">
                                <label for="loaiPhong">Loại phòng:</label>
                                <input type="text" id="loaiPhong" name="loaiPhong" class="form-control">
                            </div>

                            <div class="form-group">
                                <label for="sucChua">Sức chứa:</label>
                                <input type="number" id="sucChua" min="1" name="sucChua" class="form-control" required>
                            </div>

                            <div class="form-group">
                                <label for="khoaId">Khoa:</label>
                                <select name="khoaId" id="khoaId" class="form-control" required>
                                    <option value="">-- Chọn khoa --</option>
                                    <c:forEach var="khoa" items="${khoaList}">
                                        <option value="${khoa.id}">${khoa.tenKhoa}</option>
                                    </c:forEach>
                                </select>
                            </div>

                            <div class="form-actions">
                                <button type="submit" class="btn btn-primary">Thêm Mới</button>
                                <a href="MainController?action=listRooms" class="btn btn-secondary">Hủy</a>
                            </div>
                        </form>
                    </c:otherwise>
                </c:choose>

            </div> <%-- End .container --%>
        </div>
    </body>
</html>