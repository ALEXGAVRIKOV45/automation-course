package auto;

import com.microsoft.playwright.APIRequest;
import com.microsoft.playwright.APIRequestContext;
import com.microsoft.playwright.APIResponse;
import com.microsoft.playwright.Playwright;
import io.qameta.allure.internal.shadowed.jackson.databind.JsonNode;
import io.qameta.allure.internal.shadowed.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class TodoApiTest {
    Playwright playwright;
    APIRequestContext requestContext;
    ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void setUp() {
        playwright = Playwright.create();
        requestContext = playwright.request().newContext(
                new APIRequest.NewContextOptions()
                        .setBaseURL("https://jsonplaceholder.typicode.com")
        );
    }

    @Test
    void testTodoApi() throws Exception {
        // 1. Выполнение GET-запроса
        APIResponse response = requestContext.get("/todos/1");

        // 2. Проверка статуса
        assertEquals(200, response.status(), "Статус ответа должен быть 200");
        assertTrue(response.ok(), "Ответ должен быть успешным");

        // 3. Парсинг JSON
        String responseBody = response.text();
        JsonNode jsonNode = objectMapper.readTree(responseBody);

        // 4. Проверка структуры
        System.out.println("Ответ: " + jsonNode.toPrettyString());

        // Проверка наличия всех полей
        assertTrue(jsonNode.has("userId"), "Поле 'userId' отсутствует");
        assertTrue(jsonNode.has("id"), "Поле 'id' отсутствует");
        assertTrue(jsonNode.has("title"), "Поле 'title' отсутствует");
        assertTrue(jsonNode.has("completed"), "Поле 'completed' отсутствует");

        // Проверка типов данных
        assertTrue(jsonNode.get("userId").isInt(), "userId должен быть числом");
        assertTrue(jsonNode.get("id").isInt(), "id должен быть числом");
        assertTrue(jsonNode.get("title").isTextual(), "title должен быть строкой");
        assertTrue(jsonNode.get("completed").isBoolean(), "completed должен быть булевым");

        // Проверка конкретных значений
        assertEquals(1, jsonNode.get("userId").asInt(), "userId должен быть 1");
        assertEquals(1, jsonNode.get("id").asInt(), "id должен быть 1");
        assertEquals("delectus aut autem", jsonNode.get("title").asText(),
                "title не соответствует ожидаемому");
        assertFalse(jsonNode.get("completed").asBoolean(), "completed должен быть false");

        // Проверка дополнительных полей - их не должно быть
        assertEquals(4, jsonNode.size(), "Количество полей должно быть 4");
    }

    @AfterEach
    void tearDown() {
        requestContext.dispose();
        playwright.close();
    }
}
