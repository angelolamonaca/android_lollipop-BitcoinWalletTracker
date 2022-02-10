package com.angelolamonaca.bitcoinWalletTracker.activities;

import android.annotation.SuppressLint;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.angelolamonaca.bitcoinWalletTracker.R;
import com.angelolamonaca.bitcoinWalletTracker.data.Wallet;
import com.google.android.material.snackbar.Snackbar;
import com.google.gson.Gson;

import java.util.Locale;

public class WalletDetailsActivity extends AppCompatActivity {

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.wallet_details);

        Toolbar myToolbar = findViewById(R.id.topAppBar);
        setSupportActionBar(myToolbar);

        SharedPreferences sharedPref = getSharedPreferences("currencyMode", Context.MODE_PRIVATE);
        String currency = sharedPref.getString(getString(R.string.saved_currency_key), getString(R.string.usd));

        Gson gson = new Gson();
        Wallet wallet = gson.fromJson(getIntent().getSerializableExtra(getString(R.string.wallet)).toString(), Wallet.class);
        TextView detailsAddress = findViewById(R.id.details_address);
        detailsAddress.setText(wallet.getWalletAddress());
        detailsAddress.setOnClickListener(view -> {
            ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
            ClipData clip = ClipData.newPlainText(getString(R.string.wallet_address), wallet.getWalletAddress());
            clipboard.setPrimaryClip(clip);
            Snackbar.make(view, getString(R.string.address_copied_on_clipboard), Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show();
        });

        TextView detailsBtcBalance = findViewById(R.id.details_btc_balance);
        detailsBtcBalance.setText("BTC Balance\n" + wallet.getWalletBalanceBTC());
        TextView detailsUsdBalance = findViewById(R.id.details_fiat_balance);
        detailsUsdBalance.setText(currency.toUpperCase(Locale.ROOT) + " " + getString(R.string.balance) + "\n" + wallet.getWalletBalanceFiat());
        TextView detailsTxCount = findViewById(R.id.details_confirmed_tx_count);
        detailsTxCount.setText("Confirmed Tx Count\n" + wallet.getConfirmedTxCount());
        Button seeOnBlockstreamButton = findViewById(R.id.see_on_blockstream);
        seeOnBlockstreamButton.setOnClickListener(view -> {
            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://blockstream.info/address/" + wallet.getWalletAddress()));
            startActivity(browserIntent);
        });

    }

}
