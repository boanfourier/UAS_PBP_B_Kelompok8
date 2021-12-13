package com.medicare.prohealthymedicare.admin;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.google.android.material.snackbar.Snackbar;
import com.medicare.prohealthymedicare.R;
import com.medicare.prohealthymedicare.databinding.ActivityTambahDokterBinding;
import com.medicare.prohealthymedicare.model.ResponsePost;
import com.medicare.prohealthymedicare.network.ApiInterface;
import com.medicare.prohealthymedicare.network.ServiceGenerator;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import pub.devrel.easypermissions.EasyPermissions;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class TambahDokterActivity extends AppCompatActivity {

    public static final int REQUEST_IMAGE = 100;
    private static final int PICK_IMAGE = 1;
    private static final int PERMISSION_REQUEST_STORAGE = 2;
    private static final String TYPE_1 = "multipart";
    ActivityTambahDokterBinding binding;
    private Uri uri;
    View view;
    ProgressDialog progressDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this,R.layout.activity_tambah_dokter);
        binding.getLifecycleOwner();
        progressDialog = new ProgressDialog(TambahDokterActivity.this);

        binding.btnGalery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (EasyPermissions.hasPermissions(TambahDokterActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE)){
                    openGallery();

                }else {
                    EasyPermissions.requestPermissions(TambahDokterActivity.this,"This application need your permission to access photo gallery",PERMISSION_REQUEST_STORAGE
                            ,Manifest.permission.READ_EXTERNAL_STORAGE);
                }
            }
        });

        binding.btnUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                progressDialog.setTitle("Sedang upload ....");
                progressDialog.show();
                progressDialog.setCanceledOnTouchOutside(false);

                String nama = binding.edtNama.getText().toString().trim();
                String jenis = binding.edtjenis.getText().toString().trim();
                String jam = binding.edtjam.getText().toString().trim();
                String hari = binding.edthari.getText().toString().trim();
                String cp = binding.cp.getText().toString().trim();

                if (!nama.isEmpty() && !jenis.isEmpty() && !jam.isEmpty() && !hari.isEmpty() && !cp.isEmpty() && inputStream!=null){
                    try {
                        uploadImage(getBytes(inputStream),nama,jenis,jam,hari,cp);

                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }else {
                    progressDialog.dismiss();
                    Snackbar.make(view,  "Jangan kosongi kolom", Snackbar.LENGTH_LONG).show();
                }

            }
        });


    }

    public void openGallery() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Image"), PICK_IMAGE);
    }
    public byte[] getBytes(InputStream is) throws IOException {
        ByteArrayOutputStream byteBuff = new ByteArrayOutputStream();

        int buffSize = 1024;
        byte[] buff = new byte[buffSize];

        int len = 0;
        while ((len = is.read(buff)) != -1) {
            byteBuff.write(buff, 0, len);
        }

        return byteBuff.toByteArray();
    }

    InputStream inputStream;
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE && resultCode == Activity.RESULT_OK) {
            if(data != null) {
                try {
                    uri = data.getData();
                    InputStream is = getContentResolver().openInputStream(data.getData());
                    Picasso.get().load(uri).centerCrop().fit().into(binding.gambarMakanan);
                    inputStream = is;
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
            }
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case PERMISSION_REQUEST_STORAGE: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    openGallery();
                }

                return;
            }
        }
    }

    private void uploadImage(byte[] imagebyte,String nama, String jenis, String jam, String hari, String cp){
        RequestBody requestFile = RequestBody.create(MediaType.parse("image/jpeg"), imagebyte);
        MultipartBody.Part body = MultipartBody.Part.createFormData("foto", ""+System.currentTimeMillis(), requestFile);
        ApiInterface insertbarang = ServiceGenerator.createService(ApiInterface.class);
        Call<ResponsePost> call = insertbarang.tambahdokter(body,nama,jenis,jam,hari,cp);
        call.enqueue(new Callback<ResponsePost>() {
            @Override
            public void onResponse(Call<ResponsePost> call, Response<ResponsePost> response) {
                if (response.isSuccessful()){
                    if (response.body().getData()==1){
                        progressDialog.dismiss();
                        Toast.makeText(TambahDokterActivity.this,"tambah dokter berhasil",Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(TambahDokterActivity.this, AdminActivity.class);
                        startActivity(intent);
                        finish();
                    }
                }else {
                    progressDialog.dismiss();
                    Snackbar.make(view,  "gagal upload", Snackbar.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<ResponsePost> call, Throwable t) {
                progressDialog.dismiss();
                Snackbar.make(view,  "gagal req server", Snackbar.LENGTH_LONG).show();
                Log.d("boan", "onFailure: "+t.getMessage());
            }
        });
    }

}