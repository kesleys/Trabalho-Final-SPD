package br.ufg.inf.vacinacao.model;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;

/** Usuário do MER é a pessoa vacinada, não uma conta de autenticação. */
@Entity
@Table(name = "usuarios")
public class Usuario {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @Column(nullable = false, length = 60)
    private String nome;
    @Column(name = "data_nascimento", nullable = false)
    private LocalDate dataNascimento;
    @Enumerated(EnumType.STRING) @Column(nullable = false, length = 1)
    private Sexo sexo;
    @Column(length = 60)
    private String logradouro;
    @Column(precision = 12, scale = 0)
    private BigDecimal numero;
    @Column(length = 40)
    private String setor;
    @Column(length = 40)
    private String cidade;
    @Enumerated(EnumType.STRING) @Column(nullable = false, length = 2)
    private UF uf;

    @ManyToMany
    @JoinTable(name = "usuarios_alergias",
            joinColumns = @JoinColumn(name = "usuario_id", nullable = false),
            inverseJoinColumns = @JoinColumn(name = "alergia_id", nullable = false),
            uniqueConstraints = @UniqueConstraint(columnNames = {"usuario_id", "alergia_id"}))
    @OrderBy("nome ASC, id ASC")
    private Set<Alergia> alergias = new LinkedHashSet<>();

    protected Usuario() { }
    public Usuario(String nome, LocalDate dataNascimento, Sexo sexo, String logradouro,
                   BigDecimal numero, String setor, String cidade, UF uf) {
        this.nome = nome;
        this.dataNascimento = dataNascimento;
        this.sexo = sexo;
        this.logradouro = logradouro;
        this.numero = numero;
        this.setor = setor;
        this.cidade = cidade;
        this.uf = uf;
    }
    public void adicionarAlergia(Alergia alergia) { alergias.add(alergia); }
    public Integer getId() { return id; }
    public String getNome() { return nome; }
    public LocalDate getDataNascimento() { return dataNascimento; }
    public String getDataNascimentoFormatada() {
        return dataNascimento.format(DateTimeFormatter.ofPattern("dd/MM/uuuu"));
    }
    public Sexo getSexo() { return sexo; }
    public String getLogradouro() { return logradouro; }
    public BigDecimal getNumero() { return numero; }
    public String getSetor() { return setor; }
    public String getCidade() { return cidade; }
    public UF getUf() { return uf; }
    public Set<Alergia> getAlergias() { return Collections.unmodifiableSet(alergias); }
}
