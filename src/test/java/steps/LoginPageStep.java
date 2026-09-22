package steps;

import base.BaseLoginPageTest;
import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import pages.LoginPage;

import static org.junit.jupiter.api.Assertions.assertTrue;


public class LoginPageStep extends BaseLoginPageTest {
    protected final LoginPage loginPage;

    public LoginPageStep(Page page) {
        this.page = page;
        loginPage = new LoginPage(page);
    }

    public void fillLoginFields(String username, String password) {
        Locator usernameField = loginPage.getUserNameField();
        Locator passwordField = loginPage.getPasswordField();
        usernameField.fill(username);
        passwordField.fill(password);
    }

    public void pressSubmitButton() {
        loginPage.getSubmitButton().click();
    }

    public void checkTextVisible(String text) {
        assertTrue(page.getByText(text).isVisible());
    }

}
