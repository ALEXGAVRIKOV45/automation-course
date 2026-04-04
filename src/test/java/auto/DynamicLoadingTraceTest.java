package auto;

import com.microsoft.playwright.*;
import org.junit.jupiter.api.*;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
public class DynamicLoadingTraceTest {
    Playwright playwright;
    Browser browser;
    BrowserContext context;
    Page page;

    @Test
    void testDynamicLoadingWithTrace(TestInfo testInfo) throws IOException {

        playwright = Playwright.create();
        browser = playwright.chromium().launch();
        context = browser.newContext();

        // Настройка трассировки
        context.tracing().start(new Tracing.StartOptions()
                .setScreenshots(true)
                .setSnapshots(true)
                .setSources(true)
        );
        page = context.newPage();


        // Шаги теста
        page.navigate("https://the-internet.herokuapp.com/dynamic_loading/1");
        page.click("button"); // Клик на "Start"

        // Ожидание появления текста
        page.locator("#finish").waitFor();

        // Сохранение трассировки
        String testName = testInfo.getTestMethod().get().getName();
        Path tracesDir = Paths.get("traces");
        Files.createDirectories(tracesDir);

        Path tracePath = tracesDir.resolve(testName + ".zip");
        context.tracing().stop(new Tracing.StopOptions().setPath(tracePath));
        attachTrace(tracePath.getFileName().toString());
    }

    @AfterEach
    void tearDown() {
        context.close();
        browser.close();
        playwright.close();
    }

    private byte[] attachTrace(String name) throws IOException {
        return Files.readAllBytes(Paths.get("traces/" + name));
    }
}
