package auto;

import com.microsoft.playwright.*;
import com.microsoft.playwright.assertions.LocatorAssertions;
import org.junit.jupiter.api.*;

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


    @BeforeEach
    void setup() {
        playwright = Playwright.create();
        browser = playwright.chromium().launch(new BrowserType.LaunchOptions()
                .setSlowMo(1000)
                .setHeadless(false));
        context = browser.newContext(new Browser.NewContextOptions()
                .setRecordVideoDir(Paths.get("videos/")));
        page = context.newPage();
        page.setViewportSize(1920, 1080);
    }

    @Test
    void testCartActions() {
        page.navigate("https://www.saucedemo.com/");
        assertThat(page.locator("input[id='login-button']")).isVisible(new LocatorAssertions.IsVisibleOptions().setTimeout(3000));

        Locator userName = page.locator("input[id='user-name']");
        Locator passWord = page.locator("input[id='password']");
        userName.fill("standard_user");
        passWord.fill("secret_sauce");

        page.locator("input[id='login-button']").click();
        assertThat(page.locator("a[data-test='shopping-cart-link']")).isVisible(new LocatorAssertions.IsVisibleOptions().setTimeout(3000));


        String cart = "a[data-test='shopping-cart-link']";
        // Добавление товара 1
        page.click("button[id='add-to-cart-sauce-labs-bolt-t-shirt']");
        page.locator(cart).screenshot(new Locator.ScreenshotOptions()
                .setPath(Paths.get("screenshorts/" + getTimestampPath("cart_after_add.png"))));

        page.waitForTimeout(2000);

        // Добавление товара 2
        page.click("button[id='add-to-cart-sauce-labs-bike-light']");
        page.locator(cart).screenshot(new Locator.ScreenshotOptions()
                .setPath(Paths.get("screenshorts/" + getTimestampPath("cart_after_add.png"))));

        // Удаление товара 1
        page.click("button[id='remove-sauce-labs-bolt-t-shirt']");
        page.locator(cart).screenshot(new Locator.ScreenshotOptions()
                .setPath(Paths.get("screenshorts/" + getTimestampPath("cart_after_remove.png"))));

        page.waitForTimeout(2000);

        // Удаление товара 2
        page.click("button[id='remove-sauce-labs-bike-light']");
        page.locator(cart).screenshot(new Locator.ScreenshotOptions()
                .setPath(Paths.get("screenshorts/" + getTimestampPath("cart_after_remove.png"))));

    }

    private Path getTimestampPath(String filename) {
        LocalDateTime now = LocalDateTime.now();
        return Paths.get(now.format(DateTimeFormatter.ofPattern("yyyy_MM_dd_HH_mm_ss")) + filename);
    }

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

