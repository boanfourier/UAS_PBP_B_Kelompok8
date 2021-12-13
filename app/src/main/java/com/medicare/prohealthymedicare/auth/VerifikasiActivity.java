package com.medicare.prohealthymedicare.auth;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.medicare.prohealthymedicare.MainActivity;
import com.medicare.prohealthymedicare.R;
import com.medicare.prohealthymedicare.databinding.ActivityVerifikasiBinding;
import com.medicare.prohealthymedicare.session.Session;

public class VerifikasiActivity extends AppCompatActivity {
ActivityVerifikasiBinding binding;

    FirebaseAuth auth;
    TextView status;
    Button btnverifikasi,btnlogout;
    ProgressDialog progressDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this,R.layout.activity_verifikasi);
        binding.getLifecycleOwner();

        progressDialog = new ProgressDialog(VerifikasiActivity.this);


        auth = FirebaseAuth.getInstance();
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        binding.txtstatus.setText("Status : "+ user.isEmailVerified());

        updatestatus();

        binding.btnverifikasi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                progressDialog = ProgressDialog.show(VerifikasiActivity.this,"","Loading ...");
                progressDialog.setCanceledOnTouchOutside(false);
                FirebaseAuth.getInstance().getCurrentUser().reload().addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        updatestatus();
                    }
                });
            }
        });

        binding.btnlogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseAuth.getInstance().signOut();
                Session.setIsLogin(getBaseContext(),"");
                Intent intent = new Intent(VerifikasiActivity.this, LoginActivity.class);
                startActivity(intent);
                finish();
            }
        });

    }

    private void updatestatus(){
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user.isEmailVerified() == true){
            progressDialog.dismiss();
            Intent intent = new Intent(VerifikasiActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
        }else {
            progressDialog.dismiss();
            Toast.makeText(getBaseContext(),"silahkan verifikasi email", Toast.LENGTH_SHORT).show();
        }
    }
}