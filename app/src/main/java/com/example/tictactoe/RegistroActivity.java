package com.example.tictactoe;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class RegistroActivity extends AppCompatActivity {

    public EditText editName;
    public EditText editEmail;
    public EditText editPass;
    public Button btnRegistro;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registro);

        editName = findViewById(R.id.editTextName);
        editEmail = findViewById(R.id.editTextEmail);
        editPass = findViewById(R.id.editTextPassword);
        btnRegistro = findViewById(R.id.buttonRegistro);

        eventos();
    }

    private void eventos(){
        btnRegistro.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String name = editName.getText().toString();
                String email = editEmail.getText().toString();
                String password = editPass.getText().toString();
                if(name.isEmpty()) {
                    editName.setError("El nombre es obligatorio");

                } else if(email.isEmpty()) {
                    editEmail.setError("El email es obligatorio");
                } else if(password.isEmpty()){
                    editPass.setError("La contraseña es obligatoria");
                } else {
                    //TODO: REALIZAR EL REGISTRO EN FIREBASE AUTH
                }
            }
        });
    }
}