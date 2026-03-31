import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;

class LoanServiceTest {

    @Test
    void testLoanRequest() {
        AccountRepository repo = new AccountRepository();
        repo.save(new Account("A1", 100));

        LoanService loanService =
                new LoanService(new AccountService(repo));

        loanService.requestLoan("A1", 500);

        assertEquals(600, repo.findById("A1").balance);
    }
}
