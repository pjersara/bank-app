import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;

class BankControllerTest {

    private AccountRepository repo;
    private BankController controller;

    @BeforeEach
    void setup() {
        repo = new AccountRepository();
        repo.save(new Account("A1", 1000));
        repo.save(new Account("A2", 500));

        AccountService accService = new AccountService(repo);
        TransactionService txService = new TransactionService(accService);
        LoanService loanService = new LoanService(accService);
        AuthService authService = new AuthService();

        controller = new BankController(authService, txService, loanService);
    }

    @Test
    void testTransferAuthenticated() {
        User admin = new User("admin", "123", "admin");
        admin.password = "123";

        controller.transfer(admin, "123", "A1", "A2", 100);

        assertEquals(900, repo.findById("A1").balance);
        assertEquals(600, repo.findById("A2").balance);
    }

    @Test
    void testTransferAuthFail() {
        User user = new User("john", "pass", "user");

        assertThrows(SecurityException.class,
                () -> controller.transfer(user, "wrong", "A1", "A2", 100));
    }

    @Test
    void testLoanAuthorized() {
        User user = new User("john", "pass", "user");

        controller.loan(user, "pass", "A2", 200);

        assertEquals(700, repo.findById("A2").balance);
    }
}
