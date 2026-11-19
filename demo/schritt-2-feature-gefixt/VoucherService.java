package de.demo.shop;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Locale;
import java.util.Map;

/**
 * NEUES FEATURE: Gutscheincodes einlösen.
 * Version NACH dem Feedback von SonarQube – alle Issues behoben und getestet.
 */
public class VoucherService {

    // String-Konstruktor statt double: exakte Dezimalwerte (Regel java:S2111)
    private static final Map<String, BigDecimal> VOUCHERS = Map.of(
            "WELCOME10", new BigDecimal("0.10"),
            "STUDENT15", new BigDecimal("0.15"));

    // Unbekannter oder fehlender Code -> kein Rabatt statt NullPointerException (java:S2259)
    public BigDecimal applyVoucher(BigDecimal price, String code) {
        BigDecimal discount = code == null
                ? BigDecimal.ZERO
                : VOUCHERS.getOrDefault(code.toUpperCase(Locale.ROOT), BigDecimal.ZERO);
        return price.subtract(price.multiply(discount)).setScale(2, RoundingMode.HALF_UP);
    }

    // Das hart codierte Admin-Passwort (java:S2068) wurde entfernt:
    // Zugangsdaten gehören in einen Secret-Store, nicht in den Quellcode.
}
