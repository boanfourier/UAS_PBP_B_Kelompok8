package com.medicare.prohealthymedicare;

import android.app.DatePickerDialog;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;

import androidx.core.app.NotificationCompat;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.Toast;

import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.medicare.prohealthymedicare.database.AppDatabase;
import com.medicare.prohealthymedicare.database.entity.DokterEntity;
import com.medicare.prohealthymedicare.databinding.FragmentEditJadwalBinding;
import com.medicare.prohealthymedicare.databinding.FragmentRegistrasiBinding;
import com.medicare.prohealthymedicare.model.JenisModel;
import com.medicare.prohealthymedicare.model.PesanDokterModel;
import com.medicare.prohealthymedicare.model.ResponsePesan;
import com.medicare.prohealthymedicare.model.ResponsePost;
import com.medicare.prohealthymedicare.network.ApiInterface;
import com.medicare.prohealthymedicare.network.ServiceGenerator;
import com.medicare.prohealthymedicare.session.Session;
import com.medicare.prohealthymedicare.ui.HomeFragment;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import static android.content.Context.NOTIFICATION_SERVICE;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class EditJadwalFragment extends Fragment {
    private DatePickerDialog datePickerDialog;
    private SimpleDateFormat dateFormatter;
    private SimpleDateFormat hariformatter;
    String CHANNEL_ID = "10001";
    AppDatabase database;
    String jenis, namadokter;
    FragmentEditJadwalBinding binding;
    private String hari;
    private String tanggal;

    FirebaseAuth auth;
    String userid;
    ProgressDialog progressDialog;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_edit_jadwal, container, false);
        binding.getLifecycleOwner();
        progressDialog = new ProgressDialog(getActivity());
        database = AppDatabase.getInstance(requireContext());
        dateFormatter = new SimpleDateFormat("dd-MM-yyyy", Locale.US);
        hariformatter = new SimpleDateFormat("EEEE", Locale.US);

        auth = FirebaseAuth.getInstance();
        userid = auth.getCurrentUser().getUid();

        ApiInterface getdokter = ServiceGenerator.createService(ApiInterface.class);
        getdokter.readdokter(userid).enqueue(new Callback<ResponsePesan>() {
            @Override
            public void onResponse(Call<ResponsePesan> call, Response<ResponsePesan> response) {
                if (response.isSuccessful()) {
                    List<PesanDokterModel> jadwalmodel = response.body().getPesanDokterModels();
                    List<PesanDokterModel> jenismodel = response.body().getPesanDokterModels();

                    ArrayList<String> arrayList = new ArrayList<>();
                    for (PesanDokterModel pesanDokterModel : jenismodel) {
                        arrayList.add(pesanDokterModel.getJenis());
                    }

                    ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(requireContext(), android.R.layout.simple_spinner_item, arrayList);
                    //Memasukan Adapter pada Spinner
                    binding.spnpraktik.setAdapter(arrayAdapter);
                    binding.spnpraktik.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                        @Override
                        public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                            jenis = adapterView.getItemAtPosition(i).toString();
                            namadokter = jenismodel.get(i).getNama();

                            binding.edtkeluhan.setText(jenismodel.get(i).getKeluhan());
                            binding.edttanggalpraktik.setText(jenismodel.get(i).getJam());
                        }

                        @Override
                        public void onNothingSelected(AdapterView<?> adapterView) {
                        }
                    });

                }
            }

            @Override
            public void onFailure(Call<ResponsePesan> call, Throwable t) {
                Toast.makeText(requireContext().getApplicationContext(), "gagal api", Toast.LENGTH_SHORT).show();
                Log.d("boan", "onFailure: " + t.getMessage());
            }
        });

        binding.btncancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().getSupportFragmentManager().beginTransaction()
                        .replace(R.id.nav_host_fragment_activity_customer, new HomeFragment())
                        .commit();
            }
        });

        binding.edttanggalpraktik.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDateDialog();
            }
        });


        binding.btndaftar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressDialog.setTitle("Loading . .. ");
                progressDialog.setCanceledOnTouchOutside(false);
                progressDialog.show();

                String edtkeluhan = binding.edtkeluhan.getText().toString().trim();
                Log.d("boan", "onClick: "+ hari + tanggal);
                ApiInterface apiInterface = ServiceGenerator.createService(ApiInterface.class);
                apiInterface.updatepesan(hari, tanggal, jenis, userid, edtkeluhan).enqueue(new Callback<ResponsePost>() {
                    @Override
                    public void onResponse(Call<ResponsePost> call, Response<ResponsePost> response) {
                        progressDialog.dismiss();
                        if (response.isSuccessful()) {
                            if (response.body().getData() == 1) {
                                notifikasi("Berhasil update jadwal dokter" + jenis, "Healthy Medicare");
                                Snackbar.make(v, "Berhasil di update", Snackbar.LENGTH_LONG).show();
                            } else {
                                Snackbar.make(v, "gagal di update", Snackbar.LENGTH_LONG).show();
                            }
                        } else {
                            Snackbar.make(v, "gagal dapat response", Snackbar.LENGTH_LONG).show();

                        }
                    }

                    @Override
                    public void onFailure(Call<ResponsePost> call, Throwable t) {
                        progressDialog.dismiss();
                        Log.d("boan", "onFailure: "+t.getMessage());
                        Snackbar.make(v, "gagal koneksi API", Snackbar.LENGTH_LONG).show();
                    }
                });

                database.dokterDao().update(binding.edttanggalpraktik.getText().toString(), edtkeluhan, Session.getIsUsername(requireContext()), jenis);
                notifikasi("Berhasil update jadwal dokter" + jenis, "Healthy Medicare");
                Snackbar.make(v, "Berhasil di update", Snackbar.LENGTH_LONG).show();

            }
        });

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

    private void showDateDialog() {

        Calendar newCalendar = Calendar.getInstance();


        datePickerDialog = new DatePickerDialog(requireContext(), new DatePickerDialog.OnDateSetListener() {

            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {

                Calendar newDate = Calendar.getInstance();
                newDate.set(year, monthOfYear, dayOfMonth);

                hari = hariformatter.format(newDate.getTime());
                tanggal = dateFormatter.format(newDate.getTime());
                binding.edttanggalpraktik.setText(dateFormatter.format(newDate.getTime()));
            }

        }, newCalendar.get(Calendar.YEAR), newCalendar.get(Calendar.MONTH), newCalendar.get(Calendar.DAY_OF_MONTH));


        datePickerDialog.show();
    }

    public void notifikasi(String pesan, String pengirim) {
        String notification_title = pengirim;
        String notification_message = pesan;

        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(requireActivity())
                        .setSmallIcon(R.mipmap.ic_launcher)
                        .setContentTitle(notification_title)
                        .setContentText(notification_message);
        Intent intent = new Intent(requireActivity(), MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(requireActivity(), 0, intent, 0);
        PendingIntent resultPendingIntent =
                PendingIntent.getActivity(
                        requireActivity(),
                        0,
                        intent,
                        PendingIntent.FLAG_UPDATE_CURRENT
                );

        mBuilder.setContentIntent(resultPendingIntent);
        int mNotificationId = (int) System.currentTimeMillis();
        NotificationManager mNotifyMgr =
                (NotificationManager) requireActivity().getSystemService(NOTIFICATION_SERVICE);
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel notificationChannel = new NotificationChannel(CHANNEL_ID, "NOTIFICATION_CHANNEL_NAME", importance);

            mBuilder.setChannelId(CHANNEL_ID);
            mNotifyMgr.createNotificationChannel(notificationChannel);
        }
        mNotifyMgr.notify(mNotificationId, mBuilder.build());
    }
}