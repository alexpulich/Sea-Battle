package ru.ifmo.practice.seabattle.server;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import java.io.IOException;
import java.security.Principal;

@WebFilter("/pvbserver")
public class PvBServerFilter implements Filter {
    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        final PrincipalWithSession p = new PrincipalWithSession(httpRequest.getSession());
        HttpServletRequestWrapper wrappedRequest = new HttpServletRequestWrapper(httpRequest) {
            @Override
            public Principal getUserPrincipal() {
                return p;
            }
        };
        chain.doFilter(wrappedRequest, response);
    }

    @Override
    public void init(FilterConfig config) throws ServletException { }
    @Override
    public void destroy() { }
}