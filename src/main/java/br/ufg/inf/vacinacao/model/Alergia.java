package br.ufg.inf.vacinacao.model;

import jakarta.persistence.*;

@Entity
@Table(name = "alergias")
public class Alergia {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @Column(nullable = false, length = 40)
    private String nome;

    protected Alergia() { }
    public Alergia(String nome) { this.nome = nome; }
    public Integer getId() { return id; }
    public String getNome() { return nome; }
}
