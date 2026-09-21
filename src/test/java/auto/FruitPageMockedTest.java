package auto;

import base.BaseCustomReportTest;
import com.microsoft.playwright.*;
import org.junit.jupiter.api.*;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;

class FruitPageMockedTest extends BaseCustomReportTest {


    @Test
    @DisplayName("Mocked list fruits. Only Tomato is visible")
    void shouldRenderMockedFruit_fast() {
        page.route("**/api/v1/fruits", route ->
                        route.fulfill(new Route.FulfillOptions()
                                .setStatus(200)
                                .setContentType("application/json")
//  Код для успешного теста (закомментировать для проверки падения)
                                .setBody("""
                                        [{"name":"Tomato","id":1}]
                                        """))
// Пример кода для проверки падения, создания скриншота и добавления ошибки в отчет
//                        .setBody("""
//                                [{"name":"Tomato","id":1}, {"name":"Strawberry","id":2}]
//                                """))
        );

        page.navigate("https://demo.playwright.dev/api-mocking/");
        page.waitForSelector("text=Tomato");

        assertThat(page.getByText("Tomato")).isVisible();
        assertThat(page.getByText("Strawberry")).isHidden();
    }
}
