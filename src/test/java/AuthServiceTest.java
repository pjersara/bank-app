import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class AuthServiceTest {

    @Test
    void testAdminLoginSuccess() {
        AuthService auth = new AuthService();
        User admin = new User("admin", "admin123", "admin");

        assertTrue(auth.login(admin, "admin123"));
    }

    @Test
    void testUserLoginFail() {
        AuthService auth = new AuthService();
        User user = new User("user", "pass", "user");

        assertFalse(auth.login(user, "wrong"));
    }
}
