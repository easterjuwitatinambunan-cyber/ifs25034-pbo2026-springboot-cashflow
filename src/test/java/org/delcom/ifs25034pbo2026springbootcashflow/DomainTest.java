package org.delcom.ifs25034pbo2026springbootcashflow;

import static org.junit.jupiter.api.Assertions.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import org.delcom.ifs25034pbo2026springbootcashflow.domain.entity.SortOption;
import org.delcom.ifs25034pbo2026springbootcashflow.domain.entity.Transaction;
import org.delcom.ifs25034pbo2026springbootcashflow.domain.entity.TransactionType;
import org.delcom.ifs25034pbo2026springbootcashflow.domain.exception.DomainException;
import org.delcom.ifs25034pbo2026springbootcashflow.domain.exception.EntityNotFoundException;
import org.delcom.ifs25034pbo2026springbootcashflow.domain.exception.ValidationException;
import org.junit.jupiter.api.Test;

class DomainTest {
    @Test
    void transactionGettersAndMutators() {
        LocalDateTime now = LocalDateTime.of(2026, 1, 2, 3, 4);
        Transaction t = new Transaction(1, "Gaji", new BigDecimal("100"), TransactionType.INCOME, now);
        assertEquals(1, t.getId());
        assertEquals("Gaji", t.getDescription());
        assertEquals(new BigDecimal("100"), t.getAmount());
        assertEquals(TransactionType.INCOME, t.getType());
        assertEquals(now, t.getCreatedAt());
        assertTrue(t.isIncome());
        assertEquals(new BigDecimal("100"), t.signedAmount());
        t.changeDescription("Bonus");
        t.changeAmount(new BigDecimal("5"));
        assertEquals("Bonus", t.getDescription());
        assertEquals(new BigDecimal("5"), t.getAmount());
    }

    @Test
    void expenseHasNegativeSignedAmount() {
        Transaction t = new Transaction(2, "Makan", new BigDecimal("10"), TransactionType.EXPENSE);
        assertNotNull(t.getCreatedAt());
        assertFalse(t.isIncome());
        assertEquals(new BigDecimal("-10"), t.signedAmount());
    }

    @Test
    void typeLabels() {
        assertEquals("Pemasukan", TransactionType.INCOME.label());
        assertEquals("Pengeluaran", TransactionType.EXPENSE.label());
    }

    @Test
    void sortOptionComparators() {
        Transaction in = new Transaction(1, "a", new BigDecimal("5"), TransactionType.INCOME);
        Transaction out = new Transaction(2, "b", new BigDecimal("9"), TransactionType.EXPENSE);
        assertTrue(SortOption.AMOUNT_ASC.comparator().compare(in, out) < 0);
        assertTrue(SortOption.AMOUNT_DESC.comparator().compare(in, out) > 0);
        assertTrue(SortOption.INCOME_FIRST.comparator().compare(in, out) < 0);
        assertTrue(SortOption.INCOME_FIRST.comparator().compare(out, in) > 0);
        assertTrue(SortOption.EXPENSE_FIRST.comparator().compare(out, in) < 0);
        assertTrue(SortOption.EXPENSE_FIRST.comparator().compare(in, out) > 0);
        assertEquals(4, SortOption.values().length);
        assertEquals(SortOption.AMOUNT_ASC, SortOption.valueOf("AMOUNT_ASC"));
    }

    @Test
    void exceptions() {
        RuntimeException cause = new RuntimeException("x");
        DomainException d = new DomainException("pesan", cause);
        assertEquals("pesan", d.getMessage());
        assertSame(cause, d.getCause());
        assertEquals("v", new ValidationException("v").getMessage());
        EntityNotFoundException e = new EntityNotFoundException(7);
        assertEquals(7, e.getId());
        assertTrue(e.getMessage().contains("7"));
        assertInstanceOf(DomainException.class, e);
    }
}
