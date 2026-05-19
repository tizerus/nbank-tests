package common.extension;

import api.config.Config;
import com.codeborne.selenide.Configuration;
import com.codeborne.selenide.Selenide;
import com.codeborne.selenide.logevents.SelenideLogger;
import io.qameta.allure.selenide.AllureSelenide;
import org.junit.jupiter.api.extension.AfterEachCallback;
import org.junit.jupiter.api.extension.BeforeEachCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.remote.RemoteWebDriver;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

public class SelenideThreadLocalConfigExtension implements BeforeEachCallback, AfterEachCallback {

    private final ThreadLocal<String> listenerName = new ThreadLocal<>();
    private final ThreadLocal<RemoteWebDriver> driverThreadLocal = new ThreadLocal<>();

    @Override
    public void beforeEach(ExtensionContext context) {
        String name = "AllureSelenide-" + Thread.currentThread().getId();
        listenerName.set(name);

        // 1. Добавляем слушатель
        SelenideLogger.addListener(name, new AllureSelenide()
                .screenshots(true)
                .savePageSource(true)
                .includeSelenideSteps(true));

        // 2. КРИТИЧЕСКАЯ СЕКЦИЯ: синхронизируем изменения Configuration
        synchronized (Configuration.class) {
            Configuration.remote = Config.getProperty("remote");
            Configuration.baseUrl = Config.getProperty("base.ui.url");
            Configuration.browserSize = Config.getProperty("browser.size");
            Configuration.browser = Config.getProperty("browser");
            Configuration.browserCapabilities = getChromeOptions();
        }

        // 3. Альтернативный подход: создаем свой WebDriver (более надежно)
        // createAndSetWebDriver();
    }

    // Более надежный подход: полностью изолированный WebDriver
    private void createAndSetWebDriver() {
        try {
            ChromeOptions options = getChromeOptions();
            Map<String, Object> selenoidOptions = new HashMap<>();
            selenoidOptions.put("enableVNC", true);
            selenoidOptions.put("enableLog", true);
            options.setCapability("selenoid:options", selenoidOptions);

            RemoteWebDriver driver = new RemoteWebDriver(
                    new URL(Config.getProperty("remote")),
                    options
            );

            driverThreadLocal.set(driver);
            com.codeborne.selenide.WebDriverRunner.setWebDriver(driver);

        } catch (MalformedURLException e) {
            throw new RuntimeException("Failed to create WebDriver", e);
        }
    }

    @Override
    public void afterEach(ExtensionContext context) {
        // 1. Удаляем слушатель
        String name = listenerName.get();
        if (name != null) {
            SelenideLogger.removeListener(name);
            listenerName.remove();
        }

        // 2. Закрываем драйвер (исправленная версия)
        RemoteWebDriver driver = driverThreadLocal.get();
        if (driver != null) {
            driver.quit();
            driverThreadLocal.remove();
        }

        // 3. Альтернативно, если используете Selenide
        try {
            Selenide.closeWebDriver();
        } catch (Exception e) {
            // Логируем, но не бросаем
            System.err.println("Error closing WebDriver: " + e.getMessage());
        }
    }

    private ChromeOptions getChromeOptions() {
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--max_old_space_size=512");
        // options.addArguments("--headless=new");
        options.addArguments("--disable-dev-shm-usage");

        // Добавляем логирование браузера
        Map<String, Object> prefs = new HashMap<>();
        prefs.put("goog:loggingPrefs", Map.of("browser", "ALL"));
        options.setCapability("goog:loggingPrefs", Map.of("browser", "ALL"));

        return options;
    }
}