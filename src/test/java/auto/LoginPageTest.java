package auto;

import com.microsoft.playwright.*;
import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.assertTrue;


class LoginPageTest {
    static Playwright playwright;
    static Browser browser;
    static BrowserContext context;
    static Page page;



    @BeforeAll
    static void setup() {
        // Инициализация Playwright
        playwright = Playwright.create();
        browser = playwright.chromium().launch(new BrowserType.LaunchOptions().setHeadless(false));
    }

    @BeforeEach
    void newContext() {
        context = browser.newContext();
        page = context.newPage();
    }


    @Test
    @DisplayName("Проверка полей Username Password")
    void testLoginWithDbUser() {
        page.navigate("https://the-internet.herokuapp.com/login");

        // Выполнение логина Кирилица
        page.locator("#username").fill("Имяпользователя");
        page.locator("#password").fill("ПарольПользователя");
        page.locator("button[type='submit']").click();
        // Проверка не успешной авторизации
        assertTrue(page.getByText("Your username is invalid!").isVisible());

        // Выполнение логина Латиница
        page.locator("#username").fill("NameUser");
        page.locator("#password").fill("Password");
        page.locator("button[type='submit']").click();
        // Проверка не успешной авторизации
        assertTrue(page.getByText("Your username is invalid!").isVisible());

        // Выполнение логина Цифры
        page.locator("#username").fill("557799");
        page.locator("#password").fill("886644");
        page.locator("button[type='submit']").click();
        // Проверка не успешной авторизации
        assertTrue(page.getByText("Your username is invalid!").isVisible());

        // Выполнение логина Спецсимволы
        page.locator("#username").fill("!№;%:?*");
        page.locator("#password").fill("<>|_+)(");
        page.locator("button[type='submit']").click();
        // Проверка не успешной авторизации
        assertTrue(page.getByText("Your username is invalid!").isVisible());

        // Выполнение логина CyrLatNumSpecChar
        page.locator("#username").fill("ИмяName123&^%");
        page.locator("#password").fill("!#$456PassОсень");
        page.locator("button[type='submit']").click();
        // Проверка не успешной авторизации
        assertTrue(page.getByText("Your username is invalid!").isVisible());

        // Выполнение логина Valid password Invalid username
        page.locator("#username").fill("tomsmithh");
        page.locator("#password").fill("SuperSecretPassword!");
        page.locator("button[type='submit']").click();
        // Проверка не успешной авторизации
        assertTrue(page.getByText("Your username is invalid!").isVisible());

        // Выполнение логина Valid username Invalid password
        page.locator("#username").fill("tomsmith");
        page.locator("#password").fill("PasswordSuperSecret!");
        page.locator("button[type='submit']").click();
        // Проверка не успешной авторизации
        assertTrue(page.getByText("Your password is invalid!").isVisible());

        // Выполнение логина Valid username password
        page.locator("#username").fill("tomsmith");
        page.locator("#password").fill("SuperSecretPassword!");
        page.locator("button[type='submit']").click();
        // Проверка успешной авторизации
        assertTrue(page.locator(".flash.success").isVisible());
        assertTrue(page.url().endsWith("/secure"));
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
