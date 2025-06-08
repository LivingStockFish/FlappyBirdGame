package flappybird;
import java.util.prefs.Preferences;
public class Settings {
    private static Settings instance;
    private final Preferences prefs;
    private double hornVolume;
    private double crashVolume;
    private double bgmVolume;
    private String jumpKey;
    private String restartKey;
    private String resetKey;
    private String aspectRatio;
    private boolean fullscreen;
    private int selectedBird;
    private String playerName;
    private Settings() {
        prefs = Preferences.userNodeForPackage(Settings.class);
        loadSettings();
    }
    public static synchronized Settings getInstance() {
        if (instance == null) {
            instance = new Settings();
        }
        return instance;
    }
    public void loadSettings() {
        hornVolume = prefs.getDouble("hornVolume", 0.5);
        crashVolume = prefs.getDouble("crashVolume", 0.5);
        bgmVolume = prefs.getDouble("bgmVolume", 0.5);
        jumpKey = prefs.get("jumpKey", "SPACE");
        restartKey = prefs.get("restartKey", "ENTER");
        resetKey = prefs.get("resetKey", "R");
        aspectRatio = "Default"; 
        fullscreen = prefs.getBoolean("fullscreen", false);
        selectedBird = prefs.getInt("selectedBird", 1);
        playerName = prefs.get("playerName", null);
    }
    public void saveSettings() {
        prefs.putDouble("hornVolume", hornVolume);
        prefs.putDouble("crashVolume", crashVolume);
        prefs.putDouble("bgmVolume", bgmVolume);
        prefs.put("jumpKey", jumpKey);
        prefs.put("restartKey", restartKey);
        prefs.put("resetKey", resetKey);
        prefs.put("aspectRatio", "Default");
        prefs.putBoolean("fullscreen", fullscreen);
        prefs.putInt("selectedBird", selectedBird);
        if (playerName != null) {
            prefs.put("playerName", playerName);
        }
    }
    public double getHornVolume() { return hornVolume; }
    public void setHornVolume(double hornVolume) { this.hornVolume = hornVolume; }
    public double getCrashVolume() { return crashVolume; }
    public void setCrashVolume(double crashVolume) { this.crashVolume = crashVolume; }
    public double getBgmVolume() { return bgmVolume; }
    public void setBgmVolume(double bgmVolume) { this.bgmVolume = bgmVolume; }
    public String getJumpKey() { return jumpKey; }
    public void setJumpKey(String jumpKey) { this.jumpKey = jumpKey; }
    public String getRestartKey() { return restartKey; }
    public void setRestartKey(String restartKey) { this.restartKey = restartKey; }
    public String getResetKey() { return resetKey; }
    public void setResetKey(String resetKey) { this.resetKey = resetKey; }
    public String getAspectRatio() { return aspectRatio; }
    public void setAspectRatio(String aspectRatio) { this.aspectRatio = aspectRatio; }
    public boolean isFullscreen() { return fullscreen; }
    public void setFullscreen(boolean fullscreen) { 
        if (this.fullscreen != fullscreen) {
            this.fullscreen = fullscreen;
            System.gc();
        }
    }
    public int getSelectedBird() { return selectedBird; }
    public void setSelectedBird(int selectedBird) { this.selectedBird = selectedBird; }
    public String getPlayerName() { return playerName; }
    public void setPlayerName(String playerName) { this.playerName = playerName; }
    public int[] getDimensions() {
        if (fullscreen) {
            return new int[] {1920, 1080};
        }
        return new int[] {800, 600};
    }
}

