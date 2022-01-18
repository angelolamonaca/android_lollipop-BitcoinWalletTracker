package com.angelolamonaca.myfirstapplication.Activities;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.angelolamonaca.myfirstapplication.Data.Wallet;
import com.angelolamonaca.myfirstapplication.Data.WalletDao;
import com.angelolamonaca.myfirstapplication.Data.WalletDatabase;
import com.angelolamonaca.myfirstapplication.R;

import org.json.JSONObject;

public class AddWalletActivity extends AppCompatActivity {


    @RequiresApi(api = Build.VERSION_CODES.N)
    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.wallet_add);

        Button addWalletButton = findViewById(R.id.add_wallet_button);
        addWalletButton.setOnClickListener(view -> {

            EditText addWallettAddressEditText = findViewById(R.id.add_wallet_address);
            String newWalletAddress = addWallettAddressEditText.getEditableText().toString();
            WalletDatabase walletDatabase = WalletDatabase.getInstance(getApplicationContext());
            WalletDao walletDao = walletDatabase.walletDao();
            final String blockStreamApiCallString = "https://blockstream.info/api/address/" + newWalletAddress;

            RequestQueue queue = Volley.newRequestQueue(this);
            JsonObjectRequest getRequest = new JsonObjectRequest(Request.Method.GET, blockStreamApiCallString, null,
                    response -> {
                        try {
                            JSONObject data = response.getJSONObject("chain_stats");
                            Object balanceObject = data.get("funded_txo_sum");
                            if (!balanceObject.toString().isEmpty()) {
                                Wallet newWallet = new Wallet(newWalletAddress);
                                walletDao.insertAll(newWallet);
                                Intent intent = new Intent(this, MainActivity.class);
                                this.startActivity(intent);
                            }
                        } catch (Exception e) {
                            Log.d("Error", e.getMessage());
                        }
                    },
                    error -> Log.d("Error.Response", error.toString())
            );
            queue.add(getRequest);


        });

    }
}
