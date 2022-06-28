package com.example.appmoviesseries;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class FormLogin extends AppCompatActivity {

    /* ID do usuário autenticado */
    public String[] UID_user = {""};

    private TextView txt_tela_cadastro;
    private EditText edt_email;
    private EditText edt_senha;
    private Button   btn_Login;
    private ProgressBar prg_Login;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_form_login);

        EditText edt_email = (EditText) findViewById(R.id.edt_email);
        edt_email.setHorizontallyScrolling(true);   // Habilita o scroll horizontal para o EditText.

        getSupportActionBar().hide();               // Esconde a barra de ação

        IniciarComponentes();

        txt_tela_cadastro.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent( FormLogin.this, FormCadastro.class);
                startActivity(intent);
            }
        });

        btn_Login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = edt_email.getText().toString();
                String senha = edt_senha.getText().toString();

                hideSoftKeyboard(FormLogin.this);    /* Esconde o teclado virtual */

                if (email.isEmpty() || senha.isEmpty()) {
                    Snackbar snackbar = Snackbar.make(v, "Todos os campos devem ser preenchidos", Snackbar.LENGTH_SHORT);
                    snackbar.setBackgroundTint(Color.WHITE);
                    snackbar.setTextColor(Color.RED);
                    snackbar.show();
                } else {
                    AutenticarUsuario(v);
                }
            }
        });
    }

    private void AutenticarUsuario(View v) {
        String email = edt_email.getText().toString();
        String senha = edt_senha.getText().toString();

        FirebaseAuth.getInstance().signInWithEmailAndPassword(email, senha).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    prg_Login.setVisibility(View.VISIBLE);
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            TelaPrincipal();
                        }
                    }, 3000);
                    /* Recupera o ID do usuário - Autenticação */
                    UID_user[0] = FirebaseAuth.getInstance().getCurrentUser().getUid();
                } else {
                    String erro;
                    try {
                        throw task.getException();
                    } catch(Exception e) {
                        erro = "Falha na autenticação do usuário";
                        Snackbar snackbar = Snackbar.make(v, erro, Snackbar.LENGTH_SHORT);
                        snackbar.setBackgroundTint(Color.WHITE);
                        snackbar.setTextColor(Color.RED);
                        snackbar.show();
                    }
                }
            }
        });
    }

    /* Na carga verifica se o usuário ainda continua logado no Firebase. Se sim, carrega a Tela Principal */
    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser usuarioAtual = FirebaseAuth.getInstance().getCurrentUser();
        if (usuarioAtual != null) {
            TelaPrincipal();
        }
    }

    /* Carrega a Tela Principal */
    private void TelaPrincipal() {
        Intent intent = new Intent( FormLogin.this, FormPrincipal.class);
        startActivity(intent);
        finish();
    }


    private void IniciarComponentes() {
        txt_tela_cadastro = findViewById(R.id.txt_tela_cadastro);
        edt_email = findViewById(R.id.edt_email);
        edt_senha = findViewById(R.id.edt_senha);
        btn_Login = findViewById(R.id.btn_entrar);
        prg_Login = findViewById(R.id.progressbar_login);
    }

    /* Esconde o teclado virtual */
    private void hideSoftKeyboard(Activity activity) {
        InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(activity.INPUT_METHOD_SERVICE);
        View view = activity.getCurrentFocus();
        inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

}