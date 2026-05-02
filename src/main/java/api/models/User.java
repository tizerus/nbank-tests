package api.models;

import api.requests.skeleton.Endpoint;
import api.requests.skeleton.requests.ValidatableCrudRequester;
import api.specs.RequestSpecs;
import api.specs.ResponseSpecs;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

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

    public String getProfileName() {
        return new ValidatableCrudRequester<Profile>(
                RequestSpecs.authAsUser(getUserName(), getPassword()),
                Endpoint.PROFILE,
                ResponseSpecs.requestReturnsOk()
        )
                .get()
                .getName();
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
