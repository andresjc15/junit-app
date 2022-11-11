package com.ajcp.junit.app.model;

import com.ajcp.junit.app.exception.InsufficientAmountException;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.condition.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvFileSource;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.math.BigDecimal;
import java.time.Duration;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assumptions.*;

// @TestInstance(TestInstance.Lifecycle.PER_CLASS)
class AccountTest {

    Account account;
    private TestInfo testInfo;
    private TestReporter testReporter;

    @BeforeEach
    void initTestMethod(TestInfo testInfo, TestReporter testReporter) {
        this.account = new Account("Andres", new BigDecimal("1000.12345"));
        this.testInfo = testInfo;
        this.testReporter = testReporter;

        System.out.println("Iniciando el metodo.");
        testReporter.publishEntry(" ejecutando: " + testInfo.getDisplayName() + " " + testInfo.getTestMethod().orElse(null).getName()
                + " con las etiquetas " + testInfo.getTags().toString());
    }

    @AfterEach
    void tearDown() {
        System.out.println("Finalizando metodo de prueba.");
    }

    @BeforeAll
    static void beforeAll() {
        System.out.println("Inicializando el test");
    }

    @AfterAll
    static void afterAll() {
        System.out.println("Finalizando el test");
    }

    @Tag("account")
    @Nested
    @DisplayName("Probando atributos de la cuenta corriente")
    class AccountTestBalanceName {

        @Test
        @DisplayName("Nombre")
        void testNameAccount() {

            System.out.println(testInfo.getTags());

            if (testInfo.getTags().contains("accounts")) {
                System.out.println("contiene la etiqueta cuenta");
            }
            // account.setPerson("Andres");

            String expected = "Andres";
            String real = account.getPerson();

            assertNotNull(real, "La cuenta no puede ser nula");
            assertEquals(expected, real, "El nombre de la cuenta no es el esperado".concat(expected));
            assertTrue(real.equals("Andres"));

        }

        @Test
        @DisplayName("Saldo")
        void testBalanceAccount() {
            assertEquals(1000.12345, account.getBalance().doubleValue());
            assertFalse(account.getBalance().compareTo(BigDecimal.ZERO) < 0);
            assertTrue(account.getBalance().compareTo(BigDecimal.ZERO) > 0);
        }

        @Test
        @DisplayName("Referencia")
        void testAccountReference() {
            account = new Account("Jhon Doe", new BigDecimal("8900.997"));
            Account account2 = new Account("Jhon Doe", new BigDecimal("8900.997"));

            assertEquals(account, account2);
        }

    }

    @Nested
    class AccountOperationTest {

        @Test
        void testDebitAccount() {
            account = new Account("Andres", new BigDecimal("1000.12345"));

            account.debit(new BigDecimal(100));
            assertNotNull(account.getBalance());
            assertEquals(900, account.getBalance().intValue());
            assertEquals("900.12345", account.getBalance().toPlainString());
        }

        @Test
        void testCreditAccount() {
            account = new Account("Andres", new BigDecimal("1000.12345"));

            account.credit(new BigDecimal(100));
            assertNotNull(account.getBalance());
            assertEquals(1100, account.getBalance().intValue());
            assertEquals("1100.12345", account.getBalance().toPlainString());
        }

    }


    @Test
    @Disabled
    void testInsufficientAmountExceptionAccount() {
        fail();
        account = new Account("Andres", new BigDecimal("1000.123456"));
        Exception exception = assertThrows(InsufficientAmountException.class, () -> {
            account.debit(new BigDecimal(1500));
        });
        String actual = exception.getMessage();
        String expected = "Insufficient amount";
        assertEquals(expected, actual);
    }

    @Test
    @Disabled
    void testTransferMoneyAccount() {
        Account account1 = new Account("Jhon Doe", new BigDecimal("2500"));
        Account account2 = new Account("Andres", new BigDecimal("1500.89"));
        Bank bank = new Bank();
        bank.setName("Banco del estado");
        bank.transfer(account2, account1, new BigDecimal(500));
        assertEquals("1000.89", account2.getBalance().toPlainString());
        assertEquals("3000", account1.getBalance().toPlainString());
    }

