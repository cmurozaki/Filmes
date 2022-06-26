package com.example.appmoviesseries;

public class Filmes extends Movies {

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

    @Override
    public String getUrlImagem() {
        return urlImagem;
    }

    @Override
    public void setUrlImagem(String urlImagem) {
        this.urlImagem = urlImagem;
    }

    @Override
    public String getSinopse() {
        return sinopse;
    }

    @Override
    public void setSinopse(String sinopse) {
        this.sinopse = sinopse;
    }

    @Override
    public String getTemporadas() {
        return temporadas;
    }

    @Override
    public void setTemporadas(String temporadas) {
        this.temporadas = temporadas;
    }


    @Override
    public String getElenco() {
        return elenco;
    }

    @Override
    public void setElenco(String elenco) {
        this.elenco = elenco;
    }

    @Override
    public String getDirecao() {
        return direcao;
    }

    @Override
    public void setDirecao(String direcao) {
        this.direcao = direcao;
    }

    @Override
    public String getProducao() {
        return producao;
    }

    @Override
    public void setProducao(String producao) {
        this.producao = producao;
    }

    @Override
    public String getNota() {
        return nota;
    }

    @Override
    public void setNota(String nota) {
        this.nota = nota;
    }

    @Override
    public String getGenero() {
        return genero;
    }

    @Override
    public void setGenero(String genero) {
        this.genero = genero;
    }

    @Override
    public String getTitulo_original() {
        return titulo_original;
    }

    @Override
    public void setTitulo_original(String titulo_original) {
        this.titulo_original = titulo_original;
    }

    @Override
    public String toString() {
        return titulo_portugues;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getTitulo_portugues() {
        return titulo_portugues;
    }

    public void setTitulo_portugues(String titulo_portugues) {
        this.titulo_portugues = titulo_portugues;
    }

    public Filmes () {

    }


}
