CREATE DATABASE IF NOT EXISTS flappybird_db;

USE flappybird_db;

CREATE TABLE IF NOT EXISTS high_scores (
    id INT AUTO_INCREMENT PRIMARY KEY,
    player_name VARCHAR(50) NOT NULL DEFAULT 'Player',
    score INT NOT NULL,
    date_achieved TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS game_settings (
    id INT AUTO_INCREMENT PRIMARY KEY,
    setting_name VARCHAR(50) NOT NULL UNIQUE,
    setting_value VARCHAR(255) NOT NULL
);

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

CREATE OR REPLACE VIEW top_scores AS
SELECT player_name, score, date_achieved
FROM high_scores
ORDER BY score DESC
LIMIT 10;