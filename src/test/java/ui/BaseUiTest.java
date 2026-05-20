package ui;

import api.ApiBaseTest;
import com.codeborne.selenide.WebDriverRunner;
import common.extension.AdminSessionExtension;
import common.extension.AutoScreenshotExtension;
import common.extension.BrowserMatchExtension;
import common.extension.SelenideThreadLocalConfigExtension;
import common.extension.UserSessionExtension;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.extension.RegisterExtension;

@ExtendWith({SelenideThreadLocalConfigExtension.class,
        AdminSessionExtension.class,
        UserSessionExtension.class,
        BrowserMatchExtension.class})
public class BaseUiTest extends ApiBaseTest {

    @RegisterExtension
    static AutoScreenshotExtension screenshotOnFailure =
            new AutoScreenshotExtension(context -> WebDriverRunner.getWebDriver());

}
