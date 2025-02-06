package br.com.alura.screenmatch.model;

public enum Categoria {

    ACAO("Action"),


    COMEDIA("Comedy")  ,

    CRIME ("crime"),

    ROMANCE ("romance"),

    DRAMA("drama"),

    Desconhecida("Desconhecida");

    private String categoriaOmd;


    Categoria(String categoriaOmd) {
        this.categoriaOmd = categoriaOmd;
    }

    public static Categoria fromString(String text) {
        for (Categoria categoria : Categoria.values()) {
            if (categoria.categoriaOmd.equalsIgnoreCase(text)) {
                return categoria;
            }
        }
        throw new IllegalArgumentException("Nenhuma categoria encontrada para a string fornecida: " + text);
    }
}

