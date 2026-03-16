package api.config;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class Config {

    private static final String CONFIG_PATH= "config.properties";
    private static final Config INSTANCE = new Config();
    private final Properties properties = new Properties();

    private Config() {
        try(InputStream input = getClass().getClassLoader().getResourceAsStream(CONFIG_PATH)) {
            if (input == null) {
                throw new RuntimeException(CONFIG_PATH + "file not found in resources");
            }
            properties.load(input);
        } catch (IOException e) {
            throw new RuntimeException("Fail to load " + CONFIG_PATH);
        }
    }

    public static String getProperty(String key) {
        return INSTANCE.properties.getProperty(key);
    }

}
