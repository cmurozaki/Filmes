package com.example.appmoviesseries;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import java.util.List;

public class FormExibeFilme extends AppCompatActivity {

    TextView txt_titulo_portugues;
    TextView tzt_titulo_original;
    TextView txt_producao;
    TextView txt_direcao;
    TextView txt_elenco;
    TextView txt_temporadas;
    TextView tzt_sinopse;
    TextView txt_avaliacao_editor;
    TextView txt_genero;
    TextView txt_filme_serie;
    ImageView img_filme;
    RatingBar ratingBarEditor;

    Button btnVoltar;

    private String[] avaliacao = {
            "Excepcional",
            "Ótimo",
            "Bom",
            "Regular",
            "Fraco" };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_form_exibe_filme);

        getSupportActionBar().hide();       // Esconde a barra de ação

        InicializaComponentes();

        recebeDados();

        /* Botão VOLTAR */
        btnVoltar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(FormExibeFilme.this, FormListaFilmes.class);
                startActivity(intent);
                finish();
            }
        });

    }


    private void InicializaComponentes() {

        txt_titulo_portugues = findViewById(R.id.txt_exibe_titulo_portugues);
        tzt_titulo_original = findViewById(R.id.txt_exibe_titulo_original);
        txt_producao = findViewById(R.id.txt_exibe_producao);
        txt_direcao = findViewById(R.id.txt_exibe_direcao);
        txt_genero = findViewById(R.id.txt_exibe_genero);
        tzt_sinopse = findViewById(R.id.txt_exibe_sinopse);
        txt_elenco = findViewById(R.id.txt_exibe_elenco);
        txt_temporadas = findViewById(R.id.txt_exibe_temporadas);
        txt_avaliacao_editor = findViewById(R.id.txt_exibe_avaliacao_editor);
        txt_filme_serie = findViewById(R.id.txt_exibe_filme_serie);
        btnVoltar = findViewById(R.id.btn_exibe_filme_voltar);
        img_filme = findViewById(R.id.img_exibe_filme);
        ratingBarEditor = findViewById(R.id.ratingBarEditor);

    }


    private void recebeDados() {

        /* Recupera o parâmetro enviado pelo 'FormListaFilmes' */
        Bundle bundle = getIntent().getExtras();
        List<String> lista = bundle.getStringArrayList("exibe_filme");

        /* Verifica se todos os campos estão preenchidos */
        String tituloPortugues = "";
        String tituloOriginal = "";
        String producao = "";
        String direcao = "";
        String elenco = "";
        String filme_serie = "";
        String temporadas = "";
        String avaliacao_editor = "";
        String genero = "";
        String sinopse = "";
        String urlImagem = "";

        if (lista.get(0) != null) {
            tituloPortugues = lista.get(0).toString();
        }

        if (lista.get(1) != null) {
            tituloOriginal = lista.get(1).toString();
        }

        if (lista.get(2) != null) {
            direcao = "Direção: " + lista.get(2).toString();
        }

        if (lista.get(3) != null) {
            producao = lista.get(3).toString();
        }

        if (lista.get(4) != null) {
            avaliacao_editor = lista.get(4).toString();
        }

        if (lista.get(5) != null) {
            elenco = "Elenco: " + lista.get(5).toString();
        }

        if (lista.get(6) != null) {
            sinopse = lista.get(6).toString();
        }

        if (lista.get(7) != null) {
            temporadas = lista.get(7).toString();
        }

        if (lista.get(8) != null) {
            genero = lista.get(8).toString();
        }

        if (lista.get(9) != null) {
            filme_serie = lista.get(9).toString();
        }

        if (lista.get(10) != null) {
            urlImagem = lista.get(10).toString();
        }

        txt_titulo_portugues.setText(tituloPortugues);
        tzt_titulo_original.setText(tituloOriginal);
        txt_direcao.setText(direcao);
        txt_producao.setText(producao);
        txt_avaliacao_editor.setText(avaliacao_editor);
        txt_elenco.setText(elenco);
        tzt_sinopse.setText(sinopse);
        txt_temporadas.setText(temporadas);
        txt_genero.setText(genero);
        txt_filme_serie.setText(filme_serie);

        retornaRaiting(avaliacao_editor);

        /* Imagem */
        Picasso.get()
                .load(urlImagem)
                .into(img_filme);

    }

    private void retornaRaiting(String aval) {

        if (aval.equals(avaliacao[0])) {
            ratingBarEditor.setRating(5);
        } else if (aval.equals(avaliacao[1])) {
            ratingBarEditor.setRating(4);
        } else if (aval.equals(avaliacao[2])) {
            ratingBarEditor.setRating(3);
        } else if (aval.equals(avaliacao[3])) {
            ratingBarEditor.setRating(2);
        } else if (aval.equals(avaliacao[4])) {
            ratingBarEditor.setRating(1);
        }

    }

}