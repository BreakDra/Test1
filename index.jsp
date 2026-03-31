<%@ page contentType="text/html; charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html lang="vi">
<head>
  <meta charset="utf-8">
  <title>Đăng nhập</title>
  <link rel="stylesheet" href="css/styles.css">
</head>
<body>
  <main class="center">
    <form class="card" method="post" action="${pageContext.request.contextPath}/login">
      <h2 class="title">Đăng nhập</h2>
      <label for="username">Tên đăng nhập</label>
      <input id="username" name="username" type="text" required>
      <label for="password">Mật khẩu</label>
      <input id="password" name="password" type="password" required>
      <button type="submit">Đăng nhập</button>
    </form>

    <p class="error"><%= request.getAttribute("loginError") != null ? request.getAttribute("loginError") : "" %></p>

    <p class="muted">Chưa có tài khoản? <a href="register.html">Đăng ký</a></p>
  </main>
</body>
</html>
