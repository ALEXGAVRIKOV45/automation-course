package pages;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;

public class LoginPage {
    protected final Page page;
    protected final Locator userNameField;
    protected final Locator passwordField;
    protected final Locator submitButton;

    public Locator getUserNameField() {
        return userNameField;
    }

    public Locator getPasswordField() {
        return passwordField;
    }

    public Locator getSubmitButton() {
        return submitButton;
    }

    public LoginPage(Page page) {
        this.page = page;
        userNameField = page.locator("#username");
        passwordField = page.locator("#password");
        submitButton = page.locator("button[type='submit']");
    }

    public void navigateTo(String url) {
        page.navigate(url);
    }

}
