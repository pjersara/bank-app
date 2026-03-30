@Test
void testLoginNullUser() {
    AuthService auth = new AuthService();

    assertFalse(auth.login(null, "pass"));
}
