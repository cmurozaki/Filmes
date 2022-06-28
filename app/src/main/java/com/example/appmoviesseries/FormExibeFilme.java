package com.example.appmoviesseries;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.widget.TextView;

import java.util.List;

public class FormExibeFilme extends AppCompatActivity {

    TextView txt_titulo_portugues;
    TextView tzt_titulo_original;
    TextView txt_producao;
    TextView txt_direcao;
    TextView txt_elenco;
    TextView txt_temporadas;
    TextView tzt_sinopse;
    TextView txt_nota;
    TextView txt_genero;
    TextView txt_filme_serie;

    String campos[];

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_form_exibe_filme);

        InicializaComponentes();

        recebeDados();

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
        txt_nota = findViewById(R.id.txt_exibe_nota);
        txt_filme_serie = findViewById(R.id.txt_exibe_filme_serie);

    }

    private void recebeDados() {

        /* Recupera o parâmetro enviado pelo 'FormListaFilmes' */
        Bundle bundle = getIntent().getExtras();
        List<String> lista = bundle.getStringArrayList("exibe_filme");

        txt_titulo_portugues.setText(lista.get(0).toString());
        tzt_titulo_original.setText(lista.get(1).toString());
        txt_direcao.setText(lista.get(2).toString());
        txt_producao.setText(lista.get(3).toString());
        tzt_titulo_original.setText(lista.get(4).toString());
        tzt_titulo_original.setText(lista.get(5).toString());
        tzt_titulo_original.setText(lista.get(6).toString());
        tzt_titulo_original.setText(lista.get(7).toString());
        tzt_titulo_original.setText(lista.get(8).toString());
        tzt_titulo_original.setText(lista.get(9).toString());

    }
}