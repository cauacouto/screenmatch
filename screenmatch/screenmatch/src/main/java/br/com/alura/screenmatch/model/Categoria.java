package br.com.alura.screenmatch.model;

public enum Categoria {

    ACAO("Action","ação"),


    COMEDIA("Comedy","comédia")  ,

    CRIME ("crime","crime"),

    ROMANCE ("romance","romance"),

    DRAMA("drama","drama"),

    Desconhecida("Desconhecida","desconhecida");

    private String categoriaOmd;
    private String categoriaPortugues;


    Categoria(String categoriaOmd,String categoriaPortugues) {
        this.categoriaOmd = categoriaOmd;
        this.categoriaPortugues=categoriaPortugues;
    }

    public static Categoria fromString(String text) {
        for (Categoria categoria : Categoria.values()) {
            if (categoria.categoriaOmd.equalsIgnoreCase(text)) {
                return categoria;
            }
        }
        throw new IllegalArgumentException("Nenhuma categoria encontrada para a string fornecida: " + text);
    }

    public static Categoria fromportugues(String text) {
        for (Categoria categoria : Categoria.values()) {
            if (categoria.categoriaPortugues.equalsIgnoreCase(text)) {
                return categoria;
            }
        }
        throw new IllegalArgumentException("Nenhuma categoria encontrada para a string fornecida: " + text);
    }
}

