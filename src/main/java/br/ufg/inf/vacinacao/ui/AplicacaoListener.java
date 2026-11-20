package br.ufg.inf.vacinacao.ui;

import br.ufg.inf.vacinacao.persistence.ConfiguracaoBanco;
import br.ufg.inf.vacinacao.persistence.VacinaRepository;
import br.ufg.inf.vacinacao.service.VacinaService;
import br.ufg.inf.vacinacao.persistence.AlergiaRepository;
import br.ufg.inf.vacinacao.persistence.UsuarioRepository;
import br.ufg.inf.vacinacao.service.AlergiaService;
import br.ufg.inf.vacinacao.service.UsuarioService;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;
import jakarta.servlet.annotation.WebListener;

@WebListener
public class AplicacaoListener implements ServletContextListener {
    public static final String SERVICO_VACINAS = "servicoVacinas";
    public static final String SERVICO_ALERGIAS = "servicoAlergias";
    public static final String SERVICO_USUARIOS = "servicoUsuarios";
    public static final String SERVICO_AGENDAS = "servicoAgendas";
    private EntityManagerFactory factory;

    @Override
    public void contextInitialized(ServletContextEvent evento) {
        ServletContext contexto = evento.getServletContext();
        try {
            factory = Persistence.createEntityManagerFactory("vacinacao", ConfiguracaoBanco.carregar());
            contexto.setAttribute(SERVICO_AGENDAS, new br.ufg.inf.vacinacao.service.AgendaService(new br.ufg.inf.vacinacao.persistence.AgendaRepository(factory)));
            contexto.setAttribute(SERVICO_VACINAS, new VacinaService(new VacinaRepository(factory)));
            contexto.setAttribute(SERVICO_ALERGIAS, new AlergiaService(new AlergiaRepository(factory)));
            contexto.setAttribute(SERVICO_USUARIOS, new UsuarioService(new UsuarioRepository(factory)));
            contexto.log("Persistência inicializada: cadastro de vacinas disponível.");
        } catch (RuntimeException erro) {
            contexto.log("Falha ao inicializar persistência. Confira conf/vacinacao.properties e o PostgreSQL.", erro);
            // A página inicial permanece acessível para orientar a configuração local.
        }
    }

    @Override
    public void contextDestroyed(ServletContextEvent evento) {
        evento.getServletContext().removeAttribute(SERVICO_VACINAS);
        evento.getServletContext().removeAttribute(SERVICO_AGENDAS);
        evento.getServletContext().removeAttribute(SERVICO_ALERGIAS);
        evento.getServletContext().removeAttribute(SERVICO_USUARIOS);
        if (factory != null && factory.isOpen()) factory.close();
    }
}
