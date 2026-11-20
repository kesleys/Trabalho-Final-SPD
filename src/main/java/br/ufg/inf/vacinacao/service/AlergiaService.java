package br.ufg.inf.vacinacao.service;

import br.ufg.inf.vacinacao.model.Alergia;
import br.ufg.inf.vacinacao.persistence.AlergiaRepository;
import java.util.List;

public class AlergiaService {
    private final AlergiaRepository repository;
    public AlergiaService(AlergiaRepository repository) { this.repository = repository; }
    public List<Alergia> listar() { return repository.listar(); }
    public Alergia incluir(String nome) { return repository.incluir(validarNome(nome)); }
    public boolean excluir(String id) { return repository.excluir(Validacao.id(id)); }
    public static String validarNome(String nome) { return Validacao.texto(nome, "o nome da alergia", 40, true); }
}
