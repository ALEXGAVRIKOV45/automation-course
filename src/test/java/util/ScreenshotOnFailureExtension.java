package util;

import com.microsoft.playwright.Page;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.TestWatcher;

import java.nio.file.Paths;

public class ScreenshotOnFailureExtension implements TestWatcher {
    @Override
    public void testFailed(ExtensionContext context, Throwable cause) {
        Object testInstance = context.getRequiredTestInstance();

        try {
            // Получаем страницу из тестового класса
            Page page = (Page) testInstance.getClass()
                    .getDeclaredField("page")
                    .get(testInstance);

            if (page != null) {
                String testName = context.getDisplayName();
                String timestamp = String.valueOf(System.currentTimeMillis());
                String screenshotPath = "screenshots/" +
                        testName + "_" + timestamp + ".png";

                page.screenshot(new Page.ScreenshotOptions()
                        .setPath(Paths.get(screenshotPath))
                        .setFullPage(true));

                System.out.println("Скриншот сохранен: " + screenshotPath);
            }
        } catch (Exception e) {
            System.err.println("Не удалось сделать скриншот: " + e.getMessage());
        }
    }
}
