package ru.ifmo.practice.seabattle.server;

import ru.ifmo.practice.seabattle.db.DAOFactory;
import ru.ifmo.practice.seabattle.db.User;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.sql.SQLException;

public class LoginServlet extends HttpServlet {
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String email = req.getParameter("email").trim();
        String password = req.getParameter("password").trim();
        User user;
        try{
            user = DAOFactory.getInstance().getUserDAOimpl().login(email, password);
        }
        catch (SQLException e){
            e.printStackTrace();
            resp.sendError(resp.SC_INTERNAL_SERVER_ERROR, "server error");
        }
        if (user==null){
            resp.sendError(resp.SC_CONFLICT, "invalid email or password");
        }
        HttpSession session=req.getSession(true);
        session.setAttribute("nickname", user.getUser_nickname());
        resp.setStatus(resp.SC_OK);
    }
}
