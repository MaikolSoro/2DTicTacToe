    package com.example.tictactoe.ui;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.airbnb.lottie.LottieAnimationView;
import com.example.tictactoe.R;
import com.example.tictactoe.app.Constantes;
import com.example.tictactoe.model.Jugada;
import com.google.android.gms.common.util.concurrent.HandlerExecutor;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
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
    private ListenerRegistration listenerRegistration = null;
    private LottieAnimationView animationView;
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
            animationView.playAnimation();
            db.collection("jugadas").whereEqualTo("jugadorDosId", "")
                    .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                    if(task.getResult().size() == 0) {
                        // no existen partidas libres, crear una nueva
                        crearNuevaJugada();

                    } else {
                        boolean encontrado = false;

                        for (DocumentSnapshot docJugada : task.getResult().getDocuments()) {
                            if(docJugada.get("jugadorUnoId").equals(uid)) {
                                encontrado = true;
                                jugadaId = docJugada.getId();
                                Jugada jugada = docJugada.toObject(Jugada.class);
                                jugada.setJugadorDosId(uid);
                                db.collection("jugadas").document(jugadaId).set(jugada)
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                                // iniciamos la partida
                                                tvLoadingMessage.setText("¡Partida libre encontrada! Comienza la partida");
                                                animationView.setRepeatCount(0);
                                                animationView.setAnimation("checked-done.json");
                                                animationView.playAnimation();

                                                final Handler handler = new Handler();
                                                final Runnable r = new Runnable() {
                                                    @Override
                                                    public void run() {
                                                        startGame();
                                                    }
                                                };
                                                handler.postDelayed(r, 1500);
                                            }
                                        }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        // muestro el menu
                                        changeMenuVisibility(true);
                                        Toast.makeText(FindGameActivity.this, "Hubo algún error al entrar en la partida", Toast.LENGTH_SHORT).show();
                                    }
                                });
                                break;
                            }
                            if(!encontrado) crearNuevaJugada();
                        }

                    }
                }
            });
        }

        private void crearNuevaJugada() {
            tvLoadingMessage.setText("Creando una jugada nueva");
            Jugada nuevaJugada = new Jugada(uid);
            db.collection("jugadas").add(nuevaJugada)
                    .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                        @Override
                        public void onSuccess(DocumentReference documentReference) {
                            jugadaId = documentReference.getId();
                            // Tenemos  creada la jugada, debemos esperar a otro jugador
                            esperarJugador();
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    changeMenuVisibility(true);
                    Toast.makeText(FindGameActivity.this, "Problemas al crear una partida", Toast.LENGTH_SHORT).show();
                }
            });
        }

        private void esperarJugador() {
            tvLoadingMessage.setText("Esperando a otro jugador....");

            listenerRegistration = db.collection("jugadas")
                    .document(jugadaId).addSnapshotListener(new EventListener<DocumentSnapshot>() {
                        @Override
                        public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                            if(!documentSnapshot.get("jugadorDosId").equals("")){
                                tvLoadingMessage.setText("¡Ya ha llegado un jugador! Comienza la partida");
                                animationView.setRepeatCount(0);
                                animationView.setAnimation("checked-done.json");
                                animationView.playAnimation();

                                final Handler handler = new Handler();
                                final Runnable r = new Runnable() {
                                    @Override
                                    public void run() {
                                        startGame();
                                    }
                                };
                                handler.postDelayed(r,1500);

                            }
                        }
                    });
        }

        /*
            Iniciamos la partida
         */
        private  void startGame(){
            if(listenerRegistration != null) {
                listenerRegistration.remove();
            }
            Intent i = new Intent(FindGameActivity.this, GameActivity.class);
            i.putExtra(Constantes.EXTRA_JUGADA_ID, jugadaId);
            startActivity(i);
            jugadaId = "";
        }
        /*
            Mostrar el progressBar
         */
    private  void initProgressBar(){
        animationView = findViewById(R.id.animation_view);
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
            changeMenuVisibility(true);

        }
            /*if(jugadaId != ""){
                changeMenuVisibility(false);
                esperarJugador();
            } else {
                changeMenuVisibility(true); // para que se va por defecto el menu

            }
        }

             */

        /*
        Eliminar una jugada, cuando el usuario se salga del juego
        @Override
        protected void onStop() {

            if(listenerRegistration != null) {
                listenerRegistration.remove();
            }
            if(jugadaId != ""){
                db.collection("jugadas").document(jugadaId)
                        .delete()
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                jugadaId= "";
                            }
                        });

            }
            super.onStop();
        }

         */
    }