package com.angelolamonaca.bitcoinWalletTracker.data;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

@Database(entities = {Wallet.class}, version = 1, exportSchema = false)
public abstract class WalletDatabase extends RoomDatabase {
    public abstract WalletDao walletDao();

    private static WalletDatabase Instance;

    static public WalletDatabase getInstance(Context context) {
        if (Instance != null) return Instance;
        else {
            Instance = Room.databaseBuilder(context, WalletDatabase.class, "wallet-database")
                    .createFromAsset("database/wallet-database.db")
                    .allowMainThreadQueries()
                    .build();
            return Instance;
        }
    }
}
