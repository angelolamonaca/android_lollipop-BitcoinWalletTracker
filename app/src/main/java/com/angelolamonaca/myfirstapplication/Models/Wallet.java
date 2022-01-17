package com.angelolamonaca.myfirstapplication.Models;

import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@RequiredArgsConstructor
public class Wallet {

    @NonNull
    private String walletAddress;

    private String walletBalanceBTC;

    private String walletBalanceUSD;

    private Integer confirmedTxCount;


}