package br.ufg.inf.vacinacao.persistence;

import java.io.IOException;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

public final class ConfiguracaoBanco {
    private ConfiguracaoBanco() { }

    public static Map<String, Object> carregar() {
        String arquivo = System.getProperty("vacinacao.config");
        if (arquivo == null || arquivo.isBlank()) {
            String base = System.getProperty("catalina.base");
            if (base == null || base.isBlank()) {
                throw new IllegalStateException("Defina vacinacao.config ou use o Tomcat.");
            }
            arquivo = Path.of(base, "conf", "vacinacao.properties").toString();
        }
        return carregar(Path.of(arquivo));
    }

    public static Map<String, Object> carregar(Path arquivo) {
        Properties propriedades = new Properties();
        try (Reader leitor = Files.newBufferedReader(arquivo, StandardCharsets.UTF_8)) {
            propriedades.load(leitor);
        } catch (IOException erro) {
            throw new IllegalStateException("Não foi possível ler a configuração local do banco.", erro);
        }
        String url = exigir(propriedades, "db.url");
        if (!url.startsWith("jdbc:postgresql://")) {
            throw new IllegalStateException("db.url deve usar jdbc:postgresql://.");
        }
        Map<String, Object> configuracao = new HashMap<>();
        configuracao.put("jakarta.persistence.jdbc.driver", "org.postgresql.Driver");
        configuracao.put("jakarta.persistence.jdbc.url", url);
        configuracao.put("jakarta.persistence.jdbc.user", exigir(propriedades, "db.user"));
        configuracao.put("jakarta.persistence.jdbc.password", exigir(propriedades, "db.password"));
        // Aplicação acadêmica local: criar/atualizar sem apagar dados ao reiniciar.
        configuracao.put("hibernate.hbm2ddl.auto", "update");
        configuracao.put("hibernate.hbm2ddl.halt_on_error", "true");
        configuracao.put("hibernate.connection.pool_size", "5");
        configuracao.put("hibernate.show_sql", "false");
        configuracao.put("hibernate.format_sql", "true");
        configuracao.put("hibernate.jdbc.time_zone", "America/Sao_Paulo");
        return configuracao;
    }

    private static String exigir(Properties propriedades, String chave) {
        String valor = propriedades.getProperty(chave);
        if (valor == null || valor.isBlank()) {
            throw new IllegalStateException("Preencha " + chave + " na configuração local do banco.");
        }
        return valor;
    }
}