    @Test
    @DisplayName("Tests")
    void testRelationBankAccounts() {
        Account account1 = new Account("Jhon Doe", new BigDecimal("2500"));
        Account account2 = new Account("Andres", new BigDecimal("1500.89"));

        Bank bank = new Bank();
        bank.addAccount(account1);
        bank.addAccount(account2);

        bank.setName("Banco del estado");
        bank.transfer(account2, account1, new BigDecimal(500));

        assertAll(() -> assertEquals("1000.89", account2.getBalance().toPlainString()),
                () -> assertEquals("3000", account1.getBalance().toPlainString()),
                () -> assertEquals(2, bank.getAccounts().size()),
                () -> assertEquals("Banco del estado", account1.getBank().getName(),
                        () -> "Nombre del banco no es "),
                () -> assertEquals("Andres", bank.getAccounts().stream()
                            .filter(c -> c.getPerson().equals("Andres"))
                            .findFirst()
                            .get().getPerson()),
                () -> assertTrue(bank.getAccounts().stream()
                            .anyMatch(c -> c.getPerson().equals("Andres"))));
    }

    @Nested
    class OperativeSystemTest {

        @Test
        @EnabledOnOs(OS.WINDOWS)
        void testOnlyWindows() {

        }

        @Test
        @EnabledOnOs(OS.MAC)
        void testOnlyMac() {

        }

        @Test
        @DisabledOnOs(OS.WINDOWS)
        void testNoWindows() {

        }

    }

    @Nested
    class JavaVersionTest {

        @Test
        @EnabledOnJre(JRE.JAVA_8)
        void onlyJdk8() {

        }

        @Test
        @EnabledOnJre(JRE.JAVA_11)
        void onlyJdk11() {

        }

    }

    @Nested
    class SystemProperties {

        @Test
        void printProperties() {
            Properties properties = System.getProperties();
            properties.forEach((k, v) -> System.out.println(k + ":" + v));
        }

        @Test
        @EnabledIfSystemProperty(named = "java.version", matches = "15.0.1")
        void testJavaVersion() {

        }

        @Test
        @DisabledIfSystemProperty(named = "os.arch", matches = ".*32.*")
        void testSolo64() {

        }

        @Test
        @DisabledIfSystemProperty(named = "user.name", matches = "ajcp")
        void testUserName() {

        }

        @Test
        @DisabledIfSystemProperty(named = "ENV", matches = "dev")
        void testDev() {

        }

    }

    @Nested
    class VariablesEnvironmentTest {

        @Test
        void printEnvironmentVariables() {
            Map<String, String> env = System.getenv();
            env.forEach((k, v) -> System.out.println(k + " = " + v));
        }

        @Test
        @EnabledIfEnvironmentVariable(named = "JAVA_HOME", matches = ".*jdk-11.0.2.*")
        void testJavaHome() {

        }

        @Test
        @EnabledIfEnvironmentVariable(named = "NUMBER_OF_PROCESSORS", matches = "32")
        void testProcessors() {

        }

        @Test
        @EnabledIfEnvironmentVariable(named = "ENVIRONMENT", matches = "dev")
        void testEnv() {

        }

    }

    @DisplayName("Probando el saldo de la cuenta dev")
    @RepeatedTest(value = 3, name = "Repeticion numero {currentRepetition} de {totalRepetitions}")
    void testBalanceAccountDev(RepetitionInfo info) {

        if (info.getCurrentRepetition() == 3) {
            System.out.println("Repeticion " + info.getCurrentRepetition());
        }
        boolean esDev = "dev".equals(System.getProperty("ENV"));
        System.out.println(esDev);
        assumingThat(esDev, () -> {
            assertNotNull(account.getBalance());
            assertEquals(1000.12345, account.getBalance().doubleValue());
        });
        assertFalse(account.getBalance().compareTo(BigDecimal.ZERO) < 0);
        assertTrue(account.getBalance().compareTo(BigDecimal.ZERO) > 0);
    }

