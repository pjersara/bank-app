import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class testTransferZeroAmount {

    @Test
    void test() {
        AccountRepository repo = new AccountRepository();
        AccountService accService = new AccountService(repo);
        TransactionService tx = new TransactionService(accService);

        repo.save(new Account("A1", 200));
        repo.save(new Account("A2", 100));

        tx.transfer("A1", "A2", 0);

        assertEquals(200, repo.findById("A1").balance);
        assertEquals(100, repo.findById("A2").balance);
    }
}
