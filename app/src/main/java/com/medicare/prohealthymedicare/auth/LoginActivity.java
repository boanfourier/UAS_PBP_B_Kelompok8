package com.medicare.prohealthymedicare.auth;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.medicare.prohealthymedicare.MainActivity;
import com.medicare.prohealthymedicare.R;
import com.medicare.prohealthymedicare.admin.AdminActivity;
import com.medicare.prohealthymedicare.database.AppDatabase;
import com.medicare.prohealthymedicare.database.dao.UsersDao;
import com.medicare.prohealthymedicare.database.entity.UserEntity;
import com.medicare.prohealthymedicare.databinding.ActivityLoginBinding;
import com.medicare.prohealthymedicare.model.ResponsePost;
import com.medicare.prohealthymedicare.network.ApiInterface;
import com.medicare.prohealthymedicare.network.ServiceGenerator;
import com.medicare.prohealthymedicare.session.Session;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity {
    private ActivityLoginBinding binding;
    private AppDatabase database;
    private List<UserEntity> list = new ArrayList<>();
    private FirebaseAuth firebaseAuth;
    private ProgressDialog progressDialog;
    String status;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = DataBindingUtil.setContentView(this, R.layout.activity_login);
        binding.getLifecycleOwner();

        database = AppDatabase.getInstance(this);
        firebaseAuth = FirebaseAuth.getInstance();
        if (binding.checkAdmin.isChecked()) {
            status = "admin";
        } else {
            status = "";
        }
        binding.checkAdmin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (binding.checkAdmin.isChecked()) {
                    status = "admin";
                } else {
                    status = "";
                }
            }
        });


        binding.btnlogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressDialog = ProgressDialog.show(LoginActivity.this, "", "Loading ...");
                progressDialog.setCanceledOnTouchOutside(false);

                String email = binding.edtusername.getText().toString().trim();
                String password = binding.edtpassword.getText().toString().trim();
                if (!email.isEmpty() && !password.isEmpty()) {

                    if (status.equals("admin")) {  ApiInterface loginadmin = ServiceGenerator.createService(ApiInterface.class);
                        loginadmin.loginadmin(email,password).enqueue(new Callback<ResponsePost>() {
                            @Override
                            public void onResponse(Call<ResponsePost> call, Response<ResponsePost> response) {
                                if (response.isSuccessful()){
                                    if (response.body().getData()==1){
                                        progressDialog.dismiss();
                                        Session.setIsLogin(getBaseContext(), "admin");
                                        Intent intent = new Intent(LoginActivity.this, AdminActivity.class);
                                        startActivity(intent);
                                        finish();
                                    }else {
                                        progressDialog.dismiss();
                                        Toast.makeText(LoginActivity.this,"username dan password salah",Toast.LENGTH_SHORT).show();
                                    }
                                }else {
                                    progressDialog.dismiss();
                                    Toast.makeText(LoginActivity.this,"username dan password salah",Toast.LENGTH_SHORT).show();
                                }
                            }

                            @Override
                            public void onFailure(Call<ResponsePost> call, Throwable t) {
                                progressDialog.dismiss();
                                Toast.makeText(LoginActivity.this,"gagal koneksi server",Toast.LENGTH_SHORT).show();
                                Log.d("boan", "onFailure: "+t.getMessage());
                            }
                        });
                    } else {

                        firebaseAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                progressDialog.dismiss();
                                if (task.isSuccessful()) {
                                    if (firebaseAuth.getCurrentUser().isEmailVerified()) {
                                        Session.setIsLogin(getBaseContext(), "sukses");
                                        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                                        startActivity(intent);
                                        finish();
                                    } else {
                                        Session.setIsLogin(getBaseContext(), "verifikasi");
                                        Intent intent = new Intent(LoginActivity.this, VerifikasiActivity.class);
                                        startActivity(intent);
                                        finish();
                                    }
                                } else {
                                    Snackbar.make(v, "cek email dan password", Snackbar.LENGTH_LONG).show();
                                }

                            }
                        });

                    }


                } else {
                    progressDialog.dismiss();
                    Snackbar.make(v, "Isi semua kolom ", Snackbar.LENGTH_LONG).show();
                }

            }
        });

        binding.btnregister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
                startActivity(intent);
            }
        });

        binding.btnclear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                binding.edtusername.setText(null);
                binding.edtpassword.setText(null);
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (Session.getIsLogin(getBaseContext()).equals("sukses")) {
            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
        } else if (Session.getIsLogin(getBaseContext()).equals("verifikasi")) {
            Intent intent = new Intent(LoginActivity.this, VerifikasiActivity.class);
            startActivity(intent);
            finish();
        } else if (Session.getIsLogin(getBaseContext()).equals("admin")) {
            Intent intent = new Intent(LoginActivity.this, AdminActivity.class);
            startActivity(intent);
            finish();
        }
    }
}