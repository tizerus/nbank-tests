package common.extension;

import api.config.Config;
import com.codeborne.selenide.logevents.SelenideLogger;
import io.qameta.allure.selenide.AllureSelenide;
import io.qameta.allure.selenide.LogType;
import org.junit.jupiter.api.extension.BeforeEachCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import com.codeborne.selenide.Configuration;
import org.openqa.selenium.chrome.ChromeOptions;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;

public class SelenideThreadLocalConfigExtension implements BeforeEachCallback {

    @Override
    public void beforeEach(ExtensionContext context) {
        // Настройка для текущего потока
        Configuration.remote = Config.getProperty("remote");
        Configuration.baseUrl = Config.getProperty("base.ui.url");
        Configuration.browserSize = Config.getProperty("browser.size");
        Configuration.browser = Config.getProperty("browser");
        SelenideLogger.addListener("AllureSelenide", new AllureSelenide()
                .screenshots(true)
                .savePageSource(true)
                .includeSelenideSteps(true)
                .enableLogs(LogType.BROWSER, Level.ALL)
                .enableLogs(LogType.PERFORMANCE, Level.INFO)
                .enableLogs(LogType.SERVER, Level.INFO)
                .enableLogs(LogType.CLIENT, Level.INFO)
                .enableLogs(LogType.DRIVER, Level.INFO));

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
        //options.addArguments("--headless=new");
        options.addArguments("--disable-dev-shm-usage");
        return options;
    }
}