package database.model;

import java.sql.Timestamp;

public class HighScore {
    private int id;
    private String playerName;
    private int score;
    private Timestamp dateAchieved;
    
    public HighScore() {
    }
    
    public HighScore(String playerName, int score) {
        this.playerName = playerName;
        this.score = score;
    }
    
    public HighScore(int id, String playerName, int score, Timestamp dateAchieved) {
        this.id = id;
        this.playerName = playerName;
        this.score = score;
        this.dateAchieved = dateAchieved;
    }
    
    public int getId() {
        return id;
    }
    
    public void setId(int id) {
        this.id = id;
    }
    
    public String getPlayerName() {
        return playerName;
    }
    
    public void setPlayerName(String playerName) {
        this.playerName = playerName;
    }
    
    public int getScore() {
        return score;
    }
    
    public void setScore(int score) {
        this.score = score;
    }
    
    public Timestamp getDateAchieved() {
        return dateAchieved;
    }
    
    public void setDateAchieved(Timestamp dateAchieved) {
        this.dateAchieved = dateAchieved;
    }
    
    @Override
    public String toString() {
        return "HighScore{" +
                "id=" + id +
                ", playerName='" + playerName + '\'' +
                ", score=" + score +
                ", dateAchieved=" + dateAchieved +
                '}';
    }
}