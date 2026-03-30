import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class AccountServiceTest {

    @Test
    void testDeposit() {
        AccountRepository repo = new AccountRepository();
        AccountService service = new AccountService(repo);

        repo.save(new Account("A1", 100));

        service.deposit("A1", 50);

        assertEquals(150, repo.findById("A1").balance);
    }

    @Test
    void testWithdraw() {
        AccountRepository repo = new AccountRepository();
        AccountService service = new AccountService(repo);

        repo.save(new Account("A1", 200));

        service.withdraw("A1", 100);

        assertEquals(100, repo.findById("A1").balance);
    }
}
