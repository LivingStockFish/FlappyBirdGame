# FlappyBird Database Setup Guide

This document provides instructions for setting up the MySQL database for the FlappyBird game.

## Prerequisites

1. MySQL Server installed on your computer
2. MySQL JDBC Driver (mysql-connector-java-8.0.xx.jar)

## Database Setup Steps

### 1. Create the Database

Run the following SQL commands in MySQL:

```sql
-- Create the database if it doesn't exist
CREATE DATABASE IF NOT EXISTS flappybird_db;

-- Use the flappybird_db database
USE flappybird_db;

-- Create the high_scores table to store player scores
CREATE TABLE IF NOT EXISTS high_scores (
    id INT AUTO_INCREMENT PRIMARY KEY,
    player_name VARCHAR(50) NOT NULL DEFAULT 'Player',
    score INT NOT NULL,
    date_achieved TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Create the game_settings table to store game settings
CREATE TABLE IF NOT EXISTS game_settings (
    id INT AUTO_INCREMENT PRIMARY KEY,
    setting_name VARCHAR(50) NOT NULL UNIQUE,
    setting_value VARCHAR(255) NOT NULL
);

-- Insert default game settings
INSERT INTO game_settings (setting_name, setting_value) 
VALUES 
    ('bgm_volume', '50'),
    ('horn_volume', '50'),
    ('crash_volume', '50'),
    ('jump_key', 'Space'),
    ('restart_key', 'R'),
    ('reset_key', 'X'),
    ('fullscreen', 'false')
ON DUPLICATE KEY UPDATE setting_value = VALUES(setting_value);
```

### 2. Configure Database Connection

1. Download the MySQL JDBC driver from the [MySQL website](https://dev.mysql.com/downloads/connector/j/)
2. Place the JAR file in the `lib` directory of the project
3. Update the database connection parameters in `src/database/DatabaseConnection.java`:

```java
private static final String DB_URL = "jdbc:mysql://localhost:3306/flappybird_db";
private static final String DB_USER = "root";  // Replace with your MySQL username
private static final String DB_PASSWORD = "";  // Replace with your MySQL password
```

### 3. Enable Database Features

To enable the database features, uncomment the database-related code in:
- `src/flappybird/Settings.java`
- `src/flappybird/Game.java`
- `src/flappybird/MainMenu.java`

## Database Schema

### High Scores Table

Stores player high scores:

| Column | Type | Description |
|--------|------|-------------|
| id | INT | Primary key |
| player_name | VARCHAR(50) | Name of the player |
| score | INT | Score achieved |
| date_achieved | TIMESTAMP | When the score was achieved |

### Game Settings Table

Stores game settings:

| Column | Type | Description |
|--------|------|-------------|
| id | INT | Primary key |
| setting_name | VARCHAR(50) | Name of the setting |
| setting_value | VARCHAR(255) | Value of the setting |

## Troubleshooting

If you encounter database connection issues:

1. Verify MySQL Server is running
2. Check your username and password
3. Ensure the database and tables exist
4. Verify the JDBC driver is in the classpath