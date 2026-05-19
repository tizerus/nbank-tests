package ui;

import api.ApiBaseTest;
import com.codeborne.selenide.Selenide;
import com.codeborne.selenide.logevents.SelenideLogger;
import common.extension.AdminSessionExtension;
import common.extension.BrowserMatchExtension;
import common.extension.SelenideThreadLocalConfigExtension;
import common.extension.UserSessionExtension;
import io.qameta.allure.selenide.AllureSelenide;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.extension.ExtendWith;

@ExtendWith({SelenideThreadLocalConfigExtension.class,
        AdminSessionExtension.class,
        UserSessionExtension.class,
        BrowserMatchExtension.class})
public class BaseUiTest extends ApiBaseTest {

    @BeforeAll
    public static void setupAllureScreenshots() {
        SelenideLogger.addListener("AllureSelenide", new AllureSelenide()
                .screenshots(true)
                .savePageSource(true));
    }

    @AfterEach
    public void tearDown() {
        try {
            Selenide.closeWebDriver();
        } catch (Exception e) {
            System.err.println("Error closing WebDriver: " + e.getMessage());
        }
    }

}
