package studio.dolphinproductions.utils.keybindinglocker;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

public class ConfigManager {

    private static final String CONFIG_FILE_NAME = "config/keybindinglocker_config.json";
    private static final Gson GSON = new Gson();
    private static final File CONFIG_FILE = new File(CONFIG_FILE_NAME);

    public static Set<String> blockedKeys = new HashSet<>();
    public static boolean isLocked = false;

    static {
        loadConfig();
    }

    public static void loadConfig() {
        try {
            File parentDir = CONFIG_FILE.getParentFile();
            if (parentDir != null && !parentDir.exists()) {
                parentDir.mkdirs();
            }

            if (!CONFIG_FILE.exists()) {
                System.out.println("The config file do not exists, creating one...");
                saveConfig();
            }

            try (FileReader reader = new FileReader(CONFIG_FILE)) {
                JsonObject json = GSON.fromJson(reader, JsonObject.class);

                isLocked = json.has("isLocked") ? json.get("isLocked").getAsBoolean() : false;

                blockedKeys.clear();
                if (json.has("blockedKeys")) {
                    JsonArray keysArray = json.getAsJsonArray("blockedKeys");
                    keysArray.forEach(element -> blockedKeys.add(element.getAsString()));
                }
            }
        } catch (IOException e) {
            System.err.println("Error loading the file config: " + e.getMessage());
        }
    }

    public static void saveConfig() {
        JsonObject json = new JsonObject();
        json.addProperty("isLocked", isLocked);

        JsonArray keysArray = new JsonArray();
        blockedKeys.forEach(keysArray::add);
        json.add("blockedKeys", keysArray);

        try {
            if (!CONFIG_FILE.exists()) {
                CONFIG_FILE.createNewFile();
            }

            try (FileWriter writer = new FileWriter(CONFIG_FILE)) {
                GSON.toJson(json, writer);
                System.out.println("Config saves successfully in " + CONFIG_FILE_NAME);
            }
        } catch (IOException e) {
            System.err.println("Error writing the config file: " + e.getMessage());
        }
    }
}
