package com.example.moex;

public class Test {
    //обойдемся без JUnit для отсутствия зависимостей
    public static void main(String[] args) {
        ArrayAuctionEngine arrayEngine = new ArrayAuctionEngine();
        TsAuctionEngine tsEngine = new TsAuctionEngine();
        stress(arrayEngine);
        stress(tsEngine);

        arrayEngine = new ArrayAuctionEngine();
        tsEngine = new TsAuctionEngine();
        buyLimit(arrayEngine);
        buyLimit(tsEngine);

        arrayEngine = new ArrayAuctionEngine();
        tsEngine = new TsAuctionEngine();
        sellLimit(arrayEngine);
        sellLimit(tsEngine);

        arrayEngine = new ArrayAuctionEngine();
        tsEngine = new TsAuctionEngine();
        uselessSells(arrayEngine);
        uselessSells(tsEngine);

        arrayEngine = new ArrayAuctionEngine();
        tsEngine = new TsAuctionEngine();
        uselessBuys(arrayEngine);
        uselessBuys(tsEngine);

        arrayEngine = new ArrayAuctionEngine();
        tsEngine = new TsAuctionEngine();
        amountCoincidence(arrayEngine);
        amountCoincidence(tsEngine);

        arrayEngine = new ArrayAuctionEngine();
        tsEngine = new TsAuctionEngine();
        priceCoincidence(arrayEngine);
        priceCoincidence(tsEngine);

        arrayEngine = new ArrayAuctionEngine();
        tsEngine = new TsAuctionEngine();
        priceAmountCoincidence(arrayEngine);
        priceAmountCoincidence(tsEngine);

        arrayEngine = new ArrayAuctionEngine();
        tsEngine = new TsAuctionEngine();
        commonCase(arrayEngine);
        commonCase(tsEngine);
    }

    private static void stress(AuctionEngine auctionEngine) {
        final long start = System.currentTimeMillis();
        for (int i = 1; i <= 500000; i++) {
            final int price = i / 100;
            auctionEngine.sell(1000, price / 100, price % 100);
        }
        for (int i = 999999; i >= 500000; i--) {
            final int price = i / 100;
            auctionEngine.buy(1000, price / 100, price % 100);
        }
        final AuctionResult result = auctionEngine.resolve();
        System.out.println(System.currentTimeMillis() - start);
        assert result.equals(AuctionResult.successful(1000 * 500000, 50, 0));
    }

    private static void buyLimit(AuctionEngine auctionEngine) {
        auctionEngine.buy(10, 20, 0);
        auctionEngine.sell(20, 10, 0);
        final AuctionResult result = auctionEngine.resolve();
        assert result.equals(AuctionResult.successful(10, 15, 0));
    }

    private static void sellLimit(AuctionEngine auctionEngine) {
        auctionEngine.buy(20, 20, 0);
        auctionEngine.sell(10, 10, 0);
        final AuctionResult result = auctionEngine.resolve();
        assert result.equals(AuctionResult.successful(10, 15, 0));
    }

    private static void uselessSells(AuctionEngine auctionEngine) {
        auctionEngine.sell(10, 10, 0);
        auctionEngine.sell(10, 20, 0);
        auctionEngine.sell(10, 30, 0);
        auctionEngine.buy(5, 50, 0);
        auctionEngine.buy(10, 40, 0);
        final AuctionResult result = auctionEngine.resolve();
        assert result.equals(AuctionResult.successful(15, 30, 0));
    }

    private static void uselessBuys(AuctionEngine auctionEngine) {
        auctionEngine.sell(5, 10, 0);
        auctionEngine.sell(10, 20, 0);
        auctionEngine.buy(10, 50, 0);
        auctionEngine.buy(10, 40, 0);
        auctionEngine.buy(10, 30, 0);
        final AuctionResult result = auctionEngine.resolve();
        assert result.equals(AuctionResult.successful(15, 30, 0));
    }

    private static void amountCoincidence(AuctionEngine auctionEngine) {
        auctionEngine.sell(10, 10, 0);
        auctionEngine.sell(10, 20, 0);
        auctionEngine.sell(10, 30, 0);
        auctionEngine.buy(10, 60, 0);
        auctionEngine.buy(10, 40, 0);
        auctionEngine.buy(10, 15, 0);
        final AuctionResult result = auctionEngine.resolve();
        assert result.equals(AuctionResult.successful(20, 30, 0));
    }

    private static void priceCoincidence(AuctionEngine auctionEngine) {
        auctionEngine.sell(10, 10, 0);
        auctionEngine.sell(20, 15, 0);
        auctionEngine.buy(11, 60, 0);
        auctionEngine.buy(4, 15, 0);
        final AuctionResult result = auctionEngine.resolve();
        assert result.equals(AuctionResult.successful(15, 15, 0));
    }

    private static void priceAmountCoincidence(AuctionEngine auctionEngine) {
        auctionEngine.sell(10, 10, 0);
        auctionEngine.sell(10, 20, 0);
        auctionEngine.buy(10, 30, 0);
        auctionEngine.buy(10, 20, 0);
        final AuctionResult result = auctionEngine.resolve();
        assert result.equals(AuctionResult.successful(20, 20, 0));
    }

    private static void commonCase(AuctionEngine auctionEngine) {
        auctionEngine.sell(11, 10, 13);
        auctionEngine.sell(1, 20, 15);
        auctionEngine.sell(15, 31, 11);
        auctionEngine.sell(7, 20, 0);
        auctionEngine.buy(5, 53, 17);
        auctionEngine.buy(1, 41, 99);
        auctionEngine.buy(17, 32, 22);
        auctionEngine.buy(99, 1, 55);
        final AuctionResult result = auctionEngine.resolve();
        assert result.equals(AuctionResult.successful(23, 31, 67));
    }
}
