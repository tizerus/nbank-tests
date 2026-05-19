package ui;

import api.ApiBaseTest;
import common.extension.AdminSessionExtension;
import common.extension.BrowserMatchExtension;
import common.extension.SelenideThreadLocalConfigExtension;
import common.extension.UserSessionExtension;
import org.junit.jupiter.api.extension.ExtendWith;

@ExtendWith({SelenideThreadLocalConfigExtension.class,
        AdminSessionExtension.class,
        UserSessionExtension.class,
        BrowserMatchExtension.class})
public class BaseUiTest extends ApiBaseTest {



}
