package com.bank;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import java.util.*;

class BankAppTest {

    AccountRepository repo;
    AccountService accService;
    TransactionService txService;
    LoanService loanService;
    AuthService authService;
    BankController controller;

    User admin;
    User superUser;
    User user1;

    @BeforeEach
    void setup() {
        repo = new AccountRepository();
        accService = new AccountService(repo);
        txService = new TransactionService(accService);
        loanService = new LoanService(accService);
        authService = new AuthService();
        controller = new BankController(authService, txService, loanService);

        // setup accounts
        repo.save(new Account("A1", 1000));
        repo.save(new Account("A2", 500));
        repo.save(new Account("A3", 200));
        repo.save(new Account("A4", 0));

        // setup users
        admin = new User("admin", "admin123", "admin");
        superUser = new User("super", "super456", "superuser");
        user1 = new User("user1", "pass1", "user");
    }

    // ===================== TEST AUTH =====================
    @Test
    void testLoginAdmin() {
        assertTrue(authService.login(admin, "admin123"));
        assertFalse(authService.login(admin, "wrong"));
    }

    @Test
    void testLoginSuperUser() {
        assertTrue(authService.login(superUser, "super456"));
        assertFalse(authService.login(superUser, "badpass"));
    }

    @Test
    void testLoginRegularUser() {
        assertTrue(authService.login(user1, "pass1"));
        assertFalse(authService.login(user1, "badpass"));
    }

    // ===================== TEST TRANSACTIONS =====================
    @Test
    void testSimpleTransfer() {
        controller.transfer(admin, "admin123", "A1", "A2", 200);
        assertEquals(800, repo.findById("A1").balance);
        assertEquals(700, repo.findById("A2").balance);
    }

    @Test
    void testBatchTransfer() {
        controller.batchTransfer(superUser, "super456",
                Arrays.asList("A2", "A3"), Arrays.asList("A3", "A4"),
                Arrays.asList(50.0, 100.0));
        assertEquals(450, repo.findById("A2").balance);
        assertEquals(150, repo.findById("A3").balance);
        assertEquals(100, repo.findById("A4").balance);
    }

    @Test
    void testInvalidTransferAuth() {
        controller.transfer(user1, "wrongpass", "A1", "A2", 100);
        assertEquals(1000, repo.findById("A1").balance);
        assertEquals(500, repo.findById("A2").balance);
    }

    @Test
    void testTransferInvalidAmount() {
        controller.transfer(admin, "admin123", "A1", "A2", -50);
        assertEquals(1000, repo.findById("A1").balance);
        assertEquals(500, repo.findById("A2").balance);
    }

    // ===================== TEST LOANS =====================
    @Test
    void testRequestLoan() {
        controller.loan(admin, "admin123", "A4", 500);
        assertEquals(500, repo.findById("A4").balance);
    }

    @Test
    void testRepayLoan() {
        loanService.requestLoan("A4", 500);
        loanService.repayLoan("A4", 200);
        assertEquals(300, repo.findById("A4").balance);
    }

    // ===================== TEST ACCOUNT SERVICE =====================
    @Test
    void testDepositWithdraw() {
        accService.deposit("A1", 100);
        assertEquals(1100, repo.findById("A1").balance);

        accService.withdraw("A1", 200);
        assertEquals(900, repo.findById("A1").balance);
    }

    @Test
    void testQuickWithdraw() {
        accService.quickWithdraw("A2", 200);
        assertEquals(300, repo.findById("A2").balance);
    }

    @Test
    void testWithdrawTooMuch() {
        Exception e = assertThrows(RuntimeException.class, () -> accService.withdraw("A3", 500));
        assertEquals("Not enough money", e.getMessage());
    }

}
