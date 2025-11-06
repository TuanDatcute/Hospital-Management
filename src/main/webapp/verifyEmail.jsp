<%-- 
    Document   : verifyEmail.jsp
    Mô tả      : Trang chờ xác thực email có đếm ngược + nút gửi lại động.
--%>
<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<c:url var="controllerUrl" value="/UserController" />

<!DOCTYPE html>
<html lang="vi">
    <head>
        <meta charset="UTF-8">
        <title>Kiểm tra Email của bạn</title>
        <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.2/css/all.min.css">
        <link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/css/style.css">

        <style>
            .progress-container {
                width: 100%;
                height: 6px;
                background-color: #e9ecef;
                border-radius: 5px;
                overflow: hidden;
                margin-top: 10px;
            }

            .progress-bar {
                height: 100%;
                background-color: #007bff;
                width: 100%;
                transition: width 1s linear;
            }

            .btn-submit:disabled {
                opacity: 0.8;
                cursor: not-allowed;
            }
        </style>
    </head>

    <body class="login-page-body">
        <div class="login-container" style="max-height: 520px;">
            <div class="form-container sign-in" style="opacity: 1; z-index: 5; width: 100%;">

                <a href="${pageContext.request.contextPath}/login.jsp" class="home-link" 
                   title="Quay về Trang Đăng nhập" style="left: 20px; top: 20px;">
                    <i class="fas fa-arrow-left"></i>
                </a>

                <form action="${controllerUrl}" method="POST" class="login-form" style="padding: 0 40px;">
                    <input type="hidden" name="action" value="resendVerification">
                    <input type="hidden" name="email" value="<c:out value='${requestScope.email}'/>">

                    <h1 class="form-title" style="font-size: 24px;">
                        <i class="fas fa-envelope-open-text" 
                           style="color: #007bff; margin-bottom: 15px; font-size: 3rem;"></i>
                        <br>Đăng ký gần hoàn tất!
                    </h1>

                    <p style="text-align: center; margin-bottom: 20px; font-size: 15px; line-height: 1.6;">
                        Chúng tôi đã gửi một email xác thực đến
                        <br><strong><c:out value='${requestScope.email}'/></strong>
                        <br><br>Vui lòng kiểm tra email của bạn (cả mục Spam) để kích hoạt tài khoản.
                    </p>

                    <c:if test="${not empty requestScope.ERROR_MESSAGE}">
                        <p class="error-message">${requestScope.ERROR_MESSAGE}</p>
                    </c:if>

                    <c:if test="${not empty requestScope.SUCCESS_MESSAGE}">
                        <p class="success-message">${requestScope.SUCCESS_MESSAGE}</p>
                    </c:if>

                    <div class="form-group">
                        <button type="submit" class="btn-submit" id="resend-button" disabled>
                            <i class="fas fa-clock"></i> Gửi lại sau (<span id="countdown">30</span>s)
                        </button>

                        <!-- Thanh tiến trình -->
                        <div class="progress-container">
                            <div id="progress-bar" class="progress-bar"></div>
                        </div>
                    </div>
                </form>
            </div>
        </div>

        <!-- JAVASCRIPT COUNTDOWN + RESET SAU KHI GỬI LẠI -->
        <script>
            document.addEventListener("DOMContentLoaded", function () {
                const resendButton = document.getElementById('resend-button');
                const progressBar = document.getElementById('progress-bar');

                const totalTime = 30; // Thời gian đếm ngược (giây)
                let countdown = totalTime;
                let timer = null;

                const ICON_WAIT = "fas fa-clock";
                const ICON_SEND = "fas fa-paper-plane";
                const TEXT_SEND = "Gửi lại email xác thực";

                // --- Hàm bắt đầu đếm ngược ---
                function startCountdown() {
                    clearInterval(timer); // Tránh lỗi chạy trùng timer
                    resendButton.disabled = true;
                    countdown = totalTime;

                    // Cập nhật giao diện ban đầu
                    resendButton.innerHTML = `<i class="${ICON_WAIT}"></i> Gửi lại sau (<span id='countdown'>${countdown}</span>s)`;
                    progressBar.style.width = "100%";
                    progressBar.style.transition = 'width 1s linear';

                    // Cập nhật mỗi giây
                    timer = setInterval(() => {
                        countdown--;

                        if (countdown <= 0) {
                            // Hết giờ: Đặt trạng thái cuối cùng
                            clearInterval(timer);
                            resendButton.disabled = false;
                            resendButton.innerHTML = '<i class="fas fa-paper-plane"></i> Gửi lại email xác thực';
                            progressBar.style.width = "0%";
                        } else {
                            // Đang đếm: Chỉ cập nhật số, KHÔNG cập nhật lại toàn bộ innerHTML
                            const countdownSpan = document.getElementById('countdown');
                            if (countdownSpan) {
                                countdownSpan.textContent = countdown;
                            }
                            progressBar.style.width = (countdown / totalTime) * 100 + "%";
                        }
                    }, 1000);
                }

                // --- Bắt đầu đếm khi load trang ---
                startCountdown();

                // <-- KHỐI addEventListener ĐÃ BỊ XÓA HOÀN TOÀN -->
            });
        </script>
    </body>
</html>
