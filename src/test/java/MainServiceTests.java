package com.bank;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class MainServiceTests {

    private AccountRepository repo;
    private AccountService accountService;
    private AuthService authService;
    private TransactionService transactionService;

    private User admin;
    private User normalUser;

    @BeforeEach
    void setup() {
        repo = new AccountRepository();
        accountService = new AccountService(repo);
        authService = new AuthService();
        transactionService = new TransactionService(accountService);

        repo.save(new Account("A1", 1000));
        repo.save(new Account("A2", 500));

        admin = new User("admin", "admin123", "admin");
        normalUser = new User("user", "userpass", "user");
    }

    // ===================== AccountService =====================
    @Test
    void testDepositPositive() {
        accountService.deposit("A1", 200);
        assertEquals(1200, repo.findById("A1").balance);
    }

    @Test
    void testDepositZero() {
        accountService.deposit("A1", 0);
        assertEquals(1000, repo.findById("A1").balance);
    }

    @Test
    void testDepositNegative() {
        accountService.deposit("A1", -50);
        assertEquals(1000, repo.findById("A1").balance); // deposit ne sme da menja stanje
    }

    @Test
    void testWithdrawSufficient() {
        accountService.withdraw("A1", 500);
        assertEquals(500, repo.findById("A1").balance);
    }

    @Test
    void testWithdrawExactBalance() {
        accountService.withdraw("A2", 500);
        assertEquals(0, repo.findById("A2").balance);
    }

    @Test
    void testWithdrawInsufficientFunds() {
        RuntimeException e = assertThrows(RuntimeException.class, () -> {
            accountService.withdraw("A2", 600);
        });
        assertEquals("Not enough money", e.getMessage());
    }

    @Test
    void testWithdrawNegative() {
        RuntimeException e = assertThrows(RuntimeException.class, () -> {
            accountService.withdraw("A1", -50);
        });
        assertEquals("Invalid amount", e.getMessage());
    }

    // ===================== AuthService =====================
    @Test
    void testAdminLoginSuccess() {
        assertTrue(authService.login(admin, "admin123"));
    }

    @Test
    void testAdminLoginFail() {
        assertFalse(authService.login(admin, "wrongpass"));
    }

    @Test
    void testUserLoginSuccess() {
        assertTrue(authService.login(normalUser, "userpass"));
    }

    @Test
    void testUserLoginFail() {
        assertFalse(authService.login(normalUser, "badpass"));
    }

    @Test
    void testLoginNullUser() {
        assertFalse(authService.login(null, "any"));
    }

    // ===================== TransactionService =====================
    @Test
    void testValidTransfer() {
        transactionService.transfer("A1", "A2", 200);
        assertEquals(800, repo.findById("A1").balance);
        assertEquals(700, repo.findById("A2").balance);
    }

    @Test
    void testTransferZeroAmount() {
        transactionService.transfer("A1", "A2", 0);
        assertEquals(1000, repo.findById("A1").balance);
        assertEquals(500, repo.findById("A2").balance);
    }

    @Test
    void testTransferNegativeAmount() {
        transactionService.transfer("A1", "A2", -50);
        assertEquals(1000, repo.findById("A1").balance);
        assertEquals(500, repo.findById("A2").balance);
    }

    @Test
    void testTransferInsufficientFunds() {
        RuntimeException e = assertThrows(RuntimeException.class, () -> {
            transactionService.transfer("A2", "A1", 600);
        });
        assertEquals("Not enough money", e.getMessage());
    }

    // ===================== Account Not Found =====================
    @Test
    void testAccountNotFound() {
        assertNull(repo.findById("A999"));
    }
}
