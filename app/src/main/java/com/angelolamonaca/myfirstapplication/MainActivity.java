package com.angelolamonaca.myfirstapplication;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.View;

import com.angelolamonaca.myfirstapplication.Adapters.WalletAdapter;
import com.angelolamonaca.myfirstapplication.Models.Wallet;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        RecyclerView recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        Wallet[] wallets = new Wallet[]{
                new Wallet("bc1q5jyvmeqpvet5tlz8cqkmme33p4lwsrzxgk7hh3", 150.0, 300.0, 200.0, 100.0),
                new Wallet("bc1qv6664w08cnvy9cfltw4pzzfttvwljgawtqfw42", 1500.0, 300.0, 200.0, 100.0),
                new Wallet("bc1q5skmx82jlfkg0p9mxm2yuka2uax2tpw56w5dca", 6100.0, 300.0, 200.0, 100.0),
                new Wallet("bc1q5s2cztswev7j9dd0rtcq92tq3cc3yxmw668hhx", 3300.0, 300.0, 200.0, 100.0),
                new Wallet("bc1qh8wu20r0xj0sk74wknhts5cf54n7ya8alpxgs8", 216.0, 300.0, 200.0, 100.0),
                new Wallet("bc1q33f4wz785muluyxene9ff92zeed76klhuxtd5t", 0.06, 300.0, 200.0, 100.0),
                new Wallet("bc1qwzgs276v929el645huxf9uql797j66lvzypz7c", 6.0, 300.0, 200.0, 100.0)
        };

        WalletAdapter walletAdapter = new WalletAdapter(wallets, MainActivity.this);
        recyclerView.setAdapter(walletAdapter);

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(view -> Snackbar.make(view, "Here's a Snackbar", Snackbar.LENGTH_LONG)
                .setAction("Action", null).show());
    }
}