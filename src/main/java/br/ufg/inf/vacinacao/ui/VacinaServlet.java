package br.ufg.inf.vacinacao.ui;

import br.ufg.inf.vacinacao.model.Periodicidade;
import br.ufg.inf.vacinacao.service.VacinaService;
import jakarta.persistence.PersistenceException;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.Map;

@WebServlet("/vacinas")
public class VacinaServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws IOException, ServletException {
        response.setContentType("text/html;charset=UTF-8");
        if (servico() == null) {
            indisponivel(request, response);
            return;
        }
        Map<String, String> mensagens = Map.of(
                "incluida", "Vacina cadastrada com sucesso.",
                "excluida", "Vacina excluída com sucesso.",
                "ausente", "A vacina já foi excluída ou não foi encontrada.");
        String codigo = request.getParameter("resultado");
        request.setAttribute("sucesso", codigo == null ? null : mensagens.get(codigo));
        listar(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws IOException, ServletException {
        response.setContentType("text/html;charset=UTF-8");
        if (servico() == null) {
            indisponivel(request, response);
            return;
        }
        String acao = request.getParameter("acao");
        if (!"incluir".equals(acao) && !"excluir".equals(acao)) {
            response.sendError(400, "Ação inválida.");
            return;
        }
        try {
            String resultado;
            if ("incluir".equals(acao)) {
                servico().incluir(request.getParameter("titulo"), request.getParameter("descricao"),
                        request.getParameter("doses"), request.getParameter("periodicidade"),
                        request.getParameter("intervalo"));
                resultado = "incluida";
            } else {
                resultado = servico().excluir(request.getParameter("id")) ? "excluida" : "ausente";
            }
            // Post/Redirect/Get impede repetir a inclusão ao atualizar a listagem.
            response.setStatus(HttpServletResponse.SC_SEE_OTHER);
            response.setHeader("Location", response.encodeRedirectURL(
                    request.getContextPath() + "/vacinas?resultado=" + resultado));
        } catch (IllegalArgumentException erro) {
            response.setStatus(400);
            request.setAttribute("erro", erro.getMessage());
            listar(request, response);
        } catch (PersistenceException erro) {
            getServletContext().log("Não foi possível " + acao + " vacina.", erro);
            response.setStatus(409);
            request.setAttribute("erro", "Não foi possível concluir a operação. "
                    + "Confira a conexão com o banco. Se houver agendamentos vinculados, "
                    + "a vacina deve ser mantida.");
            listar(request, response);
        }
    }

    private VacinaService servico() {
        return (VacinaService) getServletContext().getAttribute(AplicacaoListener.SERVICO_VACINAS);
    }

    private void listar(HttpServletRequest request, HttpServletResponse response)
            throws IOException, ServletException {
        request.setAttribute("periodicidades", Periodicidade.values());
        int selecionada = 0;
        try {
            selecionada = Integer.parseInt(request.getParameter("periodicidade"));
        } catch (NumberFormatException ignored) {
            // Um valor inválido não deve quebrar a renderização da mensagem de erro.
        }
        request.setAttribute("periodicidadeSelecionada", selecionada);
        try {
            request.setAttribute("vacinas", servico().listar());
        } catch (PersistenceException erro) {
            getServletContext().log("Não foi possível consultar vacinas.", erro);
            response.setStatus(503);
            request.setAttribute("erro", "Não foi possível consultar o banco. Confira o PostgreSQL e tente novamente.");
            request.setAttribute("vacinas", List.of());
            request.setAttribute("consultaFalhou", true);
        }
        request.getRequestDispatcher("/WEB-INF/views/vacinas.jsp").forward(request, response);
    }

    private void indisponivel(HttpServletRequest request, HttpServletResponse response)
            throws IOException, ServletException {
        response.setStatus(503);
        request.getRequestDispatcher("/WEB-INF/views/banco-indisponivel.jsp").forward(request, response);
    }
}
