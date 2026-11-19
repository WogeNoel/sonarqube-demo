package de.demo.shop;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

/**
 * NEUES FEATURE: Gutscheincodes einlösen.
 * Version MIT Fehlern – so, wie sie "schnell mal" (oder per KI) geschrieben wurde.
 */
public class VoucherService {

    private static final String ADMIN_PASSWORD = "gutschein-admin-2026";

    private final Map<String, BigDecimal> vouchers = new HashMap<>();

    public VoucherService() {
        vouchers.put("WELCOME10", new BigDecimal(0.10));
        vouchers.put("STUDENT15", new BigDecimal(0.15));
    }

    public BigDecimal applyVoucher(BigDecimal price, String code) {
        BigDecimal discount = null;
        if (code != null && vouchers.containsKey(code.toUpperCase())) {
            discount = vouchers.get(code.toUpperCase());
        }
        return price.subtract(discount.multiply(price));
    }

    public boolean isAdmin(String password) {
        try {
            return ADMIN_PASSWORD.equals(password);
        } catch (RuntimeException e) {
        }
        return false;
    }
}
