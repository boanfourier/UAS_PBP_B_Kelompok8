package com.medicare.prohealthymedicare;

import android.Manifest;
import android.app.DatePickerDialog;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.graphics.pdf.PdfDocument;
import android.os.Bundle;

import androidx.core.app.NotificationCompat;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;

import android.os.Environment;
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
import com.medicare.prohealthymedicare.admin.TambahDokterActivity;
import com.medicare.prohealthymedicare.database.AppDatabase;
import com.medicare.prohealthymedicare.database.entity.DokterEntity;
import com.medicare.prohealthymedicare.databinding.FragmentRegistrasiBinding;
import com.medicare.prohealthymedicare.model.DataDokterModels;
import com.medicare.prohealthymedicare.model.JenisModel;
import com.medicare.prohealthymedicare.model.ResponseJenis;
import com.medicare.prohealthymedicare.model.ResponsePost;
import com.medicare.prohealthymedicare.network.ApiInterface;
import com.medicare.prohealthymedicare.network.ServiceGenerator;
import com.medicare.prohealthymedicare.session.Session;
import com.medicare.prohealthymedicare.ui.HomeFragment;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Random;

import static android.content.Context.NOTIFICATION_SERVICE;

import pub.devrel.easypermissions.EasyPermissions;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class RegistrasiFragment extends Fragment {
    private DatePickerDialog datePickerDialog;
    private SimpleDateFormat dateFormatter;
    private SimpleDateFormat hariformatter;
    String CHANNEL_ID = "10001";

    AppDatabase database;
    String jenis, namadokter;
    FragmentRegistrasiBinding binding;
    private String[] Item = {"Mata", "Telinga"};
    private String hari;
    private String jam;
    ProgressDialog progressDialog;
    FirebaseAuth auth;
    String userid;
    Date dateTime;
    DateFormat dateFormat;
    int pageWidth = 1200;

    Bitmap bitmap,scaleBitmap;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_registrasi, container, false);
        binding.getLifecycleOwner();
        auth = FirebaseAuth.getInstance();
        userid = auth.getCurrentUser().getUid();
        progressDialog = new ProgressDialog(getActivity());

        database = AppDatabase.getInstance(requireContext());
        dateFormatter = new SimpleDateFormat("dd-MM-yyyy", Locale.US);
        hariformatter = new SimpleDateFormat("EEEE", Locale.US);
        progressDialog.setTitle("Loading . .. ");
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();

        bitmap = BitmapFactory.decodeResource(getResources(),R.drawable.logo);
        scaleBitmap = Bitmap.createScaledBitmap(bitmap,200,200,false);
        EasyPermissions.requestPermissions(requireActivity(),"asw",100
                , Manifest.permission.WRITE_EXTERNAL_STORAGE);

        ApiInterface getjenis = ServiceGenerator.createService(ApiInterface.class);
        Call<ResponseJenis> call = getjenis.getjenis();
        call.enqueue(new Callback<ResponseJenis>() {
            @Override
            public void onResponse(Call<ResponseJenis> call, Response<ResponseJenis> response) {
                if (response.isSuccessful()) {
                    progressDialog.dismiss();
                    List<JenisModel> jenismodel = response.body().getJenisModels();

                    ArrayList<String> arrayList = new ArrayList<>();
                    for (JenisModel getjenis : jenismodel) {
                        arrayList.add(getjenis.getJenis());
                    }
                    ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(requireContext(), android.R.layout.simple_spinner_item, arrayList);

                    //Memasukan Adapter pada Spinner
                    binding.spnpraktik.setAdapter(arrayAdapter);
                    binding.spnpraktik.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                        @Override
                        public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                            jenis = adapterView.getItemAtPosition(i).toString();
                            namadokter = jenismodel.get(i).getNama();
                        }

                        @Override
                        public void onNothingSelected(AdapterView<?> adapterView) {
                        }
                    });

                }else {
                    progressDialog.dismiss();
                }
            }

            @Override
            public void onFailure(Call<ResponseJenis> call, Throwable t) {
                progressDialog.dismiss();
                Log.d("boan", "onFailure: "+t.getMessage());
            }
        });


        binding.edttanggalpraktik.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDateDialog();
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

        binding.btndaftar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                daftar(v);
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

    private void createpdf(String namadokter, String hari,String jenis, String jam,String keluhan){

        dateTime = new Date();
        PdfDocument pdfDocument = new PdfDocument();
        Paint paint = new Paint();
        Paint titlePaint = new Paint();

        PdfDocument.PageInfo pageInfo = new PdfDocument.PageInfo.Builder(1200,2010,1).create();
        PdfDocument.Page page = pdfDocument.startPage(pageInfo);

        Canvas canvas = page.getCanvas();
        canvas.drawBitmap(scaleBitmap,0,0,paint);

        paint.setColor(Color.WHITE);
        paint.setTextSize(30f);
        paint.setTextAlign(Paint.Align.RIGHT);
        canvas.drawText("Pesan Dokter",1160,40,paint);
        canvas.drawText("hari : "+hari,1160,80,paint);

        titlePaint.setTextAlign(Paint.Align.CENTER);
        titlePaint.setTypeface(Typeface.create(Typeface.DEFAULT,Typeface.BOLD));
        titlePaint.setColor(Color.WHITE);
        titlePaint.setTextSize(70);
        canvas.drawText("Pesanan anda", pageWidth / 2,500,titlePaint);

        paint.setTextAlign(Paint.Align.LEFT);
        paint.setColor(Color.BLACK);
        paint.setTextSize(16f);

        canvas.drawText("Nama Dokter : " +namadokter, 20 , 590, paint);
        canvas.drawText("Tanggal pesan : "+jam,20,640,paint);


        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(2);
        canvas.drawRect(20,780,pageWidth-20,860,paint);


        paint.setTextAlign(Paint.Align.LEFT);
        paint.setStyle(Paint.Style.FILL);
        canvas.drawText("namadokter", 40,830,paint);
        canvas.drawText("hari", 200,830,paint);
        canvas.drawText("Jenis", 700,830,paint);
        canvas.drawText("jam", 900,830,paint);
        canvas.drawText("keluhan", 1100,830,paint);

        canvas.drawLine(180,790,180,840,paint);
        canvas.drawLine(680,790,680,840,paint);
        canvas.drawLine(880,790,880,840,paint);
        canvas.drawLine(1030,790,1030,840,paint);


        canvas.drawText(namadokter,40,950,paint);
        canvas.drawText(jam,200,950,paint);
        canvas.drawText(jenis,700,950,paint);
        canvas.drawText(hari,900,950,paint);
        canvas.drawText(keluhan,1100,950,paint);


        float totalOne = 0, totalTwo = 0;

        pdfDocument.finishPage(page);
        File file = new File(Environment.getExternalStorageDirectory(), "/Dokter.pdf");
        try {
            pdfDocument.writeTo(new FileOutputStream(file));
        }catch (IOException e){
            e.printStackTrace();
        }

        pdfDocument.close();
        Toast.makeText(getActivity(),"Pdf sudah dibuat",Toast.LENGTH_SHORT).show();
    }

    private void daftar(View v) {
        progressDialog.setTitle("Daftar Dokter . . . ");
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();

        String keluhan = binding.edtkeluhan.getText().toString().trim();
        if (namadokter != null && hari != null && jenis != null && jam != null && !keluhan.isEmpty()) {
            ApiInterface pesandokter = ServiceGenerator.createService(ApiInterface.class);
            Call<ResponsePost> call = pesandokter.pesandokter(namadokter, hari, jam, jenis, userid, keluhan);
            call.enqueue(new Callback<ResponsePost>() {
                @Override
                public void onResponse(Call<ResponsePost> call, Response<ResponsePost> response) {
                    if (response.isSuccessful()) {
                        progressDialog.dismiss();
                        if (response.body().getData() == 1) {
                            createpdf(namadokter,hari,jenis,jam,keluhan);
                            notifikasi("Berhasil daftar Dokter" + jenis, "Healthy Medicare");
                            Snackbar.make(v, "Berhasil daftar", Snackbar.LENGTH_LONG).show();
                        } else {
                            Snackbar.make(v, "Sudah pernah daftar", Snackbar.LENGTH_LONG).show();
                        }
                    } else {
                        progressDialog.dismiss();
                        Snackbar.make(v, "Gagal dapat response", Snackbar.LENGTH_LONG).show();
                    }
                }

                @Override
                public void onFailure(Call<ResponsePost> call, Throwable t) {
                    progressDialog.dismiss();
                    Log.d("boan", "onFailure: " + t.getMessage());
                    Snackbar.make(v, "Gagal dapat response", Snackbar.LENGTH_LONG).show();
                }
            });

        } else {
            progressDialog.dismiss();
            Snackbar.make(v, "Lengkapi semua kolom", Snackbar.LENGTH_LONG).show();
        }

    }

    private void showDateDialog() {
        Calendar newCalendar = Calendar.getInstance();
        datePickerDialog = new DatePickerDialog(requireContext(), new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                Calendar newDate = Calendar.getInstance();
                newDate.set(year, monthOfYear, dayOfMonth);
                hari = hariformatter.format(newDate.getTime());
                jam = dateFormatter.format(newDate.getTime());
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