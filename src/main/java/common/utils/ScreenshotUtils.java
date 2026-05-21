package common.utils;

import io.qameta.allure.Allure;
import io.qameta.allure.Attachment;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;

import java.io.ByteArrayInputStream;

public class ScreenshotUtils {
    @Attachment(type = "image/png")
    public static byte[] attachPageScreenshot(WebDriver webDriver) {
        return ((TakesScreenshot) webDriver).getScreenshotAs(OutputType.BYTES);
    }

    public static void attachPageScreenshot(WebDriver webDriver, String name) {
        byte[] screenshotBytes = ((TakesScreenshot) webDriver).getScreenshotAs(OutputType.BYTES);
        Allure.attachment(name, new ByteArrayInputStream(screenshotBytes));
    }
}
