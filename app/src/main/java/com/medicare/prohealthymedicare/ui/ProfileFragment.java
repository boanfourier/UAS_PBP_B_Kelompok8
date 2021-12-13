package com.medicare.prohealthymedicare.ui;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;

import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.medicare.prohealthymedicare.EditProfilFragment;
import com.medicare.prohealthymedicare.R;
import com.medicare.prohealthymedicare.auth.LoginActivity;
import com.medicare.prohealthymedicare.database.AppDatabase;
import com.medicare.prohealthymedicare.databinding.FragmentProfileBinding;
import com.medicare.prohealthymedicare.model.ResponseBodyAuth;
import com.medicare.prohealthymedicare.model.ResponsePost;
import com.medicare.prohealthymedicare.network.ApiInterface;
import com.medicare.prohealthymedicare.network.ServiceGenerator;
import com.medicare.prohealthymedicare.session.Session;
import com.medicare.prohealthymedicare.utils.Constant;
import com.squareup.picasso.Picasso;

import org.jetbrains.annotations.NotNull;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

import static android.app.Activity.RESULT_OK;
import static android.os.Environment.getExternalStoragePublicDirectory;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import pub.devrel.easypermissions.EasyPermissions;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProfileFragment extends Fragment {

    private static final int IMAGE_CODE_CAPTURE = 10;
    private static final int PERMISSION_CAMERA = 1;
    Uri imageuri;
    private AppDatabase database;
    FragmentProfileBinding binding;

    ProgressDialog progressDialog;
    String uid;
    FirebaseUser userAuth;
    FirebaseAuth auth;
    String Userid;

    //foto
    private static final int PICK_IMAGE = 1;
    private static final int PERMISSION_REQUEST_STORAGE = 2;
    private static final String TYPE_1 = "multipart";
    private Uri uri;
    InputStream inputStream;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_profile, container, false);
        binding.getLifecycleOwner();
        //deklarasi autentifikasi firebase
        auth = FirebaseAuth.getInstance();
        Userid = auth.getCurrentUser().getUid();
        userAuth = FirebaseAuth.getInstance().getCurrentUser();
        uid = FirebaseAuth.getInstance().getUid();

        //tampilkan loading
        progressDialog = new ProgressDialog(getActivity());
        progressDialog.setTitle("Loading . . .");
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();

        //memanggil fungsi getuser
        getuser();

        //tombol logout ditekan
        binding.btnlogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //keluar dari akun firebase dan pindah ke login activity
                FirebaseAuth.getInstance().signOut();
                Session.setIsLogin(getContext(), "");
                Intent intent = new Intent(getContext().getApplicationContext(), LoginActivity.class);
                startActivity(intent);
                getActivity().finish();
            }
        });

        //ketika foto ditekan maka akan muncul tampilan pilih kamera dan gallery
        binding.profileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showBottomSheetDialog();
            }
        });

        //ketika di klik pindah ke  Editprofil fragment
        binding.edtprofil.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().getSupportFragmentManager().beginTransaction()
                        .replace(R.id.nav_host_fragment_activity_customer, new EditProfilFragment())
                        .commit();
            }
        });

        //ketika buton edit foto diklik maka akan upload foto
        binding.edtfoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                progressDialog.setTitle("Loading . . .");
                progressDialog.setCanceledOnTouchOutside(false);
                progressDialog.show();
                if (inputStream!=null || photo!=null){
                    try {
                        uploadImage();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }else {
                    progressDialog.dismiss();
                    Toast.makeText(requireContext().getApplicationContext(), "Pilih Foto terlebih dahulu", Toast.LENGTH_SHORT).show();
                }

            }
        });


        return binding.getRoot();

    }

    private void showBottomSheetDialog() {
        final BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(requireContext());
        bottomSheetDialog.setContentView(R.layout.bottom_sheet);

        LinearLayout camera = bottomSheetDialog.findViewById(R.id.btncamera);
        LinearLayout gallery = bottomSheetDialog.findViewById(R.id.btngalery);
        camera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openCamera();
            }
        });

        gallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (EasyPermissions.hasPermissions(requireActivity(), Manifest.permission.READ_EXTERNAL_STORAGE)) {
                    opengallery();

                } else {
                    EasyPermissions.requestPermissions(requireActivity(), "This application need your permission to access photo gallery", PERMISSION_REQUEST_STORAGE
                            , Manifest.permission.READ_EXTERNAL_STORAGE);
                }
            }
        });


        bottomSheetDialog.show();
    }


    private void openCamera() {

        if (EasyPermissions.hasPermissions(requireActivity(), Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE)) {
            bukakamera();

        } else {
            EasyPermissions.requestPermissions(requireActivity(), "This application need your permission to access photo gallery", PERMISSION_REQUEST_STORAGE
                    , Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        }

    }

    private void bukakamera() {
        Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
        cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, imageuri);
        startActivityForResult(cameraIntent, IMAGE_CODE_CAPTURE);
    }


    private void opengallery() {
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

    private void uploadImage() throws IOException {
        MultipartBody.Part body = null;
        if (inputStream != null) {
            byte[] byt = getBytes(inputStream);
            RequestBody requestFile = RequestBody.create(MediaType.parse("image/jpeg"), byt);
            body = MultipartBody.Part.createFormData("foto", "" + System.currentTimeMillis(), requestFile);

        } else if (photo != null) {
            File file = createTempFile(photo);
            RequestBody reqFile = RequestBody.create(MediaType.parse("image/*"), file);
            body = MultipartBody.Part.createFormData("foto", file.getName(), reqFile);
        }

        if (body != null) {
            ApiInterface updatefoto = ServiceGenerator.createService(ApiInterface.class);
            Call<ResponsePost> call = updatefoto.updatefoto(Userid, body);
            call.enqueue(new Callback<ResponsePost>() {
                @Override
                public void onResponse(Call<ResponsePost> call, Response<ResponsePost> response) {
                    try {
                        if (response.body().getData() == 1) {
                            //jika dapat response 1 maka update berhasil
                            Session.setIsFoto(requireActivity().getBaseContext(), new Constant().url+response.body().getMessage());
                            progressDialog.dismiss();
                            Toast.makeText(requireContext().getApplicationContext(), "Update berhasil", Toast.LENGTH_SHORT).show();
                        } else {
                            //jika dapat response selain 1 maka tidak ada data yang terupdate
                            progressDialog.dismiss();
                            Toast.makeText(requireContext().getApplicationContext(), "Tidak ada yang di update", Toast.LENGTH_SHORT).show();
                        }
                    } catch (Exception e) {
                        Log.d("boan", "onResponse: " + e.getMessage());
                    }

                }

                @Override
                public void onFailure(Call<ResponsePost> call, Throwable t) {
                    //jika onfailure artinya tidak dapat konek ke backend
                    progressDialog.dismiss();
                    Log.d("boan", "onFailure: " + t.getMessage());
                    Toast.makeText(requireContext().getApplicationContext(), "Tidak dapat response", Toast.LENGTH_SHORT).show();
                }
            });
        }


    }

    Bitmap photo;

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable @org.jetbrains.annotations.Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE && resultCode == Activity.RESULT_OK) {
            if (data != null) {
                try {
                    photo = null;
                    uri = data.getData();
                    InputStream is = getActivity().getContentResolver().openInputStream(data.getData());
                    Picasso.get().load(uri).centerCrop().fit().into(binding.profileImage);
                    inputStream = is;
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
            }
        }

        if (requestCode == IMAGE_CODE_CAPTURE && resultCode == RESULT_OK) {
            inputStream = null;
            imageuri = data.getData();
            photo = (Bitmap) data.getExtras().get("data");
            Picasso.get().load(createTempFile(photo)).centerCrop().fit().into(binding.profileImage);
        }

    }

    private File createTempFile(Bitmap bitmap) {
        File file = new File(getActivity().getExternalFilesDir(Environment.DIRECTORY_PICTURES)
                , System.currentTimeMillis() + "_image.webp");
        ByteArrayOutputStream bos = new ByteArrayOutputStream();

        bitmap.compress(Bitmap.CompressFormat.PNG, 0, bos);
        byte[] bitmapdata = bos.toByteArray();
        //write the bytes in file

        try {
            FileOutputStream fos = new FileOutputStream(file);
            fos.write(bitmapdata);
            fos.flush();
            fos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return file;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull @NotNull String[] permissions, @NonNull @NotNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case PERMISSION_CAMERA: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(requireContext(), "permisi  diijinkan", Toast.LENGTH_SHORT).show();

                } else {
                    Toast.makeText(requireContext(), "permisi tidak diijinkan", Toast.LENGTH_SHORT).show();

                }
            }
        }
    }

    @Override
    public void onStart() {
        super.onStart();

        String foto = Session.getIsFoto(requireContext());

        File imgFile = new File(foto);
        if (imgFile.exists()) {
            Bitmap myBitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
            binding.profileImage.setImageBitmap(myBitmap);

        }
    }

    private void getuser() {
        ApiInterface getuser = ServiceGenerator.createService(ApiInterface.class);
        getuser.getuserdetail(uid).enqueue(new Callback<ResponseBodyAuth>() {
            @Override
            public void onResponse(Call<ResponseBodyAuth> call, Response<ResponseBodyAuth> response) {
                try {
                    if (response.isSuccessful()) {
                        progressDialog.dismiss();
                        Session.setIsFirstname(requireActivity().getBaseContext(), response.body().getUsersmodel().getFirstname());
                        Session.setIsLastName(requireActivity().getBaseContext(), response.body().getUsersmodel().getLastname());
                        Session.setIsPassword(requireActivity().getBaseContext(), response.body().getUsersmodel().getPassword());
                        Session.setIsNotelp(requireActivity().getBaseContext(), response.body().getUsersmodel().getNohp());
                        Session.setIsFoto(requireActivity().getBaseContext(), new Constant().url + response.body().getUsersmodel().getFoto());
                        binding.txtfirstname.setText(response.body().getUsersmodel().getFirstname());
                        binding.txtlastname.setText(response.body().getUsersmodel().getLastname());
                        binding.txtnotelp.setText(response.body().getUsersmodel().getNohp());
                        Picasso.get().load(new Constant().url + response.body().getUsersmodel().getFoto()).centerCrop().fit().into(binding.profileImage);
                    }
                } catch (Exception e) {
                    progressDialog.dismiss();
                    Log.d("boan", "onResponse: " + e.getMessage());
                }

            }

            @Override
            public void onFailure(Call<ResponseBodyAuth> call, Throwable t) {
                progressDialog.dismiss();
                Log.d("boan", "onFailure: " + t.getMessage());
            }
        });
    }
}