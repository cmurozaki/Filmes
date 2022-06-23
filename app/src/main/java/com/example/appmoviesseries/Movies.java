package com.example.appmoviesseries;

public class Movies<setter> {

    private String userId;
    private String titulo_portugues;
    private String titulo_original;
    private String genero;
    private String nota;
    private String producao;
    private String direcao;
    private String elenco;
    private String temporadas;
    private String sinopse;
    private String urlImagem;

    public Movies() {
    }

    public Movies(String userId, String titulo_portugues, String titulo_original, String genero, String nota, String producao, String direcao, String elenco, String temporadas, String sinopse, String urlImagem) {
        this.userId = userId;
        this.titulo_portugues = titulo_portugues;
        this.titulo_original = titulo_original;
        this.genero = genero;
        this.nota = nota;
        this.producao = producao;
        this.direcao = direcao;
        this.elenco = elenco;
        this.temporadas = temporadas;
        this.sinopse = sinopse;
        this.urlImagem = urlImagem;
    }

    public String getUserId() {
        return userId;
    }

    public String getTitulo_portugues() {
        return titulo_portugues;
    }

    public String getDirecao() {
        return direcao;
    }

    public String getElenco() {
        return elenco;
    }

    public String getGenero() {
        return genero;
    }

    public String getNota() {
        return nota;
    }

    public String getProducao() {
        return producao;
    }

    public String getSinopse() {
        return sinopse;
    }

    public String getTemporadas() {
        return temporadas;
    }

    public String getTitulo_original() {
        return titulo_original;
    }

    public String getUrlImagem() {
        return urlImagem;
    }

}
