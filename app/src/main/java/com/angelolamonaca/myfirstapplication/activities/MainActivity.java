package com.angelolamonaca.myfirstapplication.activities;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.angelolamonaca.myfirstapplication.R;
import com.angelolamonaca.myfirstapplication.adapters.WalletAdapter;
import com.angelolamonaca.myfirstapplication.data.Wallet;
import com.angelolamonaca.myfirstapplication.data.WalletDao;
import com.angelolamonaca.myfirstapplication.data.WalletDatabase;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.switchmaterial.SwitchMaterial;

import org.json.JSONObject;

import java.text.DecimalFormat;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener {
    SwipeRefreshLayout mSwipeRefreshLayout;
    WalletAdapter walletAdapter;
    final Double[] btcPrice = new Double[1];
    SwitchMaterial switchMaterial;
    List<Wallet> wallets;

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        MenuItem item = menu.findItem(R.id.app_bar_switch);
        SharedPreferences sharedPref = getSharedPreferences("currencyMode", Context.MODE_PRIVATE);
        String currency = sharedPref.getString(getString(R.string.saved_currency_key), getString(R.string.usd));

        switchMaterial = item.getActionView().findViewById(R.id.actionBarCurrencySwitch);
        switchMaterial.setChecked(currency.equals(getString(R.string.eur)));
        switchMaterial.setText(currency.toUpperCase(Locale.ROOT));

        switchMaterial.setOnCheckedChangeListener((buttonView, isChecked) -> {
            SharedPreferences.Editor editor = sharedPref.edit();
            String newCurrency = isChecked ? getString(R.string.eur) : getString(R.string.usd);
            if (isChecked)
                editor.putString(getString(R.string.saved_currency_key), newCurrency);
            else
                editor.putString(getString(R.string.saved_currency_key), newCurrency);
            editor.apply();
            switchMaterial.setText(newCurrency.toUpperCase(Locale.ROOT));
            mSwipeRefreshLayout.setRefreshing(true);
            loadRecyclerViewData();
        });
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar myToolbar = findViewById(R.id.topAppBar);
        myToolbar.setNavigationOnClickListener(a -> {
            Intent intent = new Intent(this, MainActivity.class);
            this.startActivity(intent);
        });
        setSupportActionBar(myToolbar);

        WalletDatabase walletDatabase = WalletDatabase.getInstance(getApplicationContext());
        WalletDao walletDao = walletDatabase.walletDao();
        wallets = walletDao.getAll();

        RecyclerView recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        walletAdapter = new WalletAdapter(wallets, MainActivity.this);
        recyclerView.setAdapter(walletAdapter);

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(view -> {
            Intent intent = new Intent(this, AddWalletActivity.class);
            this.startActivity(intent);
        });

        mSwipeRefreshLayout = findViewById(R.id.pullToRefresh);
        mSwipeRefreshLayout.setOnRefreshListener(this);
        mSwipeRefreshLayout.setColorSchemeResources(
                R.color.orange_500,
                android.R.color.holo_green_dark,
                android.R.color.holo_orange_dark,
                android.R.color.holo_blue_dark);

        mSwipeRefreshLayout.post(() -> {
            mSwipeRefreshLayout.setRefreshing(true);
            loadRecyclerViewData();
        });
    }

    @Override
    public void onRefresh() {
        loadRecyclerViewData();
    }

    private void loadRecyclerViewData() {
        mSwipeRefreshLayout.setRefreshing(true);
        RequestQueue queue = Volley.newRequestQueue(this);
        fetchBitcoinPrice(queue);
    }

    @SuppressLint("SetTextI18n")
    private void fetchBitcoinPrice(RequestQueue queue) {

        SharedPreferences sharedPref = getSharedPreferences("currencyMode", Context.MODE_PRIVATE);
        String currency = sharedPref.getString(getString(R.string.saved_currency_key), getString(R.string.usd));

        final String coinMarketCapApiCallString = "https://pro-api.coinmarketcap.com/v1/cryptocurrency/quotes/latest?" +
                "CMC_PRO_API_KEY=feb0a658-b7bc-421f-89bd-b27209e89b77&" +
                "id=1&convert=" + currency + "&" +
                "skip_invalid=true";
        JsonObjectRequest cmcGetRequest = new JsonObjectRequest(Request.Method.GET, coinMarketCapApiCallString, null,
                response -> {
                    try {
                        JSONObject usdQuote = response.getJSONObject("data")
                                .getJSONObject("1")
                                .getJSONObject("quote")
                                .getJSONObject(currency.toUpperCase(Locale.ROOT));
                        btcPrice[0] = (Double) usdQuote.get("price");
                        fetchBitcoinBalances(queue);
                        Log.d("BTC price in " + currency, btcPrice[0].toString());
                    } catch (Exception e) {
                        Log.d("Error", e.getMessage());
                    }
                },
                error -> Log.d("Error.Response", error.toString())
        );
        queue.add(cmcGetRequest);
    }

    private void fetchBitcoinBalances(RequestQueue queue) {

        for (int i = 0; i < wallets.size(); i++) {

            final String blockStreamApiCallString = "https://blockstream.info/api/address/" +
                    wallets.get(i).getWalletAddress();

            int finalI = i;
            @SuppressLint("NotifyDataSetChanged")
            JsonObjectRequest getRequest = new JsonObjectRequest(Request.Method.GET,
                    blockStreamApiCallString,
                    null,
                    response -> {
                        try {
                            if (finalI == wallets.size() - 1)
                                mSwipeRefreshLayout.setRefreshing(false);
                            JSONObject data = response.getJSONObject("chain_stats");
                            Object balanceObject = data.get("funded_txo_sum");
                            String balanceSatoshi = "00000000" + balanceObject.toString();
                            Double balanceBtc = Double.parseDouble(
                                    balanceSatoshi
                                            .substring(0, balanceSatoshi.length() - 8) +
                                            "." +
                                            balanceSatoshi
                                                    .substring(balanceSatoshi.length() - 8, balanceSatoshi.length() - 5)
                            );
                            wallets.get(finalI).setWalletBalanceBTC(balanceBtc.toString());

                            DecimalFormat df = new DecimalFormat("#");
                            df.setMaximumFractionDigits(2);
                            wallets.get(finalI).setWalletBalanceFiat(df.format(balanceBtc * btcPrice[0]));

                            wallets.get(finalI).setConfirmedTxCount((Integer) data.get("tx_count"));

                            walletAdapter.notifyDataSetChanged();

                            Log.d("Debug", "Wallet address " +
                                    wallets.get(finalI).getWalletAddress() +
                                    " balance is " +
                                    wallets.get(finalI).getWalletBalanceBTC() +
                                    " bitcoin");

                            Log.d("Debug", "Wallet address " +
                                    wallets.get(finalI).getWalletAddress() +
                                    " balance is " +
                                    wallets.get(finalI).getWalletBalanceFiat() +
                                    " usd");

                        } catch (Exception e) {
                            Log.d("Error", e.getMessage());
                        }
                    },
                    error -> Log.d("Error.Response", error.toString())
            );
            queue.add(getRequest);
        }
    }
}