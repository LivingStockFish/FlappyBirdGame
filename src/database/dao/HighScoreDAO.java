package database.dao;

import database.DatabaseConnection;
import database.model.HighScore;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class HighScoreDAO {
    private Connection connection;
    
    public HighScoreDAO() {
        this.connection = DatabaseConnection.getInstance().getConnection();
    }
    
    public boolean addHighScore(HighScore highScore) {
        String sql = "INSERT INTO high_scores (player_name, score) VALUES (?, ?)";
        
        try (PreparedStatement statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            statement.setString(1, highScore.getPlayerName());
            statement.setInt(2, highScore.getScore());
            
            int rowsAffected = statement.executeUpdate();
            
            if (rowsAffected > 0) {
                try (ResultSet generatedKeys = statement.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        highScore.setId(generatedKeys.getInt(1));
                    }
                }
                return true;
            }
        } catch (SQLException e) {
        }
        
        return false;
    }
    
    public List<HighScore> getTopHighScores(int limit) {
        List<HighScore> highScores = new ArrayList<>();
        String sql = "SELECT * FROM high_scores ORDER BY score DESC LIMIT ?";
        
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, limit);
            
            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    HighScore highScore = new HighScore(
                            resultSet.getInt("id"),
                            resultSet.getString("player_name"),
                            resultSet.getInt("score"),
                            resultSet.getTimestamp("date_achieved")
                    );
                    highScores.add(highScore);
                }
            }
        } catch (SQLException e) {
        }
        
        return highScores;
    }
    
    public int getHighestScore() {
        String sql = "SELECT MAX(score) AS highest_score FROM high_scores";
        
        try (Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(sql)) {
            if (resultSet.next()) {
                return resultSet.getInt("highest_score");
            }
        } catch (SQLException e) {
        }
        
        return 0;
    }
    
    public boolean deleteAllHighScores() {
        String sql = "DELETE FROM high_scores";
        
        try (Statement statement = connection.createStatement()) {
            int rowsAffected = statement.executeUpdate(sql);
            return rowsAffected > 0;
        } catch (SQLException e) {
        }
        
        return false;
    }
}