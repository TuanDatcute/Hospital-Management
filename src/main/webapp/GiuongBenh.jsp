<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>

<!DOCTYPE html>
<html lang="vi">
    <head>
        <meta charset="UTF-8">
        <meta name="viewport" content="width=device-width, initial-scale=1.0">
        <title>Sơ đồ Giường bệnh</title>

        <%-- BẮT BUỘC: Thêm Font Awesome cho icon --%>
        <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.5.1/css/all.min.css">

        <%-- File CSS và JS của bạn --%>
        <link rel="stylesheet" href="<c:url value='/css/_sidebar.css'/>">
        <link rel="stylesheet" href="css/GiuongBenh.css">
        <link rel="stylesheet" href="css/StyleChungCuaQuang.css">
        <script src="<c:url value='/js/darkmodeQuang.js'/>" defer></script>
        <script src="<c:url value='/js/theme.js'/>"></script>
    </head>

    <body>
        <jsp:include page="_sidebar.jsp" />
        <div class="main-content">
            <%-- Dùng .container để bọc toàn bộ trang --%>
            <div class="container">

                <h1>Sơ đồ Giường bệnh</h1>

                <%-- HIỂN THỊ THÔNG BÁO --%>
                <c:if test="${not empty param.createBedSuccess}">
                    <div class="alert alert-success">Thêm giường mới thành công!</div>
                </c:if>
                <c:if test="${not empty param.assignSuccess}">
                    <div class="alert alert-success">Gán giường thành công!</div>
                </c:if>
                <c:if test="${not empty param.deleteSuccess}">
                    <div class="alert alert-success">Xóa giường thành công!</div>
                </c:if>

                <%-- === THAY ĐỔI 1: Trả về alert-info (CSS mới đã hỗ trợ) === --%>
                <c:if test="${not empty param.updateSuccess}">
                    <div class="alert alert-info">Cập nhật giường thành công!</div>
                </c:if>
                <c:if test="${not empty param.releaseSuccess}">
                    <div class="alert alert-info">Trả giường thành công!</div>
                </c:if>

                <%-- Lỗi (danger) --%>
                <c:if test="${not empty param.createBedError}">
                    <div class="alert alert-danger">Lỗi thêm giường: <c:out value="${param.createBedError}"/></div>
                </c:if>
                <c:if test="${not empty param.assignError}">
                    <div class="alert alert-danger">Lỗi gán giường: <c:out value="${param.assignError}"/></div>
                </c:if>
                <c:if test="${not empty param.deleteError}">
                    <div class="alert alert-danger">Lỗi xóa giường: <c:out value="${param.deleteError}"/></div>
                </c:if>
                <c:if test="${not empty param.error}">
                    <div class="alert alert-danger">Lỗi: ${param.error}</div>
                </c:if>

                <%-- HEADER (Chứa Nút Thêm, Tìm kiếm, Toggles) --%>
                <div class="page-header">

                    <%-- Nút Thêm Mới --%>
                    <a href="MainController?action=showCreateBedForm" class="btn btn-create-new">
                        <i class="fa-solid fa-plus"></i> Thêm Giường Mới
                    </a>

                    <%-- Thanh Tìm Kiếm --%>
                    <div class="search-container">
                        <form action="MainController" method="GET" class="search-form">
                            <input type="hidden" name="action" value="listBeds">
                            <i class="fa-solid fa-magnifying-glass search-icon-left"></i>
                            <input type="text"
                                   id="searchKeyword"
                                   name="searchKeyword"
                                   class="form-control"
                                   value="<c:out value='${param.searchKeyword}'/>"
                                   placeholder="Tìm tên giường, phòng, bệnh nhân...">
                            <button type="submit" class="search-button">
                                <i class="fa-solid fa-magnifying-glass"></i>
                            </button>
                        </form>
                        <a href="MainController?action=listBeds" class="btn btn-clear-search">
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

                <h3>Danh sách Giường</h3>

                <%-- BẢNG SƠ ĐỒ GIƯỜNG --%>
                <div class="table-responsive" id="bed-list-container">
                    <table>
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
                                        <%-- === THAY ĐỔI 2: Dùng class động (CSS mới đã hỗ trợ) === --%>
                                        <span class="status status-${bed.trangThai.toLowerCase()}">
                                            <c:out value="${bed.trangThai == 'DANG_SU_DUNG' ? 'Đang sử dụng' : 'Trống'}"/>
                                        </span>
                                    </td>
                                    <td>
                                        <c:out value="${bed.benhNhanId != null ? bed.tenBenhNhan : '---'}"/>
                                    </td>
                                    <td class="actions-cell">

                                        <%-- Form Gán (khi TRONG) --%>
                                        <c:if test="${bed.trangThai == 'TRONG'}">
                                            <form action="MainController" method="POST" class="inline-assign-form">
                                                <input type="hidden" name="action" value="assignBed">
                                                <input type="hidden" name="bedId" value="${bed.id}">
                                                <select name="patientId" required>
                                                    <option value="">-- Chọn bệnh nhân --</option>
                                                    <c:forEach var="patient" items="${patientList}">
                                                        <option value="${patient.id}"><c:out value="${patient.hoTen}"/></option>
                                                    </c:forEach>
                                                </select>
                                                <%-- CSS mới đã định nghĩa .btn-primary và .btn-sm --%>
                                                <button type="submit" class="btn btn-sm btn-primary">Gán</button>
                                            </form>
                                        </c:if>

                                        <%-- Form Trả (khi DANG_SU_DUNG) --%>
                                        <c:if test="${bed.trangThai == 'DANG_SU_DUNG'}">
                                            <form action="MainController" method="POST" class="inline-form">
                                                <input type="hidden" name="action" value="releaseBed">
                                                <input type="hidden" name="bedId" value="${bed.id}">
                                                <%-- CSS mới đã định nghĩa .btn-secondary --%>
                                                <button type="submit" class="btn btn-sm btn-secondary">Trả giường</button>
                                            </form>
                                        </c:if>

                                        <%-- Nút Sửa (chỉ khi TRONG) --%>
                                        <c:if test="${bed.trangThai == 'TRONG'}">
                                            <%-- CSS mới đã định nghĩa .btn-warning --%>
                                            <a href="MainController?action=getBedForUpdate&bedId=${bed.id}" class="btn btn-sm btn-warning">
                                                <i class="fa-solid fa-pencil"></i> Sửa
                                            </a>
                                        </c:if>

                                        <%-- Form Xóa (chỉ khi TRONG) --%>
                                        <c:if test="${bed.trangThai == 'TRONG'}">
                                            <form action="MainController" method="POST" class="inline-form"
                                                  onsubmit="return confirm('Bạn có chắc chắn muốn xóa giường [${bed.tenGiuong}] không?');">
                                                <input type="hidden" name="action" value="deleteBed">
                                                <input type="hidden" name="bedId" value="${bed.id}">
                                                <%-- CSS mới đã định nghĩa .btn-danger --%>
                                                <button type="submit" class="btn btn-sm btn-danger">
                                                    <i class="fa-solid fa-trash"></i> Xóa
                                                </button>
                                            </form>
                                        </c:if>
                                    </td>
                                </tr>
                            </c:forEach>
                            <c:if test="${empty bedList}">
                                <tr>
                                    <td colspan="5">Không tìm thấy giường bệnh nào.</td>
                                </tr>
                            </c:if>
                        </tbody>
                    </table>
                </div>
            </div> <%-- End .container --%>
        </div>
    </body>
</html>