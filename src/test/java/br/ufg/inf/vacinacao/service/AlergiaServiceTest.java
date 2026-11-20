package br.ufg.inf.vacinacao.service;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class AlergiaServiceTest {
    @Test void aceitaNomeEConsideraLimiteDoMer() {
        assertEquals("Alergia teste", AlergiaService.validarNome(" Alergia teste "));
        assertEquals(40, AlergiaService.validarNome("a".repeat(40)).length());
    }
    @Test void rejeitaNomeVazioOuLongo() {
        assertThrows(IllegalArgumentException.class, () -> AlergiaService.validarNome(null));
        assertThrows(IllegalArgumentException.class, () -> AlergiaService.validarNome(" "));
        assertThrows(IllegalArgumentException.class, () -> AlergiaService.validarNome("a".repeat(41)));
    }
}
