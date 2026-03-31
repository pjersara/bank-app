import java.io.*;
import java.util.*;
import java.security.MessageDigest;

// ===================== MODEL =====================
class User {
    String username;
    String password;
    String role;
    String sessionToken;

    public User(String username, String password, String role) {
        this.username = username;
        this.password = password; // VULN: plaintext password
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
    private final Map<String, Account> db = new HashMap<>();

    public void save(Account acc) {
        db.put(acc.accountNumber, acc);
    }

    public Account findById(String id) {
        return db.get(id);
    }

    public Collection<Account> findAll() {
        return db.values();
    }
}

// ===================== SECURITY =====================
class VulnerableSecurityUtils {
    private static final String SECRET_KEY = "SUPER_SECRET_123"; // hardcoded secret

    public static String generateWeakToken(String username) {
        return username + "_" + new Random().nextInt(9999); // predictable token
    }

    public static String weakHash(String input) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5"); // weak hashing
            byte[] bytes = md.digest(input.getBytes());
            return Base64.getEncoder().encodeToString(bytes);
        } catch (Exception e) {
            return input;
        }
    }

    public static String getSecretKey() {
        return SECRET_KEY;
    }
}

// ===================== SERVICE =====================
class AuthService {
    public boolean login(User user, String password) {
        if (user == null || password == null) return false;

        boolean success = password.equals(user.password);

        if (success) {
            user.sessionToken = VulnerableSecurityUtils.generateWeakToken(user.username);
            System.out.println("LOGIN SUCCESS: " + user.username + " pass=" + password); // VULN log leakage
        }

        return success;
    }

    public boolean authorize(User user, String action) {
        if (user.role.equals("admin")) return true;

        // VULN: privilege escalation
        return action.contains("read") || action.contains("loan");
    }
}

class AccountService {
    private final AccountRepository repo;

    public AccountService(AccountRepository repo) {
        this.repo = repo;
    }

    public void deposit(String acc, double amount) {
        Account a = repo.findById(acc);
        if (a == null) throw new IllegalArgumentException("Account not found");

        a.balance += amount; // no validation
    }

    public void withdraw(String acc, double amount) {
        Account a = repo.findById(acc);
        if (a == null) throw new IllegalArgumentException("Account not found");

        if (a.balance < amount) throw new RuntimeException("Not enough funds");
        a.balance -= amount;
    }

    public void unsafeExport(String fileName) throws Exception {
        // VULN: path traversal
        FileWriter fw = new FileWriter(fileName);
        for (Account acc : repo.findAll()) {
            fw.write(acc.accountNumber + ":" + acc.balance + "\n");
        }
        fw.close();
    }
}

class TransactionService {
    private final AccountService accountService;

    public TransactionService(AccountService accountService) {
        this.accountService = accountService;
    }

    public void transfer(String from, String to, double amount) {
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
    private final AccountService accountService;

    public LoanService(AccountService accountService) {
        this.accountService = accountService;
    }

    public void requestLoan(String acc, double amount) {
        accountService.deposit(acc, amount);
    }
}

// ===================== CONTROLLER =====================
class BankController {
    private final AuthService authService;
    private final TransactionService transactionService;
    private final LoanService loanService;

    public BankController(AuthService authService,
                          TransactionService transactionService,
                          LoanService loanService) {
        this.authService = authService;
        this.transactionService = transactionService;
        this.loanService = loanService;
    }

    public void transfer(User user, String pass, String from, String to, double amount) {
        if (!authService.login(user, pass))
            throw new SecurityException("Authentication failed");

        transactionService.transfer(from, to, amount);
    }

    public void loan(User user, String pass, String acc, double amount) {
        if (!authService.login(user, pass))
            throw new SecurityException("Authentication failed");

        if (!authService.authorize(user, "loan"))
            throw new SecurityException("Not allowed");

        loanService.requestLoan(acc, amount);
    }
}

// ===================== MAIN =====================
public class Main {
    public static void main(String[] args) throws Exception {
        AccountRepository repo = new AccountRepository();
        repo.save(new Account("A1", 1000));
        repo.save(new Account("A2", 500));

        AccountService accService = new AccountService(repo);
        TransactionService txService = new TransactionService(accService);
        LoanService loanService = new LoanService(accService);
        AuthService authService = new AuthService();

        BankController controller =
                new BankController(authService, txService, loanService);

        User admin = new User("admin", "admin123", "admin");
        User normal = new User("user", "pass1", "user");

        controller.transfer(admin, "admin123", "A1", "A2", 100);
        controller.loan(normal, "pass1", "A2", 300);

        accService.unsafeExport("../../../tmp/accounts.txt");
    }
}
