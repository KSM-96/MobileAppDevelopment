package com.example.smartpantrymanager;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.card.MaterialCardView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

public class PantryAdapter extends RecyclerView.Adapter<PantryAdapter.VH> {

    public static final String PREFS_NAME = "SmartPantrySettings";
    public static final String KEY_EXPIRY_ALERTS = "expiry_alerts";

    public interface OnItemClickListener {
        void onItemClick(PantryItem item);
    }

    private final List<PantryItem> items;
    private final OnItemClickListener listener;
    private final SimpleDateFormat dateFormat =
            new SimpleDateFormat("yyyy-MM-dd", Locale.US);

    public PantryAdapter(List<PantryItem> items, OnItemClickListener listener) {
        this.items = items;
        this.listener = listener;
        dateFormat.setLenient(false);
    }

    @NonNull
    @Override
    public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_pantry, parent, false);
        return new VH(view);
    }

    @Override
    public void onBindViewHolder(@NonNull VH holder, int position) {
        PantryItem item = items.get(position);

        holder.name.setText(item.name);

        String quantityText = formatQuantity(item.quantity);
        if (item.unit != null && !item.unit.trim().isEmpty()) {
            quantityText += " " + item.unit;
        }
        holder.qty.setText(quantityText);

        resetCard(holder);

        SharedPreferences preferences = holder.itemView.getContext()
                .getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        boolean alertsEnabled = preferences.getBoolean(KEY_EXPIRY_ALERTS, true);

        if (item.expiryDate == null || item.expiryDate.trim().isEmpty()) {
            holder.expiry.setVisibility(View.GONE);
        } else {
            holder.expiry.setVisibility(View.VISIBLE);
            holder.expiry.setText("Expires: " + item.expiryDate);
            if (alertsEnabled) {
                applyExpiryHighlight(holder, item);
            }
        }

        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onItemClick(item);
            }
        });
    }

    private void applyExpiryHighlight(VH holder, PantryItem item) {
        Date expiry;
        try {
            expiry = dateFormat.parse(item.expiryDate);
        } catch (ParseException e) {
            return;
        }
        if (expiry == null) {
            return;
        }

        Calendar today = Calendar.getInstance();
        clearTime(today);

        Calendar expiryDay = Calendar.getInstance();
        expiryDay.setTime(expiry);
        clearTime(expiryDay);

        long days = TimeUnit.MILLISECONDS.toDays(
                expiryDay.getTimeInMillis() - today.getTimeInMillis());

        if (days < 0) {
            // EXPIRED: red
            holder.card.setCardBackgroundColor(Color.parseColor("#FFEBEE"));
            holder.card.setStrokeColor(Color.parseColor("#E53935"));
            holder.card.setStrokeWidth(dp(holder, 2));
            holder.expiry.setTextColor(Color.parseColor("#B71C1C"));
            holder.warning.setText("⚠ EXPIRED");
            holder.warning.setTextColor(Color.parseColor("#B71C1C"));
            holder.warning.setVisibility(View.VISIBLE);
        } else if (days <= 30) {
            // EXPIRING SOON: orange
            holder.card.setCardBackgroundColor(Color.parseColor("#FFF3E0"));
            holder.card.setStrokeColor(Color.parseColor("#FB8C00"));
            holder.card.setStrokeWidth(dp(holder, 2));
            holder.expiry.setTextColor(Color.parseColor("#B75F00"));
            holder.warning.setText("⚠ EXPIRING SOON (" + days
                    + (days == 1 ? " day)" : " days)"));
            holder.warning.setTextColor(Color.parseColor("#B75F00"));
            holder.warning.setVisibility(View.VISIBLE);
        }
    }

    private void clearTime(Calendar calendar) {
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
    }

    private void resetCard(VH holder) {
        holder.card.setCardBackgroundColor(Color.WHITE);
        holder.card.setStrokeColor(Color.parseColor("#E0E0E0"));
        holder.card.setStrokeWidth(dp(holder, 1));
        holder.expiry.setTextColor(Color.parseColor("#64748B"));
        holder.warning.setVisibility(View.GONE);
    }

    private int dp(VH holder, int value) {
        return (int) (value * holder.itemView.getResources()
                .getDisplayMetrics().density + 0.5f);
    }

    /** 2.0 -> "2", 2.5 -> "2.5" */
    static String formatQuantity(double quantity) {
        if (quantity == Math.floor(quantity)) {
            return String.format(Locale.getDefault(), "%.0f", quantity);
        }
        return String.valueOf(quantity);
    }

    @Override
    public int getItemCount() {
        return items == null ? 0 : items.size();
    }

    static class VH extends RecyclerView.ViewHolder {
        final MaterialCardView card;
        final TextView name;
        final TextView qty;
        final TextView expiry;
        final TextView warning;

        VH(@NonNull View view) {
            super(view);
            card = view.findViewById(R.id.pantryCard);
            name = view.findViewById(R.id.tvPantryName);
            qty = view.findViewById(R.id.tvPantryQuantity);
            expiry = view.findViewById(R.id.tvPantryExpiry);
            warning = view.findViewById(R.id.tvPantryWarning);
        }
    }
}
