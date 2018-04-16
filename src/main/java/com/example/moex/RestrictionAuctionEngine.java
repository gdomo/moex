package com.example.moex;

/**
 * NOT THREAD SAFE !
 * <p>
 * {@link AuctionEngine} Ругающийся при отрицательных значениях
 * количества акций, количества рублей, количества копеек, а также,
 * при превшении {@link RestrictionAuctionEngine#MAX_ORDER_COUNT} суммарного количества заявок,
 * превышении {@link RestrictionAuctionEngine#MAX_ORDER_AMOUNT} количества акций в заявки,
 * и превышении {@link RestrictionAuctionEngine#MAX_PRICE} цены в копейках.
 * Эти ограничения гарантируют, что сумма акций всех заявок для одной цены поместится в int.
 */
public abstract class RestrictionAuctionEngine implements AuctionEngine {
    public static final int MAX_ORDER_COUNT = 1000000;
    public static final int MAX_ORDER_AMOUNT = 1000;
    public static final int MAX_PRICE = 100 * 100;

    private int orderCount = 0;

    @Override
    public void buy(int amount, int rubles, int kopecks) {
        processOrder(true, amount, rubles, kopecks);
    }

    @Override
    public void sell(int amount, int rubles, int kopecks) {
        processOrder(false, amount, rubles, kopecks);
    }

    private void processOrder(boolean buyNotSell, int amount, int rubles, int kopecks) {
        if (amount < 0 || rubles < 0 || kopecks < 0 || amount > MAX_ORDER_AMOUNT || rubles > MAX_PRICE / 100) {
            throw new IllegalArgumentException();
        }
        if (orderCount++ == MAX_ORDER_COUNT) {
            throw new UnsupportedOperationException();
        }
        final int priceInKopecks = rubles * 100 + kopecks;
        if (priceInKopecks > MAX_PRICE) {
            throw new IllegalArgumentException();
        }
        safeProcess(buyNotSell, (short) amount, (short) priceInKopecks);
    }

    /**
     * Принять заявку, параметры которой проверены согласно {@link RestrictionAuctionEngine}
     *
     * @param buyNotSell     true - покупка, false - продажа
     * @param amount         количество акций
     * @param priceInKopecks цена в копейках, т.е. количество целых рублей * 100 + количество оставшихся копеек
     */
    protected abstract void safeProcess(boolean buyNotSell, short amount, short priceInKopecks);
}
