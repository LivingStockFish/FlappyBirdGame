package database;

import database.dao.GameSettingDAO;
import database.dao.HighScoreDAO;
import database.model.GameSetting;
import database.model.HighScore;

import java.util.List;
import java.util.Map;

public class DatabaseManager {
    private static DatabaseManager instance;
    private final HighScoreDAO highScoreDAO;
    private final GameSettingDAO gameSettingDAO;
    
    private DatabaseManager() {
        this.highScoreDAO = new HighScoreDAO();
        this.gameSettingDAO = new GameSettingDAO();
    }
    
    public static synchronized DatabaseManager getInstance() {
        if (instance == null) {
            instance = new DatabaseManager();
        }
        return instance;
    }
    
    public boolean saveHighScore(String playerName, int score) {
        HighScore highScore = new HighScore(playerName, score);
        return highScoreDAO.addHighScore(highScore);
    }
    
    public List<HighScore> getTopHighScores(int limit) {
        return highScoreDAO.getTopHighScores(limit);
    }
    
    public int getHighestScore() {
        return highScoreDAO.getHighestScore();
    }
    
    public boolean resetHighScores() {
        return highScoreDAO.deleteAllHighScores();
    }
    
    public boolean saveSetting(String settingName, String settingValue) {
        GameSetting setting = new GameSetting(settingName, settingValue);
        return gameSettingDAO.saveSetting(setting);
    }
    
    public String getSetting(String settingName) {
        GameSetting setting = gameSettingDAO.getSettingByName(settingName);
        return setting != null ? setting.getSettingValue() : null;
    }
    
    public Map<String, String> getAllSettings() {
        return gameSettingDAO.getAllSettingsAsMap();
    }
    
    public void closeConnection() {
        DatabaseConnection.getInstance().closeConnection();
    }
}