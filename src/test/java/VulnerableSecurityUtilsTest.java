import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class VulnerableSecurityUtilsTest {

    @Test
    void testWeakToken() {
        String token = VulnerableSecurityUtils.generateWeakToken("john");
        assertTrue(token.startsWith("john_"));
    }

    @Test
    void testWeakHash() {
        String hash = VulnerableSecurityUtils.weakHash("password");
        assertNotNull(hash);
        assertNotEquals("password", hash);
    }

    @Test
    void testSecretKey() {
        assertEquals("SUPER_SECRET_123",
                VulnerableSecurityUtils.getSecretKey());
    }
}
