package auto;

import com.example.config.DbConfig;
import com.example.config.EnvConfig;
import com.microsoft.playwright.*;
import org.aeonbits.owner.ConfigFactory;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.*;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class LoginDbTest {
    private Connection connection;
    private Playwright playwright;
    private Page page;
    private Browser browser;
    private BrowserContext context;
    private static EnvConfig config;
    private static DbConfig dbConfig;


    @BeforeAll
    static void loadConfig() {
        config = ConfigFactory.create(EnvConfig.class, System.getProperties());
        dbConfig = ConfigFactory.create(DbConfig.class);
        assertNotNull(dbConfig.dbUrl());
        assertNotNull(dbConfig.dbUser());
        assertNotNull(dbConfig.dbPassword());
    }

    @BeforeEach
    void setup() throws SQLException {

        // Создание пользователя в БД
        connection = DriverManager.getConnection(
                dbConfig.dbUrl(),
                dbConfig.dbUser(),
                dbConfig.dbPassword()
        );

        try (Statement stmt = connection.createStatement()) {
            stmt.executeUpdate(
                    "INSERT INTO users (name, password) VALUES ('%s', '%s')".formatted(config.userName(), config.password())
            );
        }

        // Инициализация Playwright
        playwright = Playwright.create();
        browser = playwright.chromium().launch(new BrowserType.LaunchOptions().setHeadless(true));
        context = browser.newContext();
        page = context.newPage();
    }

    @Test
    void testLoginWithDbUser() throws SQLException {
        // Получение данных из БД
        String username = null;
        String password = null;

        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(
                     "SELECT name, password FROM users WHERE name = '%s'".formatted(config.userName()))) {

            if (rs.next()) {
                username = rs.getString("name");
                password = rs.getString("password");
            }
        }
        assertNotNull(username, "Username not found in DB");
        assertNotNull(password, "Password not found in DB");

        // Выполнение логина
        page.navigate(config.baseUrl() + "/login");
        page.locator("#username").fill(username);
        page.locator("#password").fill(password);
        page.locator("button[type='submit']").click();

        // Проверка успешной авторизации
        assertTrue(page.locator(".flash.success").isVisible());
        assertTrue(page.url().endsWith("/secure"));
    }

    @AfterEach
    void tearDown() throws SQLException {

        // Удаление тестового пользователя
        try (Statement stmt = connection.createStatement()) {
            stmt.executeUpdate(
                    "DELETE FROM users WHERE name = '%s'".formatted(config.userName())
            );
        }

        // Закрытие ресурсов
        if (connection != null) connection.close();
        if (context != null) context.close();
        if (page != null) page.close();
        if (browser != null) browser.close();
    }
}
