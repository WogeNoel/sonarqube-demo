package de.demo.shop;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.math.BigDecimal;
import org.junit.jupiter.api.Test;

class VoucherServiceTest {

    private final VoucherService service = new VoucherService();

    @Test
    void appliesKnownVoucherCaseInsensitive() {
        assertEquals(new BigDecimal("90.00"), service.applyVoucher(new BigDecimal("100.00"), "welcome10"));
    }

    @Test
    void unknownVoucherGivesNoDiscount() {
        assertEquals(new BigDecimal("100.00"), service.applyVoucher(new BigDecimal("100.00"), "GIBTSNICHT"));
    }

    @Test
    void missingVoucherGivesNoDiscount() {
        assertEquals(new BigDecimal("100.00"), service.applyVoucher(new BigDecimal("100.00"), null));
    }
}
