package ui;

import api.ApiBaseTest;
import com.codeborne.selenide.Selenide;
import common.extension.AdminSessionExtension;
import common.extension.BrowserMatchExtension;
import common.extension.ScreenshotOnFailureExtension;
import common.extension.SelenideThreadLocalConfigExtension;
import common.extension.UserSessionExtension;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.extension.ExtendWith;

@ExtendWith({SelenideThreadLocalConfigExtension.class,
        AdminSessionExtension.class,
        UserSessionExtension.class,
        ScreenshotOnFailureExtension.class,
        BrowserMatchExtension.class})
public class BaseUiTest extends ApiBaseTest {

    @AfterEach
    public void tearDown() {
        try {
            Selenide.closeWebDriver();
        } catch (Exception e) {
            System.err.println("Error closing WebDriver: " + e.getMessage());
        }
    }

}
