package auto;

import com.microsoft.playwright.*;
import com.microsoft.playwright.options.AriaRole;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;

public class DynamicControlsTest {
    Playwright playwright;
    Browser browser;
    BrowserContext context;
    Page page;

    @BeforeEach
    void setUp() {
        playwright = Playwright.create();
        browser = playwright.chromium().launch(new BrowserType.LaunchOptions().setHeadless(false));
        context = browser.newContext();
        page = context.newPage();
    }

    @Test
    void testDynamicCheckbox() {
        page.navigate("https://the-internet.herokuapp.com/dynamic_controls");
        Locator checkbox = page.locator("[type='checkbox']");
        Locator buttonRemove = page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("Remove"));
        Locator textItsGone = page.getByText("It's gone!");
        Locator buttonAdd = page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("Add"));

        // 1. Находим чекбокс с атрибутом type="checkbox".
        assertThat(checkbox).isVisible();

        //2. Кликаем на кнопку "Remove".
        buttonRemove.click();

        //3. Ожидаем исчезновения чекбокса.
        page.waitForCondition(checkbox::isHidden);

        // 4. Проверяем, что появляется текст "It's gone!".
        assertThat(textItsGone).isVisible();

        // 5. Кликает на кнопку "Add".
        buttonAdd.click();

        // 6. Проверяем, что чекбокс снова отображается.
        page.waitForCondition(checkbox::isVisible);
    }

    @AfterEach
    void tearDown() {
        page.close();
        context.close();
        browser.close();
        playwright.close();
    }
}
