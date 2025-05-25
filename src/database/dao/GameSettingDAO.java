package database.dao;

import database.DatabaseConnection;
import database.model.GameSetting;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GameSettingDAO {
    private Connection connection;
    
    public GameSettingDAO() {
        this.connection = DatabaseConnection.getInstance().getConnection();
    }
    
    public GameSetting getSettingByName(String settingName) {
        String sql = "SELECT * FROM game_settings WHERE setting_name = ?";
        
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, settingName);
            
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return new GameSetting(
                            resultSet.getInt("id"),
                            resultSet.getString("setting_name"),
                            resultSet.getString("setting_value")
                    );
                }
            }
        } catch (SQLException e) {
        }
        
        return null;
    }
    
    public List<GameSetting> getAllSettings() {
        List<GameSetting> settings = new ArrayList<>();
        String sql = "SELECT * FROM game_settings";
        
        try (Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(sql)) {
            while (resultSet.next()) {
                GameSetting setting = new GameSetting(
                        resultSet.getInt("id"),
                        resultSet.getString("setting_name"),
                        resultSet.getString("setting_value")
                );
                settings.add(setting);
            }
        } catch (SQLException e) {
        }
        
        return settings;
    }
    
    public Map<String, String> getAllSettingsAsMap() {
        Map<String, String> settingsMap = new HashMap<>();
        String sql = "SELECT setting_name, setting_value FROM game_settings";
        
        try (Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(sql)) {
            while (resultSet.next()) {
                settingsMap.put(
                        resultSet.getString("setting_name"),
                        resultSet.getString("setting_value")
                );
            }
        } catch (SQLException e) {
        }
        
        return settingsMap;
    }
    
    public boolean updateSetting(GameSetting setting) {
        String sql = "UPDATE game_settings SET setting_value = ? WHERE setting_name = ?";
        
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, setting.getSettingValue());
            statement.setString(2, setting.getSettingName());
            
            int rowsAffected = statement.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
        }
        
        return false;
    }
    
    public boolean insertSetting(GameSetting setting) {
        String sql = "INSERT INTO game_settings (setting_name, setting_value) VALUES (?, ?)";
        
        try (PreparedStatement statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            statement.setString(1, setting.getSettingName());
            statement.setString(2, setting.getSettingValue());
            
            int rowsAffected = statement.executeUpdate();
            
            if (rowsAffected > 0) {
                try (ResultSet generatedKeys = statement.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        setting.setId(generatedKeys.getInt(1));
                    }
                }
                return true;
            }
        } catch (SQLException e) {
        }
        
        return false;
    }
    
    public boolean saveSetting(GameSetting setting) {
        GameSetting existingSetting = getSettingByName(setting.getSettingName());
        
        if (existingSetting == null) {
            return insertSetting(setting);
        } else {
            return updateSetting(setting);
        }
    }
}