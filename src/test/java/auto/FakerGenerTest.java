package auto;

import com.github.javafaker.Faker;
import com.microsoft.playwright.*;

import static org.assertj.core.api.Assertions.assertThat;

public class FakerGenerTest {

    public static void main(String[] args) {
        try (Playwright playwright = Playwright.create()) {
            Browser browser = playwright.chromium().launch(new BrowserType.LaunchOptions().setHeadless(false));
            BrowserContext context = browser.newContext();
            Page page = context.newPage();

            // Генерация данных
            Faker faker = new Faker();
            String generatedName = faker.name().fullName();

            // Мокирование API
            page.route("**/dynamic_content", route -> {
                String mockResponse = "<!DOCTYPE html>\n" +
                        "<html>\n" +
                        "<head>\n" +
                        "    <title>Dynamic Content</title>\n" +
                        "</head>\n" +
                        "<body>\n" +
                        "    <h3>Dynamic Content</h3>\n" +
                        "    <div class=\"large-10 columns\">\n" +
                        "        <div class=\"row\">\n" +
                        "            <div class=\"large-2 columns\">\n" +
                        "                <img src=\"/img/avatars/Original-Facebook-Geek-Profile-Avatar-1.jpg\">\n" +
                        "            </div>\n" +
                        "            <div class=\"large-10 columns\">\n" +
                        "                <p><b>User: </b>" + generatedName + "</p>\n" +
                        "                <p>Lorem ipsum dolor sit amet, consectetur adipiscing elit.</p>\n" +
                        "            </div>\n" +
                        "        </div>\n" +
                        "    </div>\n" +
                        "</body>\n" +
                        "</html>";

                route.fulfill(new Route.FulfillOptions()
                        .setStatus(200)
                        .setContentType("text/html")
                        .setBody(mockResponse));
            });

            // Запуск теста
            page.navigate("https://the-internet.herokuapp.com/dynamic_content");
            page.waitForSelector(".large-10.columns");

            // Проверка, что имя отображается на странице
            assertThat(page.textContent(".large-10.columns")).contains(generatedName);
        }
    }
}
