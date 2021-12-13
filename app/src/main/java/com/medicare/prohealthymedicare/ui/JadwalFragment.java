package com.medicare.prohealthymedicare.ui;

import android.app.Dialog;
import android.os.Bundle;

import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.medicare.prohealthymedicare.R;
import com.medicare.prohealthymedicare.adapter.DataDokterAdapter;
import com.medicare.prohealthymedicare.adapter.JadwalAdapter;
import com.medicare.prohealthymedicare.database.AppDatabase;
import com.medicare.prohealthymedicare.database.entity.DokterEntity;
import com.medicare.prohealthymedicare.databinding.FragmentDataDokterBinding;
import com.medicare.prohealthymedicare.databinding.FragmentJadwalBinding;
import com.medicare.prohealthymedicare.model.DataDokterModels;
import com.medicare.prohealthymedicare.model.PesanDokterModel;
import com.medicare.prohealthymedicare.model.ResponseBodyDokter;
import com.medicare.prohealthymedicare.model.ResponsePesan;
import com.medicare.prohealthymedicare.network.ApiInterface;
import com.medicare.prohealthymedicare.network.ServiceGenerator;
import com.medicare.prohealthymedicare.session.Session;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class JadwalFragment extends Fragment {

    FragmentJadwalBinding binding;
    private AppDatabase database;
    private JadwalAdapter jadwalAdapter;
    private List<PesanDokterModel> dataDokterModelsList;
    private View view;
    FirebaseAuth auth;
    String userid;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        binding = DataBindingUtil.inflate(inflater,R.layout.fragment_jadwal,container,false);
        binding.getLifecycleOwner();
        binding.shimmer.startShimmer();

        auth = FirebaseAuth.getInstance();
        userid = auth.getCurrentUser().getUid();
        getjadwal(view);


        return  binding.getRoot();
    }

    private  void getjadwal(View view) {
        ApiInterface getdokter = ServiceGenerator.createService(ApiInterface.class);
        getdokter.readdokter(userid).enqueue(new Callback<ResponsePesan>() {
            @Override
            public void onResponse(Call<ResponsePesan> call, Response<ResponsePesan> response) {
                if (response.isSuccessful()) {
                    try {
                        binding.rvjadwal.setVisibility(View.VISIBLE);
                        binding.shimmer.setVisibility(View.GONE);
                        binding.shimmer.stopShimmer();
                        List<PesanDokterModel> dokterModels = response.body().getPesanDokterModels();
                        createDataList(dokterModels);

                    }catch (Exception e){
                        Log.d("boan", "onFailure: " + e.getMessage());
                        return;
                    }
                }
            }

            @Override
            public void onFailure(Call<ResponsePesan> call, Throwable t) {
                Log.d("boan", "onFailure: " + t.getMessage());
            }
        });

    }

    private void createDataList(List<PesanDokterModel> dokterModels) {
        jadwalAdapter = new JadwalAdapter(requireContext().getApplicationContext(),dokterModels);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(requireContext().getApplicationContext());

        binding.rvjadwal.setLayoutManager(layoutManager);

        binding.rvjadwal.setAdapter(jadwalAdapter);
    }


}