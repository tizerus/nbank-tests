package common.extension;

import api.config.Config;
import org.junit.jupiter.api.extension.BeforeEachCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import com.codeborne.selenide.Configuration;
import org.openqa.selenium.chrome.ChromeOptions;

import java.util.HashMap;
import java.util.Map;

public class SelenideThreadLocalConfigExtension implements BeforeEachCallback {

    @Override
    public void beforeEach(ExtensionContext context) {
        // Настройка для текущего потока
        Configuration.remote = Config.getProperty("remote");
        Configuration.baseUrl = Config.getProperty("baseUrl");
        Configuration.browserSize = Config.getProperty("browserSize");
        Configuration.browser = Config.getProperty("browser");

        ChromeOptions chromeOptions = getChromeOptions();
        Map<String, Object> selenoidOptions = new HashMap<>();
        selenoidOptions.put("enableVNC", true);
        selenoidOptions.put("enableLog", true);
        chromeOptions.setCapability("selenoid:options", selenoidOptions);

        Configuration.browserCapabilities = chromeOptions;
    }

    private ChromeOptions getChromeOptions() {
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--max_old_space_size=512");
        options.addArguments("--headless=new");
        options.addArguments("--disable-dev-shm-usage");
        return options;
    }
}