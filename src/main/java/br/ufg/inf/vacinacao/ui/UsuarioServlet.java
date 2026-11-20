package br.ufg.inf.vacinacao.ui;

import br.ufg.inf.vacinacao.model.UF;
import br.ufg.inf.vacinacao.service.AlergiaService;
import br.ufg.inf.vacinacao.service.UsuarioService;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.HashSet;
import java.util.Set;

@WebServlet("/usuarios")
public class UsuarioServlet extends CadastroServlet {
    protected String chaveServico() { return AplicacaoListener.SERVICO_USUARIOS; }
    protected String caminho() { return "/usuarios"; }
    protected String pagina() { return "usuarios"; }
    private UsuarioService servico() {
        return (UsuarioService) getServletContext().getAttribute(chaveServico());
    }
    protected void incluir(HttpServletRequest r) {
        servico().incluir(new UsuarioService.Formulario(r.getParameter("nome"),
                r.getParameter("dataNascimento"), r.getParameter("sexo"), r.getParameter("logradouro"),
                r.getParameter("numero"), r.getParameter("setor"), r.getParameter("cidade"),
                r.getParameter("uf"), r.getParameterValues("alergias")));
    }
    protected boolean excluir(String id) { return servico().excluir(id); }
    protected void carregarDados(HttpServletRequest request) {
        AlergiaService alergias = (AlergiaService) getServletContext()
                .getAttribute(AplicacaoListener.SERVICO_ALERGIAS);
        request.setAttribute("usuarios", servico().listar());
        request.setAttribute("alergias", alergias.listar());
        request.setAttribute("ufs", UF.values());
        request.setAttribute("hoje", LocalDate.now(ZoneId.of("America/Sao_Paulo")));
        Set<Integer> selecionadas = new HashSet<>();
        String[] valores = request.getParameterValues("alergias");
        if (valores != null) for (String valor : valores) {
            try { selecionadas.add(Integer.parseInt(valor)); }
            catch (NumberFormatException ignored) { }
        }
        request.setAttribute("alergiasSelecionadas", selecionadas);
    }
}
