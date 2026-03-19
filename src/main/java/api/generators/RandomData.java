package api.generators;

import org.apache.commons.lang3.RandomStringUtils;

import java.util.concurrent.ThreadLocalRandom;

public class RandomData {

    private RandomData(){}

    public static String getUserName() {
        return RandomStringUtils.randomAlphabetic(6);
    }

    public static String getPassword() {
        return RandomStringUtils.randomAlphabetic(3).toUpperCase()
                + RandomStringUtils.randomAlphabetic(3).toLowerCase()
                + RandomStringUtils.randomNumeric(3) + "$";
    }

    public static float generateFloatInclusive(float min, float max) {
        if (min > max) {
            throw new IllegalArgumentException("min не может быть больше max");
        }
        double random = ThreadLocalRandom.current().nextDouble();
        double result = min + random * (max - min);

        return (float) result;
    }

}
