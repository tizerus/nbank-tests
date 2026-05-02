package ui;

import api.ApiBaseTest;
import api.config.Config;
import api.models.CreateUserRequest;
import api.specs.RequestSpecs;
import com.codeborne.selenide.Configuration;
import com.codeborne.selenide.Selenide;
import common.extension.BrowserMatchExtension;
import common.extension.AdminSessionExtension;
import common.extension.UserSessionExtension;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;

import java.util.Map;

@ExtendWith({AdminSessionExtension.class, UserSessionExtension.class, BrowserMatchExtension.class})
public class BaseUiTest extends ApiBaseTest {

    @BeforeEach
    public void selenideSetup() {
        //Configuration.remote = Config.getProperty("remote");
        Configuration.baseUrl = Config.getProperty("baseUrl");
        //Configuration.baseUrl = "http://172.30.192.1:3000"; warsaw
        Configuration.browserSize = Config.getProperty("browserSize");
        Configuration.browser = Config.getProperty("browser");

        Configuration.browserCapabilities.setCapability("selenoid:options",
                Map.of("enableVNC", true, "enableLog", true)
                                                       );
    }

    public static void authAsUser(String user, String password) {
        Selenide.open("/");
        String authToken = RequestSpecs.getUserAuthHeader(user, password);
        Selenide.executeJavaScript("localStorage.setItem('authToken', arguments[0]);", authToken);
    }

    public static void authAsUser(CreateUserRequest user) {
        Selenide.open("/");
        String authToken = RequestSpecs.getUserAuthHeader(user.getUsername(), user.getPassword());
        Selenide.executeJavaScript("localStorage.setItem('authToken', arguments[0]);", authToken);
    }

}
