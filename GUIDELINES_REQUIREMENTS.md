# FlappyBird Game - Guidelines Requirements Implementation

This document explains how the FlappyBird game meets the requirements specified by the Guidelines.

## Requirements Implementation

### 1. Creating the new project with JDK & IDE setup - 2 marks
- The project is set up with Java and JavaFX
- The game runs successfully in the IDE (Visual Studio Code)
- The project structure follows standard Java conventions

### 2. Define the project structure - 1 mark
- The project has a clear structure with separate packages:
  - `flappybird`: Contains the main game classes
  - `database`: Contains database-related classes (model, DAO)
  - `resources`: Contains game assets (images, sounds)
  - `fonts`: Contains custom fonts used in the game

### 3. Design the database schema for the project - 1 mark
- A database schema has been designed with two main tables:
  - `high_scores`: Stores player high scores
  - `game_settings`: Stores game settings like volume levels and key bindings
- The schema is documented in `DATABASE_SETUP.md`

### 4. Create a MySQL table - 1 mark
- SQL script for creating the necessary tables is provided in `src/database/schema.sql`
- Tables include appropriate columns, data types, and constraints

### 5. Implement JDBC for database connectivity - 3 marks
- JDBC connectivity is implemented in `src/database/DatabaseConnection.java`
- The connection uses the Singleton pattern to ensure only one database connection is used
- Error handling and connection management are properly implemented

### 6. Create Model, DAO classes for the database operations - 3 marks
- Model classes:
  - `src/database/model/HighScore.java`: Represents a high score entry
  - `src/database/model/GameSetting.java`: Represents a game setting
- DAO classes:
  - `src/database/dao/HighScoreDAO.java`: Handles high score operations
  - `src/database/dao/GameSettingDAO.java`: Handles game setting operations
- A manager class (`src/database/DatabaseManager.java`) provides a simplified interface for database operations

### 7. Aesthetics and Visual Appeal of the UI - 4 marks
- The game has a visually appealing UI with:
  - Custom fonts and colors
  - Animated transitions
  - Consistent design language
  - Professional-looking buttons and menus
  - Random background images for visual variety

### 8. Component Placement and Alignment in the UI - 2 marks
- UI components are properly aligned and positioned
- The layout adapts to different screen sizes (fullscreen vs. windowed mode)
- Proper spacing and margins are used for visual clarity
- Components are grouped logically

### 9. Responsiveness and Accessibility of the UI - 2 marks
- The UI is responsive to different screen sizes
- Button hover effects provide visual feedback
- Clear visual hierarchy makes the UI easy to navigate
- High contrast colors improve readability
- Consistent button sizes and spacing improve usability

## Note on Database Implementation

The database functionality is currently disabled in the running code to avoid compilation errors, but all the necessary classes and code are provided. To enable the database features:

1. Set up MySQL as described in `DATABASE_SETUP.md`
2. Add the MySQL JDBC driver to the project
3. Uncomment the database-related code in the game classes

The high scores button in the main menu currently shows a "Coming Soon" message, but the code for the high scores screen is fully implemented in `src/flappybird/HighScoresScreen.java`.