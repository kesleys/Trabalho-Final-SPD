package br.ufg.inf.vacinacao.persistence;

import br.ufg.inf.vacinacao.model.*;
import br.ufg.inf.vacinacao.service.*;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;

class CadastrosRepositoryTest {
    private EntityManagerFactory factory;
    private Map<String, Object> config;
    private AlergiaRepository alergias;
    private UsuarioRepository usuarios;

    @BeforeEach void preparar() {
        config = new HashMap<>();
        config.put("jakarta.persistence.jdbc.driver", "org.h2.Driver");
        config.put("jakarta.persistence.jdbc.url", "jdbc:h2:mem:" + UUID.randomUUID() + ";MODE=PostgreSQL;DB_CLOSE_DELAY=-1");
        config.put("jakarta.persistence.jdbc.user", "sa");
        config.put("jakarta.persistence.jdbc.password", "");
        config.put("hibernate.hbm2ddl.auto", "create");
        config.put("hibernate.hbm2ddl.halt_on_error", "true");
        factory = Persistence.createEntityManagerFactory("vacinacao", config);
        repositorios();
    }
    private void repositorios() {
        alergias = new AlergiaRepository(factory);
        usuarios = new UsuarioRepository(factory);
    }
    private Usuario pessoa(String nome) {
        return UsuarioService.validar(new UsuarioService.Formulario(nome, "2000-01-02", "F",
                "Rua teste", "123", "Setor teste", "Goiânia", "GO", null), java.time.Clock.systemUTC());
    }
    @AfterEach void fechar() { if (factory != null && factory.isOpen()) factory.close(); }

    @Test void cadastraUsuarioSemAlergiasEConsultaTodosOsCampos() {
        usuarios.incluir(pessoa("Usuária de teste"), Set.of());
        var usuario = usuarios.listar().get(0);
        assertEquals("Usuária de teste", usuario.getNome());
        assertEquals("02/01/2000", usuario.getDataNascimentoFormatada());
        assertEquals(Sexo.F, usuario.getSexo());
        assertEquals("Rua teste", usuario.getLogradouro());
        assertEquals("123", usuario.getNumero().toPlainString());
        assertEquals("Setor teste", usuario.getSetor());
        assertEquals("Goiânia", usuario.getCidade());
        assertEquals(UF.GO, usuario.getUf());
        assertTrue(usuario.getAlergias().isEmpty());
    }

    @Test void associaVariasAlergiasSemLazyLoadingEGravaSexoEUfDoMer() {
        var a = alergias.incluir("Alergia A");
        var b = alergias.incluir("Alergia B");
        var usuario = usuarios.incluir(pessoa("Teste"), Set.of(a.getId(), b.getId()));
        assertEquals(2, usuarios.listar().get(0).getAlergias().size());
        try (var em = factory.createEntityManager()) {
            Object[] linha = (Object[]) em.createNativeQuery("select sexo, uf from usuarios where id = :id")
                    .setParameter("id", usuario.getId()).getSingleResult();
            assertEquals("F", linha[0].toString());
            assertEquals("GO", linha[1].toString());
        }
    }

    @Test void rejeitaAlergiaInexistenteSemGravarUsuarioParcial() {
        var a = alergias.incluir("Alergia A");
        assertThrows(IllegalArgumentException.class,
                () -> usuarios.incluir(pessoa("Teste"), Set.of(a.getId(), 999999)));
        assertTrue(usuarios.listar().isEmpty());
        assertEquals(1, alergias.listar().size());
    }

    @Test void excluirUsuarioPreservaAlergiasCompartilhadas() {
        var a = alergias.incluir("Alergia compartilhada");
        var primeiro = usuarios.incluir(pessoa("Primeira"), Set.of(a.getId()));
        usuarios.incluir(pessoa("Segunda"), Set.of(a.getId()));
        assertTrue(usuarios.excluir(primeiro.getId()));
        assertFalse(usuarios.excluir(primeiro.getId()));
        assertEquals(1, alergias.listar().size());
        assertEquals(1, usuarios.listar().size());
        assertEquals(1, usuarios.listar().get(0).getAlergias().size());
    }

    @Test void impedeExcluirAlergiaAssociadaELiberaAposExcluirUsuario() {
        var a = alergias.incluir("Alergia A");
        var usuario = usuarios.incluir(pessoa("Teste"), Set.of(a.getId()));
        assertThrows(IllegalArgumentException.class, () -> alergias.excluir(a.getId()));
        assertEquals(1, usuarios.listar().get(0).getAlergias().size());
        usuarios.excluir(usuario.getId());
        assertTrue(alergias.excluir(a.getId()));
        assertTrue(alergias.listar().isEmpty());
        assertFalse(alergias.excluir(a.getId()));
    }

    @Test void vinculosPermanecemDepoisDeReabrirFactory() {
        var a = alergias.incluir("Alergia persistente");
        usuarios.incluir(pessoa("Teste persistente"), Set.of(a.getId()));
        factory.close();
        config.put("hibernate.hbm2ddl.auto", "update");
        factory = Persistence.createEntityManagerFactory("vacinacao", config);
        repositorios();
        var usuario = usuarios.listar().get(0);
        assertEquals("Alergia persistente", usuario.getAlergias().iterator().next().getNome());
    }
}
