package com.angelolamonaca.myfirstapplication.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.angelolamonaca.myfirstapplication.MainActivity;
import com.angelolamonaca.myfirstapplication.Models.Wallet;
import com.angelolamonaca.myfirstapplication.R;
import com.google.android.material.snackbar.Snackbar;

public class WalletAdapter extends RecyclerView.Adapter<WalletAdapter.ViewHolder> {

    Wallet[] wallets;
    Context context;

    public WalletAdapter(Wallet[] Wallet, MainActivity activity) {
        this.wallets = Wallet;
        this.context = activity;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(R.layout.wallet_list, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        final Wallet wallet = wallets[position];
        holder.textWalletAddress.setText(wallet.getWalletAddress());
        holder.textWalletBalanceBTC.setText(String.valueOf(wallet.getWalletBalanceBTC()));
        holder.textWalletBalanceEUR.setText(String.valueOf(wallet.getWalletBalanceEUR()));
        holder.textWalletBalanceCHF.setText(String.valueOf(wallet.getWalletBalanceCHF()));
        holder.textWalletBalanceUSD.setText(String.valueOf(wallet.getWalletBalanceUSD()));

        holder.itemView.setOnClickListener(v -> Snackbar.make(v, wallet.getWalletAddress(), Snackbar.LENGTH_LONG)
                .setAction("Action", null).show());
    }

    @Override
    public int getItemCount() {
        return wallets.length;
    }


    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView textWalletAddress;
        TextView textWalletBalanceBTC;
        TextView textWalletBalanceEUR;
        TextView textWalletBalanceCHF;
        TextView textWalletBalanceUSD;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            textWalletAddress = itemView.findViewById(R.id.textWalletAddress);
            textWalletBalanceBTC = itemView.findViewById(R.id.textWalletBalanceBTC);
            textWalletBalanceEUR = itemView.findViewById(R.id.textWalletBalanceEUR);
            textWalletBalanceCHF = itemView.findViewById(R.id.textWalletBalanceCHF);
            textWalletBalanceUSD = itemView.findViewById(R.id.textWalletBalanceUSD);

        }
    }

}
