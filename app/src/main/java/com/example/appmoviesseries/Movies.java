package com.example.appmoviesseries;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

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

    public String getTitulo_original() {
        return titulo_original;
    }

    public void setTitulo_original(String titulo_original) {
        this.titulo_original = titulo_original;
    }

    public String getGenero() {
        return genero;
    }

    public void setGenero(String genero) {
        this.genero = genero;
    }

    public String getNota() {
        return nota;
    }

    public void setNota(String nota) {
        this.nota = nota;
    }

    public String getProducao() {
        return producao;
    }

    public void setProducao(String producao) {
        this.producao = producao;
    }

    public String getDirecao() {
        return direcao;
    }

    public void setDirecao(String direcao) {
        this.direcao = direcao;
    }

    public String getElenco() {
        return elenco;
    }

    public void setElenco(String elenco) {
        this.elenco = elenco;
    }

    public String getTemporadas() {
        return temporadas;
    }

    public void setTemporadas(String temporadas) {
        this.temporadas = temporadas;
    }

    public String getSinopse() {
        return sinopse;
    }

    public void setSinopse(String sinopse) {
        this.sinopse = sinopse;
    }

    public String getUrlImagem() {
        return urlImagem;
    }

    public void setUrlImagem(String urlImagem) {
        this.urlImagem = urlImagem;
    }

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

    @Override
    public String toString() {
        return titulo_portugues;
    }

}
