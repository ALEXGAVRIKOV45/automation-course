package auto;

import com.microsoft.playwright.*;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;

import java.nio.file.Paths;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertTrue;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@Execution(ExecutionMode.CONCURRENT) // Включаем параллельное выполнение
public class ParallelTests {
    private Playwright playwright;
    private Browser browser;

    private BrowserContext context;
    private Page page;
    @BeforeAll
    void setup() {
        playwright = Playwright.create();
        browser = playwright.chromium().launch(new BrowserType.LaunchOptions()
                .setHeadless(true)
                .setArgs(List.of("--disable-dev-shm-usage")));
    }

    @BeforeEach
    void createContext(TestInfo testInfo){
        context = browser.newContext(new Browser.NewContextOptions()
                .setRecordVideoDir(Paths.get("videos/"))
                .setViewportSize(1280, 720));

        page = context.newPage();
        System.out.println("Запуск теста: "+ testInfo.getDisplayName() +
                " в потоке: " + Thread.currentThread().getId());
    }
    @Test
    @DisplayName("Тест страницы Login Page")
    void testLoginPage() {

        page.navigate("https://the-internet.herokuapp.com/login");
        assertTrue(page.isVisible("button[type='submit']"));

    }

    @Test
    @DisplayName("Тест страницы Add/Remove Elements")
    void testAddRemoveElements() {

        page.navigate("https://the-internet.herokuapp.com/add_remove_elements/");
        page.click("button:text('Add Element')");
        assertTrue(page.isVisible("button.added-manually"));

    }

    @AfterEach
    void closeContext(TestInfo testInfo){
        context.close();
        System.out.println("Завершен тест: "+ testInfo.getDisplayName() +
                " в потоке: " + Thread.currentThread().getId());
    }

    @AfterAll
    void teardown() {
        browser.close();
        playwright.close();
    }


}