    @Tag("param")
    @Nested
    class ParameterizedsTest {

        @ParameterizedTest(name = "numero {index} ejecutando con valor {0} - {argumentsWithNames}")
        @ValueSource(strings = {"100", "200", "300", "500", "1000"})
        void testDebitAccountValueSource(String amount) {
            System.out.println(account.getBalance());
            account.debit(new BigDecimal(amount));
            System.out.println(account.getBalance());
            assertNotNull(account.getBalance());
            assertTrue(account.getBalance().compareTo(BigDecimal.ZERO) > 0);
        }

        @ParameterizedTest(name = "numero {index} ejecutando con valor {0} - {argumentsWithNames}")
        @CsvSource({"1,100", "2,200", "3,300", "4,500", "6,1000.12345"})
        void testDebitAccountCsvSource(String index, String amount) {
            System.out.println(index + " -> " + amount);
            System.out.println(account.getBalance());
            account.debit(new BigDecimal(amount));
            System.out.println(account.getBalance());
            assertNotNull(account.getBalance());
            assertTrue(account.getBalance().compareTo(BigDecimal.ZERO) > 0);
        }

        @org.junit.jupiter.params.ParameterizedTest(name = "numero {index} ejecutando con valor {0} - {argumentsWithNames}")
        @CsvSource({"200,100,Jhon,Andres", "250,200,Pepe,Pepe", "299,300,maria,Maria", "400,500,Pepa,pepa", "1000.12345,1000.12345,Luca,Luca"})
        void testDebitAccountCsvSource2(String balance, String amount, String expected, String actual) {
            System.out.println(balance + " -> " + balance);
            System.out.println(account.getBalance());
            account.setBalance(new BigDecimal(balance));
            account.debit(new BigDecimal(amount));
            account.setPerson(actual);
            System.out.println(account.getBalance());
            assertNotNull(account.getBalance());
            assertNotNull(account.getPerson());
            assertEquals(expected, actual);
            assertTrue(account.getBalance().compareTo(BigDecimal.ZERO) > 0);
        }

        @ParameterizedTest(name = "numero {index} ejecutando con valor {0} - {argumentsWithNames}")
        @CsvFileSource(resources = "/data.csv")
        void testDebitAccountCsvSourceFile(String amount) {
            account.debit(new BigDecimal(amount));
            System.out.println(account.getBalance());
            assertNotNull(account.getBalance());
            assertTrue(account.getBalance().compareTo(BigDecimal.ZERO) > 0);
        }

        @ParameterizedTest(name = "numero {index} ejecutando con valor {0} - {argumentsWithNames}")
        @MethodSource("amountList")
        void testDebitAccountMethodSource(String amount) {
            account.debit(new BigDecimal(amount));
            System.out.println(account.getBalance());
            assertNotNull(account.getBalance());
            assertTrue(account.getBalance().compareTo(BigDecimal.ZERO) > 0);
        }

        @ParameterizedTest(name = "numero {index} ejecutando con valor {0} - {argumentsWithNames}")
        @CsvFileSource(resources = "/data2.csv")
        void testDebitAccountCsvSourceFile2(String amount) {
            account.debit(new BigDecimal(amount));
            System.out.println(account.getBalance());
            assertNotNull(account.getBalance());
            assertTrue(account.getBalance().compareTo(BigDecimal.ZERO) > 0);
        }

    }

    static List<String> amountList() {
        return Arrays.asList("100", "200", "300", "500", "700", "1000.12345");
    }


    @Nested
    @Tag("timeout")
    class TimeoutTestExample {

        @Test
        @Timeout(5)
        void timeoutTest() throws InterruptedException {
            TimeUnit.SECONDS.sleep(2);
        }

        @Test
        @Timeout(value = 1000, unit = TimeUnit.MILLISECONDS)
        void timeoutTest2() throws InterruptedException {
            TimeUnit.SECONDS.sleep(500);
        }

        @Test
        void testTimeoutAssertions() {
            assertTimeout(Duration.ofSeconds(5), () -> {
                TimeUnit.MILLISECONDS.sleep(1500);
            });
        }
    }


}