package com.angelolamonaca.myfirstapplication;

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
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import org.json.JSONObject;

public class MainActivity extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener {
    SwipeRefreshLayout mSwipeRefreshLayout;
    WalletAdapter walletAdapter;

    Wallet[] wallets = new Wallet[]{
            new Wallet("bc1q5jyvmeqpvet5tlz8cqkmme33p4lwsrzxgk7hh3", null, 300.0, 200.0, 100.0),
            new Wallet("bc1qv6664w08cnvy9cfltw4pzzfttvwljgawtqfw42", null, 300.0, 200.0, 100.0),
            new Wallet("bc1q5skmx82jlfkg0p9mxm2yuka2uax2tpw56w5dca", null, 300.0, 200.0, 100.0),
            new Wallet("bc1q5s2cztswev7j9dd0rtcq92tq3cc3yxmw668hhx", null, 300.0, 200.0, 100.0),
            new Wallet("bc1qh8wu20r0xj0sk74wknhts5cf54n7ya8alpxgs8", null, 300.0, 200.0, 100.0),
            new Wallet("bc1q33f4wz785muluyxene9ff92zeed76klhuxtd5t", null, 300.0, 200.0, 100.0),
            new Wallet("bc1qwzgs276v929el645huxf9uql797j66lvzypz7c", null, 300.0, 200.0, 100.0)
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

    @SuppressLint("NotifyDataSetChanged")
    private void loadRecyclerViewData() {
        mSwipeRefreshLayout.setRefreshing(true);

        for (int i = 0; i < wallets.length; i++) {
            RequestQueue queue = Volley.newRequestQueue(this);

            final String url = "https://blockstream.info/api/address/" + wallets[i].getWalletAddress();

            int finalI = i;
            JsonObjectRequest getRequest = new JsonObjectRequest(Request.Method.GET, url, null,
                    response -> {
                        try {
                            JSONObject data = response.getJSONObject("chain_stats");
                            Object balanceObject = data.get("funded_txo_sum");
                            String balanceSatoshi = "00000000" + balanceObject.toString();
                            Double balanceBtc = Double.parseDouble(balanceSatoshi.substring(0, balanceSatoshi.length() - 8) + "." + balanceSatoshi.substring(balanceSatoshi.length() - 8, balanceSatoshi.length() - 5));

                            wallets[finalI].setWalletBalanceBTC(balanceBtc);

                            walletAdapter.notifyDataSetChanged();
                            Log.d("Debug", "Wallet address " + wallets[finalI].getWalletAddress() + " have " + balanceBtc + " satoshis");
                        } catch (Exception e) {
                            Log.d("Error", e.getMessage());
                        }
                    },
                    error -> Log.d("Error.Response", error.toString())
            );

            queue.add(getRequest);
        }

        mSwipeRefreshLayout.setRefreshing(false);
    }
}