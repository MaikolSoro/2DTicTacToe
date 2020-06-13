package com.example.a2dtictactoe;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.ScrollView;

public class LoginActivity extends AppCompatActivity {

    private EditText editEmail;
    private EditText editPassword;
    private Button btnLogin;
    private ScrollView formLogin;
    private ProgressBar progressBarLogin;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        editEmail = findViewById(R.id.editTextEmail);
        editPassword = findViewById(R.id.editTextPassword);
        btnLogin = findViewById(R.id.buttonLogin);
        formLogin = findViewById(R.id.formLogin);
        progressBarLogin = findViewById(R.id.progressBarLogin);
        changeLoginFormVisibility(true);
        eventos();
    }

    //Evento
    private void  eventos(){
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = editEmail.getText().toString();
                String password = editPassword.getText().toString();
                changeLoginFormVisibility(false);
            }
        });
    }

    private  void changeLoginFormVisibility(boolean showForm){
        progressBarLogin.setVisibility(showForm ? View.GONE : View.VISIBLE);
        formLogin.setVisibility(showForm ? View.VISIBLE : View.GONE);
    }
}