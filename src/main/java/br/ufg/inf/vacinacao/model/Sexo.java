package br.ufg.inf.vacinacao.model;

public enum Sexo {
    M("Masculino"), F("Feminino");
    private final String descricao;
    Sexo(String descricao) { this.descricao = descricao; }
    public String getDescricao() { return descricao; }
}
