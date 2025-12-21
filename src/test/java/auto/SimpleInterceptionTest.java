package auto;

import base.BaseTest;
import com.microsoft.playwright.*;
import com.microsoft.playwright.assertions.LocatorAssertions;
import com.microsoft.playwright.options.WaitUntilState;
import org.junit.jupiter.api.Test;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;

public class SimpleInterceptionTest extends BaseTest {
    @Test
    void simpleInterceptionTest() {
        // 1. Настраиваем перехват
        context.route("**/authenticate", route -> {

            // Получаем оригинальные данные
            Request request = route.request();
            if (request.method().equals("POST")) {
                System.out.println("POST-запрос к /authenticate успешно перехвачен!");

                // Меняем username
                String postData = request.postData();
                String modifiedData = postData;
                if (postData != null && postData.contains("username=tomsmith")) {
                    modifiedData = postData.replace("username=tomsmith", "username=HACKED_USER");
                    System.out.println("Было: " + postData);
                    System.out.println("Стало: " + modifiedData);
                } else {
                    System.out.println("Параметр username=tomsmith не найден в запросе");
                }

                // Создаем ResumeOptions с новыми данными
                Route.ResumeOptions newAuth = new Route.ResumeOptions()
                        .setPostData(modifiedData);

                // Отправляем измененный запрос
                route.resume(newAuth);

            } else {
                route.resume();
            }
        });

        // 2. Переходим на страницу
        page.navigate("https://the-internet.herokuapp.com/login", new Page.NavigateOptions()
                .setWaitUntil(WaitUntilState.DOMCONTENTLOADED));
        assertThat(page.locator("button[type='submit']")).isVisible(new LocatorAssertions.IsVisibleOptions().setTimeout(3000));

        // 3. Заполняем форму
        Locator userName = page.locator("input[name='username']");
        Locator passWord = page.locator("input[name='password']");
        userName.fill("tomsmith");
        passWord.fill("SuperSecretPassword!");

        // 4. Нажимаем кнопку
        page.click("button[type='submit']");


        // 5. Ждем и проверяем результат
//        Честно говоря говоря не понял как проверить именно результат подмены запроса получив результат
//        Поэтому проверяю Alert : username is Invalid!
        page.waitForTimeout(2000);
        Locator errorText = page.locator("//div[@class='flash error' and contains(text(),'Your username is invalid!')]");
        assertThat(errorText).isVisible(new LocatorAssertions.IsVisibleOptions().setTimeout(3000));

    }
}
