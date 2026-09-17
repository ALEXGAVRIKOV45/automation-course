package auto;

import com.microsoft.playwright.*;
import org.junit.jupiter.api.*;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

public class FruitPageMockedTest {
    static Playwright playwright;
    static Browser browser;
    BrowserContext context;
    Page page;

    @BeforeAll
    static void setUp() {
        playwright = Playwright.create();
        browser = playwright.chromium().launch(new BrowserType.LaunchOptions().setHeadless(false));
    }


    @BeforeEach
    void newPage() {
        context = browser.newContext();
        page = context.newPage();
    }


    @Test
    @DisplayName("Страница получает мок-данные мгновенно и рисует только Tomato")
    void shouldRenderMockedFruit_fast() {
        page.route("**/api/v1/fruits", route ->
                route.fulfill(new Route.FulfillOptions()
                        .setStatus(200)
                        .setContentType("application/json")
                        .setBody("""
                                [{"name":"Tomato","id":1}]
                                """))
        );

        page.navigate("https://demo.playwright.dev/api-mocking/");
        page.waitForSelector("text=Tomato");

        assertThat(page.getByText("Tomato").isVisible()).isTrue();
        assertThat(page.getByText("Strawberry").isVisible()).isFalse();
    }

    @AfterEach
    void close() {
        context.close();
    }

    @AfterAll
    static void tearDown() {
        playwright.close();
    }
}
