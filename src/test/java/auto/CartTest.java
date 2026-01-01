package auto;

import com.microsoft.playwright.*;
import com.microsoft.playwright.assertions.LocatorAssertions;
import io.qameta.allure.Allure;

import io.qameta.allure.Attachment;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.junit.jupiter.api.extension.TestWatcher;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;

public class CartTest {
    Playwright playwright;
    Browser browser;
    private BrowserContext context;
    private Page page;
    private Path screenshotDir;


    @BeforeEach
    void setup() {
        playwright = Playwright.create();
        browser = playwright.chromium().launch(new BrowserType.LaunchOptions()
                .setSlowMo(1000)
                .setHeadless(false));
        screenshotDir = Paths.get("screenshorts/");
        context = browser.newContext(new Browser.NewContextOptions()
                .setRecordVideoDir(Paths.get("videos/")));

        page = context.newPage();
        page.setViewportSize(1920, 1080);
    }

    @RegisterExtension
    TestWatcher watcher = new TestWatcher() {
        @Override
        public void testFailed(ExtensionContext extensionContext, Throwable cause) {

            try {
                if (page != null && !page.isClosed()) {
                    String testName = extensionContext.getDisplayName();
                    Path screenshotPath = screenshotDir.resolve(getTimestampPath(testName) + "_test_FAIL.png");

                    byte[] screenshot = page.screenshot(new Page.ScreenshotOptions()
                            .setPath(screenshotPath)
                            .setFullPage(true));
                    saveScreenshotToAllure(screenshot, testName);
                    System.out.println("Скриншот при падении сохранен: " + screenshotPath);
                }

            } catch (Exception e) {
                System.err.println("Ошибка при создании скриншота: " + e.getMessage());
            }
        }
    };

    @Attachment(value = "Скриншот при падении: {name}", type = "image/png")
    private byte[] saveScreenshotToAllure(byte[] screenshot, String name) {
        return screenshot;
    }

    @Test
    @DisplayName("Test add,del cart")
    void testCartActions() {
        Allure.step("1. Открытие сайта", () -> {
            page.navigate("https://www.saucedemo.com/");
            assertThat(page.locator("input[id='login-button']")).isVisible(new LocatorAssertions.IsVisibleOptions().setTimeout(3000));
        });


        Allure.step("2. Заполняем учетные данные", () -> {
            Locator userName = page.locator("input[id='user-name']");
            Locator passWord = page.locator("input[id='password']");
            userName.fill("standard_user");
            passWord.fill("secret_sauce");
        });

        Allure.step("3. Клик по кнопки входа", () -> {
            page.locator("input[id='login-button']").click();
            assertThat(page.locator("a[data-test='shopping-cart-link']")).isVisible(new LocatorAssertions.IsVisibleOptions().setTimeout(3000));
        });

        String cart = "a[data-test='shopping-cart-link']";
        Allure.step("4. Добавление товара 1", () -> {
            // Добавление товара 1
            page.click("button[id='add-to-cart-sauce-labs-bolt-t-shirt']");
            page.locator(cart).screenshot(new Locator.ScreenshotOptions()
                    .setPath(Paths.get("screenshorts/" + getTimestampPath("cart_after_add.png"))));
            page.waitForTimeout(2000);
        });

        Allure.step("5. Добавление товара 2", () -> {
            // Добавление товара 2
            page.click("button[id='add-to-cart-sauce-labs-bike-light']");
            page.locator(cart).screenshot(new Locator.ScreenshotOptions()
                    .setPath(Paths.get("screenshorts/" + getTimestampPath("cart_after_add.png"))));
        });
        Allure.step("6. Удаление товара 1", () -> {
            // Удаление товара 1
            page.click("button[id='remove-sauce-labs-bolt-t-shirt']");
            page.locator(cart).screenshot(new Locator.ScreenshotOptions()
                    .setPath(Paths.get("screenshorts/" + getTimestampPath("cart_after_remove.png"))));
        });
        page.waitForTimeout(2000);
        Allure.step("7. Удаление товара 2", () -> {
            // Удаление товара 2
            page.click("button[id='remove-sauce-labs-bike-light']");
            page.locator(cart).screenshot(new Locator.ScreenshotOptions()
                    .setPath(Paths.get("screenshorts/" + getTimestampPath("cart_after_remove.png"))));
        });
    }

    private Path getTimestampPath(String filename) {
        LocalDateTime now = LocalDateTime.now();
        return Paths.get(now.format(DateTimeFormatter.ofPattern("yyyy_MM_dd_HH_mm_ss")) + filename);
    }

//    @AfterEach
//    void attachScreenshotOnFailure(ExtensionContext extensionContext) {
//        // Проверяем наличие исключения
//        if (extensionContext.getExecutionException().isPresent()) {
//            byte[] screenshot = page.screenshot();
//            Allure.addAttachment(
//                    "Screenshot on Failure",
//                    "image/png",
//                    new ByteArrayInputStream(screenshot),
//                    ".png"
//            );
//        }
//        context.close();
//    }
    @AfterEach
    void teardown() {
        context.close();
        try {
            Path videoPath = page.video().path();
            Files.move(videoPath, Paths.get("videos/" + getTimestampPath("cart_video") + ".webm"));
        } catch (IOException e) {
            e.printStackTrace();
        }

        browser.close();
        playwright.close();
    }
}


