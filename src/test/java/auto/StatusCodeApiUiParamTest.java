package auto;

import com.example.config.EnvConfig;
import com.microsoft.playwright.*;
import com.microsoft.playwright.options.AriaRole;
import lombok.extern.slf4j.Slf4j;
import org.aeonbits.owner.ConfigFactory;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;


import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

@Slf4j
class StatusCodeApiUiParamTest {
    private Playwright playwright;
    private APIRequestContext apiRequest;
    private Browser browser;
    private BrowserContext context;
    private Page page;
    private static EnvConfig config;

    @BeforeAll
    static void loadConfig() {
        config = ConfigFactory.create(EnvConfig.class, System.getProperties());
    }

    @BeforeEach
    void setup() {
        playwright = Playwright.create();

        // Настройка API контекста
        apiRequest = playwright.request().newContext(
                new APIRequest.NewContextOptions()
                        .setBaseURL(config.baseUrl())
        );

        // Настройка браузера
        browser = playwright.chromium().launch(
                new BrowserType.LaunchOptions()
                        .setHeadless(false)
                        .setSlowMo(500)
        );

        context = browser.newContext();
        page = context.newPage();
    }

    @ParameterizedTest
    @ValueSource(ints = {200, 404})
    void testStatusCodesCombinedParam(int statusCode) {
        String pathPage = "/status_codes/" + statusCode;

        // API запрос
        int codeAPI = getApiStatusCode(statusCode);

        // Проверка доступность эндпоинтов /status_codes/200, /status_codes/404
        assertThat(codeAPI)
                .as("HTTP статус для %s", pathPage)
                .isEqualTo(statusCode);

        //Переходим на страницу /status_codes и кликаем на ссылки с кодами 200 и 404
        int codeUI = getUiStatusCode(statusCode);

        //Сравниваем статус-коды, полученные через API и UI
        assertEquals(codeAPI, codeUI , "Status code API and UI mast be Equals!");
    }

    private int getApiStatusCode(int code) {
        APIResponse responseAPI = apiRequest.get("/status_codes/" + code);
        assertEquals(code, responseAPI.status(), "API: Неверный статус код для " + code);
        return responseAPI.status();
    }

    private int getUiStatusCode(int code) {
        // Навигация на страницу статус кодов один раз
        page.navigate(config.baseUrl() + "/status_codes");
        page.waitForSelector("div.example");

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
