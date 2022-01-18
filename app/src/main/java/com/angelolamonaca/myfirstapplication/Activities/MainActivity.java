package com.angelolamonaca.myfirstapplication.Activities;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.angelolamonaca.myfirstapplication.Adapters.WalletAdapter;
import com.angelolamonaca.myfirstapplication.Models.Wallet;
import com.angelolamonaca.myfirstapplication.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import org.json.JSONObject;

import java.text.DecimalFormat;

public class MainActivity extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener {
    SwipeRefreshLayout mSwipeRefreshLayout;
    WalletAdapter walletAdapter;
    final Double[] btcPriceInUsd = new Double[1];

    Wallet[] wallets = new Wallet[]{
            new Wallet("bc1qv6664w08cnvy9cfltw4pzzfttvwljgawtqfw42"),
            new Wallet("bc1q5skmx82jlfkg0p9mxm2yuka2uax2tpw56w5dca"),
            new Wallet("bc1q5s2cztswev7j9dd0rtcq92tq3cc3yxmw668hhx"),
            new Wallet("bc1q33f4wz785muluyxene9ff92zeed76klhuxtd5t")
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        RecyclerView recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        walletAdapter = new WalletAdapter(wallets, MainActivity.this);
        recyclerView.setAdapter(walletAdapter);

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(view -> Snackbar.make(view, "Here's a Snackbar", Snackbar.LENGTH_LONG)
                .setAction("Action", null).show());

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
        RequestQueue queue = Volley.newRequestQueue(this);
        fetchBitcoinPrice(queue);
    }

    private void fetchBitcoinPrice(RequestQueue queue) {
        mSwipeRefreshLayout.setRefreshing(true);

        final String coinMarketCapApiCallString = "https://pro-api.coinmarketcap.com/v1/cryptocurrency/quotes/latest?CMC_PRO_API_KEY=feb0a658-b7bc-421f-89bd-b27209e89b77&id=1&convert=usd&skip_invalid=true";
        JsonObjectRequest cmcGetRequest = new JsonObjectRequest(Request.Method.GET, coinMarketCapApiCallString, null,
                response -> {
                    try {
                        JSONObject usdQuote = response.getJSONObject("data").getJSONObject("1").getJSONObject("quote").getJSONObject("USD");
                        btcPriceInUsd[0] = (Double) usdQuote.get("price");
                        fetchBitcoinBalances(queue);
                        Log.d("BTC price in USD", btcPriceInUsd[0].toString());
                    } catch (Exception e) {
                        Log.d("Error", e.getMessage());
                    }
                },
                error -> Log.d("Error.Response", error.toString())
        );
        queue.add(cmcGetRequest);
    }

    private void fetchBitcoinBalances(RequestQueue queue) {

        for (int i = 0; i < wallets.length; i++) {

            final String blockStreamApiCallString = "https://blockstream.info/api/address/" + wallets[i].getWalletAddress();

            int finalI = i;
            @SuppressLint("NotifyDataSetChanged") JsonObjectRequest getRequest = new JsonObjectRequest(Request.Method.GET, blockStreamApiCallString, null,
                    response -> {
                        try {
                            if (finalI == wallets.length - 1)
                                mSwipeRefreshLayout.setRefreshing(false);
                            JSONObject data = response.getJSONObject("chain_stats");
                            Object balanceObject = data.get("funded_txo_sum");
                            String balanceSatoshi = "00000000" + balanceObject.toString();
                            Double balanceBtc = Double.parseDouble(balanceSatoshi.substring(0, balanceSatoshi.length() - 8) + "." + balanceSatoshi.substring(balanceSatoshi.length() - 8, balanceSatoshi.length() - 5));
                            wallets[finalI].setWalletBalanceBTC(balanceBtc.toString());

                            DecimalFormat df = new DecimalFormat("#");
                            df.setMaximumFractionDigits(2);
                            wallets[finalI].setWalletBalanceUSD(df.format(balanceBtc*btcPriceInUsd[0]));

                            wallets[finalI].setConfirmedTxCount((Integer) data.get("tx_count"));

                            walletAdapter.notifyDataSetChanged();

                            Log.d("Debug", "Wallet address " + wallets[finalI].getWalletAddress() + " balance is " + wallets[finalI].getWalletBalanceBTC() + " bitcoin");
                            Log.d("Debug", "Wallet address " + wallets[finalI].getWalletAddress() + " balance is " + wallets[finalI].getWalletBalanceUSD() + " usd");
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