package common.extension;

import com.codeborne.selenide.Selenide;
import com.codeborne.selenide.WebDriverRunner;
import io.qameta.allure.Allure;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.TestWatcher;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.WebDriver;

import java.io.ByteArrayInputStream;

public class ScreenshotOnFailureExtension implements TestWatcher {

    @Override
    public void testFailed(ExtensionContext context, Throwable cause) {
        if (WebDriverRunner.hasWebDriverStarted() && WebDriverRunner.getWebDriver() != null) {
            try {
                Allure.step("Entering Screenshot On Failure Extension");
                byte[] screenshot = Selenide.screenshot(OutputType.BYTES);
                Allure.addAttachment("Screenshot on failure: " + context.getDisplayName(),
                        "image/png",
                        new ByteArrayInputStream(screenshot),
                        "png");

                WebDriver driver = WebDriverRunner.getWebDriver();
                String pageSource = driver.getPageSource();
                Allure.addAttachment("Page source on failure",
                        "text/html",
                        pageSource,
                        "html");

                String consoleErrors = getConsoleErrors(driver);
                if (!consoleErrors.isEmpty()) {
                    Allure.addAttachment("⚠️ Browser console errors",
                            "text/plain",
                            consoleErrors,
                            "txt");
                }

                String errorLog = getErrorLog(cause);
                Allure.addAttachment("❌ Error details",
                        "text/plain",
                        errorLog,
                        "txt");

                String systemInfo = getSystemInfo();
                Allure.addAttachment("💻 System information",
                        "text/plain",
                        systemInfo,
                        "txt");
            } catch (Exception e) {
                System.err.println("Failed to take screenshot: " + e.getMessage());
            }
        } else {
            System.err.println("WebDriver not started or already closed, skipping screenshot and logs");
        }


    }

    private String getErrorLog(Throwable cause) {
        StringBuilder sb = new StringBuilder();
        sb.append("Exception: ").append(cause.getClass().getName()).append("\n");
        sb.append("Message: ").append(cause.getMessage()).append("\n\n");
        sb.append("Stack trace:\n");
        for (StackTraceElement element : cause.getStackTrace()) {
            sb.append("  ").append(element.toString()).append("\n");
        }
        return sb.toString();
    }

    private String getConsoleErrors(WebDriver driver) {
        try {
            // Требуется добавление зависимости для логов браузера
            // В Selenide нужно включить логирование:
            // Configuration.browserCapabilities.setCapability("goog:loggingPrefs", Map.of("browser", "ALL"));
            var logs = driver.manage().logs().get("browser");
            if (logs == null || logs.getAll().isEmpty()) {
                return "";
            }

            StringBuilder sb = new StringBuilder();
            for (var log : logs.getAll()) {
                if (log.getLevel().intValue() >= java.util.logging.Level.SEVERE.intValue()) {
                    sb.append(log.getLevel()).append(": ").append(log.getMessage()).append("\n");
                }
            }
            return sb.toString();
        } catch (Exception e) {
            return "Console logs not available: " + e.getMessage();
        }
    }

    private String getSystemInfo() {
        StringBuilder sb = new StringBuilder();
        sb.append("OS: ").append(System.getProperty("os.name")).append(" (")
                .append(System.getProperty("os.version")).append(")\n");
        sb.append("Java version: ").append(System.getProperty("java.version")).append("\n");
        sb.append("Java vendor: ").append(System.getProperty("java.vendor")).append("\n");
        sb.append("User: ").append(System.getProperty("user.name")).append("\n");
        sb.append("Working directory: ").append(System.getProperty("user.dir")).append("\n");
        return sb.toString();
    }

}
