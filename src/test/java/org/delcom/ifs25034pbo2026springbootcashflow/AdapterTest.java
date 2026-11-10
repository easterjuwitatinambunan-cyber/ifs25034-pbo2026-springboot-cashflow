package org.delcom.ifs25034pbo2026springbootcashflow;

import static org.junit.jupiter.api.Assertions.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import org.delcom.ifs25034pbo2026springbootcashflow.adapter.presenter.FinancePresenter;
import org.delcom.ifs25034pbo2026springbootcashflow.adapter.repository.TransactionRepository;
import org.delcom.ifs25034pbo2026springbootcashflow.config.AppConfig;
import org.delcom.ifs25034pbo2026springbootcashflow.domain.entity.Transaction;
import org.delcom.ifs25034pbo2026springbootcashflow.domain.entity.TransactionType;
import org.delcom.ifs25034pbo2026springbootcashflow.framework.AppShell;
import org.junit.jupiter.api.Test;

class AdapterTest {
    @Test
    void repositoryCrud() {
        TransactionRepository repo = new TransactionRepository();
        Transaction a = repo.saveIncome("A", new BigDecimal("10"));
        Transaction b = repo.saveExpense("B", new BigDecimal("5"));
        assertEquals(1, a.getId());
        assertEquals(2, b.getId());
        assertEquals(2, repo.findAll().size());
        assertTrue(repo.findById(1).isPresent());
        assertTrue(repo.findById(99).isEmpty());
        assertEquals(1, repo.findBy(t -> t.getDescription().equals("B")).size());
        repo.update(a);
        assertTrue(repo.deleteById(1));
        assertFalse(repo.deleteById(1));
        List<Transaction> copy = repo.findAll();
        copy.clear();
        assertEquals(1, repo.findAll().size());
    }

    @Test
    void presenterFormatting() {
        FinancePresenter p = new FinancePresenter();
        Transaction t = new Transaction(3, "Gaji", new BigDecimal("1000000"),
                TransactionType.INCOME, LocalDateTime.of(2026, 11, 8, 9, 5));
        assertEquals("Rp 1,000,000.00", p.formatMoney(t.getAmount()));
        assertEquals("Rp 1,000,000.00", p.formatAmount(t));
        assertEquals("Pemasukan", p.formatType(t));
        assertEquals("[3] Gaji | Rp 1,000,000.00 | [Pemasukan]", p.format(t));
        assertEquals("08/11/2026 09:05", p.formatCreatedAt(t));
        assertEquals("Saldo: Rp 5.00", p.formatBalance(new BigDecimal("5")));
        assertEquals("H\nE", p.formatList(List.of(), "H", "E"));
        assertEquals("H\n" + p.format(t), p.formatList(List.of(t), "H", "E"));
        assertTrue(p.showTransactions(List.of()).contains("belum tersedia"));
        assertTrue(p.showTransactions(List.of(t)).contains("Gaji"));
        assertTrue(p.showSearchResults(List.of(), "x").contains("tidak ditemukan"));
        assertTrue(p.showSearchResults(List.of(t), "Gaji").contains("Hasil Pencarian"));
        assertTrue(p.showSortedTransactions(List.of()).contains("Terurut"));
        assertTrue(p.showSortedTransactions(List.of(t)).contains("Gaji"));
        assertTrue(p.showAddSuccess(t).startsWith("Berhasil menambah"));
        assertEquals("Berhasil menghapus transaksi.", p.showRemoveSuccess());
        assertTrue(p.showRemoveFailed(4).contains("4"));
        assertTrue(p.showInvalidId().contains("ID"));
        assertTrue(p.showInvalidChoice().contains("Pilihan"));
        assertEquals("[!] msg", p.showValidationError("msg"));
    }

    @Test
    void configAndShell() {
        AppConfig c = new AppConfig();
        TransactionRepository repo = (TransactionRepository) c.transactionRepository();
        assertNotNull(c.financeUseCase(repo));
        assertNotNull(c.financePresenter());
        assertNotNull(new AppShell());
    }
}
