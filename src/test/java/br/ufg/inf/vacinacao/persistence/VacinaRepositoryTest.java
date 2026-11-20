package br.ufg.inf.vacinacao.persistence;

import br.ufg.inf.vacinacao.model.Periodicidade;
import br.ufg.inf.vacinacao.model.Vacina;
import br.ufg.inf.vacinacao.service.VacinaService;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import jakarta.persistence.PersistenceException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/** Integração JPA/Hibernate com H2 em memória, sem tocar no PostgreSQL do usuário. */
class VacinaRepositoryTest {
    private EntityManagerFactory factory;
    private Map<String, Object> configuracao;
    private VacinaRepository repository;

    @BeforeEach void preparar() {
        configuracao = new HashMap<>();
        configuracao.put("jakarta.persistence.jdbc.driver", "org.h2.Driver");
        configuracao.put("jakarta.persistence.jdbc.url",
                "jdbc:h2:mem:" + UUID.randomUUID() + ";MODE=PostgreSQL;DB_CLOSE_DELAY=-1");
        configuracao.put("jakarta.persistence.jdbc.user", "sa");
        configuracao.put("jakarta.persistence.jdbc.password", "");
        configuracao.put("hibernate.hbm2ddl.auto", "create");
        configuracao.put("hibernate.hbm2ddl.halt_on_error", "true");
        factory = Persistence.createEntityManagerFactory("vacinacao", configuracao);
        repository = new VacinaRepository(factory);
    }

    @AfterEach void fechar() {
        if (factory != null && factory.isOpen()) factory.close();
    }

    @Test void incluiConsultaEExcluiComCaracteresEspeciais() {
        var vacina = new VacinaService(repository).incluir(
                "Teste de vacinação", "Descrição com acentos e <texto>", "3", "3", "2");
        assertNotNull(vacina.getId());
        var listada = repository.listar().get(0);
        assertEquals("Teste de vacinação", listada.getTitulo());
        assertEquals("Descrição com acentos e <texto>", listada.getDescricao());
        assertEquals(Periodicidade.MESES, listada.getPeriodicidade());
        assertEquals(2, listada.getIntervalo());
        assertTrue(repository.excluir(vacina.getId()));
        assertTrue(repository.listar().isEmpty());
        assertFalse(repository.excluir(vacina.getId()));
    }

    @Test void gravaOsQuatroCodigosInteirosDefinidosNoMer() {
        for (Periodicidade periodo : Periodicidade.values()) {
            var vacina = repository.incluir(new Vacina("Teste " + periodo.name(), null, 2, periodo, 1));
            try (var em = factory.createEntityManager()) {
                Number codigo = (Number) em.createNativeQuery(
                        "select periodicidade from vacinas where id = :id")
                        .setParameter("id", vacina.getId()).getSingleResult();
                assertEquals(periodo.getCodigo(), codigo.intValue());
                assertEquals(periodo, em.find(Vacina.class, vacina.getId()).getPeriodicidade());
            }
        }
    }

    @Test void dadosPermanecemAoReabrirFactorySemRecriarTabelas() {
        var vacina = new VacinaService(repository).incluir("Teste persistente", "", "1", null, null);
        factory.close();
        configuracao.put("hibernate.hbm2ddl.auto", "update");
        factory = Persistence.createEntityManagerFactory("vacinacao", configuracao);
        repository = new VacinaRepository(factory);
        assertEquals(vacina.getId(), repository.listar().get(0).getId());
    }

    @Test void falhaDeIntegridadeReverteOperacaoEPermiteNovaTransacao() {
        assertThrows(PersistenceException.class,
                () -> repository.incluir(new Vacina("Inválida", null, 0, null, null)));
        assertTrue(repository.listar().isEmpty());
        new VacinaService(repository).incluir("Válida", "", "1", null, null);
        assertEquals(1, repository.listar().size());
    }
}
