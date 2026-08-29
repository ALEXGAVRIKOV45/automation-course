package tests;

import com.microsoft.playwright.*;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import pages.DragDropPage;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class DragDropTest {
    Playwright playwright;
    Browser browser;
    BrowserContext context;
    Page page;

    @BeforeEach
    void setup() {
        playwright = Playwright.create();
        browser = playwright.chromium().launch(new BrowserType.LaunchOptions().setHeadless(false));
        context = browser.newContext();
        page = context.newPage();
    }

    @Test
    public void testDragAndDrop() {
        DragDropPage dragDropPage = new DragDropPage(page);
        dragDropPage.navigateTo("https://the-internet.herokuapp.com/drag_and_drop");
        dragDropPage.dragDropArea().dragAToB();
        assertEquals("A", dragDropPage.dragDropArea().getTextB());
    }

    @AfterEach
    void tearDown() {
        context.close();
        browser.close();
        playwright.close();
    }
}
