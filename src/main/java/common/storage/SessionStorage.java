package common.storage;

import api.models.CreateUserRequest;
import api.requests.steps.UserSteps;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

public class SessionStorage {

    private static final SessionStorage INSTANCE = new SessionStorage();

    private final LinkedHashMap<CreateUserRequest, UserSteps> userSteps = new LinkedHashMap<>();

    private SessionStorage(){}

    public static void addUsers(List<CreateUserRequest> users) {
        for (CreateUserRequest user: users) {
            INSTANCE.userSteps.put(user, new UserSteps(user));
        }
    }

    /**
     * @param userNumber номер пользователя начиная с 1
     * @return CreateUserRequest соотв указанному индексу
     */

    public static CreateUserRequest getUser(int userNumber) {
        return new ArrayList<>(INSTANCE.userSteps.keySet()).get(userNumber - 1);
    }

    public static CreateUserRequest getUser() {
        return getUser(1);
    }

    public static UserSteps getUserSteps(int userNumber) {
        return new ArrayList<>(INSTANCE.userSteps.values()).get(userNumber - 1);
    }

    public static UserSteps getUserSteps() {
        return getUserSteps(1);
    }

    public static void clear() {
        INSTANCE.userSteps.clear();
    }

}
