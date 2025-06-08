package database;
import database.DatabaseConnection.HighScoreEntry;
import database.model.HighScore;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
public class DatabaseManager {
    private static DatabaseManager instance;
    private final Map<String, String> settings = new HashMap<>();
    private DatabaseManager() {
    }
    public static synchronized DatabaseManager getInstance() {
        if (instance == null) {
            instance = new DatabaseManager();
        }
        return instance;
    }
    public boolean saveHighScore(String playerName, int score) {
        return DatabaseConnection.getInstance().saveHighScore(playerName, score);
    }
    public List<HighScore> getTopHighScores(int limit) {
        List<HighScoreEntry> entries = DatabaseConnection.getInstance().getTopHighScores(limit);
        List<HighScore> highScores = new ArrayList<>();
        for (HighScoreEntry entry : entries) {
            HighScore highScore = new HighScore(
                entry.id,
                entry.playerName,
                entry.score,
                entry.dateAchieved
            );
            highScores.add(highScore);
        }
        return highScores;
    }
    public int getHighestScore() {
        return DatabaseConnection.getInstance().getHighestScore();
    }
    public int getPlayerHighestScore(String playerName) {
        return DatabaseConnection.getInstance().getPlayerHighestScore(playerName);
    }
    public boolean resetHighScores() {
        return DatabaseConnection.getInstance().deleteAllHighScores();
    }
    public boolean resetPlayerHighScore(String playerName) {
        return DatabaseConnection.getInstance().resetPlayerHighScore(playerName);
    }
    public boolean saveSetting(String settingName, String settingValue) {
        settings.put(settingName, settingValue);
        return true;
    }
    public String getSetting(String settingName) {
        return settings.get(settingName);
    }
    public Map<String, String> getAllSettings() {
        return new HashMap<>(settings);
    }
    public void closeConnection() {
        DatabaseConnection.getInstance().closeConnection();
    }
}

