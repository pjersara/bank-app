import org.junit.jupiter.api.*;
import java.util.*;
import static org.junit.jupiter.api.Assertions.*;

class TransactionServiceTest {

    private AccountRepository repo;
    private TransactionService txService;

    @BeforeEach
    void setup() {
        repo = new AccountRepository();
        repo.save(new Account("A1", 1000));
        repo.save(new Account("A2", 500));
        repo.save(new Account("A3", 100));
        txService = new TransactionService(new AccountService(repo));
    }

    @Test
    void testTransferSuccess() {
        txService.transfer("A1", "A2", 200);
        assertEquals(800, repo.findById("A1").balance);
        assertEquals(700, repo.findById("A2").balance);
    }

    @Test
    void testBatchTransfer() {
        txService.batchTransfer(
                Arrays.asList("A1", "A2"),
                Arrays.asList("A2", "A3"),
                Arrays.asList(100.0, 50.0)
        );

        assertEquals(900, repo.findById("A1").balance);
        assertEquals(550, repo.findById("A2").balance);
        assertEquals(150, repo.findById("A3").balance);
    }
}
