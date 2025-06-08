package database.dao;
import database.DatabaseConnection;
import database.model.HighScore;
import java.util.ArrayList;
import java.util.List;
public class HighScoreDAO {
    public HighScoreDAO() {
    }
    public boolean addHighScore(HighScore highScore) {
        return DatabaseConnection.getInstance().saveHighScore(
            highScore.getPlayerName(), 
            highScore.getScore()
        );
    }
    public List<HighScore> getTopHighScores(int limit) {
        List<DatabaseConnection.HighScoreEntry> entries = 
            DatabaseConnection.getInstance().getTopHighScores(limit);
        List<HighScore> highScores = new ArrayList<>();
        for (DatabaseConnection.HighScoreEntry entry : entries) {
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
        int highestScore = DatabaseConnection.getInstance().getHighestScore();
        return highestScore;
    }
    public boolean deleteAllHighScores() {
        boolean result = DatabaseConnection.getInstance().deleteAllHighScores();
        return result;
    }
}

