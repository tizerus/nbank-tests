package api.models;

import api.requests.skeleton.Endpoint;
import api.requests.skeleton.requests.ValidatableCrudRequester;
import api.specs.RequestSpecs;
import api.specs.ResponseSpecs;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CreateUserResponse extends BaseModel {

    private long id;
    private String username;
    private String password;
    private String name;
    private UserRole role;
    private List<GetCustomerAccountsResponse> accounts;

    public List<GetCustomerAccountsResponse> getAllAccounts() {
        return new ValidatableCrudRequester<GetCustomerAccountsResponse>(
                RequestSpecs.authAsUser(this.username, this.password),
                Endpoint.CUSTOMER_ACCOUNTS,
                ResponseSpecs.requestReturnsOk())
                .getAll(GetCustomerAccountsResponse[].class);
    }

}
