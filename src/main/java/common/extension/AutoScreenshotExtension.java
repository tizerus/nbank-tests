package common.extension;

import common.utils.ScreenshotUtils;
import io.qameta.allure.Allure;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.TestExecutionExceptionHandler;
import org.openqa.selenium.WebDriver;

import java.util.Objects;
import java.util.function.Function;

public class AutoScreenshotExtension implements TestExecutionExceptionHandler {

    // Function to extract WebDriver from the ExtensionContext
    private final Function<ExtensionContext, WebDriver> driverSupplier;

    public AutoScreenshotExtension(Function<ExtensionContext, WebDriver> driverSupplier) {
        this.driverSupplier = driverSupplier;
    }

    @Override
    public void handleTestExecutionException(final ExtensionContext context, final Throwable throwable)
            throws Throwable {
        final WebDriver webDriver = this.driverSupplier.apply(context);
        if (Objects.nonNull(webDriver)) {
            final String attachmentName = "Extension: \n "
                    + "Autoscreenshot on failure (" + throwable.getClass().getSimpleName() + ")";
            ScreenshotUtils.attachPageScreenshot(webDriver, attachmentName);

            String pageSource = webDriver.getPageSource();
            Allure.addAttachment("Extension page source on failure",
                    "text/html",
                    pageSource,
                    "html");

            String consoleErrors = getConsoleErrors(webDriver);
            if (!consoleErrors.isEmpty()) {
                Allure.addAttachment("⚠️ Extension Browser console errors",
                        "text/plain",
                        consoleErrors,
                        "txt");
            }

            String errorLog = getErrorLog(throwable);
            Allure.addAttachment("❌ Extension Error details",
                    "text/plain",
                    errorLog,
                    "txt");

            String systemInfo = getSystemInfo();
            Allure.addAttachment("💻 Extension System information",
                    "text/plain",
                    systemInfo,
                    "txt");
        }

        throw throwable;
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
