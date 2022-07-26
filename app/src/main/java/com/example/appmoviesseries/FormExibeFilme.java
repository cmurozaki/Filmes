package com.example.appmoviesseries;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.drawable.LayerDrawable;
import android.os.Bundle;
import android.os.Parcelable;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.RatingBar;
import android.widget.Switch;
import android.widget.TextView;

import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class FormExibeFilme extends AppCompatActivity {

    private Switch btnAssistido;
    private String usuarioID;
    private String movieId;
    private Float numStars;

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
    RatingBar ratingBarUsuario;

    Button btnVoltar;
    Button btnEditar;

    /* Exibição de mensagens */
    Filmes mensagem = new Filmes();
    Filmes avaliacaoUsuario = new Filmes();

    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_form_exibe_filme);

        getSupportActionBar().hide();       // Esconde a barra de ação

        InicializaComponentes();

        recebeDados();

        InicializaFirebase();

        /* Switch ASSISTIDO */
        btnAssistido.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    ratingBarUsuario.setVisibility(View.VISIBLE);
                    atualizaAssistido(isChecked);
                } else {
                    ratingBarUsuario.setVisibility(View.INVISIBLE);
                    atualizaAssistido(isChecked);
                }
            }
        });

        /* Botão EDITAR */
        btnEditar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Filmes filme = new Filmes();

                firebaseDatabase = FirebaseDatabase.getInstance();
                databaseReference = firebaseDatabase.getReference("Filmes")
                                        .child(movieId);

                databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot)
                    {

                        ArrayList<String> nomes_campos = new ArrayList<>();

                        nomes_campos.add(snapshot.child("titulo_portugues").getValue(String.class));    // 0
                        nomes_campos.add(snapshot.child("titulo_original").getValue(String.class));     // 1
                        nomes_campos.add(snapshot.child("direcao").getValue(String.class));             // 2
                        nomes_campos.add(snapshot.child("producao").getValue(String.class));            // 3
                        nomes_campos.add(snapshot.child("avaliacao_editor").getValue(String.class));    // 4
                        nomes_campos.add(snapshot.child("elenco").getValue(String.class));              // 5
                        nomes_campos.add(snapshot.child("sinopse").getValue(String.class));             // 6
                        nomes_campos.add(snapshot.child("temporadas").getValue(String.class));          // 7
                        nomes_campos.add(snapshot.child("genero").getValue(String.class));              // 8
                        nomes_campos.add(snapshot.child("filme_serie").getValue(String.class));         // 9
                        nomes_campos.add(snapshot.child("urlImagem").getValue(String.class));           // 10
                        nomes_campos.add(snapshot.child("userId").getValue(String.class));              // 11

                        CarregaTelaCadastroFilme(nomes_campos);

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }
        });

        /* Botão VOLTAR */
        btnVoltar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                atualizaAssistido(btnAssistido.isChecked());
                Intent intent = new Intent(FormExibeFilme.this, FormListaFilmes.class);
                startActivity(intent);
                finish();
            }
        });

    }

    private void CarregaTelaCadastroFilme(ArrayList<String> campos) {
        Intent intent = new Intent(FormExibeFilme.this, FormCadastroFilmes.class);
        intent.putStringArrayListExtra("atualiza_filme", campos);
        startActivity(intent);
        finish();
    }

    /* Atualiza ASSISTIDOS */
    private void atualizaAssistido(boolean isChecked) {

        // identificaRaitingUser();

        String avaliacao;

        Assistidos assistido = new Assistidos();

        avaliacao = avaliacaoUsuario.raitingToDescription(ratingBarUsuario.getRating());

        if (isChecked) {
            assistido.setAssistido("S");
        } else {
            assistido.setAssistido("N");
        }
        assistido.setAvaliacao(avaliacao);
        assistido.setTituloPortugues(txt_titulo_portugues.getText().toString());

        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference("Assistidos");

        databaseReference
                .child(usuarioID)
                .child(movieId)
                .setValue(assistido);

    }

    /* Identifica a avaliação do usuário */
    private void identificaRaitingUser() {

        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference("Assistidos");

        /* ID do usuário logado */
        usuarioID = FirebaseAuth.getInstance().getCurrentUser().getUid();

        DatabaseReference avaliacaoDesc = databaseReference
                .child(usuarioID)
                .child(movieId)
                .child("avaliacao");

        avaliacaoDesc.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                final String value;
                try {
                    value = snapshot.getValue().toString();
                    // mensagem.msg_toast(getApplicationContext(), value);
                    atualizaRaitingBar(value);
                } catch(Exception e) {

                }
            }

            private void atualizaRaitingBar(String value) {
                Float numStar;
                numStar = avaliacaoUsuario.retornaRaiting(value);
                ratingBarUsuario.setRating(numStar);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

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
        btnAssistido = findViewById(R.id.switchAssistido);
        ratingBarUsuario = findViewById(R.id.ratingBarUsuario);
        btnEditar = findViewById(R.id.btn_edita_filme);

        ratingBarUsuario.setVisibility(View.INVISIBLE);

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

        float numStar = 1;

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

        if (lista.get(11) != null) {
            movieId = lista.get(11).toString();
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

        Filmes rating = new Filmes();
        numStar = rating.retornaRaiting(avaliacao_editor);
        ratingBarEditor.setRating(numStar);

        /* Imagem */
        if (urlImagem != null && !urlImagem.isEmpty() ) {
            Picasso.get()
                    .load(urlImagem)
                    .into(img_filme);
        }

        identificaRaitingUser();

    }

    private void InicializaFirebase() {

        FirebaseApp.initializeApp(FormExibeFilme.this);

        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference("Assistidos");

        /* ID do usuário logado */
        usuarioID = FirebaseAuth.getInstance().getCurrentUser().getUid();

        /* Verifica se o usuário assistiu ou não o filme selecionado */
        DatabaseReference filmeAssistido = databaseReference
                .child(usuarioID)
                .child(movieId)
                .child("assistido");

        filmeAssistido.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                final String filmeAssistido;
                try {
                    filmeAssistido = snapshot.getValue().toString();
                    if (filmeAssistido.equals("N")) {
                        btnAssistido.setChecked(false);
                        ratingBarUsuario.setVisibility(View.INVISIBLE);
                    } else {
                        btnAssistido.setChecked(true);
                        ratingBarUsuario.setVisibility(View.VISIBLE);
                    }
                } catch(Exception e) {

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }

        });
        /* Verifica se o usuário assistiu ou não o filme selecionado */

        /* Verifica se o perfil do usuário é de ADM */
        databaseReference = firebaseDatabase.getReference("Users");
        DatabaseReference perfil_usuario = databaseReference
                .child(usuarioID)
                .child("perfil");
        perfil_usuario.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                final String value;
                value = snapshot.getValue().toString();
                if (value.equals("adm")) {
                    btnEditar.setVisibility(View.VISIBLE);
                } else {
                    btnEditar.setVisibility(View.INVISIBLE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

}