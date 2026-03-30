

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class testDepositNegativeAmount {

    @Test
void testLoginNullUser() {
    AuthService auth = new AuthService();

    assertFalse(auth.login(null, "pass"));
}
}
