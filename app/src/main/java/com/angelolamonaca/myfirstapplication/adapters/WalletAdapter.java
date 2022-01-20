package com.angelolamonaca.myfirstapplication.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.angelolamonaca.myfirstapplication.R;
import com.angelolamonaca.myfirstapplication.activities.MainActivity;
import com.angelolamonaca.myfirstapplication.activities.WalletDetailsActivity;
import com.angelolamonaca.myfirstapplication.data.Wallet;
import com.google.gson.Gson;

import java.util.List;
import java.util.Locale;

public class WalletAdapter extends RecyclerView.Adapter<WalletAdapter.ViewHolder> {

    List<Wallet> wallets;
    Context context;

    public WalletAdapter(List<Wallet> wallets, MainActivity activity) {
        this.wallets = wallets;
        this.context = activity;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(R.layout.wallet_list, parent, false);
        return new ViewHolder(view);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        final Wallet wallet = wallets.get(position);
        holder.textWalletAddress.setText(wallet.getWalletAddress());

        if (wallet.getWalletBalanceBTC() != null)
            holder.textWalletBalanceBTC.setText(wallet.getWalletBalanceBTC() + context.getString(R.string.btc));

        SharedPreferences sharedPref = context.getSharedPreferences("currencyMode", Context.MODE_PRIVATE);
        String currency = sharedPref.getString(context.getString(R.string.saved_currency_key), "usd");

        if (wallet.getWalletBalanceFiat() != null)
            holder.textWalletBalanceFiat.setText(wallet.getWalletBalanceFiat() + " " + currency.toUpperCase(Locale.ROOT));
        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, WalletDetailsActivity.class);
            Gson gson = new Gson();
            String walletInString = gson.toJson(wallet);
            intent.putExtra("Wallet", walletInString);
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return wallets.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView textWalletAddress;
        TextView textWalletBalanceBTC;
        TextView textWalletBalanceFiat;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            textWalletAddress = itemView.findViewById(R.id.textWalletAddress);
            textWalletBalanceBTC = itemView.findViewById(R.id.textWalletBalanceBTC);
            textWalletBalanceFiat = itemView.findViewById(R.id.textWalletBalanceFiat);
        }
    }
}
