import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class AuthServiceTest {

    private final AuthService authService = new AuthService();

    @Test
    void testLoginSuccess() {
        User user = new User("john", "pass123", "user");
        assertTrue(authService.login(user, "pass123"));
        assertNotNull(user.sessionToken);
    }

    @Test
    void testLoginWrongPassword() {
        User user = new User("john", "pass123", "user");
        assertFalse(authService.login(user, "wrong"));
    }

    @Test
    void testLoginNullUser() {
        assertFalse(authService.login(null, "pass"));
    }

    @Test
    void testLoginNullPassword() {
        User user = new User("john", "pass123", "user");
        assertFalse(authService.login(user, null));
    }

    @Test
    void testAdminAuthorization() {
        User admin = new User("admin", "123", "admin");
        assertTrue(authService.authorize(admin, "delete"));
    }

    @Test
    void testUserLoanAuthorization() {
        User user = new User("user", "123", "user");
        assertTrue(authService.authorize(user, "loan"));
    }
}
