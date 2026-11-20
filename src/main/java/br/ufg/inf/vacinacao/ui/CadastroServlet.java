package br.ufg.inf.vacinacao.ui;

import jakarta.persistence.PersistenceException;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.*;
import java.io.IOException;
import java.util.Map;

/** Fluxo comum aos cadastros: formulário, validação, consulta e Post/Redirect/Get. */
public abstract class CadastroServlet extends HttpServlet {
    protected abstract String chaveServico();
    protected abstract String caminho();
    protected abstract String pagina();
    protected abstract void incluir(HttpServletRequest request);
    protected abstract boolean excluir(String id);
    protected abstract void carregarDados(HttpServletRequest request);

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        if (!verificarBanco(request, response)) return;
        String codigo = request.getParameter("resultado");
        if (codigo != null) request.setAttribute("sucesso", Map.of(
                "incluido", "Cadastro realizado com sucesso.",
                "excluido", "Registro excluído com sucesso.",
                "ausente", "Registro já excluído ou não encontrado.").get(codigo));
        renderizar(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        if (!verificarBanco(request, response)) return;
        String acao = request.getParameter("acao");
        if (!"incluir".equals(acao) && !"excluir".equals(acao)) {
            response.sendError(400, "Ação inválida.");
            return;
        }
        try {
            String resultado;
            if ("incluir".equals(acao)) {
                incluir(request);
                resultado = "incluido";
            } else {
                resultado = excluir(request.getParameter("id")) ? "excluido" : "ausente";
            }
            response.setStatus(303);
            response.setHeader("Location", response.encodeRedirectURL(
                    request.getContextPath() + caminho() + "?resultado=" + resultado));
        } catch (IllegalArgumentException erro) {
            response.setStatus(400);
            request.setAttribute("erro", erro.getMessage());
            renderizar(request, response);
        } catch (PersistenceException erro) {
            getServletContext().log("Falha no cadastro " + caminho() + ": " + acao, erro);
            response.setStatus(409);
            request.setAttribute("erro", "Não foi possível concluir a operação. Confira a conexão "
                    + "com o banco. Registros vinculados a outros cadastros devem ser mantidos.");
            renderizar(request, response);
        }
    }

    private boolean verificarBanco(HttpServletRequest request, HttpServletResponse response)
            throws IOException, ServletException {
        response.setContentType("text/html;charset=UTF-8");
        if (getServletContext().getAttribute(chaveServico()) != null) return true;
        indisponivel(request, response);
        return false;
    }

    private void renderizar(HttpServletRequest request, HttpServletResponse response)
            throws IOException, ServletException {
        try {
            carregarDados(request);
        } catch (PersistenceException erro) {
            getServletContext().log("Falha na consulta " + caminho(), erro);
            indisponivel(request, response);
            return;
        }
        request.getRequestDispatcher("/WEB-INF/views/" + pagina() + ".jsp").forward(request, response);
    }

    private void indisponivel(HttpServletRequest request, HttpServletResponse response)
            throws IOException, ServletException {
        response.setStatus(503);
        request.getRequestDispatcher("/WEB-INF/views/banco-indisponivel.jsp").forward(request, response);
    }
}
