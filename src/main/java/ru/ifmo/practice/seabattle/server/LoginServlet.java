package ru.ifmo.practice.seabattle.server;

import ru.ifmo.practice.seabattle.db.DAOFactory;
import ru.ifmo.practice.seabattle.db.User;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.xml.bind.DatatypeConverter;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.SQLException;

public class LoginServlet extends HttpServlet {
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String email = req.getParameter("email").trim();
        String password = req.getParameter("password").trim();
        MessageDigest md = null;

        try {
            md = MessageDigest.getInstance("MD5");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            resp.sendError(resp.SC_INTERNAL_SERVER_ERROR, "server error");
        }

        byte[] passBytes = password.getBytes("UTF-8");
        byte[] hashBytes = md.digest(passBytes);
        String hashString = DatatypeConverter.printHexBinary(hashBytes);

        User user = null;
        try {
            user = DAOFactory.getInstance().getUserDAOimpl().login(email, hashString);
        } catch (SQLException e) {
            e.printStackTrace();
            resp.sendError(resp.SC_INTERNAL_SERVER_ERROR, "server error");
        }
        if (user == null) {
            resp.sendError(resp.SC_CONFLICT, "invalid email or password");
        }
        HttpSession session = req.getSession(true);
        session.setAttribute("nickname", user.getUser_nickname());
        session.setAttribute("id", user.getId());
        resp.setStatus(resp.SC_OK);
    }
}
