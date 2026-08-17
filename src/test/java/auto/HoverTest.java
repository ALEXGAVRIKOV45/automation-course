package auto;

import com.microsoft.playwright.*;
import org.junit.jupiter.api.*;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;

public class HoverTest {
    static Playwright playwright;
    static Browser browser;
    BrowserContext context;
    Page page;

    @BeforeEach
    void setUp() {
        playwright = Playwright.create();
        browser = playwright.chromium().launch(new BrowserType.LaunchOptions().setHeadless(false));
        context = browser.newContext();
        page = context.newPage();
    }

    @Test
    void testHoverProfiles() {
        page.navigate("https://the-internet.herokuapp.com/hovers");
        Locator figures = page.locator(".figure");

        int count = figures.count();
        for (int i = 0; i < count; i++) {
            Locator figure = figures.nth(i);
            figure.hover();

            // Проверяем, что появилась ссылка "View profile"
            Locator profileLink = figure.locator("text=View profile");
            assertThat(profileLink).isVisible();

            // Кликаем
            profileLink.click();

            // Проверяем, что URL соответствует /users/{id}
            Assertions.assertTrue(page.url().contains("/users/%s".formatted(i+1)));

            // Возвращаемся назад
            page.goBack();
        }
    }
    @AfterEach
    void tearDown() {
        context.close();
        browser.close();
        playwright.close();
    }

}

