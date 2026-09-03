package auto;

import components.TestContext;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import pages.DynamicControlsPage;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class DynamicControlsTestDI {
    private TestContext context;
    private DynamicControlsPage controlsPage;

    @BeforeEach
    public void setup() {
        context = new TestContext();
        controlsPage = new DynamicControlsPage(context.getPage());
        context.getPage().navigate("https://the-internet.herokuapp.com/dynamic_controls");
    }

    @Test
    public void testCheckboxRemoval() {
        assertTrue(controlsPage.isCheckboxVisible());
        controlsPage.clickRemoveButton();
        controlsPage.waitCheckboxIsHidden();
        assertFalse(controlsPage.isCheckboxVisible());
    }

    @AfterEach
    public void teardown() {
        context.getPage().close();
    }
}
