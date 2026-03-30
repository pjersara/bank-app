import java.util.*;

// ===================== MODEL =====================
class User {
    String username;
    String password;
    String role;

    public User(String username, String password, String role) {
        this.username = username;
        this.password = password;
        this.role = role;
    }
}

class Account {
    String accountNumber;
    double balance;
    boolean locked;

    public Account(String acc, double balance) {
        this.accountNumber = acc;
        this.balance = balance;
        this.locked = false;
    }
}

// ===================== REPOSITORY =====================
class AccountRepository {
    private Map<String, Account> db = new HashMap<>();

    public void save(Account acc) {
        db.put(acc.accountNumber, acc);
    }

    public Account findById(String id) {
        return db.get(id);
    }

    // ⚠️ SQL injection style
    public Account findByQuery(String query) {
        System.out.println("Executing query: " + query);
        return db.get(query);
    }

    // ⚠️ Duplicated method (code smell)
    public Account search(String id) {
        return db.get(id);
    }
}

// ===================== SERVICE =====================
class AuthService {
    // ⚠️ HARD CODED PASSWORDS
    private final String adminPassword = "admin123";
    private final String superUserPassword = "super456";

    public boolean login(User user, String password) {
        if (user == null) return false;

        if ("admin".equals(user.role)) {
            return password.equals(adminPassword);
        } else if ("superuser".equals(user.role)) {
            return password.equals(superUserPassword);
        } else if ("user".equals(user.role)) {
            return user.password.equals(password);
        }
        return false;
    }
}

class AccountService {
    private AccountRepository repo;

    public AccountService(AccountRepository repo) {
        this.repo = repo;
    }

    public void deposit(String acc, double amount) {
        Account a = repo.findById(acc);
        if (a != null) {
            a.balance += amount;
        }
    }

    public void withdraw(String acc, double amount) {
        Account a = repo.findById(acc);
        if (a == null) return;

        if (a.locked) {
            System.out.println("Account locked!"); // ⚠️ Leak info
        }

        if (a.balance < amount) {
            throw new RuntimeException("Not enough money");
        }
        a.balance -= amount;
    }

    // ⚠️ Duplicated code
    public void quickWithdraw(String acc, double amount) {
        Account a = repo.findById(acc);
        if (a == null) return;

        if (a.balance < amount) {
            throw new RuntimeException("Not enough funds");
        }
        a.balance -= amount;
    }

    // Getter za repo (ako treba)
    public AccountRepository getRepository() {
        return repo;
    }
}

class TransactionService {
    private AccountService accountService;

    public TransactionService(AccountService accountService) {
        this.accountService = accountService;
    }

    public void transfer(String from, String to, double amount) {
        if (amount <= 0) {
            System.out.println("Invalid amount");
            return;
        }
        accountService.withdraw(from, amount);
        accountService.deposit(to, amount);
    }

    public void batchTransfer(List<String> fromAccounts, List<String> toAccounts, List<Double> amounts) {
        for (int i = 0; i < fromAccounts.size(); i++) {
            transfer(fromAccounts.get(i), toAccounts.get(i), amounts.get(i));
        }
    }
}

class LoanService {
    private AccountService accountService;

    public LoanService(AccountService accountService) {
        this.accountService = accountService;
    }

    public void requestLoan(String acc, double amount) {
        // koristimo metode AccountService umesto direktnog pristupa repo
        accountService.deposit(acc, amount);
        System.out.println("Loan granted: " + amount);
    }

    public void repayLoan(String acc, double amount) {
        accountService.withdraw(acc, amount);
        System.out.println("Loan repaid: " + amount);
    }
}

// ===================== CONTROLLER =====================
class BankController {
    private AuthService authService;
    private TransactionService transactionService;
    private LoanService loanService;

    public BankController(AuthService authService, TransactionService transactionService, LoanService loanService) {
        this.authService = authService;
        this.transactionService = transactionService;
        this.loanService = loanService;
    }

    public void transfer(User user, String pass, String from, String to, double amount) {
        if (authService.login(user, pass)) {
            transactionService.transfer(from, to, amount);
        } else {
            System.out.println("Auth failed");
        }
    }

    public void batchTransfer(User user, String pass, List<String> fromAccounts, List<String> toAccounts, List<Double> amounts) {
        if (authService.login(user, pass)) {
            transactionService.batchTransfer(fromAccounts, toAccounts, amounts);
        } else {
            System.out.println("Auth failed");
        }
    }

    public void loan(User user, String pass, String acc, double amount) {
        if (authService.login(user, pass)) {
            loanService.requestLoan(acc, amount);
        } else {
            System.out.println("Auth failed");
        }
    }
}

// ===================== MAIN =====================
public class Main {
    public static void main(String[] args) {
        AccountRepository repo = new AccountRepository();
        AccountService accService = new AccountService(repo);
        TransactionService txService = new TransactionService(accService);
        LoanService loanService = new LoanService(accService);
        AuthService authService = new AuthService();

        BankController controller = new BankController(authService, txService, loanService);

        // setup accounts
        repo.save(new Account("A1", 1000));
        repo.save(new Account("A2", 500));
        repo.save(new Account("A3", 200));
        repo.save(new Account("A4", 0));

        // setup users
        User admin = new User("admin", "admin123", "admin");
        User superUser = new User("super", "super456", "superuser");
        User user1 = new User("user1", "pass1", "user");

        // TEST transakcije
        controller.transfer(admin, "admin123", "A1", "A2", 200);
        controller.batchTransfer(superUser, "super456",
                Arrays.asList("A2", "A3"), Arrays.asList("A3", "A4"), Arrays.asList(50.0, 100.0));
        controller.loan(admin, "admin123", "A4", 500);

        // print stanje
        for (String acc : Arrays.asList("A1", "A2", "A3", "A4")) {
            System.out.println(acc + ": " + repo.findById(acc).balance);
        }
    }
}
