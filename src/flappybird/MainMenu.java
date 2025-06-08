package flappybird;
import javafx.animation.FadeTransition;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.effect.ColorAdjust;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCombination;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import javafx.util.Duration;
public class MainMenu {
    private Stage stage;
    private static double storedLogoX = -1;
    private static double storedLogoY = -1;
    private static double storedPlayButtonX = -1;
    private static double storedPlayButtonY = -1;
    private static double storedOptionsButtonX = -1;
    private static double storedOptionsButtonY = -1;
    private static double storedExitButtonX = -1;
    private static double storedExitButtonY = -1;
    private static boolean layoutStored = false;
    private static boolean comingFromFullscreenChange = false;
    public MainMenu(Stage stage) {
        this.stage = stage;
        if (Settings.getInstance().isFullscreen() || comingFromFullscreenChange) {
            layoutStored = false;
            storedLogoX = -1;
            storedLogoY = -1;
            storedPlayButtonX = -1;
            storedPlayButtonY = -1;
            storedOptionsButtonX = -1;
            storedOptionsButtonY = -1;
            storedExitButtonX = -1;
            storedExitButtonY = -1;
            comingFromFullscreenChange = false;
        }
    }
    public void show() {
        int randomSky;
        do {
            randomSky = (int) (Math.random() * 10) + 1;
        } while (randomSky == 4);
        String backgroundPath = "/resources/sky" + randomSky + ".png";
        Image bgImage = new Image(getClass().getResourceAsStream(backgroundPath));
        ImageView bgView = new ImageView(bgImage);
        double bgWidth, bgHeight;
        if (Settings.getInstance().isFullscreen()) {
            bgWidth = 7200;
            bgHeight = 1080;
        } else {
            bgWidth = 3000;
            bgHeight = 600;
            bgWidth = ScaleHelper.scaleWidth(bgWidth);
            bgHeight = ScaleHelper.scaleHeight(bgHeight);
        }
        if (bgWidth < ScaleHelper.getCurrentWidth()) {
            bgWidth = ScaleHelper.getCurrentWidth();
        }
        if (bgHeight < ScaleHelper.getCurrentHeight()) {
            bgHeight = ScaleHelper.getCurrentHeight();
        }
        bgView.setFitWidth(bgWidth);
        bgView.setFitHeight(bgHeight);
        bgView.setPreserveRatio(false);
        Pane layeredRoot = new Pane();
        layeredRoot.getChildren().add(bgView);
        ColorAdjust colorAdjust = new ColorAdjust();
        bgView.setEffect(colorAdjust);
        DropShadow dropShadow = new DropShadow();
        dropShadow.setOffsetX(ScaleHelper.scaleWidth(3));
        dropShadow.setOffsetY(ScaleHelper.scaleHeight(3));
        Image logoImage = new Image(getClass().getResource("/resources/mainmenu/main_menu_logo.png").toExternalForm());
        ImageView logoView = new ImageView(logoImage);
        logoView.setFitWidth(ScaleHelper.scaleWidth(300));
        logoView.setPreserveRatio(true);
        logoView.setEffect(dropShadow);
        double centerX = ScaleHelper.scaleX(400);
        double logoY = ScaleHelper.scaleY(50);
        logoView.setLayoutX(0);
        logoView.setLayoutY(0);
        logoView.setLayoutX(centerX - logoView.getBoundsInLocal().getWidth()/2 + 5);
        logoView.setLayoutY(logoY);
        layeredRoot.getChildren().add(logoView);
        ImageView startView = new ImageView(new Image(getClass().getResource("/resources/mainmenu/start.png").toExternalForm()));
        ImageView optionsView = new ImageView(new Image(getClass().getResource("/resources/mainmenu/options.png").toExternalForm()));
        ImageView scoresView = new ImageView(new Image(getClass().getResource("/resources/mainmenu/scores.png").toExternalForm()));
        ImageView exitView = new ImageView(new Image(getClass().getResource("/resources/mainmenu/exit.png").toExternalForm()));
        double buttonWidth = ScaleHelper.scaleWidth(160);
        startView.setFitWidth(buttonWidth);
        startView.setPreserveRatio(true);
        optionsView.setFitWidth(buttonWidth);
        optionsView.setPreserveRatio(true);
        scoresView.setFitWidth(buttonWidth);
        scoresView.setPreserveRatio(true);
        exitView.setFitWidth(buttonWidth);
        exitView.setPreserveRatio(true); 
        Button playButton = new Button();
        playButton.setGraphic(startView);
        styleButton(playButton);
        Button optionsButton = new Button();
        optionsButton.setGraphic(optionsView);
        styleButton(optionsButton);
        Button scoresButton = new Button();
        scoresButton.setGraphic(scoresView);
        styleButton(scoresButton);
        javafx.scene.control.Tooltip scoresToolTip = new javafx.scene.control.Tooltip(
            "Left-click: View high scores\nRight-click: Open in separate window");
        scoresToolTip.setStyle("-fx-font-size: 14px;");
        scoresButton.setTooltip(scoresToolTip);
        Button exitButton = new Button();
        exitButton.setGraphic(exitView);
        styleButton(exitButton);
        double spacing = ScaleHelper.scaleHeight(30);
        double buttonHeight = ScaleHelper.scaleHeight(80);
        double logoHeight = logoView.getBoundsInLocal().getHeight();
        double totalButtonsHeight = 4 * buttonHeight + 3 * spacing;
        double availableSpace = ScaleHelper.getCurrentHeight() - (logoY + logoHeight);
        double buttonsX = ScaleHelper.scaleX(320);
        double firstButtonY = logoY + logoHeight + (availableSpace - totalButtonsHeight) / 4;
        playButton.setLayoutX(buttonsX);
        playButton.setLayoutY(firstButtonY);
        optionsButton.setLayoutX(buttonsX);
        optionsButton.setLayoutY(firstButtonY + buttonHeight + spacing);
        scoresButton.setLayoutX(buttonsX);
        scoresButton.setLayoutY(firstButtonY + (buttonHeight + spacing) * 2);
        exitButton.setLayoutX(buttonsX);
        exitButton.setLayoutY(firstButtonY + (buttonHeight + spacing) * 3);
        playButton.setOnAction(e -> showBirdSelector());
        optionsButton.setOnAction(e -> new OptionsMenu(stage, () -> this.show(), false).show());
        scoresButton.setOnAction(e -> {
            new HighScoresScreen(stage, () -> this.show()).show();
        });
        scoresButton.setOnContextMenuRequested(e -> {
            new HighScoresScreen(stage).showInSeparateWindow();
            e.consume();
        });
        exitButton.setOnAction(e -> stage.close());
        layeredRoot.getChildren().addAll(playButton, optionsButton, scoresButton, exitButton);
        Settings settings = Settings.getInstance();
        int[] dimensions = settings.getDimensions();
        ScaleHelper.updateDimensions(dimensions[0], dimensions[1]);
        double updatedBgWidth, updatedBgHeight;
        if (Settings.getInstance().isFullscreen()) {
            updatedBgWidth = 7200;
            updatedBgHeight = 1080;
        } else {
            updatedBgWidth = 3000;
            updatedBgHeight = 600;
            updatedBgWidth = ScaleHelper.scaleWidth(updatedBgWidth);
            updatedBgHeight = ScaleHelper.scaleHeight(updatedBgHeight);
        }
        if (updatedBgWidth < ScaleHelper.getCurrentWidth()) {
            updatedBgWidth = ScaleHelper.getCurrentWidth();
        }
        if (updatedBgHeight < ScaleHelper.getCurrentHeight()) {
            updatedBgHeight = ScaleHelper.getCurrentHeight();
        }
        bgView.setFitWidth(updatedBgWidth);
        bgView.setFitHeight(updatedBgHeight);
        bgView.setPreserveRatio(false);
        logoView.setFitWidth(ScaleHelper.scaleWidth(300));
        logoView.setLayoutX(0);
        logoView.setLayoutY(0);
        double screenCenterX = ScaleHelper.scaleX(400);
        logoView.setLayoutX(screenCenterX - logoView.getBoundsInLocal().getWidth()/2 + 5);
        logoView.setLayoutY(ScaleHelper.scaleY(50));
        double updatedLogoHeight = logoView.getBoundsInLocal().getHeight();
        double updatedAvailableSpace = ScaleHelper.getCurrentHeight() - (ScaleHelper.scaleY(50) + updatedLogoHeight);
        double updatedFirstButtonY = ScaleHelper.scaleY(50) + updatedLogoHeight + (updatedAvailableSpace - totalButtonsHeight) / 4;
        playButton.setLayoutX(buttonsX);
        playButton.setLayoutY(updatedFirstButtonY);
        optionsButton.setLayoutX(buttonsX);
        optionsButton.setLayoutY(updatedFirstButtonY + buttonHeight + spacing);
        scoresButton.setLayoutX(buttonsX);
        scoresButton.setLayoutY(updatedFirstButtonY + (buttonHeight + spacing) * 2);
        exitButton.setLayoutX(buttonsX);
        exitButton.setLayoutY(updatedFirstButtonY + (buttonHeight + spacing) * 3);
        Scene scene = new Scene(layeredRoot, dimensions[0], dimensions[1]);
        scene.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ENTER) {
                showBirdSelector();
                event.consume();
            }
        });
        FadeTransition fadeIn = new FadeTransition(Duration.millis(800), layeredRoot);
        fadeIn.setFromValue(0);
        fadeIn.setToValue(1);
        fadeIn.play();
        stage.setScene(scene);
        if (comingFromFullscreenChange) {
            layoutStored = false;
            storedLogoX = -1;
            storedLogoY = -1;
            storedPlayButtonX = -1;
            storedPlayButtonY = -1;
            storedOptionsButtonX = -1;
            storedOptionsButtonY = -1;
            storedExitButtonX = -1;
            storedExitButtonY = -1;
        }
        applyFullscreen();
    }
    private void styleButton(Button button) {
        button.setStyle("-fx-background-color: transparent;");
        button.setPadding(Insets.EMPTY);
        double hoverScale = 1.1;
        button.setOnMouseEntered(e -> {
            button.setScaleX(hoverScale);
            button.setScaleY(hoverScale);
        });
        button.setOnMouseExited(e -> {
            button.setScaleX(1);
            button.setScaleY(1);
        });
    }
    private void applyFullscreen() {
        if (Settings.getInstance().isFullscreen()) {
            stage.setFullScreenExitHint("");
            stage.setFullScreenExitKeyCombination(KeyCombination.NO_MATCH);
            stage.setWidth(1920);
            stage.setHeight(1080);
            stage.setFullScreen(true);
            if (comingFromFullscreenChange) {
                comingFromFullscreenChange = false;
                javafx.application.Platform.runLater(() -> {
                    new MainMenu(stage).show();
                });
                return;
            }
            Pane root = (Pane) stage.getScene().getRoot();
            ImageView logoView = null;
            Button playButton = null;
            Button optionsButton = null;
            Button scoresButton = null;
            Button exitButton = null;
            for (javafx.scene.Node node : root.getChildren()) {
                if (node instanceof ImageView && node != root.getChildren().get(0)) {
                    logoView = (ImageView) node;
                } else if (node instanceof Button) {
                    if (playButton == null) {
                        playButton = (Button) node;
                    } else if (optionsButton == null) {
                        optionsButton = (Button) node;
                    } else if (scoresButton == null) {
                        scoresButton = (Button) node;
                    } else {
                        exitButton = (Button) node;
                    }
                }
            }
            if (logoView != null && playButton != null && optionsButton != null && scoresButton != null && exitButton != null) {
                ImageView startView = (ImageView) playButton.getGraphic();
                ImageView optionsView = (ImageView) optionsButton.getGraphic();
                ImageView scoresView = (ImageView) scoresButton.getGraphic();
                ImageView exitView = (ImageView) exitButton.getGraphic();
                logoView.setFitWidth(450);
                if (startView != null) {
                    startView.setFitWidth(200);
                }
                if (optionsView != null) {
                    optionsView.setFitWidth(200);
                }
                if (scoresView != null) {
                    scoresView.setFitWidth(200);
                }
                if (exitView != null) {
                    exitView.setFitWidth(200);
                }
                double screenWidth = 1920;
                double logoWidth = logoView.getFitWidth();
                double centerX = screenWidth / 2 - logoWidth / 2;
                double leftOffset = 200;
                logoView.setLayoutX(centerX - leftOffset);
                logoView.setLayoutY(100);
                double logoBottomY = logoView.getLayoutY() + logoView.getBoundsInLocal().getHeight();
                double buttonsX = screenWidth / 2 - 97 - leftOffset;
                double buttonSpacing = 140; 
                playButton.setLayoutX(buttonsX);
                playButton.setLayoutY(logoBottomY + 30);
                optionsButton.setLayoutX(buttonsX);
                optionsButton.setLayoutY(playButton.getLayoutY() + buttonSpacing);
                scoresButton.setLayoutX(buttonsX);
                scoresButton.setLayoutY(optionsButton.getLayoutY() + buttonSpacing);
                exitButton.setLayoutX(buttonsX);
                exitButton.setLayoutY(scoresButton.getLayoutY() + buttonSpacing);
                storedLogoX = logoView.getLayoutX();
                storedLogoY = logoView.getLayoutY();
                storedPlayButtonX = playButton.getLayoutX();
                storedPlayButtonY = playButton.getLayoutY();
                storedOptionsButtonX = optionsButton.getLayoutX();
                storedOptionsButtonY = optionsButton.getLayoutY();
                storedExitButtonX = exitButton.getLayoutX();
                storedExitButtonY = exitButton.getLayoutY();
                layoutStored = true;
            }
        }
    }
    private void showBirdSelector() {
        new BirdSelector(stage, this::startGame).show();
    }
    private void startGame() {
        Pane gameRoot = new Pane();
        Settings settings = Settings.getInstance();
        int[] dimensions = settings.getDimensions();
        ScaleHelper.updateDimensions(dimensions[0], dimensions[1]);
        Scene gameScene = new Scene(gameRoot, dimensions[0], dimensions[1]);
        Game game = new Game(gameRoot, null, () -> new MainMenu(stage).show());
        final Game gameRef = game;
        final Scene gameSceneRef = gameScene;
        game.setOptionsMenuCallback(() -> {
            new OptionsMenu(stage, () -> {
                stage.setScene(gameSceneRef);
                gameRef.pauseGame();
            }, true).show();
        });
        gameScene.setUserData(game);
        gameScene.addEventFilter(javafx.scene.input.KeyEvent.KEY_PRESSED, event -> {
            String jumpKeyStr = settings.getJumpKey();
            String restartKeyStr = settings.getRestartKey();
            String resetKeyStr = settings.getResetKey();
            KeyCode jumpKey = KeyCode.getKeyCode(jumpKeyStr);
            KeyCode restartKey = KeyCode.getKeyCode(restartKeyStr);
            KeyCode resetKey = KeyCode.getKeyCode(resetKeyStr);
            if (game.isHighScoresScreenShowing() && (event.getCode() == KeyCode.ENTER || event.getCode() == KeyCode.ESCAPE)) {
                game.forceResetHighScoresScreenFlag();
                event.consume();
                return;
            }
            if (game.isGameOver() && event.getCode() == resetKey) {
                game.resetHighScore();
                game.forceResetHighScoresScreenFlag();
                event.consume();
                return;
            }
            if (game.isHighScoresScreenShowing()) {
                event.consume();
                return;
            }
            if (game.isGameOver()) {
                if (event.getCode() == restartKey || event.getCode() == KeyCode.ENTER) {
                    game.restart();
                } 
                event.consume();
                return;
            }
            if ((event.getCode() == jumpKey || event.getCode() == KeyCode.SPACE) && !game.isPaused()) {
                game.getBird().jump();
            } 
            else if (event.getCode() == KeyCode.ESCAPE) {
                if (game.isPaused()) {
                } else {
                    game.pauseGame();
                }
                event.consume();
            }
        });
        stage.setScene(gameScene);
        applyFullscreen();
        game.start();
    }
}

