package auto;

import com.microsoft.playwright.*;
import com.microsoft.playwright.options.AriaRole;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;


import static org.assertj.core.api.Assertions.assertThat;

@Slf4j
public class StatusCodeApiUiTest {
    private Playwright playwright;
    private APIRequestContext apiRequest;
    private Browser browser;
    private BrowserContext context;
    private Page page;

    @BeforeEach
    void setup() {
        playwright = Playwright.create();

        // Настройка API контекста
        apiRequest = playwright.request().newContext(
                new APIRequest.NewContextOptions()
                        .setBaseURL("https://the-internet.herokuapp.com")
        );

        // Настройка браузера
        browser = playwright.chromium().launch(
                new BrowserType.LaunchOptions()
                        .setHeadless(false)
                        .setSlowMo(500)
        );

        context = browser.newContext();
        page = context.newPage();

        // Навигация на страницу статус кодов один раз
        page.navigate("https://the-internet.herokuapp.com/status_codes");
        page.waitForSelector("div.example");
    }

    @Test
    void testStatusCodesCombined() {
        String path200 = "/status_codes/200";
        String path404 = "/status_codes/404";

        // API запрос
        int codeAPI200 = getApiStatusCode(200);
        System.out.println("link 200 Status Code: " + codeAPI200);
        int codeAPI404 = getApiStatusCode(404);
        System.out.println("link 404 Status Code: " + codeAPI404);

        // Проверка доступность эндпоинтов /status_codes/200, /status_codes/404
        assertThat(codeAPI200)
                .as("HTTP статус для %s", path200)
                .isEqualTo(200);
        assertThat(codeAPI404)
                .as("HTTP статус для %s", path404)
                .isEqualTo(404);

        //Переходим на страницу /status_codes и кликаем на ссылки с кодами 200 и 404
        int codeUI200 = getUiStatusCode(200);
        page.goBack();
        int codeUI404 = getUiStatusCode(404);

        //Сравниваем статус-коды, полученные через API и UI
        Assertions.assertEquals(codeAPI200, codeUI200 , "Status code API and UI mast be Equals!");
        Assertions.assertEquals(codeAPI404, codeUI404 , "Status code API and UI mast be Equals!");
    }

    private int getApiStatusCode(int code) {
        APIResponse responseAPI = apiRequest.get("/status_codes/" + code);
        return responseAPI.status();
    }

    private int getUiStatusCode(int code) {
        Response response = page.waitForResponse(
                res -> res.url().endsWith("/status_codes/" + code),
                () -> page.getByRole(AriaRole.LINK, new Page.GetByRoleOptions().setName(Integer.toString(code))).click());
        return response.status();
    }

        @AfterEach
        void tearDown() {
            context.close();
            browser.close();
            playwright.close();
        }
}
