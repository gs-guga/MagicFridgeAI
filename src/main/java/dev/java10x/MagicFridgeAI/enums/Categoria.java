package dev.java10x.MagicFridgeAI.enums;

public enum Categoria {
    FRUTAS("Frutas"),
    LEGUMES_E_VERDURAS("Legumes e verduras"),
    LATICINIOS("Laticínios"),
    CARNES("Carnes"),
    OVOS("Ovos"),
    GRAOS_E_CEREAIS("Grãos e cereais"),
    BEBIDAS("Bebidas"),
    TEMPEROS("Temperos"),
    OUTROS("Outros");

    private final String rotulo;

    Categoria(String rotulo) {
        this.rotulo = rotulo;
    }

    public String getRotulo() {
        return rotulo;
    }
}
