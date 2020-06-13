package com.example.tictactoe.ui;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.ScrollView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.example.tictactoe.R;

public class LoginActivity extends AppCompatActivity {

    private EditText editEmail;
    private EditText editPassword;
    private Button btnLogin;
    private ScrollView formLogin;
    private ProgressBar progressBarLogin;
    private Button btnRegistro;
    private FirebaseAuth firebaseAuth;
    public String email;
    public String password;
    private boolean tryLogin = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        editEmail = findViewById(R.id.editTextEmail);
        editPassword = findViewById(R.id.editTextPassword);
        btnLogin = findViewById(R.id.buttonLogin);
        formLogin = findViewById(R.id.formLogin);
        progressBarLogin = findViewById(R.id.progressBarLogin);
        btnRegistro = findViewById(R.id.btnRegistro);
        firebaseAuth = FirebaseAuth.getInstance();
        changeLoginFormVisibility(true);
        eventos();
    }

    //Evento de los botones
    private void  eventos(){
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                 email = editEmail.getText().toString();
                 password = editPassword.getText().toString();

                 if(email.isEmpty()) {
                    editEmail.setError("El email es obligatorio");
                } else if(password.isEmpty()){
                    editPassword.setError("La contraseña es obligatoria");
                } else {
                    //TODO: REALIZAR EL LOGIN EN FIREBASE AUTH
                     changeLoginFormVisibility(false);
                     loginUser();
                 }
            }
        });

        // evento para el btnRegistro

        btnRegistro.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(LoginActivity.this, RegistroActivity.class);
                startActivity(i);
            }
        });
    }

    private void loginUser(){
    firebaseAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
        @Override
        public void onComplete(@NonNull Task<AuthResult> task) {
            if(task.isSuccessful()) {
                tryLogin = true;
                FirebaseUser user = firebaseAuth.getCurrentUser();
                updateUI(user);
            } else {
                Log.w("TAG", "signInError:", task.getException());
                updateUI(null);
            }
        }
    });
    }

    private  void  updateUI(FirebaseUser user) {
        if( user != null){
            //Almecenar la información del usuario en fireStore
            //TODO

            // Navegar hacia la siguiente pantalla de la aplicación

            Intent i = new Intent(LoginActivity.this, FindGameActivity.class);
            startActivity(i);
        } else {
            changeLoginFormVisibility(true);
            if (tryLogin){
                editPassword.setError("Email y/o contraseña incorrectos");
                editPassword.requestFocus();
            }

        }

    }

    private  void changeLoginFormVisibility(boolean showForm){
        progressBarLogin.setVisibility(showForm ? View.GONE : View.VISIBLE);
        formLogin.setVisibility(showForm ? View.VISIBLE : View.GONE);
    }

    @Override
    protected void onStart() {
        super.onStart();

        // Comprobamos si previamente el usuario ya ha iniciado sesión en este dispositivo

        FirebaseUser currentUser = firebaseAuth.getCurrentUser();
        updateUI(currentUser);
    }
}