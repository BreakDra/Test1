package com.example.auth;

import org.mindrot.jbcrypt.BCrypt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@Controller
public class LoginController {

    @Autowired
    private JdbcTemplate jdbc;

    @PostMapping("/login")
    public String login(@RequestParam String username,
                        @RequestParam String password,
                        HttpServletRequest request) throws Exception {

        if (username == null || username.trim().isEmpty() || password == null || password.isEmpty()) {
            String msg = URLEncoder.encode("Vui lòng nhập tên đăng nhập và mật khẩu.", StandardCharsets.UTF_8);
            return "redirect:/index.html?error=" + msg;
        }

        boolean success = false;
        try {
            String sql = "SELECT password_hash FROM users WHERE username = ?";
            String storedHash = jdbc.queryForObject(sql, new Object[]{username.trim()}, String.class);
            if (storedHash != null && BCrypt.checkpw(password, storedHash)) {
                success = true;
            }
        } catch (org.springframework.dao.EmptyResultDataAccessException ex) {
            success = false;
        }

        // Ghi log lần đăng nhập (lưu hash của mật khẩu nhập)
        String attemptHash = BCrypt.hashpw(password, BCrypt.gensalt(10));
        jdbc.update("INSERT INTO login_logs (username, attempted_password_hash, success, ip_address) VALUES (?, ?, ?, ?)",
                username.trim(), attemptHash, success ? 1 : 0, request.getRemoteAddr());

        if (success) {
            HttpSession session = request.getSession(true);
            session.setAttribute("user", username.trim());
            return "redirect:/success";
        } else {
            String msg = URLEncoder.encode("Tên đăng nhập hoặc mật khẩu không đúng", StandardCharsets.UTF_8);
            return "redirect:/index.html?error=" + msg;
        }
    }

    @GetMapping("/success")
    public String success(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("user") == null) {
            return "redirect:/index.html";
        }
        return "success";
    }

    @GetMapping("/login")
    public String loginGet() {
        return "redirect:/index.html";
    }
}
