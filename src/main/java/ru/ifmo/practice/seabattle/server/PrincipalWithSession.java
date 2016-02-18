package ru.ifmo.practice.seabattle.server;

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
        return ""; // whatever is appropriate for your app, e.g., user ID
    }
}