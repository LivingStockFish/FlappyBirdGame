package flappybird;

import javafx.animation.AnimationTimer;
import javafx.animation.FadeTransition;
import javafx.animation.PauseTransition;
import javafx.scene.effect.DropShadow;
import javafx.scene.effect.Glow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.control.Label;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.util.Duration;
import javafx.animation.Animation;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.geometry.Pos;
import javafx.scene.layout.VBox;
import javafx.scene.control.Button; 
import javafx.geometry.Insets; 
import javafx.application.Platform; 
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.stage.Modality;
import javafx.scene.Scene;
import javafx.scene.layout.HBox;

import java.io.*;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class Game {
    private Bird bird;
    private List<Pipe> pipes;
    private Pane root;
    private AnimationTimer timer;
    private int score = 0;
    private int highScore = 0;
    private Label scoreLabel;
    private Label highScoreLabel;
    private Label newHighScoreLabel;
    private Label countdownLabel;
    private boolean gameOver = false;
    private double pipeSpeed = 3;
    private long lastPipeTime = 0;
    private double pipeSpawnAccumulator = 0.0;
    private double pipeSpawnInterval = 1500.0;
    private int currentLevel = 0;
    private static final int SCORE_PER_LEVEL = 10; 
    private int pipesPassedInCurrentLevel = 0; 
    private static final int PIPES_PER_LEVEL = 10; 
    private Image nextLevelBackground = null;
    
    private ImageView secondBackground = null;

    private ImageView background;
    private double backgroundX = 0;
    private double backgroundSpeed = 1.5; 

    private final String HIGH_SCORE_FILE = "highscore.dat";
    private boolean achievedNewHighScore = false;
    private int pipesSinceHighScore = 0;
    private boolean isFlickeringHighScore = false;

    private Font flappyFont;
    private int currentThemeIndex = 0;

    private MediaPlayer bgMusicPlayer;

    private Image[] backgrounds = new Image[]{
        new Image(getClass().getResource("/resources/sky1.png").toExternalForm()),
        new Image(getClass().getResource("/resources/sky2.png").toExternalForm()),
        new Image(getClass().getResource("/resources/sky3.png").toExternalForm()),
        new Image(getClass().getResource("/resources/sky4.png").toExternalForm()),
        new Image(getClass().getResource("/resources/sky5.png").toExternalForm()),
        new Image(getClass().getResource("/resources/sky6.png").toExternalForm()),
        new Image(getClass().getResource("/resources/sky7.png").toExternalForm()),
        new Image(getClass().getResource("/resources/sky8.png").toExternalForm()),
        new Image(getClass().getResource("/resources/sky9.png").toExternalForm()),
        new Image(getClass().getResource("/resources/sky10.png").toExternalForm()),
    };

    private boolean isPaused = false;
    private VBox pauseMenu; 

    private Runnable showOptionsMenuCallback;
    private Runnable showMainMenuCallback;


    public Game(Pane root, Runnable showOptionsMenuCallback, Runnable showMainMenuCallback) {
        this.root = root;
        this.showOptionsMenuCallback = showOptionsMenuCallback;
        this.showMainMenuCallback = showMainMenuCallback;

        init();
    }
    
    
    public void setOptionsMenuCallback(Runnable callback) {
        this.showOptionsMenuCallback = callback;

    }

    private void init() {
        loadHighScore();
        Font font = Font.loadFont(getClass().getResourceAsStream("/fonts/flappyfont.ttf"), 24);
        this.flappyFont = font != null ? font : Font.font("Verdana", 24);
        
        currentLevel = 0;
        
        if (Settings.getInstance().isFullscreen()) {
            pipeSpeed = 5.0; 
            backgroundSpeed = 2.5; 
        } else {
            pipeSpeed = 3.0; 
            backgroundSpeed = 1.5; 
        }
        
        updatePipeSpawnInterval();


        background = new ImageView(backgrounds[currentThemeIndex]);
        
        double bgWidth, bgHeight;
        
        if (Settings.getInstance().isFullscreen()) {
            bgWidth = 4200;
            
            if (currentThemeIndex == 0) {
                bgHeight = 863;
            } else {
                bgHeight = 900;
            }
            
            background.setPreserveRatio(false);
            
            secondBackground = new ImageView(backgrounds[currentThemeIndex]);
            secondBackground.setPreserveRatio(false);
            secondBackground.setFitWidth(bgWidth);
            secondBackground.setFitHeight(bgHeight);
            
            secondBackground.setLayoutX(bgWidth);
            secondBackground.setLayoutY(0);
        } else {
            bgWidth = 3000;
            bgHeight = 600;
            
            bgWidth = ScaleHelper.scaleWidth(bgWidth);
            bgHeight = ScaleHelper.scaleHeight(bgHeight);
            
            secondBackground = null;
        }
        
        background.setFitWidth(bgWidth);
        background.setFitHeight(bgHeight);
        
        background.setLayoutX(0);
        background.setLayoutY(0);
        
        if (Settings.getInstance().isFullscreen()) {
            bgResetPoint = -bgWidth;
        } else {
            bgResetPoint = -(bgWidth - 800);
        }
        
        root.getChildren().add(0, background);
        
        if (secondBackground != null) {
            root.getChildren().add(1, secondBackground);
        }

        bird = new Bird();
        pipes = new ArrayList<>();
        
        DropShadow textShadow = new DropShadow(ScaleHelper.scaleHeight(3), Color.BLACK);
        
        if (Settings.getInstance().isFullscreen()) {
            scoreLabel = createLabel("Score:" + score, 30, 30, Color.WHITE);
            scoreLabel.setFont(Font.font(flappyFont.getFamily(), 36));
            scoreLabel.setEffect(textShadow);
            
            highScoreLabel = createScoreLabel("High Score:", highScore, 1015, 30, Color.GOLD, 
                Font.font(flappyFont.getFamily(), 36));
            highScoreLabel.setEffect(textShadow);
            
            newHighScoreLabel = createLabel("New High Score!", 416, 200, Color.GOLD);
            newHighScoreLabel.setFont(Font.font(flappyFont.getFamily(), 48));
            newHighScoreLabel.setVisible(false);
            
            countdownLabel = createLabel("", 730, 330, Color.RED);
            countdownLabel.setFont(Font.font(flappyFont.getFamily(), 80));
            countdownLabel.setVisible(false);
            countdownLabel.setEffect(new DropShadow(15, Color.BLACK));
        } else {
            scoreLabel = createLabel("Score:" + score, 
                ScaleHelper.scaleX(20), ScaleHelper.scaleY(20), Color.WHITE);
            scoreLabel.setEffect(textShadow);
            
            highScoreLabel = createScoreLabel("High Score:", highScore, 
                ScaleHelper.getCurrentWidth() - ScaleHelper.scaleWidth(360), ScaleHelper.scaleY(20), Color.GOLD, null);
            highScoreLabel.setEffect(textShadow);
            
            newHighScoreLabel = createLabel("New High Score!", 
                ScaleHelper.scaleX(225), ScaleHelper.scaleY(180), Color.GOLD);
            newHighScoreLabel.setVisible(false);
            
            countdownLabel = createLabel("", 
                ScaleHelper.scaleX(350), ScaleHelper.scaleY(250), Color.RED);
            countdownLabel.setFont(Font.font(flappyFont.getFamily(), 
                ScaleHelper.scaleHeight(60)));
            countdownLabel.setVisible(false);
            countdownLabel.setEffect(new DropShadow(10, Color.BLACK));
        }

        pauseMenu = createPauseMenu();
        pauseMenu.setVisible(false);

        root.getChildren().addAll(bird.getView(), scoreLabel, highScoreLabel, newHighScoreLabel, countdownLabel, pauseMenu);

        System.gc();
        
        final long[] lastFrameTime = {0};
        final double targetFPS = 60.0;
        final double frameTime = 1_000_000_000.0 / targetFPS;
        final double[] accumulator = {0.0};
        
       
        timer = new AnimationTimer() {
            @Override
            public void handle(long now) {
                if (gameOver || isPaused) return;
                
                if (lastFrameTime[0] == 0) {
                    lastFrameTime[0] = now;
                    cachedIsFullscreen = Settings.getInstance().isFullscreen();
                    fullscreenStateChecked = true;
                    updatePipeSpawnInterval();
                    return;
                }
                
                if (lastFrameTime[0] == 0) {
                    lastFrameTime[0] = now;
                    cachedIsFullscreen = Settings.getInstance().isFullscreen();
                    fullscreenStateChecked = true;
                    updatePipeSpawnInterval();
                    return;
                }
                
                double deltaTime = (now - lastFrameTime[0]) / 1_000_000.0;
                lastFrameTime[0] = now;
                
                if (deltaTime > 50.0) {
                    deltaTime = 50.0;
                }
                
                deltaTime = 16.67;
                
                accumulator[0] += deltaTime;
                
                final double physicsTimeStep = frameTime / 1_000_000.0;
                
                int updateCount = 0;
                while (accumulator[0] >= physicsTimeStep && updateCount < 3) {
                    updateCount++;
                    bird.update();
                    updatePipes();
                    checkCollision();
                    updateBackground();
                    
                    pipeSpawnAccumulator += physicsTimeStep;
                    if (pipeSpawnAccumulator >= pipeSpawnInterval) {
                        spawnPipe();
                        pipeSpawnAccumulator -= pipeSpawnInterval;
                    }
                    
                    try {
                        if (Integer.parseInt(scoreLabel.getText().replace("Score:", "")) != score) {
                            final int currentScore = score;
                            Platform.runLater(() -> {
                                scoreLabel.setText("Score:" + currentScore);
                                
                                scoreLabel.toFront();
                                highScoreLabel.toFront();
                            });
                        }
                    } catch (NumberFormatException e) {
                        final int currentScore = score;
                        Platform.runLater(() -> {
                            scoreLabel.setText("Score:" + currentScore);
                        });
                    }
                    
                    accumulator[0] -= physicsTimeStep;
                }
                
                if (score > highScore) {
                    boolean firstHighScoreAchievement = !achievedNewHighScore;
                    
                    boolean wasBlinking = isFlickeringHighScore;
                    if (highScoreLabel.getUserData() instanceof FadeTransition) {
                         ((FadeTransition) highScoreLabel.getUserData()).stop();
                    }
                    
                    achievedNewHighScore = true;
                    highScore = score;
                    
                    root.getChildren().remove(highScoreLabel);
                    
                    if (Settings.getInstance().isFullscreen()) {
                        highScoreLabel = createScoreLabel("High Score:", highScore, 1015, 30, Color.GOLD, 
                            Font.font(flappyFont.getFamily(), 36));
                    } else {
                        highScoreLabel = createScoreLabel("High Score:", highScore, 
                            ScaleHelper.getCurrentWidth() - ScaleHelper.scaleWidth(360), ScaleHelper.scaleY(20), Color.GOLD, null);
                    }
                    
                    DropShadow textShadow = new DropShadow(ScaleHelper.scaleHeight(3), Color.BLACK);
                    highScoreLabel.setEffect(textShadow);
                    root.getChildren().add(highScoreLabel);
                    
                    if (firstHighScoreAchievement) {
                        startBlinkingHighScore();
                    } 
                    else if (wasBlinking) {
                        FadeTransition blink = new FadeTransition(Duration.seconds(0.5), highScoreLabel);
                        blink.setFromValue(1.0);
                        blink.setToValue(0.0);
                        blink.setCycleCount(Animation.INDEFINITE);
                        blink.setAutoReverse(true);
                        blink.play();
                        
                        highScoreLabel.setUserData(blink);
                        
                        isFlickeringHighScore = true;
                    }
                }
            }
        };
    }

    private Label createLabel(String text, double x, double y, Color color) {
        Label label = new Label(text);
        
        if (Settings.getInstance().isFullscreen()) {
            label.setFont(Font.font(flappyFont.getFamily(), 36));
        } else {
            label.setFont(Font.font(flappyFont.getFamily(), ScaleHelper.scaleHeight(24)));
        }
        
        label.setTextFill(color);
        label.setLayoutX(x);
        label.setLayoutY(y);
        
        DropShadow shadow = new DropShadow();
        shadow.setColor(Color.BLACK);
        shadow.setRadius(Settings.getInstance().isFullscreen() ? 5 : 3);
        label.setEffect(shadow);
        
        return label;
    }
    
    private Label createRightAlignedLabel(String text, double rightEdgeX, double y, Color color, Font customFont) {
        Label label = new Label(text);
        label.setTextFill(color);
        
        if (customFont != null) {
            label.setFont(customFont);
        } else if (Settings.getInstance().isFullscreen()) {
            label.setFont(Font.font(flappyFont.getFamily(), 36));
        } else {
            label.setFont(Font.font(flappyFont.getFamily(), ScaleHelper.scaleHeight(24)));
        }
        
        label.applyCss();
        double textWidth = label.prefWidth(-1);
        
        label.setLayoutX(rightEdgeX - textWidth);
        label.setLayoutY(y);
        
        DropShadow shadow = new DropShadow();
        shadow.setColor(Color.BLACK);
        shadow.setRadius(Settings.getInstance().isFullscreen() ? 5 : 3);
        label.setEffect(shadow);
        
        return label;
    }
    
    private Label createScoreLabel(String prefix, int score, double onesDigitX, double y, Color color, Font customFont) {
        Font fontToUse;
        if (customFont != null) {
            fontToUse = customFont;
        } else if (Settings.getInstance().isFullscreen()) {
            fontToUse = Font.font(flappyFont.getFamily(), 36);
        } else {
            fontToUse = Font.font(flappyFont.getFamily(), ScaleHelper.scaleHeight(24));
        }
        
        Label tempLabel = new Label();
        tempLabel.setFont(fontToUse);
        
        tempLabel.setText("0");
        tempLabel.applyCss();
        double digitWidth = tempLabel.prefWidth(-1);
        
        tempLabel.setText(prefix);
        tempLabel.applyCss();
        double prefixWidth = tempLabel.prefWidth(-1);
        
        String scoreStr = Integer.toString(score);
        int numDigits = scoreStr.length();
        
        double labelX = onesDigitX - prefixWidth - (digitWidth * numDigits);
        
        Label label = new Label(prefix + score);
        label.setFont(fontToUse);
        label.setTextFill(color);
        label.setLayoutX(labelX);
        label.setLayoutY(y);
        
        DropShadow shadow = new DropShadow();
        shadow.setColor(Color.BLACK);
        shadow.setRadius(Settings.getInstance().isFullscreen() ? 5 : 3);
        label.setEffect(shadow);
        
        return label;
    }

    private VBox createPauseMenu() {
        if (Settings.getInstance().isFullscreen()) {
            return createFullscreenPauseMenu();
        } else {
            return createDefaultSizePauseMenu();
        }
    }
    
    
    private VBox createFullscreenPauseMenu() {
        
        VBox menu = new VBox(40);
        menu.setAlignment(Pos.CENTER);
        menu.setPadding(new Insets(80, 100, 80, 100));
        menu.setStyle("-fx-background-color: rgba(0, 0, 0, 0.7); -fx-border-color: gold; -fx-border-width: 8;");
        

        double menuWidth = 600;
        double menuHeight = 775;
        menu.setPrefSize(menuWidth, menuHeight);
        menu.setMinSize(menuWidth, menuHeight);
        menu.setMaxSize(menuWidth, menuHeight);

        double centerX = (1920/2 - menuWidth/2) - 250;
        double centerY = (1080/2 - menuHeight/2) - 100;
        menu.setLayoutX(centerX);
        menu.setLayoutY(centerY);

        Label pausedLabel = createLabel("PAUSED", 0, 0, Color.WHITE);
        pausedLabel.setFont(Font.font(flappyFont.getFamily(), 80)); 
        pausedLabel.setEffect(new DropShadow(20, Color.BLACK));
        pausedLabel.setMinWidth(500);
        pausedLabel.setPrefWidth(500);
        pausedLabel.setMaxWidth(500);
        pausedLabel.setAlignment(Pos.CENTER);
        pausedLabel.setTextOverrun(javafx.scene.control.OverrunStyle.CLIP);
        VBox.setMargin(pausedLabel, new Insets(0, 0, 30, 0));

        Button resumeButton = createFullscreenMenuButton("Resume");
        Button restartButton = createFullscreenMenuButton("Restart");
        Button mainMenuButton = createFullscreenMenuButton("Menu");
        Button exitButton = createFullscreenMenuButton("Exit");

        resumeButton.setOnAction(e -> resumeGame());
        restartButton.setOnAction(e -> restart());
        mainMenuButton.setOnAction(e -> {
            if (showMainMenuCallback != null) {
                stopBackgroundMusic();
                showMainMenuCallback.run();
            }
        });
        exitButton.setOnAction(e -> Platform.exit());

        menu.getChildren().addAll(pausedLabel, resumeButton, restartButton, mainMenuButton, exitButton);

        return menu;
    }
    
    private VBox createDefaultSizePauseMenu() {
        
        VBox menu = new VBox(ScaleHelper.scaleHeight(20));
        menu.setAlignment(Pos.CENTER);
        menu.setPadding(new Insets(
            ScaleHelper.scaleHeight(60), 
            ScaleHelper.scaleWidth(50), 
            ScaleHelper.scaleHeight(60), 
            ScaleHelper.scaleWidth(50)
        ));
        menu.setStyle("-fx-background-color: rgba(0, 0, 0, 0.7); -fx-border-color: gold; -fx-border-width: 5;");
        
        double menuWidth = ScaleHelper.scaleWidth(320);
        double menuHeight = ScaleHelper.scaleHeight(460);
        menu.setPrefSize(menuWidth, menuHeight);
        menu.setMinSize(menuWidth, menuHeight);
        menu.setMaxSize(menuWidth, menuHeight);

        double centerX = ScaleHelper.scaleX(400) - menuWidth / 2;
        double centerY = ScaleHelper.scaleY(300) - menuHeight / 2;
        menu.setLayoutX(centerX);
        menu.setLayoutY(centerY);

        Label pausedLabel = createLabel("PAUSED", 0, 0, Color.WHITE);
        pausedLabel.setFont(Font.font(flappyFont.getFamily(), ScaleHelper.scaleHeight(40)));
        pausedLabel.setEffect(new DropShadow(ScaleHelper.scaleHeight(10), Color.BLACK));
        pausedLabel.setMinWidth(ScaleHelper.scaleWidth(280));
        pausedLabel.setPrefWidth(ScaleHelper.scaleWidth(280));
        pausedLabel.setMaxWidth(ScaleHelper.scaleWidth(280));
        pausedLabel.setAlignment(Pos.CENTER);
        pausedLabel.setTextOverrun(javafx.scene.control.OverrunStyle.CLIP);
        VBox.setMargin(pausedLabel, new Insets(0, 0, ScaleHelper.scaleHeight(10), 0));

        Button resumeButton = createMenuButton("Resume");
        Button restartButton = createMenuButton("Restart");
        Button mainMenuButton = createMenuButton("Menu");
        Button exitButton = createMenuButton("Exit");

        resumeButton.setOnAction(e -> resumeGame());
        restartButton.setOnAction(e -> restart());
        mainMenuButton.setOnAction(e -> {
            if (showMainMenuCallback != null) {
                stopBackgroundMusic();
                showMainMenuCallback.run();
            }
        });
        exitButton.setOnAction(e -> Platform.exit());

        menu.getChildren().addAll(pausedLabel, resumeButton, restartButton, mainMenuButton, exitButton);

        return menu;
    }

    private Button createFullscreenMenuButton(String text) {
        Button button = new Button(text);
        button.setFont(Font.font(flappyFont.getFamily(), 44)); 
        button.setTextFill(Color.RED);
        button.setStyle("-fx-background-color: gold; -fx-border-color: brown; -fx-border-width: 5; -fx-background-radius: 8; -fx-border-radius: 8;");
        button.setPadding(new Insets(20, 40, 20, 40)); 
        double buttonWidth = 400;
        double buttonHeight = 100;
        button.setPrefWidth(buttonWidth);
        button.setMinWidth(buttonWidth);
        button.setMaxWidth(buttonWidth);
        button.setPrefHeight(buttonHeight);
        button.setMinHeight(buttonHeight);
        button.setMaxHeight(buttonHeight);
        button.setAlignment(Pos.CENTER);
        
        button.setWrapText(false);
        
        button.setOnMouseEntered(e -> {
            button.setStyle("-fx-background-color: #FFD700; -fx-border-color: #A52A2A; -fx-border-width: 5; -fx-background-radius: 8; -fx-border-radius: 8; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.8), 10, 0, 0, 0);");
            button.setScaleX(1.05);
            button.setScaleY(1.05);
        });
        
        button.setOnMouseExited(e -> {
            button.setStyle("-fx-background-color: gold; -fx-border-color: brown; -fx-border-width: 5; -fx-background-radius: 8; -fx-border-radius: 8;");
            button.setScaleX(1.0);
            button.setScaleY(1.0);
        });
        
        return button;
    }
    
    private Button createMenuButton(String text) {
        Button button = new Button(text);
        button.setFont(Font.font(flappyFont.getFamily(), ScaleHelper.scaleHeight(22)));
        button.setTextFill(Color.RED);
        button.setStyle("-fx-background-color: gold; -fx-border-color: brown; -fx-border-width: 3; -fx-background-radius: 5; -fx-border-radius: 5;");
        button.setPadding(new Insets(
            ScaleHelper.scaleHeight(10), 
            ScaleHelper.scaleWidth(20), 
            ScaleHelper.scaleHeight(10), 
            ScaleHelper.scaleWidth(20)
        ));
        double buttonWidth = ScaleHelper.scaleWidth(200);
        double buttonHeight = ScaleHelper.scaleHeight(60);
        button.setPrefWidth(buttonWidth);
        button.setMinWidth(buttonWidth);
        button.setMaxWidth(buttonWidth);
        button.setPrefHeight(buttonHeight);
        button.setMinHeight(buttonHeight);
        button.setMaxHeight(buttonHeight);
        button.setAlignment(Pos.CENTER);
        
        button.setWrapText(false);
        
        button.setOnMouseEntered(e -> {
            button.setStyle("-fx-background-color: #FFD700; -fx-border-color: #A52A2A; -fx-border-width: 3; -fx-background-radius: 5; -fx-border-radius: 5; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.6), 5, 0, 0, 0);");
            button.setScaleX(1.03);
            button.setScaleY(1.03);
        });
        
        button.setOnMouseExited(e -> {
            button.setStyle("-fx-background-color: gold; -fx-border-color: brown; -fx-border-width: 3; -fx-background-radius: 5; -fx-border-radius: 5;");
            button.setScaleX(1.0);
            button.setScaleY(1.0);
        });
        button.setTextOverrun(javafx.scene.control.OverrunStyle.CLIP);
        
        button.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);

        button.setOnMouseEntered(e -> {
            button.setScaleX(1.1);
            button.setScaleY(1.1);
        });
        button.setOnMouseExited(e -> {
            button.setScaleX(1);
            button.setScaleY(1);
        });

        return button;
    }


    public void start() {
        isPaused = false;
        countdownLabel.setVisible(true);
        startCountdown();
    }

    private void startCountdown() {
        isPaused = true;
        countdownLabel.setVisible(true);
        countdownLabel.setText("3");
        playSound("horn3.mp3");

        PauseTransition pause1 = new PauseTransition(Duration.seconds(1));
        PauseTransition pause2 = new PauseTransition(Duration.seconds(1));
        PauseTransition pause3 = new PauseTransition(Duration.seconds(1));
        PauseTransition pauseGo = new PauseTransition(Duration.seconds(0.5));

        pause1.setOnFinished(e1 -> {
            countdownLabel.setText("2");
            countdownLabel.setTextFill(Color.ORANGE);
            playSound("horn2.mp3");
            pause2.play();
        });

        pause2.setOnFinished(e2 -> {
            countdownLabel.setText("1");
            countdownLabel.setTextFill(Color.YELLOW);
            playSound("horn1.mp3");
            pause3.play();
        });

        pause3.setOnFinished(e3 -> {
            countdownLabel.setText("GO!");
            countdownLabel.setTextFill(Color.WHITE);
            playSound("go.mp3");
            pauseGo.play();
        });

        pauseGo.setOnFinished(e4 -> {
            countdownLabel.setVisible(false);
            isPaused = false;
            startGameLoop();
        });

        pause1.play();
    }

    private void startGameLoop() {
        updatePipeSpawnInterval();
        
        if (Settings.getInstance().isFullscreen()) {
            pipeSpawnAccumulator = pipeSpawnInterval - 1500.0;
        } else {
            pipeSpawnAccumulator = pipeSpawnInterval - 1000.0;
        }
        
        startBackgroundMusic();
        timer.start();
    }
    
    private void updatePipeSpawnInterval() {
        if (Settings.getInstance().isFullscreen()) {
            double screenWidth = 1920;
            double timeToTraverseScreen = screenWidth / pipeSpeed;
            pipeSpawnInterval = timeToTraverseScreen * 15.0;
            pipeSpawnInterval = Math.max(pipeSpawnInterval, 2000.0);
        } else {
            pipeSpawnInterval = 4600.0;
        }
    }

    public void restart() {
        stopBackgroundMusic();
        root.getChildren().clear();
        pipes.clear();
        score = 0;
        gameOver = false;
        isPaused = false;
        achievedNewHighScore = false;
        isFlickeringHighScore = false;
        backgroundX = 0;
        currentThemeIndex = 0;
        lastPipeTime = 0;
        pipeSpawnAccumulator = 0.0;
        
        currentLevel = 0;
        pipesPassedInCurrentLevel = 0;
        nextLevelBackground = null;
        
        fullscreenStateChecked = false;
        
        if (secondBackground != null && root.getChildren().contains(secondBackground)) {
            root.getChildren().remove(secondBackground);
            secondBackground = null;
        }
        
        if (Settings.getInstance().isFullscreen()) {
            pipeSpeed = 5.0;
            backgroundSpeed = 2.5;
        } else {
            pipeSpeed = 3.0;
            backgroundSpeed = 1.5;
        }
        
        updatePipeSpawnInterval();
        init();
        start();
    }

    private void spawnPipe() {
        boolean pipeNearSpawn = false;
        double spawnThreshold = Settings.getInstance().isFullscreen() ? 300 : 150;
        double spawnX = Settings.getInstance().isFullscreen() ? 2000 : 800;
        
        for (Pipe existingPipe : pipes) {
            if (Math.abs(existingPipe.getX() - spawnX) < spawnThreshold) {
                pipeNearSpawn = true;
                break;
            }
        }
        
        if (pipeNearSpawn) {
            return;
        }
        
        double pipeX;
        
        if (Settings.getInstance().isFullscreen()) {
            pipeX = 2000;
        } else {
            pipeX = 800;
        }
        
        Pipe pipe = new Pipe(pipeX, currentLevel % backgrounds.length);
        pipes.add(pipe);
        
        root.getChildren().addAll(pipe.getTopPipe(), pipe.getBottomPipe());

        if (isFlickeringHighScore) {
            pipesSinceHighScore++;
            
            
            if (pipesSinceHighScore >= 3) {
                stopBlinkingHighScore();
                
            }
        }
    }

    private boolean cachedIsFullscreen = false;
    private boolean fullscreenStateChecked = false;
    
    private static final int MAX_PIPES_PER_FRAME = 5;
    
    private void updatePipes() {
        if (!fullscreenStateChecked) {
            cachedIsFullscreen = Settings.getInstance().isFullscreen();
            fullscreenStateChecked = true;
        }
        
        final double speed = pipeSpeed;
        final boolean isFullscreen = cachedIsFullscreen;
        
        int pipeCount = 0;
        
        Iterator<Pipe> it = pipes.iterator();
        while (it.hasNext() && pipeCount < MAX_PIPES_PER_FRAME) {
            Pipe pipe = it.next();
            
            pipe.update(speed);
            pipeCount++;
            
            if (pipe.getTopPipe().getLayoutX() + 60 < bird.getX() && !pipe.getTopPipe().getProperties().containsKey("scored")) {
                score++;
                pipe.getTopPipe().getProperties().put("scored", true);
                
                if (isFullscreen) {
                    pipesPassedInCurrentLevel++;
                    
                    if (pipesPassedInCurrentLevel >= PIPES_PER_LEVEL) {
                        pipesPassedInCurrentLevel = 0;
                        
                        currentLevel++;
                        
                        currentThemeIndex = currentLevel % backgrounds.length;
                        
                        background.setImage(backgrounds[currentThemeIndex]);
                        
                        backgroundX = 0;
                        background.setLayoutX(0);
                        lastRenderedX = 0;
                        
                        if (secondBackground != null) {
                            secondBackground.setImage(backgrounds[currentThemeIndex]);
                            secondBackground.setLayoutX(background.getFitWidth());
                        }
                        
                        final int newColorIndex = currentThemeIndex;
                        for (Pipe existingPipe : pipes) {
                            existingPipe.updateColor(newColorIndex);
                        }
                        
                        playSound("point.mp3");
                    }
                } 
                else {
                    if (score > 0 && score % 10 == 0) {
                        int expectedLevel = score / 10;
                        
                        if (currentLevel < expectedLevel) {
                            currentLevel = expectedLevel;
                            
                            currentThemeIndex = currentLevel % backgrounds.length;
                            
                            if (nextLevelBackground != null) {
                                background.setImage(nextLevelBackground);
                                nextLevelBackground = null;
                            } else {
                                background.setImage(backgrounds[currentThemeIndex]);
                            }
                        }
                    }
                    
                    if (score % 10 == 9) {
                        int nextThemeIndex = ((score + 1) / 10) % backgrounds.length;
                        
                        if (nextLevelBackground == null) {
                            nextLevelBackground = backgrounds[nextThemeIndex];
                        }
                    }
                }
                
                continue;
            }
            
            if (pipe.isOffScreen()) {
                final Pipe pipeToRemove = pipe;
                it.remove();
                
                root.getChildren().removeAll(pipeToRemove.getTopPipe(), pipeToRemove.getBottomPipe());
            }
        }
    }

    private double cachedTopBoundary = -1;
    private double cachedBottomBoundary = -1;
    private boolean collisionBoundaryCached = false;
    
    private void checkCollision() {
        boolean isFullscreen = Settings.getInstance().isFullscreen();
        if (!collisionBoundaryCached || isFullscreenCached != isFullscreen) {
            collisionBoundaryCached = true;
            isFullscreenCached = isFullscreen;
            
            if (isFullscreen) {
                cachedTopBoundary = -26;
                cachedBottomBoundary = 797;
            } else {
                cachedTopBoundary = -10 * ScaleHelper.getScaleY();
                cachedBottomBoundary = 564 * ScaleHelper.getScaleY();
            }
        }
        
        double birdY = bird.getY();
        if (birdY <= cachedTopBoundary || birdY >= cachedBottomBoundary) {
            endGame();
            return;
        }
        
        double birdX = bird.getX();
        for (Pipe pipe : pipes) {
            double pipeX = pipe.getTopPipe().getLayoutX();
            if (Math.abs(pipeX - birdX) < 80) {
                if (pipe.collidesWith(bird)) {
                    endGame();
                    break;
                }
            }
        }
    }

    private double cachedScreenWidth = -1;
    private double cachedBgDisplayWidth = -1;
    private double cachedResetPoint = -1;
    private boolean isFullscreenCached = false;
    
    private double bgResetPoint = -2200;
    
    private double lastRenderedX = 0;
    private static final double BG_RENDER_THRESHOLD = 2.0;
    
    private void updateBackground() {
        backgroundX -= backgroundSpeed;
        
        if (Settings.getInstance().isFullscreen() && secondBackground != null) {
            if (backgroundX <= bgResetPoint) {
                backgroundX = 0;
                background.setLayoutX(backgroundX);
                secondBackground.setLayoutX(background.getFitWidth());
            } else {
                if (Math.abs(backgroundX - lastRenderedX) > BG_RENDER_THRESHOLD) {
                    background.setLayoutX(backgroundX);
                    secondBackground.setLayoutX(backgroundX + background.getFitWidth());
                    lastRenderedX = backgroundX;
                }
            }
        } else {
            if (backgroundX <= bgResetPoint) {
                backgroundX = 0;
            }
            
            if (Math.abs(backgroundX - lastRenderedX) > BG_RENDER_THRESHOLD) {
                background.setLayoutX(backgroundX);
                lastRenderedX = backgroundX;
            }
        }
    }

    private void endGame() {
        gameOver = true;
        timer.stop();
        stopBackgroundMusic();
        playSound("crash.mp3");
        saveHighScore();

         if (pauseMenu != null) {
            pauseMenu.setVisible(false);
        }

        Label gameOverLabel;
        Label restartLabel;
        Label resetHighScoreLabel;
        
        if (Settings.getInstance().isFullscreen()) {
            gameOverLabel = createLabel("Game Over!", 422, 300, Color.RED);
            gameOverLabel.setFont(Font.font(flappyFont.getFamily(), 72));
            gameOverLabel.setEffect(new DropShadow(20, Color.BLACK));
            
            String restartKeyName = Settings.getInstance().getRestartKey();
            String resetKeyName = Settings.getInstance().getResetKey();
            
          

            
            Font labelFont = Font.font(flappyFont.getFamily(), 36);
            
            String displayRestartKey = formatKeyNameForDisplay(restartKeyName);
            String displayResetKey = formatKeyNameForDisplay(resetKeyName);
            
            restartLabel = createLabel("Press " + displayRestartKey + " to restart", 30, 750, Color.WHITE);
            restartLabel.setFont(labelFont);
            
            resetHighScoreLabel = createLabel("Press " + displayResetKey + " to reset high score", 30, 800, Color.WHITE);
            resetHighScoreLabel.setFont(labelFont);
        } else {
            gameOverLabel = createLabel("Game Over!", ScaleHelper.scaleX(225), ScaleHelper.scaleY(230), Color.RED);
            gameOverLabel.setFont(Font.font(flappyFont.getFamily(), ScaleHelper.scaleHeight(36)));
            gameOverLabel.setEffect(new DropShadow(ScaleHelper.scaleHeight(10), Color.BLACK));
            
            String restartKeyName = Settings.getInstance().getRestartKey();
            String resetKeyName = Settings.getInstance().getResetKey();
            

            
            Font labelFont = Font.font(flappyFont.getFamily(), ScaleHelper.scaleHeight(20));
            
            String displayRestartKey = formatKeyNameForDisplay(restartKeyName);
            String displayResetKey = formatKeyNameForDisplay(resetKeyName);
            
            restartLabel = createLabel("Press " + displayRestartKey + " to restart", 
                ScaleHelper.scaleX(20), ScaleHelper.scaleY(530), Color.WHITE);
            restartLabel.setFont(labelFont);
            
            resetHighScoreLabel = createLabel("Press " + displayResetKey + " to reset high score", 
                ScaleHelper.scaleX(20), ScaleHelper.scaleY(560), Color.WHITE);
            resetHighScoreLabel.setFont(labelFont);
        }
        
        root.getChildren().add(gameOverLabel);
        root.getChildren().addAll(restartLabel, resetHighScoreLabel);

        startBlinkingTemporarily(gameOverLabel, 5);
        if (achievedNewHighScore) {
            newHighScoreLabel.setVisible(true);
            startBlinkingTemporarily(newHighScoreLabel, 5);
        }
    }

    private void startBlinkingTemporarily(Label label, int durationSeconds) {
        FadeTransition blink = new FadeTransition(Duration.seconds(0.5), label);
        blink.setFromValue(1.0);
        blink.setToValue(0.0);
        blink.setCycleCount(Animation.INDEFINITE);
        blink.setAutoReverse(true);
        blink.play();
        PauseTransition pause = new PauseTransition(Duration.seconds(durationSeconds));
        pause.setOnFinished(e -> blink.stop());
        pause.play();
    }

    private void startBlinkingHighScore() {
        pipesSinceHighScore = 0;
        isFlickeringHighScore = true;
        
        FadeTransition blink = new FadeTransition(Duration.seconds(0.5), highScoreLabel);
        blink.setFromValue(1.0);
        blink.setToValue(0.0);
        blink.setCycleCount(Animation.INDEFINITE);
        blink.setAutoReverse(true);
        blink.play();
        
        highScoreLabel.setUserData(blink);
        
    }

    private void stopBlinkingHighScore() {
        isFlickeringHighScore = false;
        if (highScoreLabel.getUserData() instanceof FadeTransition) {
            ((FadeTransition) highScoreLabel.getUserData()).stop();
            highScoreLabel.setOpacity(1.0);
            highScoreLabel.setUserData(null);
        }
    }
    
    
    private String formatKeyNameForDisplay(String keyName) {
        if (keyName == null || keyName.isEmpty()) {
            return "?";
        }
        
        switch (keyName.toLowerCase()) {
            case "enter":
                return "Enter";
            case "space":
                return "Space";
            case "up":
                return "Up Arrow";
            case "down":
                return "Down Arrow";
            case "left":
                return "Left Arrow";
            case "right":
                return "Right Arrow";
            case "tab":
                return "Tab";
            case "shift":
                return "Shift";
            case "control":
            case "ctrl":
                return "Ctrl";
            case "alt":
                return "Alt";
            case "escape":
                return "Escape";
            default:
                return keyName;
        }
    }

    public void resetHighScore() {
        showResetHighScoreConfirmation();
    }
    
    private void performHighScoreReset() {
        highScore = 0;
        achievedNewHighScore = false;
    
        root.getChildren().remove(highScoreLabel);
        
        if (Settings.getInstance().isFullscreen()) {
            highScoreLabel = createScoreLabel("High Score:", highScore, 1015, 30, Color.GOLD, 
                Font.font(flappyFont.getFamily(), 36));
        } else {
            highScoreLabel = createScoreLabel("High Score:", highScore, 
                ScaleHelper.getCurrentWidth() - ScaleHelper.scaleWidth(360), ScaleHelper.scaleY(20), Color.GOLD, null);
        }
        
        DropShadow textShadow = new DropShadow(ScaleHelper.scaleHeight(3), Color.BLACK);
        highScoreLabel.setEffect(textShadow);
        root.getChildren().add(highScoreLabel);
        
        saveHighScore();
        
        showTemporaryMessage("High score reset to 0!", 3);
    }
    
    private void showTemporaryMessage(String message, int seconds) {
        VBox messageBox = new VBox();
        messageBox.setAlignment(Pos.CENTER);
        messageBox.setPadding(new Insets(20));
        messageBox.setStyle(
            "-fx-background-color: rgba(139, 69, 19, 0.9);" +
            "-fx-border-color: yellow;" +
            "-fx-border-width: 3;" +
            "-fx-border-radius: 10;" +
            "-fx-background-radius: 10;"
        );
        
        Label messageLabel = new Label(message);
        messageLabel.setTextFill(Color.YELLOW);
        messageLabel.setFont(Font.font(flappyFont.getFamily(), 
            Settings.getInstance().isFullscreen() ? 42 : ScaleHelper.scaleHeight(28)));
        messageLabel.setWrapText(true);
        messageLabel.setTextAlignment(javafx.scene.text.TextAlignment.CENTER);
        messageLabel.setAlignment(Pos.CENTER);
        
        javafx.scene.effect.Glow glow = new javafx.scene.effect.Glow(0.5);
        messageLabel.setEffect(glow);
        
        DropShadow shadow = new DropShadow();
        shadow.setColor(Color.BLACK);
        shadow.setRadius(Settings.getInstance().isFullscreen() ? 5 : 3);
        messageLabel.setEffect(shadow);
        
        messageBox.getChildren().add(messageLabel);
        
        double boxWidth = Settings.getInstance().isFullscreen() ? 600 : ScaleHelper.scaleWidth(350);
        double boxHeight = Settings.getInstance().isFullscreen() ? 200 : ScaleHelper.scaleHeight(120);
        
        messageBox.setPrefSize(boxWidth, boxHeight);
        messageBox.setMinSize(boxWidth, boxHeight);
        messageBox.setMaxSize(boxWidth, boxHeight);
        
        double centerX = Settings.getInstance().isFullscreen() ? 
            (1920/2 - boxWidth/2 - 200) : (ScaleHelper.getCurrentWidth()/2 - boxWidth/2);  
        double centerY = Settings.getInstance().isFullscreen() ? 
            (1080/2 - boxHeight/2 - 180) : (ScaleHelper.getCurrentHeight()/2 - boxHeight/2);  
        
        messageBox.setLayoutX(centerX);
        messageBox.setLayoutY(centerY);
        
        root.getChildren().add(messageBox);
        messageBox.toFront();
        
        PauseTransition pause = new PauseTransition(Duration.seconds(seconds));
        pause.setOnFinished(e -> root.getChildren().remove(messageBox));
        pause.play();
    }

    private void showResetHighScoreConfirmation() {
        Stage confirmDialog = new Stage();
        confirmDialog.initStyle(StageStyle.UNDECORATED);
        
        Label confirmLabel = new Label("Reset high score?");
        confirmLabel.setTextFill(Color.YELLOW);
        confirmLabel.setWrapText(true);
        
        Glow glow = new Glow(0.5);
        confirmLabel.setEffect(glow);
        
        Font dialogFont;
        if (Settings.getInstance().isFullscreen()) {
            dialogFont = Font.font(flappyFont.getFamily(), 48); 
        } else {
            dialogFont = Font.font(flappyFont.getFamily(), ScaleHelper.scaleHeight(28));
        }
        confirmLabel.setFont(dialogFont);
        
        Button yesButton = new Button("YES");
        
        Font buttonFont;
        if (Settings.getInstance().isFullscreen()) {
            buttonFont = Font.font(dialogFont.getFamily(), 36);  
        } else {
            buttonFont = Font.font(dialogFont.getFamily(), ScaleHelper.scaleHeight(24)); 
        }
        yesButton.setFont(buttonFont);
        
        yesButton.setAlignment(Pos.CENTER);
        yesButton.setContentDisplay(javafx.scene.control.ContentDisplay.CENTER);
        double buttonWidth = Settings.getInstance().isFullscreen() ? 220 : ScaleHelper.scaleWidth(140);
        double buttonHeight = Settings.getInstance().isFullscreen() ? 100 : ScaleHelper.scaleHeight(70);
        
        String buttonStyle = 
            "-fx-background-color: gold;" + 
            "-fx-text-fill: #000000;" + 
            "-fx-font-weight: bold;" +
            "-fx-padding: 0;" +  
            "-fx-border-color: #8B4513;" +
            "-fx-border-width: 3;" +  
            "-fx-border-radius: 8;" +  
            "-fx-background-radius: 8;" + 
            "-fx-alignment: center;" +
            "-fx-content-display: center;" +  
            "-fx-text-alignment: center;";
            
        yesButton.setStyle(buttonStyle);
        yesButton.setMinSize(buttonWidth, buttonHeight);
        yesButton.setPrefSize(buttonWidth, buttonHeight);
        yesButton.setMaxSize(buttonWidth, buttonHeight);
        
        yesButton.setOnAction(e -> {
            confirmDialog.close();
            performHighScoreReset();
        });
        
        Button noButton = new Button("NO");
        noButton.setFont(buttonFont);
        
        noButton.setAlignment(Pos.CENTER);
        noButton.setContentDisplay(javafx.scene.control.ContentDisplay.CENTER);
        noButton.setStyle(buttonStyle);
        noButton.setMinSize(buttonWidth, buttonHeight);
        noButton.setPrefSize(buttonWidth, buttonHeight);
        noButton.setMaxSize(buttonWidth, buttonHeight);
        
        noButton.setOnAction(e -> confirmDialog.close());
        
        
        String hoverStyle = 
            "-fx-background-color: gold;" + 
            "-fx-text-fill: #8B4513;" + 
            "-fx-font-weight: bold;" +
            "-fx-padding: 0;" +  
            "-fx-border-color: #8B4513;" +
            "-fx-border-width: 3;" + 
            "-fx-border-radius: 8;" +  
            "-fx-background-radius: 8;" + 
            "-fx-alignment: center;" +
            "-fx-content-display: center;" +  
            "-fx-text-alignment: center;" +
            "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.6), 5, 0, 0, 0);";
            
        
        yesButton.setOnMouseEntered(e -> {
            yesButton.setStyle(hoverStyle);
           
            yesButton.setMinSize(buttonWidth, buttonHeight);
            yesButton.setPrefSize(buttonWidth, buttonHeight);
            yesButton.setMaxSize(buttonWidth, buttonHeight);
        });
        
        yesButton.setOnMouseExited(e -> {
            yesButton.setStyle(buttonStyle);
         
            yesButton.setMinSize(buttonWidth, buttonHeight);
            yesButton.setPrefSize(buttonWidth, buttonHeight);
            yesButton.setMaxSize(buttonWidth, buttonHeight);
        });
        
        noButton.setOnMouseEntered(e -> {
            noButton.setStyle(hoverStyle);
        
            noButton.setMinSize(buttonWidth, buttonHeight);
            noButton.setPrefSize(buttonWidth, buttonHeight);
            noButton.setMaxSize(buttonWidth, buttonHeight);
        });
        
        noButton.setOnMouseExited(e -> {
            noButton.setStyle(buttonStyle);
          
            noButton.setMinSize(buttonWidth, buttonHeight);
            noButton.setPrefSize(buttonWidth, buttonHeight);
            noButton.setMaxSize(buttonWidth, buttonHeight);
        });
      
        HBox buttonBox = new HBox(60, yesButton, noButton);  
        buttonBox.setAlignment(Pos.CENTER);
        buttonBox.setPadding(new Insets(30));  
        
        VBox dialogContent = new VBox(40, confirmLabel, buttonBox); 
        dialogContent.setAlignment(Pos.CENTER);
        dialogContent.setPadding(new Insets(30));
        dialogContent.setStyle("-fx-background-color: rgba(139, 69, 19, 0.9); -fx-border-color: yellow; -fx-border-width: 3;");
        
        double dialogWidth, dialogHeight;
        if (Settings.getInstance().isFullscreen()) {
            dialogWidth = 800;  
            dialogHeight = 400; 
        } else {
            dialogWidth = ScaleHelper.scaleWidth(500);
            dialogHeight = ScaleHelper.scaleHeight(300); }
     
        confirmLabel.setMinWidth(dialogWidth - 40);
        confirmLabel.setMaxWidth(dialogWidth - 40);
        confirmLabel.setPrefWidth(dialogWidth - 40);
        confirmLabel.setAlignment(Pos.CENTER);
        
        confirmLabel.setTextOverrun(javafx.scene.control.OverrunStyle.CLIP);
        confirmLabel.setEllipsisString("");
        
        confirmLabel.setMinHeight(60);
        
        Scene dialogScene = new Scene(dialogContent, dialogWidth, dialogHeight);
        confirmDialog.setScene(dialogScene);
        confirmDialog.initOwner(root.getScene().getWindow());
        confirmDialog.initModality(Modality.APPLICATION_MODAL);
        
        double centerX = root.getScene().getWindow().getX() + (root.getScene().getWindow().getWidth() - dialogWidth) / 2;
        double centerY = root.getScene().getWindow().getY() + (root.getScene().getWindow().getHeight() - dialogHeight) / 2;
        
        if (Settings.getInstance().isFullscreen()) {
            centerX -= 200; 
            centerY -= 180; 
        }
        
        confirmDialog.setX(centerX);
        confirmDialog.setY(centerY);
        
        confirmDialog.show();
    }
    
    private void loadHighScore() {
        File file = new File(HIGH_SCORE_FILE);
        if (file.exists()) {
            try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
                String line = reader.readLine();
                if (line != null) {
                    highScore = Integer.parseInt(line.trim());
                }
            } catch (IOException | NumberFormatException e) {
                highScore = 0;
            }
        }
    }

    private void saveHighScore() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(HIGH_SCORE_FILE))) {
            writer.write(String.valueOf(highScore));

        } catch (IOException ioException) {

        }
    }

    private void playSound(String filename) {
    try {
        String path = getClass().getResource("/resources/sounds/" + filename).toExternalForm();
        Media sound = new Media(path);
        MediaPlayer player = new MediaPlayer(sound);

        Settings settings = Settings.getInstance();

        if (filename.equals("crash.mp3")) {
            double volume = settings.getCrashVolume() / 100.0;
            player.setVolume(volume);
        } else if (filename.equals("horn1.mp3") || filename.equals("horn2.mp3") ||
                   filename.equals("horn3.mp3") || filename.equals("go.mp3")) {
            double volume = settings.getHornVolume() / 100.0;
            player.setVolume(volume);
        } else {
            player.setVolume(0.2);
        }

        player.setOnError(() -> { });
        player.play();
    } catch (Exception e) {
    }
}


