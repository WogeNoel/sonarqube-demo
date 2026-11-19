package de.demo.shop;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

/**
 * Berechnet Warenkorbpreise. Sauberer, getesteter Code – so soll es aussehen.
 */
public class PriceCalculator {

    private static final BigDecimal VAT_RATE = new BigDecimal("0.19");
    private static final BigDecimal FREE_SHIPPING_THRESHOLD = new BigDecimal("50.00");
    private static final BigDecimal SHIPPING_COSTS = new BigDecimal("4.99");

    public BigDecimal netTotal(List<BigDecimal> itemPrices) {
        return itemPrices.stream()
                .reduce(BigDecimal.ZERO, BigDecimal::add)
                .setScale(2, RoundingMode.HALF_UP);
    }

    public BigDecimal grossTotal(List<BigDecimal> itemPrices) {
        BigDecimal net = netTotal(itemPrices);
        BigDecimal gross = net.add(net.multiply(VAT_RATE));
        return gross.add(shippingCosts(gross)).setScale(2, RoundingMode.HALF_UP);
    }

    BigDecimal shippingCosts(BigDecimal gross) {
        return gross.compareTo(FREE_SHIPPING_THRESHOLD) >= 0 ? BigDecimal.ZERO : SHIPPING_COSTS;
    }
}
