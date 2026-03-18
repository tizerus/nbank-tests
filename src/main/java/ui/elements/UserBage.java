package ui.elements;

import api.models.UserRole;
import com.codeborne.selenide.SelenideElement;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserBage extends BaseElement {

    private String username;
    private UserRole role;

    public UserBage(SelenideElement element) {
        super(element);
        String[] textParts = element.getText().split("\n");
        this.username = textParts[0];
        this.role = UserRole.valueOf(textParts[1].toUpperCase());
    }

}
