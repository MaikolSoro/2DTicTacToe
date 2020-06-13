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
import android.widget.Toast;

import com.example.tictactoe.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

public class RegistroActivity extends AppCompatActivity {

    public EditText editName;
    public EditText editEmail;
    public EditText editPass;
    public Button btnRegistro;

    public FirebaseAuth firebaseAuth;
    public FirebaseFirestore firebaseFirestore;

    public String name;
    public String email;
    public  String password;

    private ScrollView formRegistro;
    private ProgressBar progressBarRegistre;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registro);

        editName = findViewById(R.id.editTextName);
        editEmail = findViewById(R.id.editTextEmail);
        editPass = findViewById(R.id.editTextPassword);
        btnRegistro = findViewById(R.id.buttonRegistro);
        formRegistro = findViewById(R.id.formRegistro);
        progressBarRegistre = findViewById(R.id.progressBarRegistre);

        firebaseAuth = FirebaseAuth.getInstance();
        changeRegistroFormVisibility(true);
        eventos();
    }

    private void eventos(){
        btnRegistro.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                name = editName.getText().toString();
                email = editEmail.getText().toString();
                password = editPass.getText().toString();
                if(name.isEmpty()) {
                    editName.setError("El nombre es obligatorio");

                } else if(email.isEmpty()) {
                    editEmail.setError("El email es obligatorio");
                } else if(password.isEmpty()){
                    editPass.setError("La contraseña es obligatoria");
                } else {
                    //TODO: REALIZAR EL REGISTRO EN FIREBASE AUTH
                    createUser();
                }
            }
        });
    }
    private void createUser(){
        changeRegistroFormVisibility(false);
        firebaseAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            // es invocado cuando se recibe la respuesta en firebase auth
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {

                if (task.isSuccessful()){
                    FirebaseUser user =  firebaseAuth.getCurrentUser();
                    updateUI(user);
                } else {
                    Log.w("TAG", "createUserWithEmail:failure", task.getException());
                    Toast.makeText(RegistroActivity.this, "Error en el registro", Toast.LENGTH_SHORT).show();
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

            Intent i = new Intent(RegistroActivity.this, FindGameActivity.class);
            startActivity(i);
        } else {
            changeRegistroFormVisibility(true);
            editPass.setError("Email y/o contraseña incorrectos");
            editPass.requestFocus();
        }

    }

    private  void changeRegistroFormVisibility(boolean showForm){
        progressBarRegistre.setVisibility(showForm ? View.GONE : View.VISIBLE);
        formRegistro.setVisibility(showForm ? View.VISIBLE : View.GONE);
    }
}