package com.angelolamonaca.myfirstapplication.activities;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.database.sqlite.SQLiteConstraintException;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.angelolamonaca.myfirstapplication.R;
import com.angelolamonaca.myfirstapplication.data.Wallet;
import com.angelolamonaca.myfirstapplication.data.WalletDao;
import com.angelolamonaca.myfirstapplication.data.WalletDatabase;

import org.json.JSONException;
import org.json.JSONObject;

public class AddWalletActivity extends AppCompatActivity {

    @RequiresApi(api = Build.VERSION_CODES.N)
    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.wallet_add);

        Toolbar myToolbar = findViewById(R.id.topAppBar);
        setSupportActionBar(myToolbar);

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

                                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                                builder.setTitle(getString(R.string.add_a_wallet))
                                        .setMessage(getString(R.string.sure_to_add_address) + "\n" + newWalletAddress)
                                        .setPositiveButton(R.string.yes_add_it, (dialog, id) -> {
                                            Wallet newWallet = new Wallet(newWalletAddress);
                                            try {
                                                walletDao.insertAll(newWallet);
                                                Intent intent = new Intent(this, MainActivity.class);
                                                this.startActivity(intent);
                                            } catch (SQLiteConstraintException e) {
                                                Log.e("Error.SQLite", e.getMessage());
                                                TextView addWalletErrorTextView = findViewById(R.id.add_wallet_error);
                                                addWalletErrorTextView.setText(getString(R.string.this_address_is_already_on_your_list));
                                            }
                                        })
                                        .setNegativeButton(R.string.no_maybe_later, (dialog, id) -> {
                                            // User cancelled the dialog
                                        });

                                AlertDialog alertDialog = builder.create();
                                alertDialog.show();

                            }
                        } catch (JSONException e) {
                            Log.d("Error.JSON", e.getMessage());
                        }
                    },
                    error -> {
                        Log.d("Error.Response", error.toString());
                        TextView addWalletErrorTextView = findViewById(R.id.add_wallet_error);
                        addWalletErrorTextView.setText(getString(R.string.address_should_have_one_satoshi));
                    }
            );
            queue.add(getRequest);


        });

    }

}
