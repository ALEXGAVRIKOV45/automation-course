package auto;

import com.microsoft.playwright.*;
import com.microsoft.playwright.assertions.LocatorAssertions;
import io.qameta.allure.*;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.*;

import java.io.ByteArrayInputStream;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;

@Epic("Веб-интерфейс тестов")
@Feature("Операции с чекбоксами")
public class CheckboxTest {
    private Playwright playwright;
    private Browser browser;
    private BrowserContext context;
    private Page page;
    private Path screenshotDir;


    String textScreen = "//h3[text()='Checkboxes']";
    String checkBox1 = "//form[@id='checkboxes']/input[1]";
    String checkBox2 = "//form[@id='checkboxes']/input[2]";



    @BeforeEach
    @Step("Инициализация браузера и контекста")
    void setUp() {
        playwright = Playwright.create();
        browser = playwright.chromium().launch(new BrowserType.LaunchOptions().setHeadless(false));
        screenshotDir = Paths.get("screenshorts/");
        context = browser.newContext();
        page = context.newPage();

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
    @Test
    @Story("Проверка работы чекбоксов")
    @DisplayName("Тестирование выбора/снятия чекбоксов")
    @Severity(SeverityLevel.CRITICAL)
    void testCheckboxes() {

        navigateToCheckboxesPage();
        verifyInitialState();
        toggleCheckboxes();
        verifyToggledState();
    }

    @Step("Переход на страницу /checkboxes")
    private void navigateToCheckboxesPage() {
        page.navigate("https://the-internet.herokuapp.com/checkboxes");
        assertThat(page.locator(textScreen)).isVisible(new LocatorAssertions.IsVisibleOptions().setTimeout(3000));
    }

    @Step("Проверка начального состояния чекбоксов")
    private void verifyInitialState() {
        assertThat(page.locator(checkBox1)).isVisible(new LocatorAssertions.IsVisibleOptions().setTimeout(6000));
        assertThat(page.locator(checkBox1)).not().isChecked();

        assertThat(page.locator(checkBox2)).isVisible(new LocatorAssertions.IsVisibleOptions().setTimeout(6000));
        assertThat(page.locator(checkBox2)).isChecked();
        Allure.addAttachment("Скриншот главной страницы",new ByteArrayInputStream(page.screenshot()));
    }

    @Step("Изменение состояния чекбоксов")
    private void toggleCheckboxes() {
        page.locator(checkBox1).click();
        page.locator(checkBox2).click();

    }

    @Step("Проверка что чекбоксы изменены")
    private void verifyToggledState() {
        assertThat(page.locator(checkBox1)).isChecked();
        assertThat(page.locator(checkBox2)).not().isChecked();

        Allure.addAttachment("Скриншот изменения состояния чекбоксов",new ByteArrayInputStream(page.screenshot()));
    }

    @AfterEach
    @Step("Закрытие ресурсов")
    void tearDown() {
        context.close();
        browser.close();
        playwright.close();
    }

    private Path getTimestampPath(String filename) {
        LocalDateTime now = LocalDateTime.now();
        return Paths.get(now.format(DateTimeFormatter.ofPattern("yyyy_MM_dd_HH_mm_ss")) + filename);
    }

    @Attachment(value = "Скриншот при падении: {name}", type = "image/png")
    private byte[] saveScreenshotToAllure(byte[] screenshot, String name) {
        return screenshot;
    }
}
