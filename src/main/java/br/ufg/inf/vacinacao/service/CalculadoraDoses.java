package br.ufg.inf.vacinacao.service;

import br.ufg.inf.vacinacao.model.Periodicidade;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/** Calcula datas; a persistência atômica de todas as doses será feita no serviço JPA. */
public final class CalculadoraDoses {
    public record DosePlanejada(int numero, LocalDate data) {}

    public List<DosePlanejada> calcular(LocalDate primeiraData, int doses,
                                        Periodicidade periodicidade, Integer intervalo) {
        Objects.requireNonNull(primeiraData, "Informe a data inicial.");
        if (doses < 1) {
            throw new IllegalArgumentException("A quantidade de doses deve ser positiva.");
        }
        if (doses == 1) {
            return List.of(new DosePlanejada(1, primeiraData));
        }
        if (periodicidade == null || intervalo == null || intervalo < 1) {
            throw new IllegalArgumentException(
                    "Para várias doses, informe periodicidade e intervalo positivo.");
        }

        List<DosePlanejada> resultado = new ArrayList<>();
        for (int indice = 0; indice < doses; indice++) {
            long deslocamento = Math.multiplyExact((long) indice, intervalo.longValue());
            LocalDate data = switch (periodicidade) {
                case DIAS -> primeiraData.plusDays(deslocamento);
                case SEMANAS -> primeiraData.plusWeeks(deslocamento);
                case MESES -> primeiraData.plusMonths(deslocamento);
                case ANOS -> primeiraData.plusYears(deslocamento);
            };
            resultado.add(new DosePlanejada(indice + 1, data));
        }
        return List.copyOf(resultado);
    }
}
