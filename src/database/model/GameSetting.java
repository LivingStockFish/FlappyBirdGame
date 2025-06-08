package database.model;
public class GameSetting {
    private int id;
    private String settingName;
    private String settingValue;
    public GameSetting() {
    }
    public GameSetting(String settingName, String settingValue) {
        this.settingName = settingName;
        this.settingValue = settingValue;
    }
    public GameSetting(int id, String settingName, String settingValue) {
        this.id = id;
        this.settingName = settingName;
        this.settingValue = settingValue;
    }
    public int getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
    }
    public String getSettingName() {
        return settingName;
    }
    public void setSettingName(String settingName) {
        this.settingName = settingName;
    }
    public String getSettingValue() {
        return settingValue;
    }
    public void setSettingValue(String settingValue) {
        this.settingValue = settingValue;
    }
    @Override
    public String toString() {
        return "GameSetting{" +
                "id=" + id +
                ", settingName='" + settingName + '\'' +
                ", settingValue='" + settingValue + '\'' +
                '}';
    }
}

