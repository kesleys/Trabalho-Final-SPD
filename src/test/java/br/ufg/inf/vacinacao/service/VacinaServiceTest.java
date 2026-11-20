package br.ufg.inf.vacinacao.service;

import br.ufg.inf.vacinacao.model.Periodicidade;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class VacinaServiceTest {
    @Test void validaDadosEConverteCodigoDoMer() {
        var vacina = VacinaService.validar("  Vacina de teste  ", " Descrição ", "3", "3", "2");
        assertEquals("Vacina de teste", vacina.getTitulo());
        assertEquals("Descrição", vacina.getDescricao());
        assertEquals(3, vacina.getDoses());
        assertEquals(Periodicidade.MESES, vacina.getPeriodicidade());
        assertEquals(2, vacina.getIntervalo());
    }

    @Test void doseUnicaNaoArmazenaIntervaloSemSentido() {
        var vacina = VacinaService.validar("Dose única", "", "1", "4", "10");
        assertNull(vacina.getDescricao());
        assertNull(vacina.getPeriodicidade());
        assertNull(vacina.getIntervalo());
    }

    @Test void rejeitaTituloVazioOuAcimaDoLimiteEDescricaoLonga() {
        assertThrows(IllegalArgumentException.class,
                () -> VacinaService.validar(" ", "", "1", null, null));
        assertThrows(IllegalArgumentException.class,
                () -> VacinaService.validar("a".repeat(61), "", "1", null, null));
        assertThrows(IllegalArgumentException.class,
                () -> VacinaService.validar("Teste", "a".repeat(201), "1", null, null));
    }

    @Test void rejeitaDosesInvalidasMesmoSemValidacaoDoNavegador() {
        for (String valor : new String[]{"", "0", "-1", "1.5", "abc", "2147483648"}) {
            assertThrows(IllegalArgumentException.class,
                    () -> VacinaService.validar("Teste", "", valor, null, null), valor);
        }
    }

    @Test void variasDosesExigemIntervaloEPeriodicidadeValidos() {
        assertThrows(IllegalArgumentException.class,
                () -> VacinaService.validar("Teste", "", "2", null, "2"));
        assertThrows(IllegalArgumentException.class,
                () -> VacinaService.validar("Teste", "", "2", "5", "2"));
        assertThrows(IllegalArgumentException.class,
                () -> VacinaService.validar("Teste", "", "2", "1", "0"));
        assertThrows(IllegalArgumentException.class,
                () -> VacinaService.validar("Teste", "", "2", "1", null));
    }
}
