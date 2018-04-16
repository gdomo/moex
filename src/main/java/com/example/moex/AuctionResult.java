package com.example.moex;

public class AuctionResult {
    private static final AuctionResult FRUSTRATED = new AuctionResult(-1, -1, -1, false);
    private final int amount;
    private final int rubles;
    private final int kopecks;
    private final boolean successful;

    private AuctionResult(int amount, int rubles, int kopecks, boolean successful) {
        this.amount = amount;
        this.rubles = rubles;
        this.kopecks = kopecks;
        this.successful = successful;
    }

    public static AuctionResult successful(int amount, int rubles, int kopecks) {
        return new AuctionResult(amount, rubles, kopecks, true);
    }

    public static AuctionResult frustrated() {
        return FRUSTRATED;
    }

    public int getAmount() {
        if (!successful) {
            throw new UnsupportedOperationException();
        }
        return amount;
    }

    public int getRubles() {
        if (!successful) {
            throw new UnsupportedOperationException();
        }
        return rubles;
    }

    public int getKopecks() {
        if (!successful) {
            throw new UnsupportedOperationException();
        }
        return kopecks;
    }

    public boolean isSuccessful() {
        return successful;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        AuctionResult that = (AuctionResult) o;

        if (amount != that.amount) return false;
        if (rubles != that.rubles) return false;
        if (kopecks != that.kopecks) return false;
        return successful == that.successful;

    }

    @Override
    public int hashCode() {
        int result = amount;
        result = 31 * result + rubles;
        result = 31 * result + kopecks;
        result = 31 * result + (successful ? 1 : 0);
        return result;
    }
}
