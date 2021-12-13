package com.medicare.prohealthymedicare.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.medicare.prohealthymedicare.R;
import com.medicare.prohealthymedicare.database.entity.DokterEntity;
import com.medicare.prohealthymedicare.model.PesanDokterModel;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class JadwalAdapter extends RecyclerView.Adapter<JadwalAdapter.ViewAdapter> {
    private List<PesanDokterModel> list;
    private Context context;
    private Dialog dialog;
    private Dialog btnhapus;
    private ArrayList<PesanDokterModel> TransaksiList;

    public interface Dialog{
        void onHapus(int position);
    }


    public void setDialog(Dialog dialog) {
        this.dialog = dialog;
    }

    public JadwalAdapter(Context context, List<PesanDokterModel> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public ViewAdapter onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_jadwal, parent, false);
        return new ViewAdapter(view);
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull ViewAdapter holder, int position) {
        Integer antri = list.get(position).getAntri();
        String hari = list.get(position).getJam();
        String tanggal = list.get(position).getHari();
        String doktor = list.get(position).getNama();
        String jenis = list.get(position).getJenis();

        holder.txtantri.setText(antri.toString());
        holder.txthari.setText("Hari : " + tanggal);
        holder.txttanggal.setText("Tanggal : " + hari);
        holder.txtdoktor.setText("Dr Praktik : " + doktor);
        holder.txtjenis.setText("Jenis : " + jenis);


    }

    public void filterList(ArrayList<PesanDokterModel> filteredList) {
        TransaksiList = filteredList;
        notifyDataSetChanged();
    }


    @Override
    public int getItemCount() {
        return list.size();
    }

    class ViewAdapter extends RecyclerView.ViewHolder {

        TextView txtantri, txthari, txttanggal, txtjenis,txtdoktor;

        public ViewAdapter(@NonNull @NotNull View itemView) {
            super(itemView);
            txtantri = itemView.findViewById(R.id.txtantri);
            txthari = itemView.findViewById(R.id.txthari);
            txttanggal = itemView.findViewById(R.id.txttanggal);
            txtjenis = itemView.findViewById(R.id.txtjenis);
            txtdoktor = itemView.findViewById(R.id.txtdoktor);


        }
    }
}
