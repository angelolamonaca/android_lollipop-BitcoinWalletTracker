package com.angelolamonaca.myfirstapplication.Activities;

import android.annotation.SuppressLint;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.angelolamonaca.myfirstapplication.Data.Wallet;
import com.angelolamonaca.myfirstapplication.R;
import com.google.android.material.snackbar.Snackbar;
import com.google.gson.Gson;

public class WalletDetailsActivity extends AppCompatActivity {


    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.wallet_details);
        Gson gson = new Gson();
        Wallet wallet = gson.fromJson(getIntent().getSerializableExtra("Wallet").toString(), Wallet.class);
        TextView detailsAddress = findViewById(R.id.details_address);
        detailsAddress.setText(wallet.getWalletAddress());
        detailsAddress.setOnClickListener(view -> {
            ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
            ClipData clip = ClipData.newPlainText("wallet address", wallet.getWalletAddress());
            clipboard.setPrimaryClip(clip);
            Snackbar.make(view, "Address copied on clipboard", Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show();
        });

        TextView detailsBtcBalance = findViewById(R.id.details_btc_balance);
        detailsBtcBalance.setText("BTC Balance\n" + wallet.getWalletBalanceBTC());
        TextView detailsUsdBalance = findViewById(R.id.details_usd_balance);
        detailsUsdBalance.setText("USD Balance\n" + wallet.getWalletBalanceUSD());
        TextView detailsTxCount = findViewById(R.id.details_confirmed_tx_count);
        detailsTxCount.setText("Confirmed Tx Count\n" + wallet.getConfirmedTxCount());
        Button seeOnBlockstreamButton = findViewById(R.id.see_on_blockstream);
        seeOnBlockstreamButton.setOnClickListener(view -> {
            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://blockstream.info/address/"+wallet.getWalletAddress()));
            startActivity(browserIntent);
        });

    }
}
