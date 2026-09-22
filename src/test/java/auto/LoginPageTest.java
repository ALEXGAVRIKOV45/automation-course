package auto;

import base.BaseLoginPageTest;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import pages.LoginPage;

import java.util.stream.Stream;

class LoginPageTest extends BaseLoginPageTest {

    static Stream<Arguments> loginData() {
        return Stream.of(
                Arguments.of("Имяпользователя", "ПарольПользователя", "Your username is invalid!"),
                Arguments.of("NameUser", "Password", "Your username is invalid!"),
                Arguments.of("557799", "886644", "Your username is invalid!"),
                Arguments.of("!№;%:?*", "<>|_+)(", "Your username is invalid!"),
                Arguments.of("ИмяName123&^%", "!#$456PassОсень", "Your username is invalid!"),
                Arguments.of("tomsmithh", "SuperSecretPassword!", "Your username is invalid!"),
                Arguments.of("tomsmith", "PasswordSuperSecret!", "Your password is invalid!"),
                Arguments.of("tomsmith", "SuperSecretPassword!", "You logged into a secure area!")
        );
    }

    @ParameterizedTest(name = "[{index}] {0} / {1} -> {2}")
    @MethodSource("loginData")
    void testLoginPage(String username, String password, String messageText) {
        LoginPage loginPage = new LoginPage(page);

        loginPage.navigateTo("https://the-internet.herokuapp.com/login");
        step.fillLoginFields(username, password);
        step.pressSubmitButton();
        step.checkTextVisible(messageText);
    }
}
