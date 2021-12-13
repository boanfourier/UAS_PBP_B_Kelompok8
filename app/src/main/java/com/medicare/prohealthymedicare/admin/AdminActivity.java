package com.medicare.prohealthymedicare.admin;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.medicare.prohealthymedicare.R;
import com.medicare.prohealthymedicare.adapter.DataDokterAdapter;
import com.medicare.prohealthymedicare.auth.LoginActivity;
import com.medicare.prohealthymedicare.databinding.ActivityAdminBinding;
import com.medicare.prohealthymedicare.model.DataDokterModels;
import com.medicare.prohealthymedicare.model.ResponseBodyDokter;
import com.medicare.prohealthymedicare.model.ResponsePost;
import com.medicare.prohealthymedicare.network.ApiInterface;
import com.medicare.prohealthymedicare.network.ServiceGenerator;
import com.medicare.prohealthymedicare.session.Session;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AdminActivity extends AppCompatActivity {
    ActivityAdminBinding binding;
    private DataDokterAdapter adapter;
    private List<DataDokterModels> dataDokterModelsList;
    private View view;
    ProgressDialog progressDialog;
    private AlertDialog.Builder dialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_admin);
        binding.getLifecycleOwner();
        progressDialog = new ProgressDialog(this);
        getdatadokter(view);

        binding.btnlogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Session.setIsLogin(getBaseContext(),"");
                Intent intent = new Intent(AdminActivity.this, LoginActivity.class);
                startActivity(intent);
                finish();
            }
        });

        binding.btnadddokter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AdminActivity.this, TambahDokterActivity.class);
                startActivity(intent);
            }
        });
    }

    void getdatadokter(View view) {
        ApiInterface getdokter = ServiceGenerator.createService(ApiInterface.class);
        getdokter.getdokter().enqueue(new Callback<ResponseBodyDokter>() {
            @Override
            public void onResponse(Call<ResponseBodyDokter> call, Response<ResponseBodyDokter> response) {
                if (response.isSuccessful()) {

                    List<DataDokterModels> dokterModels = response.body().getDataDokterModels();
                    createDataList(dokterModels);
                }
            }

            @Override
            public void onFailure(Call<ResponseBodyDokter> call, Throwable t) {
                Toast.makeText(AdminActivity.this,"gagal api",Toast.LENGTH_SHORT).show();
                Log.d("boan", "onFailure: " + t.getMessage());
            }
        });

    }

    private void createDataList(List<DataDokterModels> dokterModels) {
        adapter = new DataDokterAdapter(dokterModels);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(AdminActivity.this);

        binding.rvdokter.setLayoutManager(layoutManager);

        binding.rvdokter.setAdapter(adapter);
        adapter.setDialog(new DataDokterAdapter.Dialog() {
            @Override
            public void onClick(int position, String nama, String hari, String jam, String cp,Integer id_dokter,String foto,String jenis) {
                final CharSequence[] dialogItem = {String.format("Update"), "Hapus"};
                dialog = new AlertDialog.Builder(AdminActivity.this);
                dialog.setItems(dialogItem, new DialogInterface.OnClickListener() {
                    @SuppressLint("ShowToast")
                    @Override
                    public void onClick(DialogInterface dialog, int i) {
                        switch (i) {
                            case 0:
                                Intent intent = new Intent(AdminActivity.this, DetailActivity.class);
                                intent.putExtra("nama", nama);
                                intent.putExtra("hari", hari);
                                intent.putExtra("jam", jam);
                                intent.putExtra("cp", cp);
                                intent.putExtra("id_barang", id_dokter);
                                intent.putExtra("foto", foto);
                                intent.putExtra("jenis", jenis);
                                startActivity(intent);
                                break;

                            case 1:
                                progressDialog.setTitle("Sedang hapus dokter");
                                progressDialog.setCanceledOnTouchOutside(false);
                                progressDialog.show();
                                ApiInterface hapusdokter = ServiceGenerator.createService(ApiInterface.class);
                                hapusdokter.hapusdokter(id_dokter).enqueue(new Callback<ResponsePost>() {
                                    @Override
                                    public void onResponse(Call<ResponsePost> call, Response<ResponsePost> response) {
                                        if (response.isSuccessful()){
                                            if (response.body().getData() == 1){
                                                progressDialog.dismiss();
                                                Toast.makeText(AdminActivity.this,"data berhasil di hapus",Toast.LENGTH_SHORT).show();
                                                onStart();
                                            }else {
                                                progressDialog.dismiss();
                                            }
                                        }else {
                                            progressDialog.dismiss();
                                            Toast.makeText(AdminActivity.this,"tidak dapat response",Toast.LENGTH_SHORT).show();

                                        }
                                    }

                                    @Override
                                    public void onFailure(Call<ResponsePost> call, Throwable t) {
                                        progressDialog.dismiss();
                                        Toast.makeText(AdminActivity.this,"masalah jaringan",Toast.LENGTH_SHORT).show();

                                    }
                                });
                                break;
                        }
                    }
                });
                dialog.show();
            }
        });
        adapter.notifyDataSetChanged();

    }

    @Override
    protected void onStart() {
        super.onStart();
        getdatadokter(view);
    }
}