package br.ufg.inf.vacinacao.model;

import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import org.hibernate.annotations.Check;

@Entity
@Table(name = "vacinas")
@Check(constraints = "doses >= 1 and (doses = 1 or (periodicidade is not null "
        + "and periodicidade between 1 and 4 and intervalo is not null and intervalo > 0))")
public class Vacina {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false, length = 60)
    private String titulo;

    @Column(length = 200)
    private String descricao;

    @Column(nullable = false)
    private int doses;

    @Convert(converter = PeriodicidadeConverter.class)
    @Column
    private Periodicidade periodicidade;

    @Column
    private Integer intervalo;

    protected Vacina() { }

    public Vacina(String titulo, String descricao, int doses,
                  Periodicidade periodicidade, Integer intervalo) {
        this.titulo = titulo;
        this.descricao = descricao;
        this.doses = doses;
        this.periodicidade = periodicidade;
        this.intervalo = intervalo;
    }

    public Integer getId() { return id; }
    public String getTitulo() { return titulo; }
    public String getDescricao() { return descricao; }
    public int getDoses() { return doses; }
    public Periodicidade getPeriodicidade() { return periodicidade; }
    public Integer getIntervalo() { return intervalo; }
}
