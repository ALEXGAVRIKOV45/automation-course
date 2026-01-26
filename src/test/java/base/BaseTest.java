package base;

import com.microsoft.playwright.*;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;

import java.util.Collections;
import java.util.Properties;

public class BaseTest {
    static Playwright playwright;
    Browser browser;
    public BrowserContext context;
    public Page page;

    protected static Properties config;

    @BeforeAll
    static void setupConfig() {
        config = ConfigLoader.load();  // Загружаем конфиг 1 раз

        // Выбор браузера на основе параметра
        BrowserType browserType = switch (config.getProperty("browser")) {
            case "firefox" -> playwright.firefox();
            case "webkit" -> playwright.webkit();
            default -> playwright.chromium();  // Значение по умолчанию
        };
    }


    @BeforeEach
    void setUp() {
        playwright = Playwright.create();
        browser = playwright.chromium().launch(new BrowserType.LaunchOptions()
                .setHeadless(false));
        context = browser.newContext();
        page = context.newPage();
    }



    @AfterEach
    void tearDown() {
        browser.close();
        playwright.close();
    }
}