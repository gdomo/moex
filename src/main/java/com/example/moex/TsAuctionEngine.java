package com.example.moex;

import java.util.Iterator;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.TreeMap;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * NOT THREAD SAFE !
 * <p>
 * Реализация {@link AuctionEngine} на TreeMap
 * Прием заявки - O(log N), где N - количество уже принятых заявок
 * Рассчет - O(N)
 */
public class TsAuctionEngine extends RestrictionAuctionEngine {
    //map-ы цен в копейках на "holder" количества
    private final TreeMap<Short, AtomicInteger> buyOrders = new TreeMap<>((o1, o2) -> o2 - o1); //обратный порядок
    private final TreeMap<Short, AtomicInteger> sellOrders = new TreeMap<>();

    @Override
    protected void safeProcess(boolean buyNotSell, short amount, short priceInKopecks) {
        final TreeMap<Short, AtomicInteger> map = buyNotSell ? buyOrders : sellOrders;
        Optional.ofNullable(map.putIfAbsent(priceInKopecks, new AtomicInteger(amount)))
                .ifPresent(currentAmount -> currentAmount.addAndGet(amount));
    }

    @Override
    public AuctionResult resolve() {
        if (buyOrders.isEmpty() || sellOrders.isEmpty()) {
            return AuctionResult.frustrated();
        }
        final Iterator<Entry<Short, AtomicInteger>> sellIterator = sellOrders.entrySet().iterator();
        final Iterator<Entry<Short, AtomicInteger>> buyIterator = buyOrders.entrySet().iterator();
        Entry<Short, AtomicInteger> acceptedBuy = buyIterator.next();
        Entry<Short, AtomicInteger> acceptedSell = sellIterator.next();
        if (acceptedBuy.getKey() < acceptedSell.getKey()) {
            return AuctionResult.frustrated();
        }
        Entry<Short, AtomicInteger> currentBuy = acceptedBuy;
        Entry<Short, AtomicInteger> currentSell = acceptedSell;
        int acceptedSellAmount = acceptedSell.getValue().get();
        int acceptedBuyAmount = acceptedBuy.getValue().get();
        boolean moved = true;
        //поиск двух крайних точек:
        //цены продажи слева и цены покупки справа
        //зададим их по краям и будем сдвигать друг к другу
        while (moved) {
            moved = false;
            //двигать вправо цену продажи, до тех пор пока она не станет правее точки покупки
            //или до тех пор пока сумма акций на продажу не превысит сумму акций на покупку
            //что означает что двигать точку правее бесполезно, т.к. акции из новой точки пока что все равно еще некому купить
            while (sellIterator.hasNext() && currentSell.getKey() <= acceptedBuy.getKey() && acceptedSellAmount < acceptedBuyAmount) {
                currentSell = sellIterator.next();
                if (currentSell.getKey() <= acceptedBuy.getKey()) {
                    acceptedSell = currentSell;
                    acceptedSellAmount += currentSell.getValue().get();
                    moved = true;
                }
            }
            //двигать влево цену покупки, до тех пор пока она не станет левее точки продажи
            //или до тех пор пока сумма акций на покупку не превысит сумму акций на продажу
            while (buyIterator.hasNext() && currentBuy.getKey() >= acceptedSell.getKey() && acceptedBuyAmount < acceptedSellAmount) {
                currentBuy = buyIterator.next();
                if (currentBuy.getKey() >= acceptedSell.getKey()) {
                    acceptedBuy = currentBuy;
                    acceptedBuyAmount += currentBuy.getValue().get();
                    moved = true;
                }
            }
            //если текущее количество акций на покупку и продажу совпадает -
            //значит надо сдвинуть обе точки (если можно), или закончить
            while (acceptedBuyAmount == acceptedSellAmount && buyIterator.hasNext() && sellIterator.hasNext()) {
                currentBuy = buyIterator.next();
                currentSell = sellIterator.next();
                if (currentBuy.getKey() >= currentSell.getKey()) {
                    acceptedBuy = currentBuy;
                    acceptedSell = currentSell;
                    acceptedBuyAmount += currentBuy.getValue().get();
                    acceptedSellAmount += currentSell.getValue().get();
                    moved = true;
                } else {
                    moved = false;
                }
            }
        }
        final int kopecks = (acceptedBuy.getKey() + acceptedSell.getKey() + 1) / 2; // округление вверх
        return AuctionResult.successful(Math.min(acceptedBuyAmount, acceptedSellAmount), kopecks / 100, kopecks % 100);
    }
}
