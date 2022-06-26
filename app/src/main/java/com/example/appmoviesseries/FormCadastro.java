package com.example.appmoviesseries;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class FormCadastro extends AppCompatActivity {

    private EditText edt_nome;
    private EditText edt_email;
    private EditText edt_celular;
    private EditText edt_senha1;
    private EditText edt_senha;
    private Button   btn_cadastrar;

    String[] mensagens = { "Todos os campos são obrigatórios",                  // 0
                           "Cadastro realizado com sucesso",                    // 1
                           "Senhas devem ser iguais",                           // 2
                           "Digite uma senha com no mínimo de 6 caracteres",    // 3
                           "E-mail já cadastrado",                              // 4
                           "E-mail informado é inválido"};                      // 5
    String usuarioID;
    String perfil;
    ProgressBar progressBar;

    FirebaseDatabase firebase;
    DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_form_cadastro);

        getSupportActionBar().hide();       // Esconde a barra de ação

        IniciarComponentes();

        InicializarFirebase();

        /* Náscara para o telefone. */
        // EditText edt_celular = (EditText) findViewById(R.id.edt_celular);
        TextWatcher mask = MaskEditUtil.mask(edt_celular, "(##) #####-####");
        edt_celular.addTextChangedListener(mask);

        /* Botão CADASTRAR USUÁRIO */
        btn_cadastrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                hideSoftKeyboard(FormCadastro.this);    /* Esconde o teclado virtual */

                String nome    = edt_nome.getText().toString();
                String email   = edt_email.getText().toString();
                String celular = edt_celular.getText().toString();
                String senha   = edt_senha.getText().toString();        /*Firebase: mínimo de 6 caracteres*/
                String senha1  = edt_senha1.getText().toString();

                /* Verifica se todos os campos foram preenchidos */
                if (nome.isEmpty() || email.isEmpty() || celular.isEmpty() || senha.isEmpty() ) {
                    Snackbar snackbar = Snackbar.make(view, mensagens[0], Snackbar.LENGTH_SHORT);
                    snackbar.setBackgroundTint(Color.WHITE);
                    snackbar.setTextColor(Color.BLACK);
                    snackbar.show();
                } else {
                    /* Verifica se as senhas digitadas são iguais */
                    if (senha.equals(senha1)) {
                        CadastrarUsuario(view);
                        // CarregarTelaPrincipal();
                    } else {
                        Snackbar snackbar = Snackbar.make(view, mensagens[2], Snackbar.LENGTH_SHORT);
                        snackbar.setBackgroundTint(Color.WHITE);
                        snackbar.setTextColor(Color.RED);
                        snackbar.show();
                    }
                }
            }
        });

    }

    private void InicializarFirebase() {
        FirebaseApp.initializeApp(FormCadastro.this);

        firebase = FirebaseDatabase.getInstance();
        //firebase.setPersistenceEnabled(true);

        databaseReference = firebase.getReference();
    }

    private void CadastrarUsuario(View v) {

        String email = edt_email.getText().toString();
        String senha = edt_senha.getText().toString();

        FirebaseAuth.getInstance().createUserWithEmailAndPassword(email, senha).addOnCompleteListener(new OnCompleteListener<AuthResult>() {

            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {

                    SalvarDadosUsuario();   /* Grava os dados do usuário */

                    /* Barra de PROGRESSO */
                    progressBar.setVisibility(View.VISIBLE);new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() { CarregarTelaPrincipal(); }
                    }, 3000 );

                    Toast toast = Toast.makeText(getApplicationContext(), mensagens[1], Toast.LENGTH_SHORT);
                    toast.show();

                } else {
                    String erro;
                    try {
                        throw task.getException();

                    } catch(FirebaseAuthWeakPasswordException e) {
                        erro = mensagens[3];

                    } catch( FirebaseAuthUserCollisionException e) {
                        erro = mensagens[4];
                        
                    } catch(FirebaseAuthInvalidCredentialsException e) {
                        erro = mensagens[5];

                    } catch(Exception e) {
                        erro = "Falha ao cadastrar o usuário";
                    }

                    Snackbar snackbar = Snackbar.make(v, erro, Snackbar.LENGTH_SHORT);
                    snackbar.setBackgroundTint(Color.WHITE);
                    snackbar.setTextColor(Color.RED);
                    snackbar.show();

                }
            }
        });
    }

    private void SalvarDadosUsuario() {

        String nome = edt_nome.getText().toString();
        String celular = edt_celular.getText().toString();
        perfil = "user";     /* Inicialmente o perfil é de usuário */

        /* ------------------------------------------------------------------ */
        /* Cadastro do usuário - Realtime */
        Users user = new Users();

        user.setUserId(UUID.randomUUID().toString());
        user.setNome(edt_nome.getText().toString());
        user.setCelular(edt_celular.getText().toString());
        user.setPerfil("usuario");

        databaseReference.child("Users").child(user.getUserId()).setValue(user);
        /* Cadastro do usuário - Realtime */
        /* ------------------------------------------------------------------ */


        /* ------------------------------------------------------------------ */
        /* Cadastro do usuário - Firestore */
        /* Faz a instância do banco de dados */
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        /* Mapa dos campos NOME, CELULAR e PERFIL na coleção "usuarios" */
        Map<String,Object> usuarios=new HashMap<>();
        usuarios.put("nome", nome);
        usuarios.put("celular", celular);
        usuarios.put("perfil", perfil);

        /* Get do ID do usuário */
        usuarioID = FirebaseAuth.getInstance().getCurrentUser().getUid();

        /* Grava o documento no banco de dados Firebase */
        DocumentReference docummentReference = db.collection( "usuarios").document(usuarioID);
        docummentReference.set(usuarios).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Log.d("db", "Sucesso na gravação do cadastro");
            }
        })
        .addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d("db_erro", "Erro ao gravar os dados do cadastro" + e.toString());
            }
        });
        /* Cadastro do usuário - Firestore */
        /* ------------------------------------------------------------------ */

    }

    private void IniciarComponentes() {
        edt_nome      = findViewById(R.id.edt_nome);
        edt_email     = findViewById(R.id.edt_email);
        edt_celular   = findViewById(R.id.edt_celular);
        edt_senha1    = findViewById(R.id.edt_senha1);
        edt_senha     = findViewById(R.id.edt_senha2);
        btn_cadastrar = findViewById(R.id.btn_cadastrar);
        progressBar   = findViewById(R.id.progressbar_cadastro);
    }

    private void CarregarTelaPrincipal() {
        Intent intent = new Intent( FormCadastro.this, FormPrincipal.class);
        startActivity(intent);
        finish();
    }

    private void CarregarTelaLogin() {
        Intent intent = new Intent( FormCadastro.this, FormLogin.class);
        startActivity(intent);
        finish();
    }

    /* Esconde o teclado virtual */
    private void hideSoftKeyboard(Activity activity) {
        InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(activity.INPUT_METHOD_SERVICE);
        View view = activity.getCurrentFocus();
        inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

}


