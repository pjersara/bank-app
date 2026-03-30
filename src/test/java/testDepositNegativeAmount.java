@Test
void testDepositNegativeAmount() {
    AccountRepository repo = new AccountRepository();
    AccountService service = new AccountService(repo);

    repo.save(new Account("A1", 100));

    service.deposit("A1", -50);

    assertEquals(50, repo.findById("A1").balance); // pokazuje bug
}
