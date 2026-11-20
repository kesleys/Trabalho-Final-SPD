package br.ufg.inf.vacinacao.service;
import org.junit.jupiter.api.Test;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;
class ExportacaoCsvTest {
    @Test void protegeFormulasEAspas(){
        assertEquals("\"'=1+1\"",ExportacaoCsv.celula("=1+1"));
        assertEquals("\"'  +1\"",ExportacaoCsv.celula("  +1"));
        assertEquals("\"a;\"\"b\"\"\"",ExportacaoCsv.celula("a;\"b\""));
    }
    @Test void exportacaoVaziaTemCabecalhoUtf8(){
        assertTrue(ExportacaoCsv.gerar(List.of()).startsWith("\uFEFFUsuário;Data;Hora;Situação;Vacina;Dose;"));
    }
}
