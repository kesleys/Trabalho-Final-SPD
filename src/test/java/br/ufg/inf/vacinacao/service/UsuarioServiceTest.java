package br.ufg.inf.vacinacao.service;

import br.ufg.inf.vacinacao.model.UF;
import java.time.Clock;
import java.time.Instant;
import java.time.ZoneId;
import java.util.Set;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class UsuarioServiceTest {
    private final Clock clock = Clock.fixed(Instant.parse("2026-11-19T12:00:00Z"), ZoneId.of("America/Sao_Paulo"));
    private UsuarioService.Formulario formulario(String nome, String data, String sexo, String numero, String uf) {
        return new UsuarioService.Formulario(nome, data, sexo, "Rua de teste", numero,
                "Setor teste", "Goiânia", uf, null);
    }

    @Test void aceitaUsuarioComTodosOsDadosE27Ufs() {
        var usuario = UsuarioService.validar(formulario(" Pessoa teste ", "2000-12-30", "M", "120", "GO"), clock);
        assertEquals("Pessoa teste", usuario.getNome());
        assertEquals("Goiânia", usuario.getCidade());
        assertEquals("120", usuario.getNumero().toPlainString());
        assertEquals("30/12/2000", usuario.getDataNascimentoFormatada());
        assertEquals(27, UF.values().length);
        assertTrue(usuario.getAlergias().isEmpty());
    }

    @Test void aceitaEnderecoOpcional() {
        var usuario = UsuarioService.validar(new UsuarioService.Formulario(
                "Teste", "2000-01-01", "F", "", "", "", "", "DF", null), clock);
        assertNull(usuario.getNumero());
        assertNull(usuario.getLogradouro());
        assertNull(usuario.getCidade());
    }

    @Test void rejeitaNascimentoFuturoOuDataInvalida() {
        for (String data : new String[]{"2026-11-20", "2025-02-29", "", "abc"}) {
            assertThrows(IllegalArgumentException.class,
                    () -> UsuarioService.validar(formulario("Teste", data, "M", "1", "GO"), clock));
        }
    }

    @Test void rejeitaNomeSexoUfInvalidos() {
        assertThrows(IllegalArgumentException.class,
                () -> UsuarioService.validar(formulario(" ", "2000-01-01", "M", "1", "GO"), clock));
        assertThrows(IllegalArgumentException.class,
                () -> UsuarioService.validar(formulario("Teste", "2000-01-01", "X", "1", "GO"), clock));
        assertThrows(IllegalArgumentException.class,
                () -> UsuarioService.validar(formulario("Teste", "2000-01-01", "M", "1", "XX"), clock));
    }

    @Test void respeitaLimitesDoMerENumeroNumerico() {
        assertThrows(IllegalArgumentException.class,
                () -> UsuarioService.validar(formulario("a".repeat(61), "2000-01-01", "M", "1", "GO"), clock));
        for (String numero : new String[]{"12A", "-1", "1.5", "1234567890123"}) {
            assertThrows(IllegalArgumentException.class,
                    () -> UsuarioService.validar(formulario("Teste", "2000-01-01", "M", numero, "GO"), clock));
        }
        assertThrows(IllegalArgumentException.class, () -> UsuarioService.validar(
                new UsuarioService.Formulario("Teste", "2000-01-01", "M", "a".repeat(61), "1",
                        "", "", "GO", null), clock));
        assertThrows(IllegalArgumentException.class, () -> UsuarioService.validar(
                new UsuarioService.Formulario("Teste", "2000-01-01", "M", "", "1",
                        "a".repeat(41), "", "GO", null), clock));
        assertThrows(IllegalArgumentException.class, () -> UsuarioService.validar(
                new UsuarioService.Formulario("Teste", "2000-01-01", "M", "", "1",
                        "", "a".repeat(41), "GO", null), clock));
    }

    @Test void idsDeAlergiasPodemSerVaziosEMarcacoesDuplicadasNaoRepetemVinculo() {
        assertEquals(Set.of(), UsuarioService.idsAlergias(null));
        assertEquals(Set.of(1, 2), UsuarioService.idsAlergias(new String[]{"1", "2", "1"}));
        for (String id : new String[]{"0", "-1", "abc"}) {
            assertThrows(IllegalArgumentException.class,
                    () -> UsuarioService.idsAlergias(new String[]{id}));
        }
    }
}
