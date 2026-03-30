package com.example.auth;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;
import java.sql.*;
import org.mindrot.jbcrypt.BCrypt;

@WebServlet("/login")
public class LoginServlet extends HttpServlet {
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.setCharacterEncoding("UTF-8");
        resp.setCharacterEncoding("UTF-8");

        String username = req.getParameter("username");
        String password = req.getParameter("password");
        String ip = req.getRemoteAddr();

        if (username == null || username.trim().isEmpty() || password == null || password.isEmpty()) {
            req.setAttribute("loginError", "Vui lòng nhập tên đăng nhập và mật khẩu.");
            req.getRequestDispatcher("login.jsp").forward(req, resp);
            return;
        }

        boolean success = false;
        try (Connection conn = DBUtil.getConn();
             PreparedStatement ps = conn.prepareStatement("SELECT password_hash FROM users WHERE username = ?")) {

            ps.setString(1, username.trim());
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    String storedHash = rs.getString("password_hash");
                    if (storedHash != null && BCrypt.checkpw(password, storedHash)) {
                        success = true;
                    }
                }
            }

            // Ghi log lần đăng nhập (lưu hash của mật khẩu nhập)
            String attemptHash = BCrypt.hashpw(password, BCrypt.gensalt(10));
            try (PreparedStatement logPs = conn.prepareStatement(
                    "INSERT INTO login_logs (username, attempted_password_hash, success, ip_address) VALUES (?, ?, ?, ?)")) {
                logPs.setString(1, username.trim());
                logPs.setString(2, attemptHash);
                logPs.setInt(3, success ? 1 : 0);
                logPs.setString(4, ip);
                logPs.executeUpdate();
            }

        } catch (Exception e) {
            // Ghi log server (không in mật khẩu)
            e.printStackTrace();
            throw new ServletException(e);
        }

        if (success) {
            HttpSession session = req.getSession(true);
            session.setAttribute("user", username.trim());
            resp.sendRedirect("success.jsp");
        } else {
            req.setAttribute("loginError", "Tên đăng nhập hoặc mật khẩu không đúng");
            req.getRequestDispatcher("login.jsp").forward(req, resp);
        }
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.sendRedirect("login.jsp");
    }
}
