import org.junit.jupiter.api.*;
import java.io.File;
import java.util.*;
import static org.junit.jupiter.api.Assertions.*;

class AdvancedCoverageTest {

    private AccountRepository repo;
    private AccountService accountService;
    private BankController controller;

    @BeforeEach
    void setup() {
        repo = new AccountRepository();
        repo.save(new Account("A1", 1000));
        repo.save(new Account("A2", 500));

        accountService = new AccountService(repo);
        TransactionService tx = new TransactionService(accountService);
        LoanService loan = new LoanService(accountService);
        AuthService auth = new AuthService();

        controller = new BankController(auth, tx, loan);
    }

    @Test
    void testUnsafeExport() throws Exception {
        String fileName = "test_accounts.txt";
        accountService.unsafeExport(fileName);

        File file = new File(fileName);
        assertTrue(file.exists());
        file.delete();
    }

@Test
void testLoanAllowedForRegularUserDueToVulnerability() {
    User user = new User("john", "pass", "user");

    AccountRepository repo = new AccountRepository();
    repo.save(new Account("A1", 100));

    AccountService acc = new AccountService(repo);
    LoanService loan = new LoanService(acc);
    TransactionService tx = new TransactionService(acc);
    AuthService auth = new AuthService();

    BankController controller = new BankController(auth, tx, loan);

    assertDoesNotThrow(() ->
            controller.loan(user, "pass", "A1", 100));

    assertEquals(200, repo.findById("A1").balance);
}

    @Test
    void testAuthorizeFalseBranch() {
        AuthService auth = new AuthService();
        User guest = new User("guest", "pass", "guest");

        assertFalse(auth.authorize(guest, "delete"));
    }

    @Test
    void testBatchTransferMultiple() {
        TransactionService tx = new TransactionService(accountService);

        tx.batchTransfer(
                Arrays.asList("A1"),
                Arrays.asList("A2"),
                Arrays.asList(100.0)
        );

        assertEquals(900, repo.findById("A1").balance);
        assertEquals(600, repo.findById("A2").balance);
    }
}
