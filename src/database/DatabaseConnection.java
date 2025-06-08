package database;
import java.io.*;
import java.sql.Connection;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
public class DatabaseConnection {
    private static final String DATA_FOLDER = "data";
    private static final String HIGH_SCORES_FILE = DATA_FOLDER + File.separator + "highscores.dat";
    private static DatabaseConnection instance;
    private Connection connection; 
    private DatabaseConnection() {
        try {
            File dataDir = new File(DATA_FOLDER);
            if (!dataDir.exists()) {
                dataDir.mkdirs();
            }
            File highScoresFile = new File(HIGH_SCORES_FILE);
            if (!highScoresFile.exists()) {
                highScoresFile.createNewFile();
            }
        } catch (Exception e) {
        }
    }
    public static synchronized DatabaseConnection getInstance() {
        if (instance == null) {
            instance = new DatabaseConnection();
        }
        return instance;
    }
    public Connection getConnection() {
        return null;
    }
    public void closeConnection() {
    }
    public boolean saveHighScore(String playerName, int score) {
        try {
            List<HighScoreEntry> highScores = loadHighScores();
            if (playerName != null) {
                playerName = playerName.replace("...", "");
            }
            boolean playerExists = false;
            for (HighScoreEntry entry : highScores) {
                if (entry.playerName != null && entry.playerName.equalsIgnoreCase(playerName)) {
                    playerExists = true;
                    if (score > entry.score) {
                        entry.score = score;
                        entry.dateAchieved = new Timestamp(System.currentTimeMillis());
                    }
                    break;
                }
            }
            if (!playerExists) {
                HighScoreEntry newEntry = new HighScoreEntry();
                newEntry.id = highScores.size() + 1;
                newEntry.playerName = playerName;
                newEntry.score = score;
                newEntry.dateAchieved = new Timestamp(System.currentTimeMillis());
                highScores.add(newEntry);
            }
            saveHighScores(highScores);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
    public List<HighScoreEntry> getTopHighScores(int limit) {
        try {
            List<HighScoreEntry> allScores = loadHighScores();
            allScores.sort((a, b) -> Integer.compare(b.score, a.score));
            List<HighScoreEntry> topScores = new ArrayList<>();
            for (int i = 0; i < Math.min(limit, allScores.size()); i++) {
                topScores.add(allScores.get(i));
            }
            return topScores;
        } catch (Exception e) {
            return new ArrayList<>();
        }
    }
    public int getHighestScore() {
        try {
            List<HighScoreEntry> allScores = loadHighScores();
            if (allScores.isEmpty()) {
                return 0;
            }
            int highestScore = 0;
            for (HighScoreEntry entry : allScores) {
                if (entry.score > highestScore) {
                    highestScore = entry.score;
                }
            }
            return highestScore;
        } catch (Exception e) {
            return 0;
        }
    }
    public int getPlayerHighestScore(String playerName) {
        try {
            if (playerName == null || playerName.isEmpty()) {
                return 0;
            }
            List<HighScoreEntry> allScores = loadHighScores();
            if (allScores.isEmpty()) {
                return 0;
            }
            int highestScore = 0;
            for (HighScoreEntry entry : allScores) {
                if (entry.playerName != null && entry.playerName.equalsIgnoreCase(playerName) && entry.score > highestScore) {
                    highestScore = entry.score;
                }
            }
            return highestScore;
        } catch (Exception e) {
            return 0;
        }
    }
    public boolean deleteAllHighScores() {
        try {
            try (PrintWriter writer = new PrintWriter(new FileWriter(HIGH_SCORES_FILE))) {
            }
            return true;
        } catch (Exception e) {
            return false;
        }
    }
    public boolean resetPlayerHighScore(String playerName) {
        try {
            if (playerName == null || playerName.isEmpty()) {
                return false;
            }
            List<HighScoreEntry> highScores = loadHighScores();
            List<HighScoreEntry> updatedScores = new ArrayList<>();
            boolean playerFound = false;
            for (HighScoreEntry entry : highScores) {
                if (entry.playerName != null && entry.playerName.equalsIgnoreCase(playerName)) {
                    playerFound = true;
                } else {
                    updatedScores.add(entry);
                }
            }
            if (playerFound) {
                saveHighScores(updatedScores);
                return true;
            }
            return false;
        } catch (Exception e) {
            return false;
        }
    }
    private List<HighScoreEntry> loadHighScores() {
        List<HighScoreEntry> highScores = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(HIGH_SCORES_FILE))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.trim().isEmpty()) continue;
                List<String> parts = new ArrayList<>();
                StringBuilder currentPart = new StringBuilder();
                boolean escaped = false;
                for (int i = 0; i < line.length(); i++) {
                    char c = line.charAt(i);
                    if (escaped) {
                        currentPart.append(c);
                        escaped = false;
                    } else if (c == '\\') {
                        escaped = true;
                    } else if (c == ',') {
                        parts.add(currentPart.toString());
                        currentPart = new StringBuilder();
                    } else {
                        currentPart.append(c);
                    }
                }
                parts.add(currentPart.toString());
                if (parts.size() >= 4) {
                    HighScoreEntry entry = new HighScoreEntry();
                    entry.id = Integer.parseInt(parts.get(0));
                    entry.playerName = parts.get(1);
                    entry.score = Integer.parseInt(parts.get(2));
                    entry.dateAchieved = new Timestamp(Long.parseLong(parts.get(3)));
                    highScores.add(entry);
                }
            }
        } catch (Exception e) {
        }
        return highScores;
    }
    private void saveHighScores(List<HighScoreEntry> highScores) {
        try (PrintWriter writer = new PrintWriter(new FileWriter(HIGH_SCORES_FILE))) {
            for (HighScoreEntry entry : highScores) {
                String escapedName = entry.playerName.replace(",", "\\,");
                writer.println(entry.id + "," + escapedName + "," + entry.score + "," + entry.dateAchieved.getTime());
            }
        } catch (Exception e) {
        }
    }
    public static class HighScoreEntry {
        public int id;
        public String playerName;
        public int score;
        public Timestamp dateAchieved;
    }
}

