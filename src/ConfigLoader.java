import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

public class ConfigLoader {
    private String apiUrl;
    private String botName;

    public ConfigLoader(String filePath) throws IOException {
        Properties props = new Properties();

        try (FileInputStream input = new FileInputStream(filePath)) {
            props.load(input);
        } catch (IOException e) {
            throw new IOException("Configuration file not found or unreadable: " + filePath, e);
        }

        apiUrl = props.getProperty("api.url");
        if (apiUrl == null || apiUrl.isEmpty()) {
            throw new IOException("Missing required property: api.url");
        }

        botName = props.getProperty("bot.name", "DefaultBotName");
    }

    public String getApiUrl() {
        return apiUrl;
    }

    public String getBotName() {
        return botName;
    }
}
