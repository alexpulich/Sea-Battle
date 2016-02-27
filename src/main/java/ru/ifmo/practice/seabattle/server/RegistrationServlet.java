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
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RegistrationServlet extends HttpServlet {
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.setCharacterEncoding("UTF-8");
        resp.setCharacterEncoding("UTF-8");
        String email = req.getParameter("email").trim();
        String password = req.getParameter("password").trim();
        String confirmPassword = req.getParameter("password-confirm").trim();
        String nickname = req.getParameter("login").trim();

        PrintWriter respWriter = resp.getWriter();
        RegistrationResponse regResp = new RegistrationResponse();

        if (!validateEmail(email)) {
            regResp.setValidEmail(false);
        }
        if (!validateNickname(nickname)) {
            regResp.setValidNickname(false);
        }
        if (!validatePassword(password)) {
            regResp.setValidPassword(false);
        }
        if (!password.equals(confirmPassword)) {
            regResp.setValidPassConfirm(false);
        }

        try {
            if (!DAOFactory.getInstance().getUserDAOimpl().isNicknameUnique(nickname)) {
                regResp.setUniqueNickname(false);
            }
            if (!DAOFactory.getInstance().getUserDAOimpl().isEmailUnique(email)) {
                regResp.setUniqueEmail(false);
            }
        } catch (SQLException e) {
            regResp.setServerOk(false);
        }

        MessageDigest md = null;
        try {
            md = MessageDigest.getInstance("MD5");
        } catch (NoSuchAlgorithmException e) {
            regResp.setServerOk(false);
        }

        String hashString = null;
        if (md != null) {
            byte[] passBytes = password.getBytes("UTF-8");
            byte[] hashBytes = md.digest(passBytes);
            hashString = DatatypeConverter.printHexBinary(hashBytes);
        }

        User user = new User(nickname, email, hashString);
        try {
            DAOFactory.getInstance().getUserDAOimpl().addUser(user);
        } catch (SQLException e) {
            regResp.setServerOk(false);
            regResp.setUserRegistered(false);
        }

        User usr = null;
        try {
            usr = DAOFactory.getInstance().getUserDAOimpl().getUserByNickname(user.getUser_nickname());//для получения id юзера после внесения в базу
        } catch (SQLException e) {
            regResp.setServerOk(false);
        }
        if (usr != null) {
            HttpSession session = req.getSession(true);
            session.setAttribute("nickname", nickname);
            session.setAttribute("id", user.getId());
            Log.getInstance().sendMessage(this.getClass(), "Зарегистрирован пользователь " + user.getId() + "  " + user.getUser_nickname() + "  " + user.getEmail());
        }

        String message = new Gson().toJson(new Message<>(regResp));
        respWriter.print(message);
    }

    private boolean validateEmail(String email) {
        //official standard RFC 5322 regex
        Pattern pattern = Pattern.compile("(?:[a-z0-9!#$%&'*+/=?^_`{|}~-]+(?:\\.[a-z0-9!#$%&'*+/=?^_`{|}~-]+)*" +
                "|\"(?:[\\x01-\\x08\\x0b\\x0c\\x0e-\\x1f\\x21\\x23-\\x5b\\x5d-\\x7f]|\\\\[\\x01-\\x09\\x0b\\x0c\\x0e-\\x7f])*\")" +
                "@(?:(?:[a-z0-9](?:[a-z0-9-]*[a-z0-9])?\\.)+[a-z0-9](?:[a-z0-9-]*[a-z0-9])?" +
                "|\\[(?:(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.){3}(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?|" +
                "[a-z0-9-]*[a-z0-9]:(?:[\\x01-\\x08\\x0b\\x0c\\x0e-\\x1f\\x21-\\x5a\\x53-\\x7f]|\\\\[\\x01-\\x09\\x0b\\x0c\\x0e-\\x7f])+)\\])");
        Matcher matcher = pattern.matcher(email);
        if (!matcher.matches()) {
            return false;
        }
        if (email.length() > 254) {
            return false;
        }
        return true;
    }

    private boolean validateNickname(String nickname) {
        Pattern pattern = Pattern.compile("\\w{2,20}");
        Matcher matcher = pattern.matcher(nickname);
        return matcher.matches();
    }

    private boolean validatePassword(String password) {
        Pattern pattern = Pattern.compile("[a-zA-Z0-9_!^-]{6,20}");
        Matcher matcher = pattern.matcher(password);
        return matcher.matches();
    }
}
