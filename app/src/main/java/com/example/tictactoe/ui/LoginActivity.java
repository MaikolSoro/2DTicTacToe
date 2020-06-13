package com.example.tictactoe.ui;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.ScrollView;
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
                String email = editEmail.getText().toString();
                String password = editPassword.getText().toString();

                 if(email.isEmpty()) {
                    editEmail.setError("El email es obligatorio");
                } else if(password.isEmpty()){
                    editPassword.setError("La contraseña es obligatoria");
                } else {
                    //TODO: REALIZAR EL LOGIN EN FIREBASE AUTH
                     changeLoginFormVisibility(false);
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

    private  void changeLoginFormVisibility(boolean showForm){
        progressBarLogin.setVisibility(showForm ? View.GONE : View.VISIBLE);
        formLogin.setVisibility(showForm ? View.VISIBLE : View.GONE);
    }
}