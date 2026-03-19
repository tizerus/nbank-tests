package api;

import org.assertj.core.api.SoftAssertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;

public class ApiBaseTest {
    protected SoftAssertions softAssert;

    @BeforeEach
    public void setupTest() {
        this.softAssert = new SoftAssertions();
    }

    @AfterEach
    public void afterTest() {
        softAssert.assertAll();
    }
}
