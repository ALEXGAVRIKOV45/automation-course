package com.example;

import com.example.config.EnvironmentConfig;
import com.microsoft.playwright.BrowserType;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.Playwright;
import com.microsoft.playwright.Response;
import org.aeonbits.owner.ConfigFactory;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class StatusCodeTest {
    private EnvironmentConfig config;
    private Page page;

    @BeforeEach
    public void setup() {
        config = ConfigFactory.create(EnvironmentConfig.class, System.getenv());
        page = Playwright.create().chromium().launch(new BrowserType.LaunchOptions()
                .setHeadless(false)).newPage();
    }

    @Test
    public void test200() {
        Response response = page.navigate(config.baseUrl() + "/status_codes/200");
        assertEquals(200, response.status());
    }

    @AfterEach
    public void teardown() {
        page.close();
    }
}
