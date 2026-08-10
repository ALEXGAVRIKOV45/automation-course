package auto;

import com.microsoft.playwright.*;
import org.junit.jupiter.api.*;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class DynamicLoadingTraceTest {
    Playwright playwright;
    Browser browser;
    BrowserContext context;
    Page page;

    @Test
    void testDynamicLoadingWithTrace(TestInfo testInfo) throws IOException {

        playwright = Playwright.create();
        browser = playwright.chromium().launch(new BrowserType.LaunchOptions().setHeadless(false));
        context = browser.newContext();

        // Настройка трассировки
        context.tracing().start(new Tracing.StartOptions()
                .setScreenshots(true)
                .setSnapshots(true)
                .setSources(true)
        );
        page = context.newPage();

        // Переменная для хранения статуса ответа
        CompletableFuture<Integer> responseStatus = new CompletableFuture<>();


        // Перехват сетевых запросов
        page.onResponse(response -> {
            String url = response.url();
            System.out.println("Перехвачен запрос: " + url);


            // Проверяем, что запрос к /dynamic_loading
            if (url.contains("/dynamic_loading") && response.request().method().equals("GET")) {
                int status = response.status();
                System.out.println("Статус ответа для /dynamic_loading: " + status);
                responseStatus.complete(status);
            }
        });

        // Шаг 1: Переход на страницу
        page.navigate("https://the-internet.herokuapp.com/dynamic_loading/1");

        // Шаг 2: Клик на кнопку "Start"
        page.click("button");

        // Шаг 3: Ожидание появления текста "Hello World!"
        page.locator("#finish").waitFor(new Locator.WaitForOptions().setTimeout(30000));
        String finishText = page.locator("#finish").textContent();
        System.out.println("Появился текст: " + finishText);
        assertTrue(finishText.contains("Hello World!"), "Текст 'Hello World!' не найден");

        // Шаг 4: Проверка статуса ответа
        try {
            Integer status = responseStatus.get(10, TimeUnit.SECONDS);
            assertEquals(200, status, "Статус ответа не 200");
            System.out.println("Запрос к /dynamic_loading завершился успешно со статусом: " + status);
        } catch (Exception e) {
            throw new AssertionError("Не удалось перехватить запрос к /dynamic_loading", e);
        }

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
