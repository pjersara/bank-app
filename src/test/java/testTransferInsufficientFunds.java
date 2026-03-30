import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class testTransferInsufficientFunds {

    @Test
    void test() {
        AccountRepository repo = new AccountRepository();
        AccountService accService = new AccountService(repo);
        TransactionService tx = new TransactionService(accService);

        repo.save(new Account("A1", 50));
        repo.save(new Account("A2", 100));

        assertThrows(RuntimeException.class, () -> {
            tx.transfer("A1", "A2", 200);
        });
    }
}
