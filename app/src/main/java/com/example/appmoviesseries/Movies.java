package com.example.appmoviesseries;

public class Movies<setter> {

    private final String userId;
    private final String titulo_portugues;
    private final String titulo_original;
    private final String genero;
    private final String nota;
    private final String producao;
    private final String direcao;
    private final String elenco;
    private final String temporadas;
    private final String sinopse;
    private final String urlImagem;


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

    public String getTitulo_portugues() {
        return titulo_portugues;
    }

    public String getUrlImagem() {
        return urlImagem;
    }

    public String getUserId() {
        return userId;
    }

}
