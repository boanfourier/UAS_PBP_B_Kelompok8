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

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.medicare.prohealthymedicare.R;
import com.medicare.prohealthymedicare.database.AppDatabase;
import com.medicare.prohealthymedicare.database.entity.UserEntity;
import com.medicare.prohealthymedicare.databinding.ActivityRegisterBinding;
import com.medicare.prohealthymedicare.model.ResponseBodyAuth;
import com.medicare.prohealthymedicare.model.ResponsePost;
import com.medicare.prohealthymedicare.network.ApiInterface;
import com.medicare.prohealthymedicare.network.ServiceGenerator;
import com.medicare.prohealthymedicare.session.Session;

import java.util.prefs.Preferences;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RegisterActivity extends AppCompatActivity {

    ActivityRegisterBinding binding;
    private AppDatabase database;
    FirebaseAuth firebaseAuth;
    ProgressDialog progressDialog;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_register);
        binding.getLifecycleOwner();
        database = AppDatabase.getInstance(this);

        firebaseAuth = FirebaseAuth.getInstance();

        binding.btnregister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                progressDialog = ProgressDialog.show(RegisterActivity.this,"Loading","...");
                progressDialog.setCanceledOnTouchOutside(false);
                String firstname = binding.edtfirstname.getText().toString().trim();
                String lastname = binding.edtlastname.getText().toString().trim();
                String email = binding.edtusername.getText().toString().trim();
                String password = binding.edtpassword.getText().toString().trim();
                String nohp = binding.edtnohp.getText().toString().trim();

                if (!firstname.isEmpty() && !lastname.isEmpty() && !email.isEmpty() && !nohp.isEmpty()) {
                    firebaseAuth.createUserWithEmailAndPassword(email,password).addOnCompleteListener((task -> {
                        if (task.isSuccessful()){
                            //send verification email
                            FirebaseUser user = firebaseAuth.getCurrentUser();
                            user.sendEmailVerification().addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void unused) {

                                    ApiInterface setregister = ServiceGenerator.createService(ApiInterface.class);
                                    Call<ResponsePost> call = setregister.registerusers(
                                            firebaseAuth.getUid(),
                                            firstname,
                                            lastname,
                                            email,
                                            nohp,
                                            password
                                    );

                                    call.enqueue(new Callback<ResponsePost>() {
                                        @Override
                                        public void onResponse(Call<ResponsePost> call, Response<ResponsePost> response) {
                                            try {
                                                if (response.isSuccessful()){
                                                    if (response.body().getData() == 1){
                                                        progressDialog.dismiss();
                                                        Session.setIsLogin(getBaseContext(),"verifikasi");
                                                        Toast.makeText(RegisterActivity.this,"email sudah dikirim",Toast.LENGTH_SHORT).show();
                                                        Intent intent = new Intent(RegisterActivity.this, VerifikasiActivity.class);
                                                        startActivity(intent);
                                                        finish();
                                                    }else {
                                                        progressDialog.dismiss();
                                                        Log.d("boan", "onResponse: "+response.body());
                                                        Toast.makeText(RegisterActivity.this,"gagal",Toast.LENGTH_SHORT).show();

                                                    }

                                                }else {
                                                    progressDialog.dismiss();
                                                    Log.d("boan", "onResponse: "+response.body());
                                                    Toast.makeText(RegisterActivity.this,"gagal",Toast.LENGTH_SHORT).show();
                                                }

                                            }catch (Exception e){
                                                progressDialog.dismiss();
                                                Log.d("boan", "onResponse: "+e.getMessage());
                                            }

                                        }

                                        @Override
                                        public void onFailure(Call<ResponsePost> call, Throwable t) {
                                            progressDialog.dismiss();
                                            Log.d("boan", "onResponse: "+t.getMessage());
                                            Toast.makeText(RegisterActivity.this,"gagal",Toast.LENGTH_SHORT).show();
                                        }
                                    });

                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    progressDialog.dismiss();
                                    Snackbar.make(view, "gagal" + e.getMessage(), Snackbar.LENGTH_LONG).show();
                                    Log.d("boan", "onResponse: "+e.getMessage());
                                }
                            });
                            //==========

                            Snackbar.make(view, "Sukses ", Snackbar.LENGTH_LONG).show();
                        }else {
                            Snackbar.make(view, "Gagal ", Snackbar.LENGTH_LONG).show();
                        }
                    }));

                    //==========

                } else {
                    Snackbar.make(view, "Isi semua kolom ", Snackbar.LENGTH_LONG).show();
                }
            }
        });

    }
}