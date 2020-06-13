    package com.example.tictactoe.ui;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.tictactoe.R;
import com.example.tictactoe.app.Constantes;
import com.example.tictactoe.model.Jugada;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

    public class FindGameActivity extends AppCompatActivity {
    private TextView tvLoadingMessage;
    private ProgressBar progressBar;
    private ScrollView layoutProgressBar;
    private  ScrollView layoutMenuJuego;
    private Button btnJugar;
    private Button btnRanking;
    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore db;
    private FirebaseUser firebaseUser;
    private String uid;
    private  String jugadaId;
        @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_find_game);

        layoutProgressBar = findViewById(R.id.layoutprogressBar);
        layoutMenuJuego = findViewById(R.id.menuJuego);
        btnJugar = findViewById(R.id.buttonJugar);
        btnRanking = findViewById(R.id.buttonRanking);

        initProgressBar();
        initFirebase();
        eventos();

    }
    private void  initFirebase(){
            firebaseAuth = FirebaseAuth.getInstance();
            db = FirebaseFirestore.getInstance();
            firebaseUser = firebaseAuth.getCurrentUser();
            uid = firebaseUser.getUid();
    }
    private void eventos(){
            btnJugar.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    changeMenuVisibility(false);
                    buscarJugadaLibre();
                }
            });
            btnRanking.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                }
            });
    }

        private void buscarJugadaLibre() {
            tvLoadingMessage.setText("Buscando partida...");
            db.collection("jugadas").whereEqualTo("jugadorDosId", "")
                    .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                    if(task.getResult().size() == 0) {
                        //TODO no existen partidas libres, crear una nueva
                    } else {
                        DocumentSnapshot docJugada = task.getResult().getDocuments().get(0);
                        jugadaId = docJugada.getId();
                        Jugada jugada = docJugada.toObject(Jugada.class);
                        jugada.setJugadorDosId(uid);
                        db.collection("jugadas").document(jugadaId).set(jugada)
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        // iniciamos la partida
                                        startGame();
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                // muestro el menu
                                changeMenuVisibility(true);
                                Toast.makeText(FindGameActivity.this, "Hubo algún error al entrar en la partida", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                }
            });
        }

        /*
            Iniciamos la partida
         */
        private  void startGame(){
            Intent i = new Intent(FindGameActivity.this, GameActivity.class);
            i.putExtra(Constantes.EXTRA_JUGADA_ID, jugadaId);
            startActivity(i);
        }
        /*
            Mostrar el progressBar
         */
    private  void initProgressBar(){
        tvLoadingMessage = findViewById(R.id.textViewLoading);
        progressBar = findViewById(R.id.progressBarJugadas);

        progressBar.setIndeterminate(true);
        tvLoadingMessage.setText("Cargando...");

        // Modificar la visibilidad por defecto del progressBar y menuJuego
        changeMenuVisibility(true);
    }

    /*
        Mostrar menu del juego
     */
    private void changeMenuVisibility(boolean showMenu){
        layoutProgressBar.setVisibility(showMenu ? View.GONE : View.VISIBLE);
        layoutMenuJuego.setVisibility(showMenu ? View.VISIBLE : View.GONE);
    }

        @Override
        protected void onResume() {
            super.onResume();
            changeMenuVisibility(true); // para que se va por defecto el menu
        }
    }