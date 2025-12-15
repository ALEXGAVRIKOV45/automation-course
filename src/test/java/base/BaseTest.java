package base;

import com.microsoft.playwright.*;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;

import java.util.Collections;

public class BaseTest {
    Playwright playwright;
    Browser browser;
    public BrowserContext context;
    public Page page;


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