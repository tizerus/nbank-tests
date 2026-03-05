package requests.steps;

import generators.RandomModelGenerator;
import models.CreateAccountResponse;
import models.CreateUserRequest;
import models.CreateUserResponse;
import models.User;
import requests.skeleton.Endpoint;
import requests.skeleton.requests.ValidatableCrudRequester;
import specs.RequestSpecs;
import specs.ResponseSpecs;

import java.util.ArrayList;
import java.util.List;

public class AdminSteps {

    public static CreateUserRequest createUserResponse() {
        CreateUserRequest createUserRequest = RandomModelGenerator.generate(CreateUserRequest.class);

        new ValidatableCrudRequester<CreateUserResponse>(
                RequestSpecs.adminSpec(),
                Endpoint.ADMIN_USER,
                ResponseSpecs.entityCreated())
                .post(createUserRequest);

        return createUserRequest;
    }

    public static User createUserAndAcc(int accNumbers) {
        CreateUserRequest createUserRequest1 = AdminSteps.createUserResponse();
        return User.builder()
                .userRequest(createUserRequest1)
                .accountResponse(createAccList(createUserRequest1, accNumbers))
                .build();
    }

    private static List<CreateAccountResponse> createAccList(CreateUserRequest userRequest1, int accNumbers) {
        List<CreateAccountResponse> result = new ArrayList<>();
        CreateAccountResponse accountResponse1;
        for (int i = 0; i < accNumbers; i++) {
            result.add(UserSteps.createAccount(userRequest1));
        }
        return result;
    }

}
