package br.ufg.inf.vacinacao.ui;

import br.ufg.inf.vacinacao.model.Situacao;
import br.ufg.inf.vacinacao.service.*;
import jakarta.persistence.PersistenceException;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;
import java.util.List;

@WebServlet("/agendas")
public class AgendaServlet extends HttpServlet {
    private AgendaService servico(){return (AgendaService)getServletContext().getAttribute(AplicacaoListener.SERVICO_AGENDAS);}
    protected void doGet(HttpServletRequest r,HttpServletResponse s)throws IOException,ServletException{
        if(!disponivel(r,s))return;
        try{
            if("csv".equals(r.getParameter("formato"))){
                String usuario=r.getParameter("usuario");
                if(usuario==null||usuario.isBlank())throw new IllegalArgumentException("Selecione um usuário para exportar.");
                var agendas=servico().listar(usuario,null,null);
                s.setContentType("text/csv;charset=UTF-8");
                s.setHeader("Content-Disposition","attachment; filename=agendamentos-usuario.csv");
                s.getWriter().write(ExportacaoCsv.gerar(agendas));return;
            }
            if("ok".equals(r.getParameter("resultado")))r.setAttribute("sucesso","Operação concluída com sucesso.");
            renderizar(r,s);
        }catch(IllegalArgumentException e){s.setStatus(400);r.setAttribute("erro",e.getMessage());r.setAttribute("filtroInvalido",true);renderizar(r,s);}
        catch(PersistenceException e){falha(r,s,e);}
    }
    protected void doPost(HttpServletRequest r,HttpServletResponse s)throws IOException,ServletException{
        if(!disponivel(r,s))return;
        try{
            String acao=r.getParameter("acao");
            if("incluir".equals(acao))servico().agendar(r.getParameter("usuarioId"),r.getParameter("vacinaId"),r.getParameter("dataInicial"),r.getParameter("hora"),r.getParameter("observacoes"));
            else if("baixar".equals(acao))servico().baixar(r.getParameter("id"),r.getParameter("destino"));
            else if("excluir".equals(acao))servico().excluir(r.getParameter("id"));
            else throw new IllegalArgumentException("Ação inválida.");
            s.setStatus(303);s.setHeader("Location",s.encodeRedirectURL(r.getContextPath()+"/agendas?resultado=ok"));
        }catch(IllegalArgumentException e){s.setStatus(400);r.setAttribute("erro",e.getMessage());renderizar(r,s);}
        catch(PersistenceException e){falha(r,s,e);}
    }
    private boolean disponivel(HttpServletRequest r,HttpServletResponse s)throws IOException,ServletException{
        s.setContentType("text/html;charset=UTF-8");
        if(servico()!=null)return true;
        s.setStatus(503);r.getRequestDispatcher("/WEB-INF/views/banco-indisponivel.jsp").forward(r,s);return false;
    }
    private void falha(HttpServletRequest r,HttpServletResponse s,Exception erro)throws IOException,ServletException{
        getServletContext().log("Falha em agendas",erro);s.setStatus(503);
        r.getRequestDispatcher("/WEB-INF/views/banco-indisponivel.jsp").forward(r,s);
    }
    private void renderizar(HttpServletRequest r,HttpServletResponse s)throws IOException,ServletException{
        try{
            String dia="hoje".equals(r.getParameter("periodo"))?servico().hoje().toString():r.getParameter("dataFiltro");
            r.setAttribute("agendas",Boolean.TRUE.equals(r.getAttribute("filtroInvalido"))?List.of():servico().listar(r.getParameter("usuario"),dia,r.getParameter("situacao")));
            r.setAttribute("usuarios",((UsuarioService)getServletContext().getAttribute(AplicacaoListener.SERVICO_USUARIOS)).listar());
            r.setAttribute("vacinas",((VacinaService)getServletContext().getAttribute(AplicacaoListener.SERVICO_VACINAS)).listar());
            r.setAttribute("situacoes",Situacao.values());
            r.getRequestDispatcher("/WEB-INF/views/agendas.jsp").forward(r,s);
        }catch(PersistenceException e){falha(r,s,e);}
    }
}
