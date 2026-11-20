package br.ufg.inf.vacinacao.persistence;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import static org.junit.jupiter.api.Assertions.*;

class ConfiguracaoBancoTest {
    @TempDir Path pasta;

    @Test void leConfiguracaoExternaEmUtf8SemHabilitarRecriacaoDoBanco() throws Exception {
        Path arquivo = pasta.resolve("banco.properties");
        Files.writeString(arquivo, "db.url=jdbc:postgresql://localhost:5432/vacinacao\n"
                + "db.user=usuario_teste\ndb.password=senha_fictícia\n", StandardCharsets.UTF_8);
        var config = ConfiguracaoBanco.carregar(arquivo);
        assertEquals("senha_fictícia", config.get("jakarta.persistence.jdbc.password"));
        assertEquals("update", config.get("hibernate.hbm2ddl.auto"));
    }

    @Test void rejeitaArquivoAusenteOuSenhaNaoPreenchida() throws Exception {
        assertThrows(IllegalStateException.class,
                () -> ConfiguracaoBanco.carregar(pasta.resolve("ausente.properties")));
        Path arquivo = pasta.resolve("incompleto.properties");
        Files.writeString(arquivo, "db.url=jdbc:postgresql://localhost:5432/vacinacao\ndb.user=teste\n");
        var erro = assertThrows(IllegalStateException.class, () -> ConfiguracaoBanco.carregar(arquivo));
        assertTrue(erro.getMessage().contains("db.password"));
    }
}
