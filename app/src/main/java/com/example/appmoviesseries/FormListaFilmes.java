package com.example.appmoviesseries;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnCreateContextMenuListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.auth.User;
import com.squareup.picasso.Picasso;
import com.xwray.groupie.GroupAdapter;
import com.xwray.groupie.Item;
import com.xwray.groupie.OnItemClickListener;
import com.xwray.groupie.ViewHolder;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class FormListaFilmes extends AppCompatActivity {

    private FirebaseFirestore firebaseFirestore;
    private ListView listView;

    private List<Movies> moviesList = new ArrayList<Movies>();
    private ArrayAdapter<Movies> moviesArrayAdapter;

    private GroupAdapter adapter;

    private String ordenarPor;

    FirebaseDatabase firebase;
    DatabaseReference databaseReference;

    Button btnVoltar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_form_lista_filmes);

        /* RecyclerView - Exibe a lista de filmes */
        RecyclerView rv = findViewById(R.id.recycler);

        adapter = new GroupAdapter();
        rv.setAdapter(adapter);
        rv.setLayoutManager(new LinearLayoutManager(this));
        /* RecyclerView - Exibe a lista de filmes */

        /* Barra de Ação */
        // Esconde a barra de ação:
        //getSupportActionBar().hide();

        // Altera a cor de fundo:
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.black)));
        /* Barra de Ação */

        InicializarComponentes();

        InicializarFirebase();

        /* Selecionado um item da lista */
        adapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(@NonNull Item item, @NonNull View view) {

                MovieItem filmeSelecionado = (MovieItem) item;

                ArrayList<String> nomes_campos = new ArrayList<>();

                nomes_campos.add(filmeSelecionado.movie.getTitulo_portugues());     // 0
                nomes_campos.add(filmeSelecionado.movie.getTitulo_original());      // 1
                nomes_campos.add(filmeSelecionado.movie.getDirecao());              // 2
                nomes_campos.add(filmeSelecionado.movie.getProducao());             // 3
                nomes_campos.add(filmeSelecionado.movie.getNota());                 // 4
                nomes_campos.add(filmeSelecionado.movie.getElenco());               // 5
                nomes_campos.add(filmeSelecionado.movie.getSinopse());              // 6
                nomes_campos.add(filmeSelecionado.movie.getTemporadas());           // 7
                nomes_campos.add(filmeSelecionado.movie.getGenero());               // 8
                nomes_campos.add(filmeSelecionado.movie.getFilme_serie());          // 9
                nomes_campos.add(filmeSelecionado.movie.getUrlImagem());            // 10

                CarregaTelaExibeFilme( nomes_campos );

            }
        });

        /* Lista os filmes */
        ordenarPor = "titulo_portugues";
        eventoDatabase(ordenarPor);

        /* Botão VOLTAR */
        btnVoltar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(FormListaFilmes.this, FormPrincipal.class);
                startActivity(intent);
                finish();
            }
        });

    }

    /* Exibe as opções de menu */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);

        /* Pesquisar Filmes */
        MenuItem pesquisar = menu.findItem(R.id.menu_busca);
        SearchView edt_pesquisar = (SearchView) pesquisar.getActionView();
        edt_pesquisar.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                pesquisaFilmes(newText.toUpperCase());
                return true;
            }
        });
        /* Pesquisar Filmes */

        return super.onCreateOptionsMenu(menu);

    }


    /* Item selecionado no menu */
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        if (id==R.id.menu_busca) {
            Toast toast = Toast.makeText(getApplicationContext(), "ok", Toast.LENGTH_SHORT);
            toast.show();
        }

        return super.onOptionsItemSelected(item);

    }

    private void CarregaTelaExibeFilme(ArrayList<String> campos) {
        Intent intent = new Intent(FormListaFilmes.this, FormExibeFilme.class);
        intent.putStringArrayListExtra("exibe_filme", campos);
        startActivity(intent);
        finish();
    }


    ValueEventListener valueEventListener = new ValueEventListener() {
        @Override
        public void onDataChange(@NonNull DataSnapshot snapshot) {
            moviesList.clear();
            for (DataSnapshot dataSnapshot:snapshot.getChildren()) {
                // Movies movie = dataSnapshot.getValue(Movies.class);      // Não funciona
                Filmes movie = dataSnapshot.getValue(Filmes.class);
                // moviesList.add(movie);
                adapter.add(new MovieItem(movie));
            }
            moviesArrayAdapter = new ArrayAdapter<Movies>(FormListaFilmes.this, android.R.layout.simple_list_item_1, moviesList);
        }

        @Override
        public void onCancelled(@NonNull DatabaseError error) {

        }
    };

    private void eventoDatabase(String ordenarPor) {
        /* Lista todos os filmes */
        //databaseReference.orderByChild(ordenarPor)
        //        .addListenerForSingleValueEvent(valueEventListener);

        Query query = databaseReference.orderByChild(ordenarPor);

        /* Lista registros iguais */
        /*
        Query query = databaseReference.orderByChild(ordenarPor)
                .equalTo("OZARK");
        */

        /* Lista registros que começam com 2, por exemmplo */
        /*
        Query query = databaseReference.orderByChild(ordenarPor)
                .startAt("2")
                .endAt("2\uf8ff");
        */

        query.addListenerForSingleValueEvent(valueEventListener);

    }


    private void pesquisaFilmes(String newText) {
        moviesList.clear();
        // FirebaseFirestore.getInstance().collection("Filmes").whereIn("titulo_original", Collections.singletonList(newText));
        Query query = databaseReference.orderByChild("titulo_original")
                .startAt(newText)
                .endAt(newText+"\uf8ff");

        query.addListenerForSingleValueEvent(valueEventListener);
        adapter.clear();
    }

    /*
    private void eventoDatabase(String ordenarPor) {
        //atabaseReference.child("Filmes").orderByChild(ordenarPor)
        databaseReference.orderByChild(ordenarPor)
                .addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                moviesList.clear();
                for (DataSnapshot dataSnapshot:snapshot.getChildren()) {
                    // Movies movie = dataSnapshot.getValue(Movies.class);      // Não funciona
                    Filmes movie = dataSnapshot.getValue(Filmes.class);
                    // moviesList.add(movie);
                    adapter.add(new MovieItem(movie));
                }
                moviesArrayAdapter = new ArrayAdapter<Movies>(FormListaFilmes.this, android.R.layout.simple_list_item_1, moviesList);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
    */

    private class MovieItem extends Item<ViewHolder> {

        private final Filmes movie;

        private MovieItem(Filmes movie) {
            this.movie = movie;
        }


        @Override
        public void bind(@NonNull ViewHolder viewHolder, int position) {

            TextView txtTituloPortugues = viewHolder.itemView.findViewById(R.id.txt_titulo_portugues);
            TextView txtGenero = viewHolder.itemView.findViewById(R.id.txt_genero);
            ImageView imgFilme = viewHolder.itemView.findViewById(R.id.imageView);

            /* Textos */
            txtTituloPortugues.setText(movie.getTitulo_portugues());
            txtGenero.setText(movie.getGenero());

            /* Imagem */
            Picasso.get()
                    .load(movie.getUrlImagem())
                    .into(imgFilme);

        }

        @Override
        public int getLayout() {
            return R.layout.item_filme;
        }
    }


    /* Inicializa o Realtime Firebase e faz referência a tabela FILMES */
    private void InicializarFirebase() {
        FirebaseApp.initializeApp(FormListaFilmes.this);

        firebase = FirebaseDatabase.getInstance();
        //firebase.setPersistenceEnabled(true);

        databaseReference = firebase.getReference("Filmes");
        //databaseReference = firebase.getReference();
    }

    private void InicializarComponentes() {
        btnVoltar = findViewById(R.id.btn_lista_filmes_voltar);
    }

 }