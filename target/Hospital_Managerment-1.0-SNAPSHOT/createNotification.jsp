<%@ page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <title>Gửi Thông Báo Mới</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.1.3/dist/css/bootstrap.min.css" rel="stylesheet">
</head>
<body>

    <div class="container mt-5" style="max-width: 600px;">
        <h1 class="mb-4">✉️ Soạn Thông Báo Mới</h1>

        <form action="MainController" method="POST">
            
            <input type="hidden" name="action" value="createNotification">

            <div class="mb-3">
                <label for="taiKhoanId" class="form-label">**Gửi đến ID Tài Khoản:**</label>
                <input type="number" class="form-control" id="taiKhoanId" name="taiKhoanId" required>
                <div class="form-text">Nhập ID của người nhận (bệnh nhân, bác sĩ...).</div>
            </div>

            <div class="mb-3">
                <label for="tieuDe" class="form-label">**Tiêu đề:**</label>
                <input type="text" class="form-control" id="tieuDe" name="tieuDe" required>
            </div>

            <div class="mb-3">
                <label for="noiDung" class="form-label">**Nội dung:**</label>
                <textarea class="form-control" id="noiDung" name="noiDung" rows="5" required></textarea>
            </div>

            <button type="submit" class="btn btn-primary">Gửi Thông Báo</button>
            <a href="MainController?action=listNotifications" class="btn btn-secondary">Hủy</a>
        </form>
    </div>

</body>
</html>