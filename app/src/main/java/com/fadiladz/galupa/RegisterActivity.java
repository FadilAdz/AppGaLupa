package com.fadiladz.galupa;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.fadiladz.galupa.databinding.ActivityRegisterBinding;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class RegisterActivity extends AppCompatActivity {

    private ActivityRegisterBinding binding;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Inisialisasi binding
        binding = ActivityRegisterBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Inisialisasi Firestore
        db = FirebaseFirestore.getInstance();

        setupAction();
    }

    private void setupAction() {
        binding.tvLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                startActivity(intent);
            }
        });

        binding.btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Ambil input dan hilangkan spasi di awal dan akhir
                String nama = binding.etNama.getText().toString().trim();
                String email = binding.etEmail.getText().toString().trim();
                String password = binding.etPassword.getText().toString().trim();

                // Validasi input
                if (email.isEmpty() || nama.isEmpty() || password.isEmpty()) {
                    Toast.makeText(RegisterActivity.this, "Lengkapi data", Toast.LENGTH_SHORT).show();
                } else {
                    buatAkun(email, nama, password);
                }
            }
        });
    }

    private void buatAkun(String email, String nama, String password) {
        // Tampilkan toast loading jika ingin
        Toast.makeText(RegisterActivity.this, "Sedang membuat akun...", Toast.LENGTH_SHORT).show();

        // Buat map untuk data user
        Map<String, Object> user = new HashMap<>();
        user.put("email", email);
        user.put("nama", nama);
        user.put("password", password);

        // Tambahkan ke Firestore dengan add() - akan membuat ID dokumen otomatis
        db.collection("users")
                .add(user)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        // Berhasil menambahkan data
                        Toast.makeText(RegisterActivity.this, "Berhasil membuat akun", Toast.LENGTH_SHORT).show();

                        // Log ID dokumen yang dibuat (opsional)
                        String docId = documentReference.getId();
                        System.out.println("DocumentSnapshot added with ID: " + docId);

                        // Pindah ke halaman login
                        Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                        startActivity(intent);
                        finish(); // Tutup halaman register
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        // Gagal menambahkan data
                        Toast.makeText(RegisterActivity.this, "Gagal membuat akun: " + e.getMessage(), Toast.LENGTH_SHORT).show();

                        // Log error untuk debugging
                        System.out.println("Error adding document: " + e.getMessage());
                    }
                });
    }
}