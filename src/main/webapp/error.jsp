<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
    <head>
        <meta charset="UTF-8">
        <title>Lỗi truy cập</title>
        <style>
            body {
                font-family: Arial, sans-serif;
                text-align: center;
                padding: 50px;
            }
            .error-container {
                border: 1px solid #ddd;
                padding: 30px;
                display: inline-block;
                background-color: #f9f9f9;
                box-shadow: 0 2px 4px rgba(0,0,0,0.1);
            }
            h1 {
                color: #d9534f;
            }
            p {
                font-size: 1.2em;
            }
            a {
                color: #0275d8;
                text-decoration: none;
            }
        </style>
    </head>
    <body>
        <div class="error-container">
            <h1>Truy cập bị từ chối</h1>
            <p>
                <%-- Lấy thông báo lỗi từ request (do Filter đẩy qua) --%>
                ${requestScope.errorMessage != null ? requestScope.errorMessage : "Bạn không có quyền để thực hiện hành động này."}
            </p>
            <br/>
            <p><a href="javascript:history.back()">Quay lại trang trước</a></p>
        </div>
    </body>
</html>