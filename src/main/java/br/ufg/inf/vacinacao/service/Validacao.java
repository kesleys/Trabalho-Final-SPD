package br.ufg.inf.vacinacao.service;

final class Validacao {
    private Validacao() { }
    static String texto(String valor, String campo, int limite, boolean obrigatorio) {
        String texto = valor == null ? "" : valor.strip();
        if (obrigatorio && texto.isEmpty()) throw new IllegalArgumentException("Informe " + campo + ".");
        if (texto.codePointCount(0, texto.length()) > limite) {
            throw new IllegalArgumentException(campo + " deve ter até " + limite + " caracteres.");
        }
        return texto.isEmpty() ? null : texto;
    }
    static int id(String valor) {
        try {
            int id = Integer.parseInt(valor);
            if (id < 1) throw new NumberFormatException();
            return id;
        } catch (NumberFormatException erro) {
            throw new IllegalArgumentException("Identificador inválido.");
        }
    }
}
