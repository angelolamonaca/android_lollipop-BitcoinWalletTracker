package com.angelolamonaca.myfirstapplication.Models;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@RequiredArgsConstructor
public class Wallet {
    private String walletAddress;
    private Double walletBalanceBTC;
    private Double walletBalanceEUR;
    private Double walletBalanceCHF;
    private Double walletBalanceUSD;
}