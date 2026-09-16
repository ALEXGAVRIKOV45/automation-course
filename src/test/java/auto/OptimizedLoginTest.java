package auto;

import com.microsoft.playwright.*;
import com.microsoft.playwright.options.Cookie;
import org.junit.jupiter.api.*;

import java.util.List;

class OptimizedLoginTest {
    static Playwright playwright;
    static Browser browser;
    static BrowserContext context;
    static Page page;
    static List<Cookie> authCookies;

    @BeforeAll
    static void setUpClass() {
        playwright = Playwright.create();
        browser = playwright.chromium().launch(new BrowserType.LaunchOptions().setHeadless(false));
        authCookies = performLogin(browser);
    }

    @BeforeEach
    void setUp() {
        // Создаём новый контекст и добавляем сохранённые cookies для каждого теста
        context = browser.newContext();
        context.addCookies(authCookies);
        page = context.newPage();
    }

    @Test
    void testSecureArea() {
        page.navigate("https://the-internet.herokuapp.com/secure");
        // Проверяем, что пользователь аутентифицирован
        Assertions.assertTrue(page.locator("h2").textContent().contains("Secure Area"));
    }

    private static List<Cookie> performLogin(Browser browser) {
        BrowserContext contextTemp = browser.newContext();
        Page pageTemp = contextTemp.newPage();
        pageTemp.navigate("https://the-internet.herokuapp.com/login");
        pageTemp.locator("#username").fill("tomsmith");
        pageTemp.locator("#password").fill("SuperSecretPassword!");
        pageTemp.locator("button[type='submit']").click();
        List<Cookie> pageCookies = contextTemp.cookies();
        pageTemp.close();
        contextTemp.close();
        return pageCookies;
    }

    @AfterEach
    void tearDown() {
        if (page != null) page.close();
        if (context != null) context.close();
    }

    @AfterAll
    static void tearDownClass() {
        if (browser != null) browser.close();
        if (playwright != null) playwright.close();
    }
}
