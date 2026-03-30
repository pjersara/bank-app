import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class testDepositZeroAmount {

    @Test
    void test() {
        AccountRepository repo = new AccountRepository();
        AccountService service = new AccountService(repo);

        repo.save(new Account("A1", 100));

        service.deposit("A1", 0);

        assertEquals(100, repo.findById("A1").balance);
    }
}
