package ru.ifmo.practice.seabattle.server.loginserver;

import com.google.gson.Gson;
import ru.ifmo.practice.seabattle.server.Message;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.io.PrintWriter;

public class LogoutServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.setCharacterEncoding("UTF-8");
        resp.setCharacterEncoding("UTF-8");
        resp.setHeader("Access-Control-Allow-Origin", "*");
        PrintWriter respWriter = resp.getWriter();

        //Флаги
        boolean logout = false;
        boolean wasLogged = false;
        //

        HttpSession session = req.getSession();
        if (session != null) {
            wasLogged = true;
            session.invalidate();
            logout = true;
        }
        LogoutResponse logResp = new LogoutResponse(logout, wasLogged);
        String message = new Gson().toJson(new Message<>(logResp));
        respWriter.print(message);
        respWriter.close();
    }
}
