package com.example.appmoviesseries;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.DialogInterface;
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
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.appmoviesseries.databinding.ActivityMainBinding;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class FormCadastroFilmes extends AppCompatActivity {

    private StorageReference mStorageRef;
    private Boolean imagemSelecionada = false;
    private Boolean blnResult;
    private String urlImagem = "";

    FirebaseDatabase firebase;
    DatabaseReference databaseReference;

    TextView edt_titulo_portugues;
    TextView edt_titulo_original;
    TextView edt_producao;
    TextView edt_direcao;
    TextView edt_elenco;
    TextView edt_temporadas;
    TextView edt_sinopse;
    Spinner spinner_generos;
    Spinner spinner_avaliacao_editor;
    Button btn_gravar;
    Button btn_voltar;
    Button btn_imagem;
    Uri selecionadoUri;
    ImageView img_filme;
    RadioGroup radioGroup;
    RadioButton radioBtnFilme;
    RadioButton radioBtnSerie;
    String usuarioID;
    StorageReference imgRef;
    ProgressBar progressBar;

    String movieId;

    /* Exibição de mensagens */
    Filmes mensagem = new Filmes();

@Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_form_cadastro_filmes);

        mStorageRef = FirebaseStorage.getInstance().getReference();

        getSupportActionBar().hide();       // Esconde a barra de ação

        IniciarComponentes();
        
        InicializarFirebase();

        /* Combo box (Spinner) */
        /* Faz a leitura do array de strings */
        ArrayAdapter adapter_generos = ArrayAdapter.createFromResource(this, R.array.array_generos, android.R.layout.simple_spinner_dropdown_item);
        spinner_generos.setAdapter(adapter_generos);

        ArrayAdapter adapter_avaliacao_editor = ArrayAdapter.createFromResource(this, R.array.array_avaliacao_editor, android.R.layout.simple_spinner_dropdown_item);
        spinner_avaliacao_editor.setAdapter(adapter_avaliacao_editor);
        /* Combo box (Spinner) */

        recebeDados();

        /* Digitação somente em caixa alta*/
        edt_titulo_portugues.setFilters(new InputFilter[]{new InputFilter.AllCaps()});
        edt_producao.setFilters(new InputFilter[]{new InputFilter.AllCaps()});

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
                gravarFilme_realtime();
            }
        });
        /* Botão CADASTRAR */

        /* Botão VOLTAR */
        btn_voltar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                VoltarTela();
            }
        });
        /* Botão VOLTAR */

    }

    /* Gravação dos dados do filme - Realtime */
    private void gravarFilme_realtime() {

        /* Caso tenha selecionado uma imagem, verfica a url */
        if (!imagemSelecionada) {

            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("SEM IMAGEM");
            builder.setMessage(R.string.grava_sem_imagem);
            builder.setPositiveButton("SIM", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    imagemSelecionada = true;
                }
            });
            builder.setNegativeButton("NÃO", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    return;
                }
            });

            AlertDialog alerta = builder.create();
            alerta.show();

        }

        if (!imagemSelecionada) {

            mensagem.msg_toast(getApplicationContext(), "Selecione uma imagem");

        } else {

            Movies movie = new Movies();

            /* Identifica se foi selecionado FILME ou SÉRIE */
            String filme_serie;
            if (radioBtnFilme.isChecked()) {
                filme_serie = "Filme";
            } else {
                filme_serie = "Série";
            }

            if (movieId==null) {
                /* Adiciona */
                movie.setUserId(UUID.randomUUID().toString());
            } else {
                /* Atualiza */
                movie.setUserId(movieId);
            }
            movie.setTitulo_portugues(edt_titulo_portugues.getText().toString());
            movie.setTitulo_original(edt_titulo_original.getText().toString());
            movie.setGenero(spinner_generos.getSelectedItem().toString());
            movie.setProducao(edt_producao.getText().toString());
            movie.setDirecao(edt_direcao.getText().toString());
            movie.setElenco(edt_elenco.getText().toString());
            movie.setSinopse(edt_sinopse.getText().toString());
            movie.setNota(spinner_avaliacao_editor.getSelectedItem().toString());
            movie.setTemporadas(edt_temporadas.getText().toString());
            movie.setFilme_serie(filme_serie);
            if (urlImagem != null) {
                movie.setUrlImagem(urlImagem);
            }

            databaseReference.child("Filmes").child(movie.getUserId()).setValue(movie);

            mensagem.msg_toast(getApplicationContext(), "Filme/Série cadastrado com sucesso");

            /* Barra de PROGRESSO */
            progressBar.setVisibility(View.VISIBLE);new Handler().postDelayed(new Runnable() {
                @Override
                public void run() { LimpaCampos(); }
            }, 3000 );

        }

    }


    /* Inicializa o banco de dados */
    private void InicializarFirebase() {

        FirebaseApp.initializeApp(FormCadastroFilmes.this);

        firebase = FirebaseDatabase.getInstance();

        databaseReference = firebase.getReference();

    }

    private void VoltarTela() {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Alerta");
        builder.setMessage(R.string.confirma_voltar);
        builder.setPositiveButton("SIM", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent(FormCadastroFilmes.this, FormPrincipal.class);
                startActivity(intent);
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


    /* Grava os dados no Banco de Dados */
    private void gravarFilmes() {

        /* Verifica se todos os campos obrigatórios foram preenchidos */
        if (validaCampo()) {

            String titulo_portugues = edt_titulo_portugues.getText().toString();
            String titulo_original = edt_titulo_original.getText().toString();
            String genero = spinner_generos.getSelectedItem().toString();
            String nota = spinner_avaliacao_editor.getSelectedItem().toString();
            String producao = edt_producao.getText().toString();
            String direcao = edt_direcao.getText().toString();
            String elenco = edt_elenco.getText().toString();
            String temporadas = edt_temporadas.getText().toString();
            String sinopse = edt_sinopse.getText().toString();
            String urlImagem = "";

            /* Caso tenha selecionado uma imagem, verfica a url */
            if (imagemSelecionada) {
                urlImagem = imgRef.toString();
            }

            /* Barra de PROGRESSO */
            progressBar.setVisibility(View.VISIBLE);new Handler().postDelayed(new Runnable() {
                @Override
                public void run() { LimpaCampos(); }
            }, 3000 );

            /* Campos validados, gravar os dados no firebase. */
            String userId;
            if (movieId==null) {
                userId = FirebaseAuth.getInstance().getUid();
            } else {
                userId = movieId;
            }

            /* Criado objeto 'movies' para adicionar os valores na collection 'filmes' */
            Movies movies = new Movies(userId, titulo_portugues, titulo_original, genero, nota,
                    producao, direcao, elenco, temporadas, sinopse, urlImagem);

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

            mensagem.msg_toast(getApplicationContext(), "Filme/Série cadastrado com sucesso");

        } else {

            mensagem.msg_toast(getApplicationContext(), "Dados inconsistentes");

        }
    }

    /* Limpa os campos */
    private void LimpaCampos() {

        edt_titulo_portugues.setText("");
        edt_titulo_original.setText("");
        edt_producao.setText("");
        edt_elenco.setText("");
        edt_direcao.setText("");
        edt_sinopse.setText("");
        edt_temporadas.setText("");
        img_filme.setImageDrawable(null);

        /* Envia o foco para o título em português */
        edt_titulo_portugues.requestFocus();

        progressBar.setVisibility(View.INVISIBLE);

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
        final StorageReference imgRef = FirebaseStorage.getInstance().getReference("/imagens/"+filename);
        // imgRef = mStorageRef.child("/imagens/" + filename); - funciona também
        UploadTask uploadTask = imgRef.putBytes(imagem);

        uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                imagemSelecionada = true;
                imgRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        urlImagem = uri.toString();
                    }
                })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.i("Erro Imagem", e.getMessage(), e);
                            }
                        });

                /* Barra de PROGRESSO */
                progressBar.setVisibility(View.VISIBLE);new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() { capturouImagem(); }
                }, 3000 );

                // Toast.makeText( getApplicationContext(), "Imagem gravada com sucesso", Toast.LENGTH_SHORT).show();
            }

            private void capturouImagem() {
                progressBar.setVisibility(View.INVISIBLE);
                btn_gravar.setEnabled(true);
            }
        });

    }

    /* Validação do campos */
    private boolean validaCampo() {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        Boolean validaCampos = false;
        Boolean result;
        String strErro = null;

        String titulo_portugues = edt_titulo_portugues.getText().toString();
        String titulo_original = edt_titulo_original.getText().toString();
        String genero = spinner_generos.getSelectedItem().toString();
        String nota = spinner_avaliacao_editor.getSelectedItem().toString();
        String producao = edt_producao.getText().toString();
        String direcao = edt_direcao.getText().toString();
        String elenco = edt_elenco.getText().toString();
        String temporadas = edt_temporadas.getText().toString();
        String sinopse = edt_sinopse.getText().toString();

        /* Verifica se o título em português foi preenchido */
        if (validaCampos && titulo_portugues.isEmpty()) {
            /* Título em português em branco */
            strErro = "Preencher TÍTULO EM PORTUGUÊS";
            result = false;
        } else {
            result = true;
        }

        /* Verifica se o título original foi preenchido */
        if (validaCampos && result && titulo_original.isEmpty()) {
            /* Título original em branco */
            strErro = "Preencher TÍTULO ORIGINAL";
            result = false;
        }

        /* Verifica se o gênero foi escolhido */
        if (validaCampos && result && genero.isEmpty()) {
            /* Gênero em branco */
            strErro = "Escolher um GÊNERO";
            result = false;
        }

        /* Verifica se a produção foi preenchida */
        if (validaCampos && result && producao.isEmpty()) {
            /* Produção em branco */
            strErro = "Preencher a PRODUÇÃO";
            result = false;
        }

        /* Verifica se a direção foi preenchida */
        if (validaCampos && result && direcao.isEmpty()) {
            /* Produção em branco */
            strErro = "Preencher a DIREÇÃO";
            result = false;
        }

        /* Verifica se o elenco foi preenchido */
        if (validaCampos && result && elenco.isEmpty()) {
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
        if (validaCampos && result && sinopse.isEmpty()) {
            /* Sinopse em branco */
            strErro = "Preencher a SINOPSE";
            result = false;
        }

        /* Verifica se a nota foi preenchida */
        if (validaCampos && result && nota.isEmpty()) {
            /* Nota em branco */
            strErro = "Preencher a NOTA DE AVALIAÇÃO";
            result = false;
        }

        if (!imagemSelecionada) {
            builder.setTitle("Sem Imagem");
            builder.setMessage(R.string.grava_sem_imagem);
            builder.setPositiveButton("SIM", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    imagemSelecionada = true;
                }
            });
            builder.setNegativeButton("NÃO", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    imagemSelecionada = false;
                }
            });
            AlertDialog alerta = builder.create();
            alerta.show();
        }

        /* Caso tenha alguma inconsistência exebe mensagem ao usuário */
        if (!result) {

            mensagem.msg_toast(getApplicationContext(), strErro);

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
        spinner_avaliacao_editor = findViewById(R.id.spinner_avaliacao_editor);
        btn_voltar = findViewById(R.id.btn_cadastro_filmes_voltar);
        btn_gravar = findViewById(R.id.btn_cadastro_filmes_cadastrar);
        btn_imagem = findViewById(R.id.btn_cadastro_filmes_upload_imagem);
        img_filme = findViewById(R.id.img_filme);
        radioGroup = findViewById(R.id.radioGroupFilmeSerie);
        radioBtnFilme = findViewById(R.id.radioBtnFilme);
        radioBtnSerie = findViewById(R.id.radioBtnSerie);
        progressBar = findViewById(R.id.progressbar_cadastro_filme);
        // btn_gravar.setEnabled(false);
    }

    private void recebeDados() {

        /* Recupera o parâmetro enviado pelo 'FormExibeFilme' */
        Bundle bundle = getIntent().getExtras();

        if (bundle==null) {
            return;
        }

        List<String> lista = bundle.getStringArrayList("atualiza_filme");

        if (lista.get(0) != null) {
            edt_titulo_portugues.setText(lista.get(0));
        }

        if (lista.get(1) != null) {
            edt_titulo_original.setText(lista.get(1));
        }

        if (lista.get(2) != null) {
            edt_direcao.setText(lista.get(2));
        }

        if (lista.get(3) != null) {
            edt_producao.setText(lista.get(3));
        }

        if (lista.get(4) != null) {
            spinner_avaliacao_editor.setSelection(identificaIdAvaliacaoEditor(lista.get(4)));
        }

        if (lista.get(5) != null) {
            edt_elenco.setText(lista.get(5));
        }

        if (lista.get(6) != null) {
            edt_sinopse.setText(lista.get(6));
        }

        if (lista.get(7) != null) {
            edt_temporadas.setText(lista.get(7));
        }

        /* GÊNERO */
        if (lista.get(8) != null) {
            spinner_generos.setSelection(identificaIdGenero(lista.get(8)));
        }

        if (lista.get(9) != null) {
            if (lista.get(9).equals("Filme")) {
                radioBtnFilme.setChecked(true);
            } else {
                radioBtnSerie.setChecked(true);
            }
        }

        if (lista.get(10) != null) {
            urlImagem = lista.get(10);
        }

        if (lista.get(11) != null) {
            movieId = lista.get(11);
        }

    }

    private int identificaIdAvaliacaoEditor(String avaliacao) {

        int numRetorno = 0;

        if (avaliacao.equals(getResources().getString(R.string.aval_excepcional))) {
            numRetorno = 0;
        } else if (avaliacao.equals(getResources().getString(R.string.aval_otimo))) {
            numRetorno = 1;
        } else if (avaliacao.equals(getResources().getString(R.string.aval_bom))) {
            numRetorno = 2;
        } else if (avaliacao.equals(getResources().getString(R.string.aval_regular))) {
            numRetorno = 3;
        }else if (avaliacao.equals(getResources().getString(R.string.aval_fraco))) {
            numRetorno = 4;
        }

        return numRetorno;

    }

    private int identificaIdGenero(String genero) {

        int numRetorno = 0;

        if (genero.equals(getResources().getString(R.string.gen_animacao))) {
            numRetorno = 0;
        } else if (genero.equals(getResources().getString(R.string.gen_aventura))) {
            numRetorno = 1;
        } else if (genero.equals(getResources().getString(R.string.gen_comedia))) {
            numRetorno = 2;
        } else if (genero.equals(getResources().getString(R.string.gen_drama))) {
            numRetorno = 3;
        } else if (genero.equals(getResources().getString(R.string.gen_ficcao))) {
            numRetorno = 4;
        } else if (genero.equals(getResources().getString(R.string.gen_policial))) {
            numRetorno = 5;
        } else if (genero.equals(getResources().getString(R.string.gen_suspense))) {
            numRetorno = 6;
        } else if (genero.equals(getResources().getString(R.string.gen_terror))) {
            numRetorno = 7;
        }

        return numRetorno;

    }

}