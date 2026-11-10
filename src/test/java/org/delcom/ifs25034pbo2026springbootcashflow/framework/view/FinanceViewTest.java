package org.delcom.ifs25034pbo2026springbootcashflow.framework.view;

import static org.junit.jupiter.api.Assertions.*;

import com.github.mvysny.kaributesting.v10.MockVaadin;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import java.util.List;
import org.delcom.ifs25034pbo2026springbootcashflow.adapter.presenter.FinancePresenter;
import org.delcom.ifs25034pbo2026springbootcashflow.adapter.repository.TransactionRepository;
import org.delcom.ifs25034pbo2026springbootcashflow.domain.entity.Transaction;
import org.delcom.ifs25034pbo2026springbootcashflow.usecase.FinanceUseCase;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class FinanceViewTest {
    private FinanceUseCase useCase;
    private FinanceView view;

    @BeforeEach
    void setUp() {
        MockVaadin.setup();
        useCase = new FinanceUseCase(new TransactionRepository());
        view = new FinanceView(useCase, new FinancePresenter());
        UI.getCurrent().add(view);
    }

    @AfterEach
    void tearDown() {
        MockVaadin.tearDown();
    }

    private List<Transaction> items() {
        return view.grid.getListDataView().getItems().toList();
    }

    private Button findButton(com.vaadin.flow.component.Component root, String text) {
        return root.getChildren().flatMap(c -> java.util.stream.Stream.concat(
                        java.util.stream.Stream.of(c), c.getChildren()))
                .filter(Button.class::isInstance).map(Button.class::cast)
                .filter(b -> text.equals(b.getText())).findFirst().orElseThrow();
    }

    private void add(boolean income, String desc, String amount) {
        view.openAddDialog(income);
        view.descriptionInput.setValue(desc);
        view.amountInput.setValue(amount);
        view.confirmButton.click();
    }

    @Test
    void startsEmpty() {
        assertTrue(items().isEmpty());
        assertEquals("0", view.totalCount.getText());
        assertTrue(view.statusLabel.getText().contains("belum tersedia"));
    }

    @Test
    void addIncomeAndExpenseUpdatesSummary() {
        add(true, "Gaji", "1000");
        add(false, "Makan", "250");
        assertEquals(2, items().size());
        assertEquals("2", view.totalCount.getText());
        assertEquals("Rp 1,000.00", view.incomeTotal.getText());
        assertEquals("Rp 250.00", view.expenseTotal.getText());
        assertEquals("Rp 750.00", view.balanceLabel.getText());
        assertEquals("Daftar Transaksi", view.statusLabel.getText());
    }

    @Test
    void invalidInputKeepsDialogOpen() {
        view.openAddDialog(true);
        view.descriptionInput.setValue("");
        view.amountInput.setValue("5");
        view.confirmButton.click();
        assertTrue(view.currentDialog.isOpened());
        assertTrue(items().isEmpty());
        view.cancelButton.click();
        assertFalse(view.currentDialog.isOpened());
    }

    @Test
    void searchFiltersAndBlankResets() {
        add(true, "Gaji", "1000");
        add(false, "Makan", "250");
        view.searchField.setValue("gaji");
        findButton(view, "Cari").click();
        assertEquals(1, items().size());
        assertTrue(view.statusLabel.getText().startsWith("Hasil Pencarian"));

        view.searchField.setValue("tidak ada");
        findButton(view, "Cari").click();
        assertTrue(items().isEmpty());
        assertTrue(view.statusLabel.getText().contains("tidak ditemukan"));

        view.searchField.setValue("   ");
        findButton(view, "Cari").click();
        assertEquals(2, items().size());

        view.searchField.setValue("gaji");
        findButton(view, "Muat Ulang").click();
        assertEquals(2, items().size());
        assertEquals("", view.searchField.getValue());
    }

    @Test
    void toolbarButtonsOpenDialogs() {
        findButton(view, "Tambah Pemasukan").click();
        assertTrue(view.currentDialog.isOpened());
        assertEquals("Menambah Pemasukan", view.currentDialog.getHeaderTitle());
        findButton(view, "Tambah Pengeluaran").click();
        assertEquals("Menambah Pengeluaran", view.currentDialog.getHeaderTitle());
    }

    @Test
    void deleteFlow() {
        add(true, "Gaji", "1000");
        Transaction t = items().get(0);
        HorizontalLayout actions = view.actionButtons(t);
        ((Button) actions.getChildren().findFirst().orElseThrow()).click();
        assertEquals("Menghapus Transaksi", view.currentDialog.getHeaderTitle());
        view.cancelButton.click();
        assertEquals(1, items().size());

        ((Button) actions.getChildren().findFirst().orElseThrow()).click();
        view.confirmButton.click();
        assertTrue(items().isEmpty());
    }

    @Test
    void deleteAlreadyRemovedShowsError() throws Exception {
        add(true, "Gaji", "1000");
        Transaction t = items().get(0);
        view.openDeleteDialog(t);
        useCase.removeTransaction(t.getId());
        view.confirmButton.click();
        assertTrue(items().isEmpty());
    }

    @Test
    void attachSetsLanguage() {
        assertEquals("main", view.getElement().getAttribute("role"));
    }
}
