<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<!DOCTYPE html>
<html lang="vi">
    <head>
        <meta charset="UTF-8">
        <meta name="viewport" content="width=device-width, initial-scale=1.0">

        <c:choose>
            <c:when test="${not empty bedToUpdate}">
                <title>Cập nhật Giường Bệnh</title>
            </c:when>
            <c:otherwise>
                <title>Thêm Giường Bệnh Mới</title>
            </c:otherwise>
        </c:choose>

        <%-- BẮT BUỘC: Font Awesome và CSS/JS --%>
        <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.5.1/css/all.min.css">
        <link rel="stylesheet" href="css/StyleChungCuaQuang.css">
        <link rel="stylesheet" href="css/GiuongBenh.css">
        <script src="<c:url value='/js/darkmodeQuang.js'/>" defer></script>
    </head>
    <body>

        <div class="container" style="max-width: 800px;"> <%-- Container hẹp hơn cho form --%>

            <%-- Nút gạt Dark Mode (đặt trong header) --%>
                <div class="theme-switch-wrapper">
                    <label class="theme-switch" for="theme-toggle">
                        <input type="checkbox" id="theme-toggle" />
                        <span class="slider">
                            <i class="fa-solid fa-sun sun-icon"></i>
                            <i class="fa-solid fa-moon moon-icon"></i>
                        </span>
                    </label>
                </div>

            <%-- Hiển thị lỗi (nếu có) --%>
            <c:if test="${not empty requestScope.error}">
                <div class="alert alert-danger"><c:out value="${requestScope.error}"/></div>
            </c:if>
            <c:if test="${not empty param.updateError}">
                <div class="alert alert-danger">Lỗi cập nhật: <c:out value="${param.updateError}"/></div>
            </c:if>
            <c:if test="${not empty param.createBedError}">
                <div class="alert alert-danger">Lỗi thêm giường: <c:out value="${param.createBedError}"/></div>
            </c:if>


            <%-- ============================================= --%>
            <%--        FORM THÊM MỚI / CẬP NHẬT               --%>
            <%-- ============================================= --%>
            <c:choose>
                <%-- TRƯỜNG HỢP 1: CẬP NHẬT --%>
                <c:when test="${not empty bedToUpdate}">
                    <h3>Cập nhật Giường Bệnh: <c:out value="${bedToUpdate.tenGiuong}"/></h3>

                    <form action="MainController" method="POST">
                        <input type="hidden" name="action" value="updateBed">
                        <input type="hidden" name="bedId" value="${bedToUpdate.id}">

                        <div class="form-group">
                            <label for="tenGiuong">Tên giường:</label>
                            <input type="text" id="tenGiuong" name="tenGiuong" value="<c:out value='${bedToUpdate.tenGiuong}'/>" class="form-control" required>
                        </div>

                        <div class="form-group">
                            <label for="phongBenhId">Thuộc phòng:</label>
                            <select id="phongBenhId" name="phongBenhId" class="form-control" required>
                                <option value="">-- Chọn phòng --</option>
                                <c:forEach var="room" items="${roomList}">
                                    <option value="${room.id}" ${room.id == bedToUpdate.phongBenhId ? 'selected' : ''}>
                                        <c:out value="${room.tenPhong}"/> (<c:out value="${room.tenKhoa}"/>)
                                    </option>
                                </c:forEach>
                            </select>
                        </div>

                        <div class="form-actions">
                            <button type="submit" class="btn btn-primary">Cập nhật Giường</button>
                            <a href="MainController?action=listBeds" class="btn btn-secondary">Hủy</a>
                        </div>
                    </form>
                </c:when>

                <%-- TRƯỜNG HỢP 2: THÊM MỚI --%>
                <c:otherwise>
                    <h3>Thêm Giường Bệnh Mới</h3>

                    <form action="MainController" method="POST">
                        <input type="hidden" name="action" value="createBed">

                        <div class="form-group">
                            <label for="tenGiuong">Tên giường:</label>
                            <input type="text" id="tenGiuong" name="tenGiuong" value="<c:out value='${param.tenGiuong}'/>" class="form-control" required>
                        </div>

                        <div class="form-group">
                            <label for="phongBenhId">Thuộc phòng:</label>
                            <select id="phongBenhId" name="phongBenhId" class="form-control" required>
                                <option value="">-- Chọn phòng --</option>
                                <c:forEach var="room" items="${roomList}">
                                    <option value="${room.id}" ${room.id == param.phongBenhId ? 'selected' : ''}>
                                        <c:out value="${room.tenPhong}"/> (<c:out value="${room.tenKhoa}"/>)
                                    </option>
                                </c:forEach>
                            </select>
                        </div>

                        <div class="form-actions">
                            <button type="submit" class="btn btn-primary">Thêm Giường</button>
                            <a href="MainController?action=listBeds" class="btn btn-secondary">Hủy</a>
                        </div>
                    </form>
                </c:otherwise>
            </c:choose>
        </div> <%-- End .container --%>
    </body>
</html>