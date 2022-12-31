package com.example.mi_trip.ui.home.home;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.PopupMenu;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mi_trip.R;
import com.example.mi_trip.databinding.HomeBinding;
import com.example.mi_trip.models.TripModel;
import com.example.mi_trip.repo.remote.network.FirebaseDB;

import java.util.ArrayList;
import java.util.List;

public class HomeAdaptor extends RecyclerView.Adapter<HomeAdaptor.ViewHolder>{

    List<TripModel> list = new ArrayList<>();
    List<TripModel> canceldList = new ArrayList<>();
    Context cntxt;
    FirebaseDB mFirebaseDB;
    private HomeBinding binding;

    public HomeAdaptor(List<TripModel> tripDetails, FragmentActivity activity) {
        this.list = tripDetails;
        this.cntxt = activity;
    }

    @NonNull
    @Override
    public HomeAdaptor.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflate = LayoutInflater.from(parent.getContext());
        View v = inflate.inflate(R.layout.custom_card_row, parent, false);
        mFirebaseDB = FirebaseDB.getInstance();
        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(@NonNull HomeAdaptor.ViewHolder holder, @SuppressLint("RecyclerView") int position) {
        holder.start.setText(list.get(position).startloc);
        holder.name.setText(list.get(position).tripname);
        holder.date.setText(list.get(position).date);

        holder.time.setText(list.get(position).time);
        holder.end.setText(list.get(position).endloc);

        holder.startnowBtn.setOnClickListener(v -> {
            list.get(position).setStatus("Done!");
            mFirebaseDB.addTripToHistory(list.get(position));
            mFirebaseDB.removeFromUpcoming(list.get(position));
            Uri gmmIntentUri = Uri.parse("google.navigation:q=" + list.get(position).getEndloc());
            Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
            mapIntent.setPackage("com.google.android.apps.maps");
            cntxt.startActivity(mapIntent);
        });

        holder.popup.setOnClickListener(v -> {
            PopupMenu popupMenu = new PopupMenu(cntxt, holder.popup);
            popupMenu.inflate(R.menu.home_card_menu);
            popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                @Override
                public boolean onMenuItemClick(MenuItem item) {

                    if (item.getItemId() == R.id.addNote) {
                        PopupMenu pop = new PopupMenu(cntxt, v);
                        pop.inflate(R.menu.notes_menu);
                        for (String n : list.get(position).getNotes()) {

                            pop.getMenu().add(n);
                        }
                        pop.show();
                    }

                    if (item.getItemId() == R.id.edit) {

                    }

                    if (item.getItemId() == R.id.delete) {

                    }

                    if (item.getItemId() == R.id.cancel) {
                        list.get(position).setStatus("Canceled!");
                        mFirebaseDB.addTripToHistory(list.get(position));
                        mFirebaseDB.removeFromUpcoming(list.get(position));
                        notifyDataSetChanged();
                    }
                    return false;
                }
            });
            popupMenu.show();
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

//        CardView cardview;
        TextView start, end, date, time, name;
        Button popup, startnowBtn;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            startnowBtn = itemView.findViewById(R.id.startnow);
            start = itemView.findViewById(R.id.start_loc_id);
            end = itemView.findViewById(R.id.end_loc_id);
            time = itemView.findViewById(R.id.Time_id);
            name = itemView.findViewById(R.id.trip_name_id);
            date = itemView.findViewById(R.id.Date_id);
            popup = itemView.findViewById(R.id.pop_menu_id);
        }
    }
}
