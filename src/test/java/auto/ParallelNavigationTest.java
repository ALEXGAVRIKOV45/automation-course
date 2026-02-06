package auto;

import com.microsoft.playwright.*;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.nio.file.Paths;
import java.util.List;
import java.util.regex.Pattern;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;


@Execution(ExecutionMode.CONCURRENT) // Включаем параллельное выполнение
public class ParallelNavigationTest {
    static Playwright playwright;
    static Browser browser;
    static BrowserContext context;
    static Page page;

    @BeforeAll
    static void setup() {
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
    @ParameterizedTest
    @ValueSource(strings = {"/login", "/dropdown", "/javascript_alerts"})
    void testPageLoad(String path) {
        page.navigate("https://the-internet.herokuapp.com" + path);
        // Проверяем, что заголовок страницы существует (не пустой)
        assertThat(page).hasTitle(Pattern.compile(".*"));
        context.close();
    }

    @AfterEach
    void closeContext(TestInfo testInfo){

        try {
            context.close();
        }
        finally {
            System.out.println("Завершен тест: "+ testInfo.getDisplayName() +
                    " в потоке: " + Thread.currentThread().getId());
        }

    }
    @AfterAll
    static void tearDown() {
        browser.close();
        playwright.close();
    }
}
