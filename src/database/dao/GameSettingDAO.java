package database.dao;
import database.DatabaseManager;
import database.model.GameSetting;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
public class GameSettingDAO {
    public GameSettingDAO() {
    }
    public GameSetting getSettingByName(String settingName) {
        String value = DatabaseManager.getInstance().getSetting(settingName);
        if (value != null) {
            return new GameSetting(1, settingName, value);
        }
        return null;
    }
    public List<GameSetting> getAllSettings() {
        List<GameSetting> settings = new ArrayList<>();
        Map<String, String> settingsMap = DatabaseManager.getInstance().getAllSettings();
        int id = 1;
        for (Map.Entry<String, String> entry : settingsMap.entrySet()) {
            settings.add(new GameSetting(id++, entry.getKey(), entry.getValue()));
        }
        return settings;
    }
    public Map<String, String> getAllSettingsAsMap() {
        return DatabaseManager.getInstance().getAllSettings();
    }
    public boolean updateSetting(GameSetting setting) {
        return DatabaseManager.getInstance().saveSetting(
            setting.getSettingName(), 
            setting.getSettingValue()
        );
    }
    public boolean insertSetting(GameSetting setting) {
        return DatabaseManager.getInstance().saveSetting(
            setting.getSettingName(), 
            setting.getSettingValue()
        );
    }
    public boolean saveSetting(GameSetting setting) {
        return DatabaseManager.getInstance().saveSetting(
            setting.getSettingName(), 
            setting.getSettingValue()
        );
    }
}

