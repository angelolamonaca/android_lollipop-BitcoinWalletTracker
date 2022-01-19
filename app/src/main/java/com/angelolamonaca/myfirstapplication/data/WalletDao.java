package com.angelolamonaca.myfirstapplication.data;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

@Dao
public interface WalletDao {
    @Query("SELECT * FROM wallet")
    List<Wallet> getAll();

    @Query("SELECT * FROM wallet WHERE wallet_address LIKE :walletAddress LIMIT 1")
    Wallet findByAddress(String walletAddress);

    @Insert
    void insertAll(Wallet... wallets);

    @Delete
    void delete(Wallet wallet);
}
