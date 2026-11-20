package br.ufg.inf.vacinacao.service;

import br.ufg.inf.vacinacao.model.*;
import br.ufg.inf.vacinacao.persistence.UsuarioRepository;
import java.math.BigDecimal;
import java.time.Clock;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeParseException;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

public class UsuarioService {
    public record Formulario(String nome, String dataNascimento, String sexo, String logradouro,
                             String numero, String setor, String cidade, String uf, String[] alergias) { }
    private final UsuarioRepository repository;
    private final Clock clock;
    public UsuarioService(UsuarioRepository repository) {
        this(repository, Clock.system(ZoneId.of("America/Sao_Paulo")));
    }
    public UsuarioService(UsuarioRepository repository, Clock clock) {
        this.repository = repository;
        this.clock = clock;
    }
    public List<Usuario> listar() { return repository.listar(); }
    public Usuario incluir(Formulario dados) {
        Usuario usuario = validar(dados, clock);
        return repository.incluir(usuario, idsAlergias(dados.alergias()));
    }
    public boolean excluir(String id) { return repository.excluir(Validacao.id(id)); }

    public static Usuario validar(Formulario dados, Clock clock) {
        String nome = Validacao.texto(dados.nome(), "o nome do usuário", 60, true);
        LocalDate nascimento;
        try {
            nascimento = LocalDate.parse(dados.dataNascimento() == null ? "" : dados.dataNascimento());
        } catch (DateTimeParseException erro) {
            throw new IllegalArgumentException("Informe uma data de nascimento válida.");
        }
        if (nascimento.isAfter(LocalDate.now(clock))) {
            throw new IllegalArgumentException("A data de nascimento não pode ser futura.");
        }
        Sexo sexo;
        UF uf;
        try { sexo = Sexo.valueOf(dados.sexo() == null ? "" : dados.sexo()); }
        catch (IllegalArgumentException erro) { throw new IllegalArgumentException("Selecione o sexo."); }
        try { uf = UF.valueOf(dados.uf() == null ? "" : dados.uf()); }
        catch (IllegalArgumentException erro) { throw new IllegalArgumentException("Selecione uma UF válida."); }
        String logradouro = Validacao.texto(dados.logradouro(), "Logradouro", 60, false);
        String setor = Validacao.texto(dados.setor(), "Setor", 40, false);
        String cidade = Validacao.texto(dados.cidade(), "Cidade", 40, false);
        String numeroTexto = dados.numero() == null ? "" : dados.numero().strip();
        BigDecimal numero = null;
        if (!numeroTexto.isEmpty()) {
            if (!numeroTexto.matches("[0-9]{1,12}")) {
                throw new IllegalArgumentException("Número do endereço deve conter até 12 dígitos, sem letras ou decimais.");
            }
            numero = new BigDecimal(numeroTexto);
        }
        return new Usuario(nome, nascimento, sexo, logradouro, numero, setor, cidade, uf);
    }

    public static Set<Integer> idsAlergias(String[] valores) {
        Set<Integer> ids = new LinkedHashSet<>();
        if (valores != null) for (String valor : valores) ids.add(Validacao.id(valor));
        return ids;
    }
}
