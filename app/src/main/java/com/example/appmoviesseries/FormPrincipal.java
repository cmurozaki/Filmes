package com.example.appmoviesseries;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.xwray.groupie.GroupAdapter;
import com.xwray.groupie.Item;
import com.xwray.groupie.ViewHolder;

import java.util.ArrayList;
import java.util.List;

/* TELA PRINCIPAL - Contém todas as opções do aplicativo */

public class FormPrincipal extends AppCompatActivity {

    private Button btn_logoff;
    private Button btn_tela_principal_catalogo;
    private Button btn_cadastrar_filmes;
    private Button btn_assistidos;
    private TextView txt_nome_usuario;

    FirebaseDatabase firebase;
    FirebaseFirestore firebaseFirestore;
    DatabaseReference databaseReference;
    DatabaseReference usuarios;

    Boolean isADM = false;

    /* Exibição de mensagens */
    Filmes mensagem = new Filmes();

    String usuarioID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_form_principal);

        /* ID do usuário logado */
        usuarioID = FirebaseAuth.getInstance().getCurrentUser().getUid();

        /* Esconde a barra de ação */
        getSupportActionBar().hide();

        IniciarComponentes();

        InicializarFirebase();

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

        /* Botão ASSISTIDOS */
        btn_assistidos.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onClick(View v) {
                btn_assistidos.setTextColor(getColor(R.color.white));
                exibirAssistidos();
            }
        });

    }

    private void exibirAssistidos() {

        Intent intent = new Intent(FormPrincipal.this, FormListaFilmesAssistidos.class);
        startActivity(intent);
        finish();

        //firebase = FirebaseDatabase.getInstance();
        //databaseReference = firebase.getReference("Assistidos");

        //Query query = databaseReference
        //        .child(usuarioID);

        //query.addListenerForSingleValueEvent(new ValueEventListener() {
        //    @Override
        //    public void onDataChange(@NonNull DataSnapshot snapshot) {

        //        List<Assistidos> lst_assistidos = new ArrayList<Assistidos>();

        //        for (DataSnapshot data: snapshot.getChildren()) {
        //            Assistidos assistido = data.getValue(Assistidos.class);
        //           if (assistido.getAssistido().equals("S")) {
        //                lst_assistidos.add(assistido);
        //            }
        //        }

                /*
                mensagem.msg_toast(getApplicationContext(), lst_assistidos.size()+"");

                for (int i = 0; i < lst_assistidos.size(); i++) {
                    mensagem.msg_toast(getApplicationContext(), usuarioID+"/"+lst_assistidos.get(i).getTituloPortugues()+"");
                }
                */

            }

        //    @Override
        //    public void onCancelled(@NonNull DatabaseError error) {

        //    }
        //});

    //}

    /* Carrega a tela com a lista dos filmes */
    private void listarFilmes() {
        Intent intent = new Intent(FormPrincipal.this, FormListaFilmes.class);
        intent.putExtra("tipo", "todos");
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
        btn_assistidos = findViewById(R.id.btn_principal_assistidos);
        txt_nome_usuario = findViewById(R.id.txtUsuario);
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

    private void InicializarFirebase() {

        FirebaseApp.initializeApp(FormPrincipal.this);

        firebase = FirebaseDatabase.getInstance();
        databaseReference = firebase.getReference();

        /* ID do usuário logado */
        String usuarioID = FirebaseAuth.getInstance().getCurrentUser().getUid();

        /* Pesquisa no Realtime de acordo com o path/campo */
        /* Caminho: /Users/<id usuário>/"nome" */
        DatabaseReference nome_usuario = databaseReference.child("Users").child(usuarioID).child("nome");

        nome_usuario.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                final String value;
                value = snapshot.getValue().toString();
                txt_nome_usuario.setText("Olá, " + value);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }

        });

        /* Verifica se o perfil do usuário é de ADM */
        DatabaseReference perfil_usuario = databaseReference.child("Users").child(usuarioID).child("perfil");

        perfil_usuario.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                final String value;
                value = snapshot.getValue().toString();
                if (value.equals("adm")) {
                    isADM = true;
                    btn_cadastrar_filmes.setVisibility(View.VISIBLE);
                } else {
                    btn_cadastrar_filmes.setVisibility(View.INVISIBLE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }

        });
        /* Verifica se o perfil do usuário é de ADM */

    }



    /* Faz o logoff do Firebase */
    private void sairAplicativo() {
        FirebaseAuth.getInstance().signOut();
        TelaLogin();
    }
}