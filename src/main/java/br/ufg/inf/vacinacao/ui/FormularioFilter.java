package br.ufg.inf.vacinacao.ui;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.util.UUID;

@WebFilter("/*")
public class FormularioFilter implements Filter {
    @Override
    public void doFilter(ServletRequest requisicao, ServletResponse resposta, FilterChain cadeia)
            throws IOException, ServletException {
        requisicao.setCharacterEncoding("UTF-8");
        resposta.setCharacterEncoding("UTF-8");
        HttpServletRequest request = (HttpServletRequest) requisicao;
        HttpServletResponse response = (HttpServletResponse) resposta;
        if ("POST".equalsIgnoreCase(request.getMethod())) {
            HttpSession session = request.getSession(false);
            String esperado = session == null ? null : (String) session.getAttribute("csrf");
            if (esperado == null || !esperado.equals(request.getParameter("csrf"))) {
                response.sendError(403, "Reabra o formulário e tente novamente.");
                return;
            }
        } else if ("GET".equalsIgnoreCase(request.getMethod())
                && (request.getServletPath().isEmpty() || request.getServletPath().equals("/vacinas")
                || request.getServletPath().equals("/alergias") || request.getServletPath().equals("/usuarios") || request.getServletPath().equals("/agendas"))) {
            HttpSession session = request.getSession();
            if (session.getAttribute("csrf") == null) {
                session.setAttribute("csrf", UUID.randomUUID().toString());
            }
        }
        cadeia.doFilter(requisicao, resposta);
    }
}
