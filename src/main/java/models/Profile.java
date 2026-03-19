package models;

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
public class Profile extends BaseModel {

    private long id;
    private String username;
    private String password;
    private String name;
    private UserRole role;
    private List<GetCustomerAccountsResponse> accounts;

}
