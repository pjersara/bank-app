import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class TransactionServiceTest {

    @Test
    void testTransfer() {
        AccountRepository repo = new AccountRepository();
        AccountService accService = new AccountService(repo);
        TransactionService tx = new TransactionService(accService);

        repo.save(new Account("A1", 500));
        repo.save(new Account("A2", 100));

        tx.transfer("A1", "A2", 200);

        assertEquals(300, repo.findById("A1").balance);
        assertEquals(300, repo.findById("A2").balance);
    }
}
