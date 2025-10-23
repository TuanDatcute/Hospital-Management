<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>

<!DOCTYPE html>
<html lang="vi">
    <head>
        <meta charset="UTF-8">
        <meta name="viewport" content="width=device-width, initial-scale=1.0">
        <title>Thông tin Phiếu Khám Bệnh</title>
        <style>
            body {
                font-family: Arial, sans-serif;
                background-color: #f4f4f9;
                display: flex;
                justify-content: center;
                align-items: center;
                padding: 20px;
            }
            .container {
                background-color: #ffffff;
                padding: 25px 40px;
                border-radius: 8px;
                box-shadow: 0 4px 8px rgba(0, 0, 0, 0.1);
                width: 100%;
                max-width: 800px;
            }
            h1 {
                text-align: center;
                color: #333;
                margin-bottom: 20px;
            }
            .form-grid {
                display: grid;
                grid-template-columns: 1fr 1fr;
                gap: 20px;
            }
            .form-group {
                display: flex;
                flex-direction: column;
            }
            .full-width {
                grid-column: 1 / -1; /* Trải dài trên cả 2 cột */
            }
            label {
                margin-bottom: 5px;
                font-weight: bold;
                color: #555;
            }
            input[type="text"],
            input[type="number"],
            input[type="datetime-local"],
            textarea {
                padding: 10px;
                border: 1px solid #ccc;
                border-radius: 4px;
                font-size: 16px;
                width: 100%;
                box-sizing: border-box;
            }
            textarea {
                resize: vertical;
                min-height: 80px;
            }
            .button-group {
                grid-column: 1 / -1;
                display: flex;
                justify-content: flex-end;
                gap: 10px;
                margin-top: 20px;
            }
            button {
                padding: 10px 20px;
                border: none;
                border-radius: 4px;
                font-size: 16px;
                cursor: pointer;
            }
            .btn-submit {
                background-color: #007bff;
                color: white;
            }
            .btn-reset {
                background-color: #6c757d;
                color: white;
            }
        </style>
    </head>
    <body>

        <div class="container">
            <h1>Phiếu Khám Bệnh</h1>

            <form action="EMRCoreController" method="POST">

                <input type="hidden" id="phieuKhamBenhId" name="action" value="createEncounter">

                <div class="form-grid">
                    <div class="form-group">
                        <label for="maPhieuKham">Mã Phiếu Khám</label>
                        <input type="text" id="maPhieuKham" name="maPhieuKham" required>
                    </div>

                    <div class="form-group">
                        <label for="thoiGianKham">Thời Gian Khám</label>
                        <input type="datetime-local" id="thoiGianKham" name="thoiGianKham" required>
                    </div>

                    <div class="form-group">
                        <label for="benhNhanId">ID Bệnh Nhân</label>
                        <input type="number" id="benhNhanId" name="benhNhanId" required>
                    </div>

                    <div class="form-group">
                        <label for="nhanVienId">ID Nhân Viên (Bác sĩ)</label>
                        <input type="number" id="nhanVienId" name="bacSiId" required>
                    </div>

                    <div class="form-group full-width">
                        <label for="trieuChung">Triệu Chứng</label>
                        <textarea id="trieuChung" name="trieuChung" rows="3"></textarea>
                    </div>

                    <div class="form-group">
                        <label for="nhietDo">Nhiệt Độ (°C)</label>
                        <input type="number" id="nhietDo" name="nhietDo" step="0.1" placeholder="Ví dụ: 37.5">
                    </div>

                    <div class="form-group">
                        <label for="huyetAp">Huyết Áp (mmHg)</label>
                        <input type="text" id="huyetAp" name="huyetAp" placeholder="Ví dụ: 120/80">
                    </div>

                    <div class="form-group">
                        <label for="nhipTim">Nhịp Tim (lần/phút)</label>
                        <input type="number" id="nhipTim" name="nhipTim" placeholder="Ví dụ: 80">
                    </div>

                    <div class="form-group">
                        <label for="nhipTho">Nhịp Thở (lần/phút)</label>
                        <input type="number" id="nhipTho" name="nhipTho" placeholder="Ví dụ: 18">
                    </div>

                    <div class="form-group full-width">
                        <label for="chanDoan">Chẩn Đoán</label>
                        <textarea id="chanDoan" name="chanDoan" rows="4"></textarea>
                    </div>

                    <div class="form-group full-width">
                        <label for="ketLuan">Kết Luận & Dặn Dò</label>
                        <textarea id="ketLuan" name="ketLuan" rows="4"></textarea>
                    </div>

                    <div class="form-group">
                        <label for="ngayTaiKham">Ngày Tái Khám</label>
                        <input type="datetime-local" id="ngayTaiKham" name="ngayTaiKham">
                    </div>

                    <div class="form-group">
                        <label for="lichHenId">ID Lịch Hẹn (nếu có)</label>
                        <input type="number" id="lichHenId" name="lichHenId">
                    </div>
                    <c:if test="${not empty ERROR_MESSAGE}">
                        <div class="error-box">
                            <strong>Lỗi!</strong> ${requestScope.ERROR_MESSAGE}
                        </div>
                    </c:if>
                    <div class="button-group">
                        <button type="reset" class="btn-reset">Làm lại</button>
                        <button type="submit" class="btn-submit">Lưu Phiếu Khám</button>
                    </div>
                </div>
            </form>
        </div>

    </body>
</html>