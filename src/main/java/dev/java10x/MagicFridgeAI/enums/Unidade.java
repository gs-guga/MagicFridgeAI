package dev.java10x.MagicFridgeAI.enums;

public enum Unidade {
    UNIDADE("unidade(s)"),
    GRAMAS("g"),
    MILILITROS("ml");

    private final String texto;

    Unidade(String texto) {
        this.texto = texto;
    }

    public String getTexto() {
        return texto;
    }
}