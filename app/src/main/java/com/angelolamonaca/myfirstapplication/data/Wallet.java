package com.angelolamonaca.myfirstapplication.data;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@RequiredArgsConstructor
@Entity
public class Wallet {

    @NonNull
    @PrimaryKey
    @ColumnInfo(name = "wallet_address")
    String walletAddress;

    @Ignore
    String walletBalanceBTC;

    @Ignore
    String walletBalanceUSD;

    @Ignore
    Integer confirmedTxCount;


}