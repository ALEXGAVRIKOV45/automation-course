package base;

import com.microsoft.playwright.*;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.RegisterExtension;
import util.CustomReportExtension;
import util.HtmlReportGenerator;

public class BaseCustomReportTest {
    protected static Playwright playwright;
    protected static Browser browser;
    protected Page page;

    @BeforeAll
    static void setup() {
    playwright = Playwright.create();
    browser = playwright.chromium().launch(new BrowserType.LaunchOptions().setHeadless(true));
    }

    // Вручную регистрируем расширение
    @RegisterExtension
    protected CustomReportExtension screenshotExtension = new CustomReportExtension();


    @BeforeEach
    void setUp() {
        page = browser.newPage();
        screenshotExtension.setPage(page);
    }

    @AfterEach
    void tearDown() {
        if (browser != null) browser.close();
        if (playwright != null) playwright.close();
    }

    @AfterAll
    static void teardown() {
        // Генерация отчета после всех тестов
        HtmlReportGenerator.generateReport(
                CustomReportExtension.getResults(),
                "test-report.html"
        );
    }

    public Page getPage() {
        return page;
    }
}
