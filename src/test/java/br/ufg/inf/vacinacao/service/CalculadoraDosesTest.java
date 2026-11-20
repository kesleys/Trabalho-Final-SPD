package br.ufg.inf.vacinacao.service;

import br.ufg.inf.vacinacao.model.Periodicidade;
import java.time.LocalDate;
import java.util.List;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class CalculadoraDosesTest {
    private final CalculadoraDoses calculadora = new CalculadoraDoses();

    @Test void criaCincoDosesACadaTresAnosConformeEnunciado() {
        var doses = calculadora.calcular(LocalDate.of(2026, 11, 22), 5,
                Periodicidade.ANOS, 3);
        assertEquals(List.of(2026, 2029, 2032, 2035, 2038),
                doses.stream().map(d -> d.data().getYear()).toList());
        assertEquals(List.of(1, 2, 3, 4, 5),
                doses.stream().map(d -> d.numero()).toList());
        assertTrue(doses.stream().allMatch(d -> d.data().getMonthValue() == 11
                && d.data().getDayOfMonth() == 22));
    }

    @Test void naoPropagaAjusteDeFevereiroParaMarco() {
        var doses = calculadora.calcular(LocalDate.of(2027, 1, 31), 3,
                Periodicidade.MESES, 1);
        assertEquals(List.of(LocalDate.of(2027, 1, 31), LocalDate.of(2027, 2, 28),
                LocalDate.of(2027, 3, 31)), doses.stream().map(d -> d.data()).toList());
    }

    @Test void ajustaDiaBissextoESuaDataPodeSerRecuperada() {
        var doses = calculadora.calcular(LocalDate.of(2024, 2, 29), 5,
                Periodicidade.ANOS, 1);
        assertEquals(LocalDate.of(2025, 2, 28), doses.get(1).data());
        assertEquals(LocalDate.of(2028, 2, 29), doses.get(4).data());
    }

    @Test void doseUnicaNaoExigeIntervaloOuPeriodicidade() {
        var data = LocalDate.of(2026, 11, 22);
        assertEquals(List.of(new CalculadoraDoses.DosePlanejada(1, data)),
                calculadora.calcular(data, 1, null, null));
    }

    @Test void calculaIntervalosEmDiasESemanas() {
        var data = LocalDate.of(2026, 11, 22);
        assertEquals(data.plusDays(20), calculadora.calcular(data, 3,
                Periodicidade.DIAS, 10).get(2).data());
        assertEquals(data.plusWeeks(4), calculadora.calcular(data, 3,
                Periodicidade.SEMANAS, 2).get(2).data());
    }

    @Test void rejeitaParametrosInvalidos() {
        var data = LocalDate.of(2026, 11, 22);
        assertThrows(IllegalArgumentException.class,
                () -> calculadora.calcular(data, 0, null, null));
        assertThrows(IllegalArgumentException.class,
                () -> calculadora.calcular(data, 2, Periodicidade.DIAS, 0));
        assertThrows(IllegalArgumentException.class,
                () -> calculadora.calcular(data, 2, null, 2));
        assertThrows(NullPointerException.class,
                () -> calculadora.calcular(null, 1, null, null));
    }
}
