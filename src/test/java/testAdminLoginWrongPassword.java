import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class testAdminLoginWrongPassword {

    @Test
    void test() {
        AuthService auth = new AuthService();
        User admin = new User("admin", "admin123", "admin");

        assertFalse(auth.login(admin, "wrong"));
    }
}
