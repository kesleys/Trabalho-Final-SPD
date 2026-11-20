package br.ufg.inf.vacinacao.model;

/** Códigos definidos no MER fornecido pelo professor. */
public enum Periodicidade {
    DIAS(1, "Dias"), SEMANAS(2, "Semanas"), MESES(3, "Meses"), ANOS(4, "Anos");

    private final int codigo;
    private final String descricao;

    Periodicidade(int codigo, String descricao) {
        this.codigo = codigo;
        this.descricao = descricao;
    }

    public int getCodigo() { return codigo; }
    public String getDescricao() { return descricao; }

    public static Periodicidade porCodigo(int codigo) {
        for (Periodicidade valor : values()) {
            if (valor.codigo == codigo) return valor;
        }
        throw new IllegalArgumentException("Periodicidade deve ser de 1 a 4.");
    }
}
