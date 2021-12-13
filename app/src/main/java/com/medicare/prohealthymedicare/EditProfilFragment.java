package com.medicare.prohealthymedicare;

import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.medicare.prohealthymedicare.database.AppDatabase;
import com.medicare.prohealthymedicare.database.entity.UserEntity;
import com.medicare.prohealthymedicare.databinding.FragmentEditProfilBinding;
import com.medicare.prohealthymedicare.model.ResponseBodyAuth;
import com.medicare.prohealthymedicare.model.ResponsePost;
import com.medicare.prohealthymedicare.network.ApiInterface;
import com.medicare.prohealthymedicare.network.ServiceGenerator;
import com.medicare.prohealthymedicare.session.Session;
import com.medicare.prohealthymedicare.ui.ProfileFragment;
import com.medicare.prohealthymedicare.utils.Constant;
import com.squareup.picasso.Picasso;

import java.io.File;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class EditProfilFragment extends Fragment {

    FragmentEditProfilBinding binding;
    private AppDatabase database;
    String uid;
    FirebaseUser userAuth;
    ProgressDialog progressDialog;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_edit_profil, container, false);
        binding.getLifecycleOwner();
        progressDialog = new ProgressDialog(getActivity());
        uid = FirebaseAuth.getInstance().getUid();
        userAuth = FirebaseAuth.getInstance().getCurrentUser();

        String foto = Session.getIsFoto(requireContext());


        File imgFile = new File(foto);
        if (imgFile.exists()) {
            Bitmap myBitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
            binding.profileImage.setImageBitmap(myBitmap);

        }

        binding.btnback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getActivity().getSupportFragmentManager().beginTransaction()
                        .replace(R.id.nav_host_fragment_activity_customer, new ProfileFragment())
                        .commit();

            }
        });


        binding.btnsimpan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressDialog.setTitle("Update Data . . ..");
                progressDialog.setCanceledOnTouchOutside(false);
                progressDialog.show();

                String firstname = binding.edtfirstname.getText().toString().trim();
                String lastname = binding.edtlastname.getText().toString().trim();
                String password = binding.edtpassword.getText().toString().trim();
                String nohp = binding.edtnohp.getText().toString().trim();

                //apakah input tidak kosong ?
                if (!firstname.isEmpty() && !lastname.isEmpty() && !password.isEmpty()) {
                    //jika input tidak kosong maka akan mengecek lagi
                    // apakah password pada edittext sama dengan password di database ?
                    if (Session.getIsPassword(requireActivity().getBaseContext()).equals(password)) {
                        //jika iya maka akan update pada database mysql
                        updateakun(firstname, lastname, password,nohp);
                    } else {
                        //jika password berbeda maka akan merubah password di firebase terlebih dahulu
                        //sedang cek apakah email dan password sudah sama apa belum
                        AuthCredential credential = EmailAuthProvider
                                .getCredential(userAuth.getEmail(), Session.getIsPassword(requireActivity().getBaseContext()));
                        userAuth.reauthenticate(credential).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    //jika sudah berhasil masuk autentifikasi
                                    // lakukan update password
                                    userAuth.updatePassword(password).addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()) {
                                                //jika password firebase berhasil dirubah maka akan update data users di database
                                                progressDialog.dismiss();
                                                updateakun(firstname, lastname, password,nohp);
                                            } else {
                                                progressDialog.dismiss();
                                                Toast.makeText(requireContext().getApplicationContext(), "Error", Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    });
                                } else {
                                    progressDialog.dismiss();
                                    Toast.makeText(requireContext().getApplicationContext(), "Error", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                    }

                }else {
                    progressDialog.dismiss();
                    Toast.makeText(requireContext().getApplicationContext(), "Jangan kosongi kolom", Toast.LENGTH_SHORT).show();
                }

            }
        });

        //akan kembali ke fragment profil
        binding.btncancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().getSupportFragmentManager().beginTransaction()
                        .replace(R.id.nav_host_fragment_activity_customer, new ProfileFragment())
                        .commit();

            }
        });
        return binding.getRoot();
    }

    private void updateakun(String firstname, String lastname, String password, String nohp) {
        //update data akun mysql
        ApiInterface updateakun = ServiceGenerator.createService(ApiInterface.class);
        Call<ResponsePost> call = updateakun.updateauth(uid, firstname, lastname, password,nohp);
        call.enqueue(new Callback<ResponsePost>() {
            @Override
            public void onResponse(Call<ResponsePost> call, Response<ResponsePost> response) {
                if (response.body().getData() == 1) {
                    //jika dapat response 1 maka update berhasil
                    Session.setIsFirstname(requireActivity().getBaseContext(), firstname);
                    Session.setIsLastName(requireActivity().getBaseContext(), lastname);
                    Session.setIsPassword(requireActivity().getBaseContext(), password);
                    progressDialog.dismiss();
                    Toast.makeText(requireContext().getApplicationContext(), "Update berhasil", Toast.LENGTH_SHORT).show();
                } else {
                    //jika dapat response selain 1 maka tidak ada data yang terupdate
                    progressDialog.dismiss();
                    Toast.makeText(requireContext().getApplicationContext(), "Tidak ada yang di update", Toast.LENGTH_SHORT).show();
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
        //=========end data akun mysql
    }

    private void getakundetail() {
        //menampilkan data
        String firstname = Session.getIsFirstname(requireActivity().getBaseContext());
        String lastname = Session.getIsLastName(requireActivity().getBaseContext());
        String password = Session.getIsPassword(requireActivity().getBaseContext());
        String nohp = Session.getIsNotelp(requireActivity().getBaseContext());
        binding.edtfirstname.setText(firstname);
        binding.edtlastname.setText(lastname);
        binding.edtpassword.setText(password);
        binding.edtnohp.setText(nohp);
        Picasso.get().load(Session.getIsFoto(requireActivity().getBaseContext())).centerCrop().fit().into(binding.profileImage);

    }

    @Override
    public void onStart() {
        super.onStart();
        getakundetail();

    }
}