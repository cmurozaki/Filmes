package com.example.appmoviesseries;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.auth.User;
import com.squareup.picasso.Picasso;
import com.xwray.groupie.GroupAdapter;
import com.xwray.groupie.Item;
import com.xwray.groupie.OnItemClickListener;
import com.xwray.groupie.ViewHolder;

import java.util.ArrayList;
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

        RecyclerView rv = findViewById(R.id.recycler);

        adapter = new GroupAdapter();
        rv.setAdapter(adapter);
        rv.setLayoutManager(new LinearLayoutManager(this));

        getSupportActionBar().hide();       // Esconde a barra de ação

        InicializarComponentes();

        InicializarFirebase();

        /* Selecionado um item da lista */
        adapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(@NonNull Item item, @NonNull View view) {

                MovieItem filmeSelecionado = (MovieItem) item;

                ArrayList<String> nomes_campos = new ArrayList<>();

                nomes_campos.add(filmeSelecionado.movie.getTitulo_portugues());
                nomes_campos.add(filmeSelecionado.movie.getTitulo_original());
                nomes_campos.add(filmeSelecionado.movie.getDirecao());
                nomes_campos.add(filmeSelecionado.movie.getProducao());
                nomes_campos.add(filmeSelecionado.movie.getNota());
                nomes_campos.add(filmeSelecionado.movie.getElenco());
                nomes_campos.add(filmeSelecionado.movie.getSinopse());
                nomes_campos.add(filmeSelecionado.movie.getTemporadas());
                nomes_campos.add(filmeSelecionado.movie.getGenero());
                nomes_campos.add(filmeSelecionado.movie.getFilme_serie());

                CarregaTelaExibeFilme( nomes_campos );

                Toast toast = Toast.makeText( getApplicationContext(), filmeSelecionado.movie.getDirecao().toString(), Toast.LENGTH_SHORT );
                toast.show();
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

    private void CarregaTelaExibeFilme(ArrayList<String> campos) {
        Intent intent = new Intent(FormListaFilmes.this, FormExibeFilme.class);
        intent.putStringArrayListExtra("exibe_filme", campos);
        // intent.putExtra("exibe_filme", campos );
        startActivity(intent);
        finish();
    }

    private void eventoDatabase(String ordenarPor) {
        databaseReference.child("Filmes").orderByChild(ordenarPor).addValueEventListener(new ValueEventListener() {
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

    private void InicializarFirebase() {
        FirebaseApp.initializeApp(FormListaFilmes.this);

        firebase = FirebaseDatabase.getInstance();
        //firebase.setPersistenceEnabled(true);

        databaseReference = firebase.getReference();
    }

    private void InicializarComponentes() {
        btnVoltar = findViewById(R.id.btn_lista_filmes_voltar);
    }

 }