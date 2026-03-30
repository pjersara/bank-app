import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class testUserLoginSuccess {

    @Test
    void test() {
        AuthService auth = new AuthService();
        User user = new User("user", "pass", "user");

        assertTrue(auth.login(user, "pass"));
    }
}
