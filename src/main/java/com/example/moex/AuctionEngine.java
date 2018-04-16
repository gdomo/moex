package com.example.moex;

public interface AuctionEngine {
    void buy(int amount, int rubles, int kopecks);

    void sell(int amount, int rubles, int kopecks);

    AuctionResult resolve();
}
