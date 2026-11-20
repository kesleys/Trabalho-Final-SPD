package br.ufg.inf.vacinacao.service;

import br.ufg.inf.vacinacao.model.Periodicidade;
import br.ufg.inf.vacinacao.model.Vacina;
import br.ufg.inf.vacinacao.persistence.VacinaRepository;
import java.util.List;

public class VacinaService {
    private final VacinaRepository repository;

    public VacinaService(VacinaRepository repository) {
        this.repository = repository;
    }

    public List<Vacina> listar() { return repository.listar(); }

    public Vacina incluir(String titulo, String descricao, String doses,
                          String periodicidade, String intervalo) {
        return repository.incluir(validar(titulo, descricao, doses, periodicidade, intervalo));
    }

    public boolean excluir(String id) {
        return repository.excluir(inteiroPositivo(id, "Identificador"));
    }

    public static Vacina validar(String titulo, String descricao, String dosesTexto,
                                  String periodicidadeTexto, String intervaloTexto) {
        String tituloLimpo = texto(titulo);
        String descricaoLimpa = texto(descricao);
        if (tituloLimpo.isEmpty()) throw new IllegalArgumentException("Informe o título da vacina.");
        if (tamanho(tituloLimpo) > 60) {
            throw new IllegalArgumentException("O título deve ter até 60 caracteres.");
        }
        if (tamanho(descricaoLimpa) > 200) {
            throw new IllegalArgumentException("A descrição deve ter até 200 caracteres.");
        }
        int doses = inteiroPositivo(dosesTexto, "Quantidade de doses");
        Periodicidade periodicidade = null;
        Integer intervalo = null;
        if (doses > 1) {
            periodicidade = Periodicidade.porCodigo(inteiroPositivo(periodicidadeTexto, "Periodicidade"));
            intervalo = inteiroPositivo(intervaloTexto, "Intervalo");
        }
        return new Vacina(tituloLimpo, descricaoLimpa.isEmpty() ? null : descricaoLimpa,
                doses, periodicidade, intervalo);
    }

    private static int inteiroPositivo(String valor, String campo) {
        try {
            int numero = Integer.parseInt(texto(valor));
            if (numero < 1) throw new NumberFormatException();
            return numero;
        } catch (NumberFormatException erro) {
            throw new IllegalArgumentException(campo + " deve ser um inteiro maior que zero.");
        }
    }

    private static int tamanho(String texto) { return texto.codePointCount(0, texto.length()); }
    private static String texto(String valor) { return valor == null ? "" : valor.strip(); }
}
