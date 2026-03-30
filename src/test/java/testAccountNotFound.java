import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class testAccountNotFound {

    @Test
    void test() {
        AccountRepository repo = new AccountRepository();
        AccountService service = new AccountService(repo);

        assertThrows(NullPointerException.class, () -> {
            service.deposit("UNKNOWN", 100);
        });
    }
}
