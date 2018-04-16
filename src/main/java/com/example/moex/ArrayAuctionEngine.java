package com.example.moex;

/**
 * NOT THREAD SAFE !
 * Реализация {@link AuctionEngine} на массиве
 * <p>
 * Прием заявки - O(1)
 * Рассчет - O(W), где W - максимальная цена заявки
 */
public class ArrayAuctionEngine extends RestrictionAuctionEngine {
    private final int[] buyOrders = new int[MAX_PRICE + 1]; //для читаемости пусть номер ячейки == цена в копейках
    private final int[] sellOrders = new int[MAX_PRICE + 1];

    private int maxBuyPrice = -1;
    private int leastSellPrice = MAX_PRICE + 1;

    @Override
    protected void safeProcess(boolean buyNotSell, short amount, short priceInKopecks) {
        if (priceInKopecks > MAX_PRICE) {
            throw new IllegalArgumentException();
        }
        final int[] target;
        if (buyNotSell) {
            target = buyOrders;
            if (priceInKopecks > maxBuyPrice) {
                maxBuyPrice = priceInKopecks;
            }
        } else {
            target = sellOrders;
            if (priceInKopecks < leastSellPrice) {
                leastSellPrice = priceInKopecks;
            }
        }

        target[priceInKopecks] += amount;
    }

    @Override
    public AuctionResult resolve() {
        if (maxBuyPrice < leastSellPrice) {
            return AuctionResult.frustrated();
        }
        int acceptedBuyIndex = maxBuyPrice;
        int acceptedSellIndex = leastSellPrice;
        int acceptedSellAmount = sellOrders[acceptedSellIndex];
        int acceptedBuyAmount = buyOrders[acceptedBuyIndex];
        boolean moved = true;
        //поиск двух крайних точек:
        //цены продажи слева и цены покупки справа
        //зададим их по краям и будем сдвигать друг к другу
        while (moved) {
            moved = false;
            //двигать вправо цену продажи, до тех пор пока она не станет правее точки покупки
            //или до тех пор пока сумма акций на продажу не превысит сумму акций на покупку
            //что означает что двигать точку правее бесполезно, т.к. акции из новой точки пока что все равно еще некому купить
            for (int currentSellIndex = acceptedSellIndex + 1; currentSellIndex <= acceptedBuyIndex && acceptedSellAmount < acceptedBuyAmount; currentSellIndex++) {
                final int amount = sellOrders[currentSellIndex];
                if (amount > 0 && currentSellIndex <= acceptedBuyIndex) {
                    acceptedSellIndex = currentSellIndex;
                    acceptedSellAmount += amount;
                    moved = true;
                }
            }
            //двигать влево цену покупки, до тех пор пока она не станет левее точки продажи
            //или до тех пор пока сумма акций на покупку не превысит сумму акций на продажу
            for (int currentBuyIndex = acceptedBuyIndex - 1; currentBuyIndex >= acceptedSellIndex && acceptedBuyAmount < acceptedSellAmount; currentBuyIndex--) {
                final int amount = buyOrders[currentBuyIndex];
                if (amount > 0 && currentBuyIndex >= acceptedSellIndex) {
                    acceptedBuyIndex = currentBuyIndex;
                    acceptedBuyAmount += amount;
                    moved = true;
                }
            }
            //если текущее количество акций на покупку и продажу совпадает -
            //значит надо сдвинуть обе точки (если можно), или закончить
            while (acceptedBuyAmount == acceptedSellAmount && acceptedSellIndex < acceptedBuyIndex) {
                //пролистать до следующей заявки на продажу
                int currentSellIndex = acceptedSellIndex + 1;
                int sellAmount = sellOrders[currentSellIndex];
                while (currentSellIndex < acceptedBuyIndex && sellAmount == 0) {
                    currentSellIndex++;
                    sellAmount = sellOrders[currentSellIndex];
                }
                if (currentSellIndex == acceptedBuyIndex) {
                    moved = false;
                    break;
                }
                //пролистать до следующей заявки на покупку
                int currentBuyIndex = acceptedBuyIndex - 1;
                int buyAmount = buyOrders[currentBuyIndex];
                while (currentBuyIndex > acceptedSellIndex && buyAmount== 0) {
                    currentBuyIndex--;
                    buyAmount = buyOrders[currentBuyIndex];
                }
                if (currentBuyIndex == acceptedSellIndex) {
                    moved = false;
                    break;
                }

                if (currentSellIndex <= currentBuyIndex) {
                    acceptedBuyIndex = currentBuyIndex;
                    acceptedSellIndex = currentSellIndex;
                    acceptedBuyAmount += buyAmount;
                    acceptedSellAmount += sellAmount;
                    moved = true;
                } else {
                    moved = false;
                }
            }
        }
        final int kopecks = (acceptedBuyIndex + acceptedSellIndex + 1) / 2; // округление вверх
        return AuctionResult.successful(Math.min(acceptedBuyAmount, acceptedSellAmount), kopecks / 100, kopecks % 100);
    }
}
