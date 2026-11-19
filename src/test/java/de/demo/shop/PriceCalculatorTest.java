package de.demo.shop;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.math.BigDecimal;
import java.util.List;
import org.junit.jupiter.api.Test;

class PriceCalculatorTest {

    private final PriceCalculator calculator = new PriceCalculator();

    @Test
    void sumsNetPrices() {
        assertEquals(new BigDecimal("30.00"),
                calculator.netTotal(List.of(new BigDecimal("10"), new BigDecimal("20"))));
    }

    @Test
    void addsVatAndShippingBelowThreshold() {
        // 10.00 netto -> 11.90 brutto + 4.99 Versand
        assertEquals(new BigDecimal("16.89"),
                calculator.grossTotal(List.of(new BigDecimal("10.00"))));
    }

    @Test
    void freeShippingAboveThreshold() {
        // 100.00 netto -> 119.00 brutto, versandkostenfrei
        assertEquals(new BigDecimal("119.00"),
                calculator.grossTotal(List.of(new BigDecimal("100.00"))));
    }
}
