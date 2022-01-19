package com.angelolamonaca.myfirstapplication.data;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

@Database(entities = {Wallet.class}, version = 1)
public abstract class WalletDatabase extends RoomDatabase {
    public abstract WalletDao walletDao();

    static WalletDatabase Instance;

    static public WalletDatabase getInstance(Context context) {
        return Instance != null ? Instance : Room.databaseBuilder(context, WalletDatabase.class, "wallet-database")
                .createFromAsset("database/wallet-database.db")
                .allowMainThreadQueries()
                .build();
    }
}
