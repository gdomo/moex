package com.example.moex;

import java.util.Scanner;

/**
 * Читает из stdin строки вида <br/> {@code <B|S> <количество> <рубли>.<копейки>} <br/>
 * После ввода пустой строки выполняет расчет и выводит в stdout строку вида <br/>{@code <количество> <рубли>.<копейки>}
 */
public class StdInOutProcessor {
    public static void processStd(AuctionEngine engine) {
        final Scanner input = new Scanner(System.in);
        for (String line = input.nextLine(); !line.isEmpty(); line = input.nextLine()) {
            line = line.trim();
            final String[] order = line.split(" ");
            final short amount = Short.parseShort(order[1]);
            final String[] price = order[2].split("\\.");
            final byte rubles = Byte.parseByte(price[0]);
            final byte kopecks = Byte.parseByte(price[1]);
            switch (order[0]) {
                case "B": {
                    engine.buy(amount, rubles, kopecks);
                    break;
                }
                case "S": {
                    engine.sell(amount, rubles, kopecks);
                    break;
                }
            }
        }
        final AuctionResult auctionInfo = engine.resolve();
        if (auctionInfo.isSuccessful()) {
            System.out.println(String.format("%d %d.%02d",
                    auctionInfo.getAmount(),
                    auctionInfo.getRubles(),
                    auctionInfo.getKopecks()));
        } else {
            System.out.println("0 n/a");
        }
    }
}
