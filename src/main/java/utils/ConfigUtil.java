package utils;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

// Utility class for loading and accessing application configuration properties
public class ConfigUtil {
    private static Properties properties = new Properties();

    static {
        try {
            FileInputStream input = new FileInputStream("src/main/resources/app.properties");
            properties.load(input);
            input.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static String getProperty(String key) {
        return properties.getProperty(key);
    }
    public static String getAuthToken() {
        return "Bearer " + getProperty("token");
    }
}