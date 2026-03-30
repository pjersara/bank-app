import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class testWithdrawNegativeAmount {

    @Test
    void test() {
        AccountRepository repo = new AccountRepository();
        AccountService service = new AccountService(repo);

        repo.save(new Account("A1", 100));

        service.withdraw("A1", -50);

        assertEquals(150, repo.findById("A1").balance); // pokazuje bug
    }
}
