package ru.ifmo.practice.seabattle.server;

import com.google.gson.Gson;
import ru.ifmo.practice.seabattle.db.DAOFactory;
import ru.ifmo.practice.seabattle.db.User;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.xml.bind.DatatypeConverter;
import java.io.IOException;
import java.io.PrintWriter;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.SQLException;

public class LoginServlet extends HttpServlet {
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.setCharacterEncoding("UTF-8");
        resp.setCharacterEncoding("UTF-8");
        String email = req.getParameter("email").trim();
        String password = req.getParameter("password").trim();

        //Флаги
        boolean login = true;
        boolean serverOk = true;
        //

        PrintWriter respWriter = resp.getWriter();

        MessageDigest md = null;
        try {
            md = MessageDigest.getInstance("MD5");
        } catch (NoSuchAlgorithmException e) {
            serverOk = false;
        }

        String hashString = null;
        if (md != null) {
            byte[] passBytes = password.getBytes("UTF-8");
            byte[] hashBytes = md.digest(passBytes);
            hashString = DatatypeConverter.printHexBinary(hashBytes);
        }
        User user = null;
        try {
            user = DAOFactory.getInstance().getUserDAOimpl().login(email, hashString);
        } catch (SQLException e) {
            serverOk = false;
        }
        if (user == null) {
            serverOk = false;
        } else {
            HttpSession session = req.getSession(true);
            session.setAttribute("nickname", user.getUser_nickname());
            session.setAttribute("id", user.getId());
            Log.getInstance().sendMessage(this.getClass(), "Авторизовался пользователь " + user.getId() + "  " + user.getUser_nickname() + "  " + user.getEmail());
        }
        LoginResponse logResp = new LoginResponse(login, serverOk);
        String message = new Gson().toJson(new Message<>(logResp));
        respWriter.print(message);
    }
}
