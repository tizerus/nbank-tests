package ui.elements;

import com.codeborne.selenide.SelenideElement;
import lombok.Getter;
import lombok.Setter;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Getter
@Setter
public class DepositUserAccount extends BaseElement{

    String fullInfo;
    String accName;
    String balance;

    public DepositUserAccount(SelenideElement element) {
        super(element);
        this.fullInfo = element.getSelectedOption().getText();
        this.accName = element.getSelectedOption().getText().split(" ")[0];
        Pattern pattern = Pattern.compile("\\$(\\d+\\.?\\d*)");
        Matcher matcher = pattern.matcher(fullInfo);
        String amount = "";
        if (matcher.find()) {
            amount = matcher.group(1);
        }
        this.balance = amount;
    }

}
