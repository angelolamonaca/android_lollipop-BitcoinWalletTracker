package com.angelolamonaca.myfirstapplication.Adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Parcelable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.angelolamonaca.myfirstapplication.Activities.MainActivity;
import com.angelolamonaca.myfirstapplication.Activities.WalletDetailsActivity;
import com.angelolamonaca.myfirstapplication.Models.Wallet;
import com.angelolamonaca.myfirstapplication.R;
import com.google.android.material.snackbar.Snackbar;
import com.google.gson.Gson;

import org.json.JSONObject;

public class WalletAdapter extends RecyclerView.Adapter<WalletAdapter.ViewHolder> {

    Wallet[] wallets;
    Context context;

    public WalletAdapter(Wallet[] wallets, MainActivity activity) {
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
        final Wallet wallet = wallets[position];
        holder.textWalletAddress.setText(wallet.getWalletAddress());
        holder.textWalletBalanceBTC.setText(wallet.getWalletBalanceBTC() + " BTC");
        holder.textWalletBalanceUSD.setText(wallet.getWalletBalanceUSD() + " USD");
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
        return wallets.length;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView textWalletAddress;
        TextView textWalletBalanceBTC;
        TextView textWalletBalanceUSD;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            textWalletAddress = itemView.findViewById(R.id.textWalletAddress);
            textWalletBalanceBTC = itemView.findViewById(R.id.textWalletBalanceBTC);
            textWalletBalanceUSD = itemView.findViewById(R.id.textWalletBalanceUSD);
        }
    }
}
