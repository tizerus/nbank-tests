package common.extension;

import api.models.CreateUserRequest;
import api.requests.steps.AdminSteps;
import common.annotations.UserSession;
import common.storage.SessionStorage;
import org.junit.jupiter.api.extension.BeforeEachCallback;
import org.junit.jupiter.api.extension.ExtensionContext;

import java.util.ArrayList;
import java.util.List;

public class UserSessionExtension implements BeforeEachCallback {

    @Override
    public void beforeEach(ExtensionContext extensionContext) {
        UserSession annotation = extensionContext.getRequiredTestMethod().getAnnotation(UserSession.class);
        if (annotation != null) {
            int userSessionsNumber = annotation.value();
            SessionStorage.clear();

            List<CreateUserRequest> users = new ArrayList<>();
            for (int i = 0; i < userSessionsNumber; i++) {
                users.add(AdminSteps.createUserResponse());
            }
            SessionStorage.addUsers(users);
        }
    }

}
