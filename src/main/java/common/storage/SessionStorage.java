package common.storage;

import api.models.CreateUserRequest;
import api.requests.steps.UserSteps;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class SessionStorage {

    private static final ThreadLocal<SessionStorage> INSTANCE = ThreadLocal.withInitial(SessionStorage::new);

    private final Map<CreateUserRequest, UserSteps> userSteps = Collections.synchronizedMap(new LinkedHashMap<>());

    private SessionStorage(){}

    private static SessionStorage getInstance() {
        return INSTANCE.get();
    }

    public static void addUsers(List<CreateUserRequest> users) {
        for (CreateUserRequest user: users) {
            getInstance().userSteps.put(user, new UserSteps(user));
        }
    }

    /**
     * @param userNumber номер пользователя начиная с 1
     * @return CreateUserRequest соотв указанному индексу
     */

    public static CreateUserRequest getUser(int userNumber) {
        return new ArrayList<>(getInstance().userSteps.keySet()).get(userNumber - 1);
    }

    public static CreateUserRequest getUser() {
        return getUser(1);
    }

    public static UserSteps getUserSteps(int userNumber) {
        return new ArrayList<>(getInstance().userSteps.values()).get(userNumber - 1);
    }

    public static UserSteps getUserSteps(CreateUserRequest userRequest) {
        return getInstance().userSteps.get(userRequest);
    }

    public static UserSteps getUserSteps() {
        return getUserSteps(1);
    }

    public static void clear() {
        getInstance().userSteps.clear();
        INSTANCE.remove();
    }

}
