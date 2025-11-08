<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>

<!DOCTYPE html>
<html lang="vi">
    <head>
        <meta charset="UTF-8">
        <meta name="viewport" content="width=device-width, initial-scale=1.0">
        <title>Quản lý Phòng Bệnh</title>

        <%-- BƯỚC 1: THÊM FONT AWESOME (BẮT BUỘC CHO ICON) --%>
        <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.5.1/css/all.min.css">

        <%-- BƯỚC 2: LIÊN KẾT CSS VÀ JS CỦA BẠN --%>
        <link rel="stylesheet" href="css/PhongBenh.css">

        <%-- defer: Giúp trang tải nhanh hơn, JS chạy sau khi HTML tải xong --%>
        <script src="<c:url value='/js/darkmodeQuang.js'/>" defer></script>
        <script src="<c:url value='/js/danhSachPhieuKham.js'/>" defer></script>

    </head>

    <body>

        <%-- BƯỚC 3: DÙNG CLASS .container LÀM KHUNG CHÍNH --%>
        <div class="container">

            <h1>Quản lý Phòng Bệnh</h1>

            <%-- BƯỚC 4: CẬP NHẬT THÔNG BÁO SANG CLASS .alert --%>
            <c:if test="${not empty param.createSuccess}">
                <div class="alert alert-success">Thêm phòng thành công!</div>
            </c:if>
            <c:if test="${not empty param.updateSuccess}">
                <div class="alert alert-success">Cập nhật phòng thành công!</div>
            </c:if>
            <c:if test="${not empty param.deleteSuccess}">
                <div class="alert alert-success">Xóa phòng thành công!</div>
            </c:if>
            <c:if test="${not empty param.deleteError}">
                <div class="alert alert-danger">
                    Lỗi xóa phòng: <c:out value="${param.deleteError}"/>
                </div>
            </c:if>

            <%-- BƯỚC 5: DÙNG .page-header ĐỂ GOM NHÓM CÁC NÚT ĐIỀU KHIỂN --%>
            <div class="page-header">

                <%-- Nút Thêm Mới --%>
                <a href="MainController?action=showCreateRoomForm" class="btn btn-create-new">
                    <i class="fa-solid fa-plus"></i> Thêm phòng bệnh
                </a>

                <%-- Thanh Tìm Kiếm (dùng cấu trúc từ CSS) --%>
                <div class="search-container">
                    <form action="MainController" method="GET" class="search-form">
                        <input type="hidden" name="action" value="listRooms">
                        <i class="fa-solid fa-magnifying-glass search-icon-left"></i>
                        <input type="text"
                               id="searchKeyword"
                               name="searchKeyword"
                               class="form-control"
                               value="<c:out value='${param.searchKeyword}'/>"
                               placeholder="Tìm tên phòng, loại, tên khoa...">
                        <button type="submit" class="search-button">
                            <i class="fa-solid fa-magnifying-glass"></i>
                        </button>
                    </form>

                    <%-- Nút Xóa Lọc --%>
                    <a href="MainController?action=listRooms" class="btn btn-clear-search">
                        <i class="fa-solid fa-times"></i> Xóa lọc
                    </a>
                </div>

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
            </div>

            <%-- Tiêu đề cho bảng --%>
            <h3>Danh sách Phòng Bệnh</h3>

            <%-- BƯỚC 6: DÙNG .table-responsive VÀ STYLE BẢNG TỪ CSS --%>
            <div class="table-responsive" id="room-list-container">
                <%-- ID "room-list-container" ở trên sẽ được dùng bởi JS --%>
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

                                <td class="actions-cell">
                                    <%-- 
                                        CÁCH 1 (Mặc định): Sửa (Xanh lá), Xóa (Xanh dương)
                                    --%>
                                    <%-- Nút Sửa (style <a> trong bảng) --%>
                                    <a href="MainController?action=getRoomForUpdate&roomId=${room.id}">
                                        <i class="fa-solid fa-pencil"></i> Sửa
                                    </a>

                                    <%-- Nút Xóa (style <button> trong bảng) --%>
                                    <form action="MainController" method="POST"
                                          onsubmit="return confirm('Bạn có chắc chắn muốn xóa phòng [${room.tenPhong}]?');">
                                        <input type="hidden" name="action" value="deleteRoom">
                                        <input type="hidden" name="roomId" value="${room.id}">
                                        <button type="submit">
                                            <i class="fa-solid fa-trash"></i> Xóa
                                        </button>
                                    </form>

                                    <%-- 
                                        CÁCH 2 (Khuyên dùng): Sửa (Vàng), Xóa (Đỏ)
                                        Để dùng cách này, hãy XÓA CÁCH 1 ở trên
                                        và bỏ comment CÁCH 2 ở dưới.
                                    --%>
                                    <%--
                                    <a href="MainController?action=getRoomForUpdate&roomId=${room.id}" class="btn-warning">
                                        <i class="fa-solid fa-pencil"></i> Sửa
                                    </a>
                                    <form action="MainController" method="POST"
                                          onsubmit="return confirm('Bạn có chắc chắn muốn xóa phòng [${room.tenPhong}]?');">
                                        <input type="hidden" name="action" value="deleteRoom">
                                        <input type="hidden" name="roomId" value="${room.id}">
                                        <button type="submit" class="btn-danger">
                                            <i class="fa-solid fa-trash"></i> Xóa
                                        </button>
                                    </form>
                                    --%>
                                </td>
                            </tr>
                        </c:forEach>

                        <%-- Xử lý khi không có kết quả --%>
                        <c:if test="${empty roomList}">
                            <tr>
                                <td colspan="6">
                                    Không tìm thấy phòng bệnh nào.
                                </td>
                            </tr>
                        </c:if>
                    </tbody>
                </table>
            </div> <%-- End .table-responsive --%>

        </div> <%-- End .container --%>

    </body>
</html>