package common.extension;

import com.codeborne.selenide.Configuration;
import common.annotations.Browsers;
import org.junit.jupiter.api.extension.ConditionEvaluationResult;
import org.junit.jupiter.api.extension.ExecutionCondition;
import org.junit.jupiter.api.extension.ExtensionContext;

import java.util.Arrays;

public class BrowserMatchExtension implements ExecutionCondition {

    @Override
    public ConditionEvaluationResult evaluateExecutionCondition(ExtensionContext extensionContext) {
        Browsers annotations =  extensionContext.getElement()
                .map(el -> el.getAnnotation(Browsers.class))
                .orElse(null);
        if (annotations == null) {
            return ConditionEvaluationResult.enabled("No browsers restrictions.");
        }

        String currentBrowser = Configuration.browser;
        boolean match = Arrays.asList(annotations.values()).contains(currentBrowser);
        if (match) {
            return ConditionEvaluationResult.enabled("Current browser enabled for this test: " + currentBrowser);
        } else {
            return ConditionEvaluationResult.disabled(String.format("Current browser %s not available for this test. "
                    + "Available browsers: %s", currentBrowser, Arrays.toString(annotations.values())));

        }
    }

}
