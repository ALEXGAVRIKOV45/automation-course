package util;

import com.microsoft.playwright.Page;
import org.junit.jupiter.api.extension.*;

import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class CustomReportExtension implements TestWatcher, BeforeEachCallback, AfterTestExecutionCallback {
    private static final List<TestResult> results = new ArrayList<>();
    private long startTime;
    private Page page;
    String screenshotPath;

    public static List<TestResult> getResults() {
        return CustomReportExtension.results;
    }


    @Override
    public void beforeEach(ExtensionContext context) {
        startTime = System.currentTimeMillis();
    }

    @Override
    public void testSuccessful(ExtensionContext context) {
        results.add(new TestResult(
                context.getDisplayName(),
                "Passed",
                System.currentTimeMillis() - startTime,
                null,
                null
        ));
    }

    @Override
    public void testFailed(ExtensionContext context, Throwable cause) {
        System.out.println(results);
        results.add(new TestResult(
                context.getDisplayName(),
                "Failed",
                System.currentTimeMillis() - startTime,
                cause.getMessage(),
                screenshotPath
        ));
    }


    @Override
    public void afterTestExecution(ExtensionContext context) throws Exception {
        if (context.getExecutionException().isPresent() && page != null) {
            screenshotPath = "screenshots/" + context.getDisplayName() + ".png";
            page.screenshot(new Page.ScreenshotOptions().setPath(Paths.get(screenshotPath)));
        }
    }

    public void setPage(Page page) {
        this.page = page;
    }
}




