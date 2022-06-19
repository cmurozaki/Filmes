package com.example.appmoviesseries;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

public class PerfilUsuario extends AppCompatActivity {

    private TextView txt_nome_usuario;
    private TextView txt_email;
    private Button   btn_sair;

    FirebaseFirestore db = FirebaseFirestore.getInstance(); /* Instância do banco Firestone Database */

    String usuarioID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_perfil_usuario);

        getSupportActionBar().hide();

        IniciarComponentes();

        /* LOGOFF */
        btn_sair.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseAuth.getInstance().signOut();
                TelaLogin();
            }
        });

    }


    private void TelaLogin() {
        Intent intent = new Intent( PerfilUsuario.this, FormLogin.class);
        startActivity(intent);
        finish();
    }


    /* Ciclo de vida da activity */
    @Override
    protected void onStart() {
        super.onStart();

        String email = FirebaseAuth.getInstance().getCurrentUser().getEmail();  /* Recupera o EMAIL do usuário logado */
        usuarioID = FirebaseAuth.getInstance().getCurrentUser().getUid();       /* Recupera o ID do usuário logado */

        /* Recupera os documentos da coleção "usuarios" do usuário logado */
        DocumentReference documentReference = db.collection("usuarios").document(usuarioID);

        documentReference.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException error) {
                if (documentSnapshot != null) {
                    txt_nome_usuario.setText(documentSnapshot.getString("nome"));
                    txt_email.setText(email);
                }
            }
        });
    }

    private void IniciarComponentes() {
        txt_nome_usuario = findViewById(R.id.txt_nome_usuario);
        txt_email        = findViewById(R.id.txt_email);
        btn_sair         = findViewById(R.id.btn_sair);
    }
}