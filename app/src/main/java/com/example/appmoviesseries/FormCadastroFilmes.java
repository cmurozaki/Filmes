package com.example.appmoviesseries;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.text.InputFilter;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.appmoviesseries.databinding.ActivityMainBinding;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class FormCadastroFilmes extends AppCompatActivity {

    private StorageReference mStorageRef;

    TextView edt_titulo_portugues;
    TextView edt_titulo_original;
    TextView edt_producao;
    TextView edt_direcao;
    TextView edt_elenco;
    TextView edt_temporadas;
    TextView edt_sinopse;
    Spinner spinner_generos;
    Spinner spinner_notas;
    Button btn_gravar;
    Button btn_voltar;
    Button btn_imagem;
    Uri selecionadoUri;
    ImageView img_filme;
    RadioGroup radioGroup;
    String usuarioID;
    StorageReference imgRef;
    ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_form_cadastro_filmes);

        mStorageRef = FirebaseStorage.getInstance().getReference();

        getSupportActionBar().hide();       // Esconde a barra de ação

        IniciarComponentes();

        /* Combo box (Spinner) */
        /* Faz a leitura do array de strings */
        ArrayAdapter adapter_generos = ArrayAdapter.createFromResource(this, R.array.array_generos, android.R.layout.simple_spinner_dropdown_item);
        spinner_generos.setAdapter(adapter_generos);

        ArrayAdapter adapter_notas = ArrayAdapter.createFromResource(this, R.array.array_notas, android.R.layout.simple_spinner_dropdown_item);
        spinner_notas.setAdapter(adapter_notas);
        /* Combo box (Spinner) */

        /* Digitação somente em caixa alta*/
        edt_titulo_portugues.setFilters(new InputFilter[]{new InputFilter.AllCaps()});

        /* Botão CARREGAR IMAGEM */
        btn_imagem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selecionarImagem();
            }
        });
        /* Botão CARREGAR IMAGEM */

        /* Botão CADASTRAR */
        btn_gravar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gravarFilmes();
            }
        });
        /* Botão CADASTRAR */

        /* Botão VOLTAR */
        btn_voltar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(FormCadastroFilmes.this, FormPrincipal.class);
                startActivity(intent);
                finish();
            }
        });
        /* Botão VOLTAR */

    }


    /* Grava os dados no Banco de Dados */
    private void gravarFilmes() {

        /* Verifica se todos os campos obrigatórios foram preenchidos */
        if (validaCampo()) {

            String titulo_portugues = edt_titulo_portugues.getText().toString();
            String titulo_original = edt_titulo_original.getText().toString();
            String genero = spinner_generos.getSelectedItem().toString();
            String nota = spinner_notas.getSelectedItem().toString();
            String producao = edt_producao.getText().toString();
            String direcao = edt_direcao.getText().toString();
            String elenco = edt_elenco.getText().toString();
            String temporadas = edt_temporadas.getText().toString();
            String sinopse = edt_sinopse.getText().toString();
            String urlimagem = imgRef.toString();

            /* Barra de PROGRESSO */
            progressBar.setVisibility(View.VISIBLE);new Handler().postDelayed(new Runnable() {
                @Override
                public void run() { CarregarTelaPrincipal(); }
            }, 3000 );

            /* Campos validados, gravar os dados no firebase. */
            String userId = FirebaseAuth.getInstance().getUid();

            /* Criado objeto 'movies' para adicionar os valores na collection 'filmes' */
            Movies movies = new Movies(userId, titulo_portugues, titulo_original, genero, nota, producao, direcao, elenco, temporadas, sinopse, urlimagem);

            FirebaseFirestore.getInstance().collection("filmes")
                    .add(movies)
                    .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                        @Override
                        public void onSuccess(DocumentReference documentReference) {
                            Log.i("Sucesso na gravação", documentReference.getId());
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.i("Erro na gravação", e.getMessage());
                        }
                    });
            /* Campos validados, gravar os dados no firebase. */

            Toast toast = Toast.makeText(getApplicationContext(), "Filme/Série cadastrado com sucesso.", Toast.LENGTH_SHORT);
            toast.show();

            // CarregarTelaPrincipal();

        } else {
            Toast toast = Toast.makeText(getApplicationContext(), "Dados inconsistentes", Toast.LENGTH_SHORT);
            toast.show();
        }
    }

    private void CarregarTelaPrincipal() {
        Intent intent = new Intent(FormCadastroFilmes.this, FormPrincipal.class);
        startActivity(intent);
        finish();
    }

    private void gravaImagem() {
        /* ERRO NO PUTFILE ??? */
        String filename = UUID.randomUUID().toString();
        StorageReference ref = FirebaseStorage.getInstance().getReference("/imagens/"+filename);
        // final StorageReference ref = FirebaseStorage.getInstance().getReference();
        ref.putFile(selecionadoUri)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        ref.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                Log.i( "Upload com sucesso", selecionadoUri.toString() );
                            }
                        });
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e("Erro Upload Imagem", e.getMessage());
                    }
                });
    }

    /* Grava a imagem no Storage do Firebase */
    private void gravaImg() {
        /* DICA:
        No Firebase Storage - Rules:
            rules_version = '2';
            service firebase.storage {
                match /b/{bucket}/o {
                    match /{allPaths=**} {
                        allow read, write: if request.auth != null;
                    }
                }
             }
        */
        Bitmap bitmap = ((BitmapDrawable)img_filme.getDrawable()).getBitmap();
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);
        byte[] imagem = byteArrayOutputStream.toByteArray();

        String filename = UUID.randomUUID().toString() + ".jpeg";
        imgRef = FirebaseStorage.getInstance().getReference("/imagens/"+filename);
        // imgRef = mStorageRef.child("/imagens/" + filename); - funciona também
        UploadTask uploadTask = imgRef.putBytes(imagem);

        uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Toast.makeText( getApplicationContext(), "Imagem gravada com sucesso", Toast.LENGTH_SHORT).show();
            }
        });

    }

    /* Validação do campos */
    private boolean validaCampo() {

        Boolean result;
        String strErro = null;

        String titulo_portugues = edt_titulo_portugues.getText().toString();
        String titulo_original = edt_titulo_original.getText().toString();
        String genero = spinner_generos.getSelectedItem().toString();
        String nota = spinner_notas.getSelectedItem().toString();
        String producao = edt_producao.getText().toString();
        String direcao = edt_direcao.getText().toString();
        String elenco = edt_elenco.getText().toString();
        String temporadas = edt_temporadas.getText().toString();
        String sinopse = edt_sinopse.getText().toString();

        /* Verifica se o título em português foi preenchido */
        if (titulo_portugues.isEmpty()) {
            /* Título em português em branco */
            strErro = "Preencher TÍTULO EM PORTUGUÊS";
            result = false;
        } else {
            result = true;
        }

        /* Verifica se o título original foi preenchido */
        if (result && titulo_original.isEmpty()) {
            /* Título original em branco */
            strErro = "Preencher TÍTULO ORIGINAL";
            result = false;
        }

        /* Verifica se o gênero foi escolhido */
        if (result && genero.isEmpty()) {
            /* Gênero em branco */
            strErro = "Escolher um GÊNERO";
            result = false;
        }

        /* Verifica se a produção foi preenchida */
        if (result && producao.isEmpty()) {
            /* Produção em branco */
            strErro = "Preencher a PRODUÇÃO";
            result = false;
        }

        /* Verifica se a direção foi preenchida */
        if (result && direcao.isEmpty()) {
            /* Produção em branco */
            strErro = "Preencher a DIREÇÃO";
            result = false;
        }

        /* Verifica se o elenco foi preenchido */
        if (result && elenco.isEmpty()) {
            /* Elenco em branco */
            strErro = "Preencher o ELENCO";
            result = false;
        }

        /* Verifica se o item da RadioGroup foi selecionado */
        int itemRadioGroupSelecionado = radioGroup.getCheckedRadioButtonId();
        if (result) {
            if (itemRadioGroupSelecionado != -1) {
                /* Indica que algum item foi selecionado */
                result = true;
            } else {
                strErro = "Selecionar: FILME ou SÉRIE";
                result = false;
            }
        }

        /* Caso seja uma SÉRIE deve preencher as temporadas */
        if (result && itemRadioGroupSelecionado == 1 && temporadas.isEmpty() ) {
            strErro = "Para SÉRIE é obrigatório preencher as temporadas.";
            result = false;
        }

        /* Verifica se a sinopse foi preenchida */
        if (result && sinopse.isEmpty()) {
            /* Sinopse em branco */
            strErro = "Preencher a SINOPSE";
            result = false;
        }

        /* Verifica se a nota foi preenchida */
        if (result && nota.isEmpty()) {
            /* Nota em branco */
            strErro = "Preencher a NOTA DE AVALIAÇÃO";
            result = false;
        }

        /* Caso tenha alguma inconsistência exebe mensagem ao usuário */
        if (!result) {
            Toast toast = Toast.makeText(getApplicationContext(), strErro, Toast.LENGTH_SHORT);
            toast.show();
        }

        return result;

    }


    /* Método para seleção da IMAGEM */
    private void selecionarImagem() {
        // Intent intent = new Intent(Intent.ACTION_PICK );
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT );
        intent.setType("image/*");
        startActivityForResult(intent, 0);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 0) {
            selecionadoUri = data.getData();
            Bitmap bitmap = null;
            img_filme.setImageURI(selecionadoUri);
            gravaImg();
            /*try {
                //bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), selecionadoUri);
                //img_filme.setImageDrawable(new BitmapDrawable(bitmap));
            } catch (IOException e) {
                e.printStackTrace();
            }*/

        }
    }

    private void IniciarComponentes() {
        edt_titulo_portugues = findViewById(R.id.edt_titulo_portugues);
        edt_titulo_original = findViewById(R.id.edt_titulo_original);
        edt_producao = findViewById(R.id.edt_producao);
        edt_direcao = findViewById(R.id.edt_direcao);
        edt_elenco = findViewById(R.id.edt_elenco);
        edt_temporadas = findViewById(R.id.edt_temporadas);
        edt_sinopse = findViewById(R.id.edt_sinopse);
        spinner_generos = findViewById(R.id.spinner_generos);
        spinner_notas = findViewById(R.id.spinner_notas);
        btn_voltar = findViewById(R.id.btn_cadastro_filmes_voltar);
        btn_gravar = findViewById(R.id.btn_cadastro_filmes_cadastrar);
        btn_imagem = findViewById(R.id.btn_cadastro_filmes_upload_imagem);
        img_filme = findViewById(R.id.img_filme);
        radioGroup = findViewById(R.id.radioGroupFilmeSerie);
        progressBar = findViewById(R.id.progressbar_cadastro_filme);
    }


}