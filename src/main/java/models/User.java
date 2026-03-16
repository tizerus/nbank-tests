package models;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import requests.skeleton.Endpoint;
import requests.skeleton.requests.ValidatableCrudRequester;
import specs.RequestSpecs;
import specs.ResponseSpecs;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class User {

    private CreateUserRequest userRequest;
    private List<CreateAccountResponse> accountResponse;

    public String getUserName() {
        return this.userRequest.getUsername();
    }

    public String getPassword() {
        return this.userRequest.getPassword();
    }

    public List<String> getAccountsNumbers() {
        return accountResponse.stream()
                .map(CreateAccountResponse::getAccountNumber)
                .toList();
    }

    public float getBalance(String accName) {
        return new ValidatableCrudRequester<Profile>(
                RequestSpecs.authAsUser(getUserName(), getPassword()),
                Endpoint.PROFILE,
                ResponseSpecs.requestReturnsOk()
        )
                .get()
                .getAccounts().stream()
                .filter(accNumber -> accNumber.getAccountNumber().equals(accName))
                .findFirst()
                .orElseThrow(() -> new AssertionError("Аккаунт: " + accName + " не найден"))
                .getBalance();
    }


}
