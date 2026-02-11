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
    static Browser browser;
    public BrowserContext context;
    public Page page;
    static BrowserType browserType;
    protected static Properties config;

    @BeforeAll
    static void setupConfig() {
        playwright = Playwright.create();
        config = ConfigLoader.load();  // Загружаем конфиг 1 раз

        // Выбор браузера на основе параметра
        browserType = switch (config.getProperty("browser")) {
            case "firefox" -> playwright.firefox();
            case "webkit" -> playwright.webkit();
            default -> playwright.chromium();  // Значение по умолчанию
        };
    }


    @BeforeEach
    void setUp() {
        browser = browserType.launch(new BrowserType.LaunchOptions()
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