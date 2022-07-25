package com.example.appmoviesseries;

import android.content.Context;
import android.widget.Toast;

public class Filmes extends Movies {

    private String userId;
    private String titulo_portugues;
    private String titulo_original;
    private String genero;
    private String avaliacao_editor;
    private String producao;
    private String direcao;
    private String elenco;
    private String temporadas;
    private String sinopse;
    private String urlImagem;
    private String filme_serie;

    public String getFilme_serie() {
        return filme_serie;
    }

    public void setFilme_serie(String filme_serie) {
        this.filme_serie = filme_serie;
    }

    private String[] avaliacao = {
            "Excepcional",
            "Ótimo",
            "Bom",
            "Regular",
            "Fraco" };

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
    public String getAvaliacao_editor() {
        return avaliacao_editor;
    }

    @Override
    public void setNota(String nota) {
        this.avaliacao_editor = nota;
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

    public void msg_toast(Context context, String mensagem) {
        Toast toast = Toast.makeText(context, mensagem, Toast.LENGTH_SHORT);
        toast.show();
    }

    public float retornaRaiting(String aval) {

        float numStar = 1;

        if (aval.equals(avaliacao[0])) {
            numStar = 5;
        } else if (aval.equals(avaliacao[1])) {
            numStar = 4;
        } else if (aval.equals(avaliacao[2])) {
            numStar = 3;
        } else if (aval.equals(avaliacao[3])) {
            numStar = 2;
        } else if (aval.equals(avaliacao[4])) {
            numStar = 1;
        }

        return numStar;

    }

    public String raitingToDescription(float stars) {

        String description = "";

        if (stars==1) {
            description = avaliacao[4];
        } else if (stars==2) {
            description = avaliacao[3];
        } else if (stars==3) {
            description = avaliacao[2];
        } else if (stars==4) {
            description = avaliacao[1];
        } else if (stars==5) {
            description = avaliacao[0];
        } else if (stars==0) {
            description = "Sem Avaliação";
        }

        return description;

    }

}
