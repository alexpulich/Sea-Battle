package ru.ifmo.practice.seabattle.server.battleservers;

import javax.servlet.http.HttpSession;
import java.security.Principal;

public class PrincipalWithSession implements Principal {
    private final HttpSession session;

    public PrincipalWithSession(HttpSession session) {
        this.session = session;
    }

    public HttpSession getSession() {
        return session;
    }

    @Override
    public String getName() {
        return "";
    }
}