package com.example.appmoviesseries;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;

import com.google.firebase.auth.FirebaseAuth;

/* TELA PRINCIPAL - Contém todas as opções do aplicativo */

public class FormPrincipal extends AppCompatActivity {

    private Button btn_logoff;
    private Button btn_tela_principal_catalogo;
    private Button btn_cadastrar_filmes;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_form_principal);

        /* Esconde a barra de ação */
        getSupportActionBar().hide();

        IniciarComponentes();

        /* LOGOFF */
        btn_logoff.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onClick(View v) {
                btn_logoff.setTextColor(getColor(R.color.white));
                confirmarLogoff(v);
                /*FirebaseAuth.getInstance().signOut();
                TelaLogin();*/
            }
        });

        /* Botão LISTAR FILMES - CATÁLOGO */
        btn_tela_principal_catalogo.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onClick(View v) {
                btn_tela_principal_catalogo.setTextColor(getColor(R.color.white));
                listarFilmes();
            }
        });

        /* Botão CADASTRAR FILMES E SÉRIES */
        btn_cadastrar_filmes.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onClick(View v) {
                btn_cadastrar_filmes.setTextColor(getColor(R.color.white));
                cadastrarFilmes(v);
            }
        });

    }

    /* Carrega a tela com a lista dos filmes */
    private void listarFilmes() {
        Intent intent = new Intent(FormPrincipal.this, FormListaFilmes.class);
        startActivity(intent);
        finish();
    }

    /* Carrega a Tela de Cadastro de Filmes */
    private void cadastrarFilmes(View v) {
        Intent intent = new Intent( FormPrincipal.this, FormCadastroFilmes.class);
        startActivity(intent);
        finish();
    }

    /* Carrega a Tela de Login */
    private void TelaLogin() {
        Intent intent = new Intent( FormPrincipal.this, FormLogin.class);
        startActivity(intent);
        finish();
    }

    private void IniciarComponentes() {
        btn_logoff = findViewById(R.id.btn_logoff);
        btn_tela_principal_catalogo = findViewById(R.id.btn_principal_catalogo);
        btn_cadastrar_filmes = findViewById(R.id.btn_cadastrar_filmes);
    }

    private void confirmarLogoff(View v) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Sair do Aplicativo");
        builder.setMessage(R.string.msg_logoff);
        builder.setPositiveButton("SIM", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                sairAplicativo();
                finish();
            }
        });
        builder.setNegativeButton("Não", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                return;
            }
        });
        AlertDialog alerta = builder.create();
        alerta.show();
    }

    /* Faz o logoff do Firebase */
    private void sairAplicativo() {
        FirebaseAuth.getInstance().signOut();
        TelaLogin();
    }
}