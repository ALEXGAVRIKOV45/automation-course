package auto;

import base.BaseTest;
import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Route;
import com.microsoft.playwright.assertions.LocatorAssertions;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import java.util.Collections;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;

public class StatusCodeInterceptionTest extends BaseTest {

    String btn404 = "//a[@href='status_codes/404']";
    String newText = "//h3[text()='Mocked Success Response']";

    @Test
    void testMockedStatusCode() {
        // Перехват запроса к /status_codes/404
        context.route("**/status_codes/404", route -> {
            route.fulfill(new Route.FulfillOptions()
                    .setStatus(200)
                    .setHeaders(Collections.singletonMap("Content-Type", "text/html"))
                    .setBody("<h3>Mocked Success Response</h3>")
            );
        });

        page.onRequest(request -> {
            if (request.url().equals("https://the-internet.herokuapp.com/status_codes/404")){
                System.out.println("Request URL: " + request.url());
                System.out.println("Request Method: " + request.method());
                System.out.println("Is Navigation Request: " + request.isNavigationRequest());
            }
        });
        page.onResponse(response -> {
            if (response.url().equals("https://the-internet.herokuapp.com/status_codes/404")) {
                System.out.println("Response URL: " + response.url());
                System.out.println("Response Status: " + response.status());
                Assertions.assertEquals(200, response.status(), "Статус страницы должен быть 200");
            }
        });

        page.navigate("https://the-internet.herokuapp.com/status_codes");

        // Клик по ссылке "404"
        Locator button404 = page.locator(btn404);
        assertThat(button404).isVisible(new LocatorAssertions.IsVisibleOptions().setTimeout(3000));
        button404.click();

        // Проверка мок-текста
        Locator newText404 = page.locator(newText);
        assertThat(newText404).isVisible(new LocatorAssertions.IsVisibleOptions().setTimeout(3000));


    }


}
