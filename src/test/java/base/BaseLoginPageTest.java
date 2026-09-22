package base;

import com.microsoft.playwright.*;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import steps.LoginPageStep;

public class BaseLoginPageTest {
    static Playwright playwright;
    static Browser browser;
    BrowserContext context;
    public Page page;
    public LoginPageStep step;


    @BeforeAll
    static void setup() {
        // Инициализация Playwright
        playwright = Playwright.create();
        browser = playwright.chromium().launch(new BrowserType.LaunchOptions().setHeadless(true));
    }

    @BeforeEach
    void newContext() {
        context = browser.newContext();
        page = context.newPage();
        step = new LoginPageStep(page);
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
