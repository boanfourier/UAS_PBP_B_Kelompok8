package com.medicare.prohealthymedicare;

import android.app.Dialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.TextView;
import android.widget.Toast;

import com.medicare.prohealthymedicare.adapter.DataDokterAdapter;
import com.medicare.prohealthymedicare.admin.AdminActivity;
import com.medicare.prohealthymedicare.databinding.FragmentDataDokterBinding;
import com.medicare.prohealthymedicare.model.DataDokterModels;
import com.medicare.prohealthymedicare.model.ResponseBodyDokter;
import com.medicare.prohealthymedicare.network.ApiInterface;
import com.medicare.prohealthymedicare.network.ServiceGenerator;
import com.medicare.prohealthymedicare.ui.HomeFragment;
import com.medicare.prohealthymedicare.ui.ProfileFragment;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class DataDokterFragment extends Fragment {
    private FragmentDataDokterBinding binding;
    private DataDokterAdapter adapter;
    private List<DataDokterModels> dataDokterModelsList;
    private View view;
    String namadokter;
    private Dialog customDialog;
    private TextView txtnama,txthari,txtjam,txtcp;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater,R.layout.fragment_data_dokter,container,false);
        binding.shimmer.startShimmer();
        getdatadokter(view);

        binding.btnback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().getSupportFragmentManager().beginTransaction()
                        .replace(R.id.nav_host_fragment_activity_customer, new HomeFragment())
                        .commit();
            }
        });



        return binding.getRoot();
    }

    void getdatadokter(View view) {
        ApiInterface getdokter = ServiceGenerator.createService(ApiInterface.class);
        getdokter.getdokter().enqueue(new Callback<ResponseBodyDokter>() {
            @Override
            public void onResponse(Call<ResponseBodyDokter> call, Response<ResponseBodyDokter> response) {
                try {
                    if (response.isSuccessful()) {
                        binding.rvdokter.setVisibility(View.VISIBLE);
                        binding.shimmer.setVisibility(View.GONE);
                        binding.shimmer.stopShimmer();
                        List<DataDokterModels> dokterModels = response.body().getDataDokterModels();
                        createDataList(dokterModels);
                    }
                }catch (Exception e){
                    Log.d("boan", "onResponse: "+e.getMessage());
                    return;
                }

            }

            @Override
            public void onFailure(Call<ResponseBodyDokter> call, Throwable t) {
                Log.d("boan", "onFailure: " + t.getMessage());
            }
        });

    }

    private void createDataList(List<DataDokterModels> dokterModels) {
        adapter = new DataDokterAdapter(dokterModels);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(requireContext().getApplicationContext());


        adapter.setDialog(new DataDokterAdapter.Dialog() {
            @Override
            public void onClick(int position, String nama, String hari, String jam, String cp,Integer id,String foto,String jenis) {
                initCustomDialog(nama, hari, jam, cp);
                customDialog.show();
            }


        });

        binding.rvdokter.setLayoutManager(layoutManager);

        binding.rvdokter.setAdapter(adapter);

    }

    private void initCustomDialog(String namadokter,String hari, String jam , String cp){
        customDialog = new Dialog(requireContext());
        customDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);

        customDialog.setContentView(R.layout.showdatadokter);
        customDialog.setCancelable(true);

        txtnama = customDialog.findViewById(R.id.txtnama);
        txthari = customDialog.findViewById(R.id.txthari);
        txtjam = customDialog.findViewById(R.id.txtjam);
        txtcp = customDialog.findViewById(R.id.txtcp);
        txtnama.setText("Dokter : "+ namadokter);
        txthari.setText("Hari : "+ hari);
        txtjam.setText("Jam : "+ jam);
        txtcp.setText("Cp : "+ cp);
    }


}