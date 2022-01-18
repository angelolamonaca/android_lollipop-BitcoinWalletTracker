package com.angelolamonaca.myfirstapplication.Activities;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.room.Room;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.angelolamonaca.myfirstapplication.Adapters.WalletAdapter;
import com.angelolamonaca.myfirstapplication.Data.Wallet;
import com.angelolamonaca.myfirstapplication.Data.WalletDao;
import com.angelolamonaca.myfirstapplication.Data.WalletDatabase;
import com.angelolamonaca.myfirstapplication.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.json.JSONObject;

import java.text.DecimalFormat;
import java.util.List;

public class MainActivity extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener {
    SwipeRefreshLayout mSwipeRefreshLayout;
    WalletAdapter walletAdapter;
    final Double[] btcPriceInUsd = new Double[1];

    List<Wallet> wallets;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

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

        for (int i = 0; i < wallets.size(); i++) {

            final String blockStreamApiCallString = "https://blockstream.info/api/address/" + wallets.get(i).getWalletAddress();

            int finalI = i;
            @SuppressLint("NotifyDataSetChanged") JsonObjectRequest getRequest = new JsonObjectRequest(Request.Method.GET, blockStreamApiCallString, null,
                    response -> {
                        try {
                            if (finalI == wallets.size() - 1)
                                mSwipeRefreshLayout.setRefreshing(false);
                            JSONObject data = response.getJSONObject("chain_stats");
                            Object balanceObject = data.get("funded_txo_sum");
                            String balanceSatoshi = "00000000" + balanceObject.toString();
                            Double balanceBtc = Double.parseDouble(balanceSatoshi.substring(0, balanceSatoshi.length() - 8) + "." + balanceSatoshi.substring(balanceSatoshi.length() - 8, balanceSatoshi.length() - 5));
                            wallets.get(finalI).setWalletBalanceBTC(balanceBtc.toString());

                            DecimalFormat df = new DecimalFormat("#");
                            df.setMaximumFractionDigits(2);
                            wallets.get(finalI).setWalletBalanceUSD(df.format(balanceBtc * btcPriceInUsd[0]));

                            wallets.get(finalI).setConfirmedTxCount((Integer) data.get("tx_count"));

                            walletAdapter.notifyDataSetChanged();

                            Log.d("Debug", "Wallet address " + wallets.get(finalI).getWalletAddress() + " balance is " + wallets.get(finalI).getWalletBalanceBTC() + " bitcoin");
                            Log.d("Debug", "Wallet address " + wallets.get(finalI).getWalletAddress() + " balance is " + wallets.get(finalI).getWalletBalanceUSD() + " usd");
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