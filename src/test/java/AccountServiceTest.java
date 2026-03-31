import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;

class AccountServiceTest {

    private AccountRepository repo;
    private AccountService service;

    @BeforeEach
    void setup() {
        repo = new AccountRepository();
        repo.save(new Account("A1", 1000));
        repo.save(new Account("A2", 500));
        service = new AccountService(repo);
    }

    @Test
    void testDepositSuccess() {
        service.deposit("A1", 200);
        assertEquals(1200, repo.findById("A1").balance);
    }

    @Test
    void testWithdrawSuccess() {
        service.withdraw("A1", 300);
        assertEquals(700, repo.findById("A1").balance);
    }

    @Test
    void testWithdrawInsufficientFunds() {
        assertThrows(RuntimeException.class,
                () -> service.withdraw("A2", 1000));
    }

    @Test
    void testAccountNotFoundDeposit() {
        assertThrows(IllegalArgumentException.class,
                () -> service.deposit("X1", 100));
    }

    @Test
    void testAccountNotFoundWithdraw() {
        assertThrows(IllegalArgumentException.class,
                () -> service.withdraw("X1", 100));
    }
}
