package com.example.auth;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;
import java.sql.*;
import java.sql.SQLIntegrityConstraintViolationException;
import org.mindrot.jbcrypt.BCrypt;

@WebServlet("/register")
public class RegistrationServlet extends HttpServlet {

  @Override
  protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
    req.setCharacterEncoding("UTF-8");
    resp.setCharacterEncoding("UTF-8");

    String username = req.getParameter("username");
    String password = req.getParameter("password");

    if (username == null || username.trim().isEmpty() || password == null || password.isEmpty()) {
      resp.setContentType("text/html; charset=UTF-8");
      resp.getWriter().println("Vui lòng nhập tên đăng nhập và mật khẩu. <a href='register.html'>Quay lại</a>");
      return;
    }

    // Hash mật khẩu bằng BCrypt
    String hashed = BCrypt.hashpw(password, BCrypt.gensalt(10));

    // Lưu vào DB
    try (Connection conn = DBUtil.getConn();
         PreparedStatement ps = conn.prepareStatement("INSERT INTO users (username, password_hash) VALUES (?, ?)")) {

      ps.setString(1, username.trim());
      ps.setString(2, hashed);
      ps.executeUpdate();

      // Sau khi tạo tài khoản thành công, chuyển về trang login
      resp.sendRedirect("login.html");

    } catch (SQLIntegrityConstraintViolationException e) {
      // username đã tồn tại
      resp.setContentType("text/html; charset=UTF-8");
      resp.getWriter().println("Tên đăng nhập đã tồn tại. <a href='register.html'>Quay lại</a>");
    } catch (Exception e) {
      throw new ServletException(e);
    }
  }
}
