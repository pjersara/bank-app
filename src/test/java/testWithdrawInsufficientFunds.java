import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class testWithdrawInsufficientFunds {

    @Test
    void test() {
        AccountRepository repo = new AccountRepository();
        AccountService service = new AccountService(repo);

        repo.save(new Account("A1", 50));

        assertThrows(RuntimeException.class, () -> {
            service.withdraw("A1", 100);
        });
    }
}
