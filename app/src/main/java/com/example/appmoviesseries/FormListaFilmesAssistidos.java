package com.example.appmoviesseries;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.xwray.groupie.GroupAdapter;
import com.xwray.groupie.Item;
import com.xwray.groupie.ViewHolder;

public class FormListaFilmesAssistidos extends AppCompatActivity {

    private GroupAdapter adapter;
    private FirebaseDatabase firebase;
    private DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_form_lista_filmes_assistidos);

        // Esconde a barra de ação:
        getSupportActionBar().hide();

        RecyclerView rv = findViewById(R.id.recycleAssistidos);

        adapter = new GroupAdapter();
        rv.setAdapter(adapter);
        rv.setLayoutManager(new LinearLayoutManager(this));

        listaFilmesAssistidos();

    }

    private void listaFilmesAssistidos() {

        firebase = FirebaseDatabase.getInstance();
        databaseReference = firebase.getReference("Assistidos");

        /* ID do usuário logado */
        String usuarioID = FirebaseAuth.getInstance().getCurrentUser().getUid();

        Query query = databaseReference
                .child(usuarioID);

        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot:snapshot.getChildren()) {
                    Assistidos assistido = dataSnapshot.getValue(Assistidos.class);
                    if (assistido.getAssistido().equals("S")) {
                        adapter.add(new AssistidoItem(assistido));
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    private class AssistidoItem extends Item<ViewHolder> {

        private final Assistidos assistidos;

        private AssistidoItem(Assistidos assistidos) {
            this.assistidos = assistidos;
        }

        @Override
        public void bind(@NonNull ViewHolder viewHolder, int position) {
            TextView txtAssistidoTituloPortugues = viewHolder.itemView.findViewById(R.id.txtItemAssistido);
            txtAssistidoTituloPortugues.setText(assistidos.getTituloPortugues());
        }

        @Override
        public int getLayout() {
            return R.layout.item_assistidos;
        }
    }
}