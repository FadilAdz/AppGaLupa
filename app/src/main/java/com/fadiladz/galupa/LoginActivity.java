package com.fadiladz.galupa;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.fadiladz.galupa.databinding.ActivityLoginBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

public class LoginActivity extends AppCompatActivity {

    private ActivityLoginBinding binding;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        db = FirebaseFirestore.getInstance();

        setupAction();
    }

    private void setupAction() {
        binding.tvRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
                startActivity(intent);
            }
        });

        binding.btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = binding.etEmail.getText().toString();
                String password = binding.etPassword.getText().toString();

                if (email.isEmpty() || password.isEmpty()){
                    Toast.makeText(LoginActivity.this, "Email Atau Password Salah", Toast.LENGTH_SHORT).show();
                }else {
                    checkAkun(email, password);
                }
            }

            private void checkAkun(String email, String password) {
                db.collection("users")
                        .whereEqualTo("email", email)
                        .get()
                        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                if (task.isSuccessful() && !task.getResult().isEmpty()){
                                    QuerySnapshot querySnapshot = task.getResult();
                                    boolean isPasswordCorrect = false;

                                    for (QueryDocumentSnapshot document : querySnapshot){
                                        String storedPassword = document.getString("password");
                                        if (storedPassword != null && storedPassword.equals(password)){
                                            isPasswordCorrect = true;
                                            break;
                                        }
                                    }

                                    if (isPasswordCorrect){
                                        Toast.makeText(LoginActivity.this, "Login Berhasil", Toast.LENGTH_SHORT).show();
                                        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                                        startActivity(intent);
                                    }else {
                                        Toast.makeText(LoginActivity.this, "Password Salah", Toast.LENGTH_SHORT).show();
                                    }
                                }else {
                                    Toast.makeText(LoginActivity.this, "Akun Tidak Ditemukan", Toast.LENGTH_SHORT).show();
                                }
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(LoginActivity.this, "Gagal Login", Toast.LENGTH_SHORT).show();
                            }
                        });
            }
        });
    }
}