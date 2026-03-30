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

    public Account(String acc, double balance) {
        this.accountNumber = acc;
        this.balance = balance;
    }
}

// ===================== REPOSITORY =====================
class AccountRepository {
    Map<String, Account> db = new HashMap<>();

    public void save(Account acc) {
        db.put(acc.accountNumber, acc);
    }

    public Account findById(String id) {
        return db.get(id);
    }

    // ⚠️ SQL injection style (simulacija)
    public Account findByQuery(String query) {
        System.out.println("Executing query: " + query);
        return db.get(query); // loše
    }
}

// ===================== SERVICE =====================
class AuthService {

    // ⚠️ HARD CODED PASSWORD
    private final String adminPassword = "admin123";

    public boolean login(User user, String password) {
        if (user == null) return false;

        // ⚠️ LOŠA LOGIKA
        if (user.role.equals("admin")) {
            return password.equals(adminPassword);
        }
        return user.password.equals(password);
    }
}

class AccountService {
    private AccountRepository repo;

    public AccountService(AccountRepository repo) {
        this.repo = repo;
    }

    public void deposit(String acc, double amount) {
        Account a = repo.findById(acc);

        // ⚠️ NEMA VALIDACIJE
        a.balance += amount;
    }

    public void withdraw(String acc, double amount) {
        Account a = repo.findById(acc);

        if (a.balance < amount) {
            throw new RuntimeException("Not enough money"); // ⚠️ leak info
        }
        a.balance -= amount;
    }
}

class TransactionService {
    private AccountService accountService;

    public TransactionService(AccountService accountService) {
        this.accountService = accountService;
    }

    public void transfer(String from, String to, double amount) {

        // ⚠️ kompleksna logika (CC raste)
        if (amount > 0) {
            accountService.withdraw(from, amount);
            accountService.deposit(to, amount);
        } else {
            System.out.println("Invalid amount");
        }
    }
}

// ===================== CONTROLLER =====================
class BankController {
    private AuthService authService;
    private TransactionService transactionService;

    public BankController(AuthService authService, TransactionService transactionService) {
        this.authService = authService;
        this.transactionService = transactionService;
    }

    public void transfer(User user, String pass, String from, String to, double amount) {

        if (authService.login(user, pass)) {
            transactionService.transfer(from, to, amount);
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
        AuthService authService = new AuthService();

        BankController controller = new BankController(authService, txService);

        // setup
        repo.save(new Account("A1", 1000));
        repo.save(new Account("A2", 500));

        User admin = new User("admin", "admin123", "admin");

        // TEST
        controller.transfer(admin, "admin123", "A1", "A2", 200);

        System.out.println("A1: " + repo.findById("A1").balance);
        System.out.println("A2: " + repo.findById("A2").balance);
    }
}
