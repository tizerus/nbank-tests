package common.extension;

import common.annotations.SkipForBrokenImage;
import org.junit.jupiter.api.extension.ConditionEvaluationResult;
import org.junit.jupiter.api.extension.ExecutionCondition;
import org.junit.jupiter.api.extension.ExtensionContext;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class SkipForBrokenImageExtension implements ExecutionCondition {

    private static Boolean isBrokenImage = null;

    private static boolean isBrokenImage() {
        if (isBrokenImage == null) {
            try {
                Path dockerComposePath = Paths.get("infra/docker_compose/docker-compose.yml");
                String content = Files.readString(dockerComposePath);

                // Проверяем наличие битого образа в файле
                isBrokenImage = content.contains("nobugsme/nbank:with_database_with_fix_with_swagger");

                if (isBrokenImage) {
                    System.out.println("Detected broken backend image, tests with @SkipForBrokenImage will be skipped");
                }
            } catch (Exception e) {
                System.err.println("Could not read docker-compose.yml: " + e.getMessage());
                isBrokenImage = false;
            }
        }
        return isBrokenImage;
    }

    @Override
    public ConditionEvaluationResult evaluateExecutionCondition(ExtensionContext context) {
        boolean hasAnnotation = context.getElement()
                .map(el -> el.isAnnotationPresent(SkipForBrokenImage.class))
                .orElse(false);
        if (!hasAnnotation) {
            return ConditionEvaluationResult.enabled("No @SkipForBrokenImage annotation");
        }
        if (isBrokenImage()) {
            return ConditionEvaluationResult.disabled(
                    "Skipped: docker-compose.yml contains broken image " +
                            "'nobugsme/nbank:with_database_with_fix_with_swagger'");
        }
        return ConditionEvaluationResult.enabled("Working backend image detected");
    }
}