public void startBackgroundMusic() {
    try {
        String path = getClass().getResource("/resources/sounds/background.mp3").toExternalForm();
        Media media = new Media(path);
        bgMusicPlayer = new MediaPlayer(media);
        bgMusicPlayer.setCycleCount(MediaPlayer.INDEFINITE);

        double volume = Settings.getInstance().getBgmVolume() / 100.0;
        bgMusicPlayer.setVolume(volume);

        bgMusicPlayer.setOnError(() -> { });
        bgMusicPlayer.play();
    } catch (Exception e) {
    }
}


    private void stopBackgroundMusic() {
        if (bgMusicPlayer != null) {
            bgMusicPlayer.stop();
        }
    }

    public void pauseGame() {
        isPaused = true;
        timer.stop();
        if (bgMusicPlayer != null) {
            bgMusicPlayer.pause();
        }
        
        if (pauseMenu != null) {
            root.getChildren().remove(pauseMenu);
        }
        
        pauseMenu = createPauseMenu();
        root.getChildren().add(pauseMenu);
        
        pauseMenu.setVisible(true);
        pauseMenu.toFront();
        

    }

    private void resumeGame() {
        if (pauseMenu != null) {
            pauseMenu.setVisible(false);
        }
        
        startCountdown();
        

    }


    public Bird getBird() {
        return bird;
    }

    public boolean isGameOver() {
        return gameOver;
    }

    public boolean isPaused() {
        return isPaused;
    }
}
