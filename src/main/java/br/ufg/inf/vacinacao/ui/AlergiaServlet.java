package br.ufg.inf.vacinacao.ui;

import br.ufg.inf.vacinacao.service.AlergiaService;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;

@WebServlet("/alergias")
public class AlergiaServlet extends CadastroServlet {
    protected String chaveServico() { return AplicacaoListener.SERVICO_ALERGIAS; }
    protected String caminho() { return "/alergias"; }
    protected String pagina() { return "alergias"; }
    private AlergiaService servico() {
        return (AlergiaService) getServletContext().getAttribute(chaveServico());
    }
    protected void incluir(HttpServletRequest request) { servico().incluir(request.getParameter("nome")); }
    protected boolean excluir(String id) { return servico().excluir(id); }
    protected void carregarDados(HttpServletRequest request) { request.setAttribute("alergias", servico().listar()); }
}
