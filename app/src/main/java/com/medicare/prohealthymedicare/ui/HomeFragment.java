package com.medicare.prohealthymedicare.ui;

import android.os.Bundle;

import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.medicare.prohealthymedicare.DataDokterFragment;
import com.medicare.prohealthymedicare.EditJadwalFragment;
import com.medicare.prohealthymedicare.KetentuanFragment;
import com.medicare.prohealthymedicare.R;
import com.medicare.prohealthymedicare.RegistrasiFragment;
import com.medicare.prohealthymedicare.databinding.FragmentHomeBinding;
import com.medicare.prohealthymedicare.session.Session;


public class HomeFragment extends Fragment {

    FragmentHomeBinding binding;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        binding = DataBindingUtil.inflate(inflater,R.layout.fragment_home,container,false);
        binding.getLifecycleOwner();

        binding.btndatadokter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().getSupportFragmentManager().beginTransaction()
                        .replace(R.id.nav_host_fragment_activity_customer, new DataDokterFragment())
                        .commit();
            }
        });

        binding.btnregistrasi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().getSupportFragmentManager().beginTransaction()
                        .replace(R.id.nav_host_fragment_activity_customer, new RegistrasiFragment())
                        .commit();
            }
        });

        binding.btninformasi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().getSupportFragmentManager().beginTransaction()
                        .replace(R.id.nav_host_fragment_activity_customer, new KetentuanFragment())
                        .commit();
            }
        });

        binding.btneditjadwal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().getSupportFragmentManager().beginTransaction()
                        .replace(R.id.nav_host_fragment_activity_customer, new EditJadwalFragment())
                        .commit();
            }
        });
        return binding.getRoot();

    }

    @Override
    public void onStart() {
        super.onStart();
        binding.txtuser.setText(Session.getIsUsername(requireContext()));
    }
}