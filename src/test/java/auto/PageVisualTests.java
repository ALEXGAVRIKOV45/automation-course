package auto;

import base.BaseTest;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.assertions.LocatorAssertions;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import ru.yandex.qatools.ashot.comparison.ImageDiff;
import ru.yandex.qatools.ashot.comparison.ImageDiffer;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;

public class PageVisualTests extends BaseTest {

    @Test
    void testHomePageVisual() throws IOException {
        page.navigate("https://the-internet.herokuapp.com");
        assertThat(page.locator("//h1[text()='Welcome to the-internet']")).isVisible(new LocatorAssertions.IsVisibleOptions().setTimeout(3000));
        Path actualPath = Paths.get("actual.png");
        page.screenshot(new Page.ScreenshotOptions()
                .setPath(actualPath));
//        Скриншот страницы
        Path refPath = Paths.get("expected.png");

//        Скриншот full страницы. Если сравнивать с ней то будет создан файл diff.png
//        Path refPath = Paths.get("expected_full_page.png");

        long mismatch = Files.mismatch(actualPath, refPath);
        if (!(mismatch == -1L)) {
            System.out.println("есть разница");
            Path diffPath = Paths.get("diff.png");
            BufferedImage expectedImage = ImageIO.read(refPath.toFile());
            BufferedImage actualImage = ImageIO.read(actualPath.toFile());

            ImageDiffer differ = new ImageDiffer();
            ImageDiff diff = differ.makeDiff(expectedImage, actualImage);

            if (diff.hasDiff()) {
                BufferedImage diffImage = diff.getMarkedImage();
                ImageIO.write(diffImage, "png", diffPath.toFile());
                System.out.println("Diff сохранен: " + diffPath);

                System.out.println("Размер diff: " + diff.getDiffSize());
                System.out.println("Diff регионов: " + diff.getDiffSize());
            }
        }
    }
}
