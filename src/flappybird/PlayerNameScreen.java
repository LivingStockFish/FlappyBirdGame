package flappybird;
import javafx.animation.FadeTransition;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.effect.DropShadow;
import javafx.scene.effect.Glow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCombination;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import javafx.util.Duration;
import java.util.List;
public class PlayerNameScreen {
    private final Stage stage;
    private final Runnable startGameCallback;
    private Font flappyFont;
    public PlayerNameScreen(Stage stage, Runnable startGameCallback) {
        this.stage = stage;
        this.startGameCallback = startGameCallback;
        try {
            Font font = Font.loadFont(getClass().getResourceAsStream("/fonts/flappyfont.ttf"), 24);
            this.flappyFont = font != null ? font : Font.font("Verdana", 24);
        } catch (Exception e) {
            this.flappyFont = Font.font("Verdana", 24);
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
        Settings settings = Settings.getInstance();
        int[] dimensions = settings.getDimensions();
        ScaleHelper.updateDimensions(dimensions[0], dimensions[1]);
        double bgWidth, bgHeight;
        if (settings.isFullscreen()) {
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
        Pane root = new Pane();
        root.getChildren().add(bgView);
        Label titleLabel = new Label("ENTER YOUR NAME");
        titleLabel.setTextFill(Color.GOLD);
        titleLabel.setWrapText(false); 
        Glow glow = new Glow(0.5);
        titleLabel.setEffect(glow);
        Font titleFont;
        if (Settings.getInstance().isFullscreen()) {
            titleFont = Font.font(flappyFont.getFamily(), 50); 
        } else {
            titleFont = Font.font(flappyFont.getFamily(), ScaleHelper.scaleHeight(32)); 
        }
        titleLabel.setFont(titleFont);
        titleLabel.setMinWidth(Settings.getInstance().isFullscreen() ? 800 : ScaleHelper.scaleWidth(500)); 
        titleLabel.setPrefWidth(Settings.getInstance().isFullscreen() ? 800 : ScaleHelper.scaleWidth(500)); 
        titleLabel.setMaxWidth(Settings.getInstance().isFullscreen() ? 800 : ScaleHelper.scaleWidth(500)); 
        titleLabel.setAlignment(Pos.CENTER);
        DropShadow dropShadow = new DropShadow();
        dropShadow.setOffsetX(ScaleHelper.scaleWidth(3));
        dropShadow.setOffsetY(ScaleHelper.scaleHeight(3));
        dropShadow.setColor(Color.BLACK);
        titleLabel.setEffect(dropShadow);
        TextField nameField = new TextField();
        String previousName = getPreviousPlayerName();
        if (previousName != null && !previousName.isEmpty() && !previousName.equals("Anonymous")) {
            nameField.setText(previousName);
        } else {
            nameField.setText("Player");
        }
        double textFieldWidth = Settings.getInstance().isFullscreen() ? 700 : ScaleHelper.scaleWidth(440); 
        double textFieldHeight = Settings.getInstance().isFullscreen() ? 75 : ScaleHelper.scaleHeight(50); 
        nameField.setMinSize(textFieldWidth, textFieldHeight);
        nameField.setPrefSize(textFieldWidth, textFieldHeight);
        nameField.setMaxSize(textFieldWidth, textFieldHeight);
        nameField.setStyle(
            "-fx-background-color: #FFFFE0;" + 
            "-fx-text-fill: #8B4513;" + 
            "-fx-font-size: " + (Settings.getInstance().isFullscreen() ? 36 : ScaleHelper.scaleHeight(24)) + "px;" + 
            "-fx-padding: 10;" +
            "-fx-border-color: #8B4513;" +
            "-fx-border-width: 3;" +
            "-fx-border-radius: 5;" +
            "-fx-background-radius: 5;"
        );
        Button startButton = new Button("START");
        Font buttonFont;
        if (Settings.getInstance().isFullscreen()) {
            buttonFont = Font.font(flappyFont.getFamily(), 32);  
        } else {
            buttonFont = Font.font(flappyFont.getFamily(), ScaleHelper.scaleHeight(20)); 
        }
        startButton.setFont(buttonFont);
        startButton.setAlignment(Pos.CENTER);
        startButton.setContentDisplay(javafx.scene.control.ContentDisplay.CENTER);
        double buttonWidth = Settings.getInstance().isFullscreen() ? 180 : ScaleHelper.scaleWidth(140); 
        double buttonHeight = Settings.getInstance().isFullscreen() ? 80 : ScaleHelper.scaleHeight(60); 
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
        startButton.setStyle(buttonStyle);
        startButton.setMinSize(buttonWidth, buttonHeight);
        startButton.setPrefSize(buttonWidth, buttonHeight);
        startButton.setMaxSize(buttonWidth, buttonHeight);
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
            "-fx-text-alignment: center;";
        startButton.setMinSize(buttonWidth, buttonHeight);
        startButton.setPrefSize(buttonWidth, buttonHeight);
        startButton.setMaxSize(buttonWidth, buttonHeight);
        startButton.setOnMouseEntered(e -> {
            startButton.setStyle(hoverStyle);
        });
        startButton.setOnMouseExited(e -> {
            startButton.setStyle(buttonStyle);
        });
        startButton.setOnMousePressed(e -> {
            startButton.setStyle(buttonStyle);
        });
        startButton.setOnMouseReleased(e -> {
            if (startButton.isHover()) {
                startButton.setStyle(hoverStyle);
            } else {
                startButton.setStyle(buttonStyle);
            }
        });
        Image backImage = new Image(getClass().getResource("/resources/mainmenu/back.png").toExternalForm());
        ImageView backView = new ImageView(backImage);
        backView.setFitWidth(ScaleHelper.scaleWidth(50));
        backView.setPreserveRatio(true);
        Button backButton = new Button();
        backButton.setGraphic(backView);
        styleButton(backButton);
        if (Settings.getInstance().isFullscreen()) {
            backButton.setLayoutX(100); 
            backButton.setLayoutY(100); 
            ImageView backGraphic = (ImageView) backButton.getGraphic();
            backGraphic.setFitWidth(80);
        } else {
            backButton.setLayoutX(ScaleHelper.scaleX(20));
            backButton.setLayoutY(ScaleHelper.scaleY(20));
        }
        backButton.setOnAction(e -> {
            new BirdSelector(stage, this::show).show();
        });
        VBox contentBox = new VBox(30);
        contentBox.setAlignment(Pos.CENTER);
        contentBox.getChildren().addAll(titleLabel, nameField, startButton);
        double contentWidth = Settings.getInstance().isFullscreen() ? 800 : ScaleHelper.scaleWidth(500); 
        contentBox.setMinWidth(contentWidth);
        contentBox.setMaxWidth(contentWidth);
        contentBox.setPrefWidth(contentWidth);
        contentBox.setPadding(new Insets(30));
        contentBox.setStyle("-fx-background-color: rgba(139, 69, 19, 0.7); -fx-border-color: gold; -fx-border-width: 3; -fx-border-radius: 10; -fx-background-radius: 10;");
        double contentBoxX;
        double contentBoxY;
        if (Settings.getInstance().isFullscreen()) {
            contentBoxX = 250; 
            contentBoxY = 170; 
        } else {
            contentBoxX = (ScaleHelper.getCurrentWidth() - contentWidth) / 2;
            contentBoxY = (ScaleHelper.getCurrentHeight() - contentBox.prefHeight(-1)) / 2 - ScaleHelper.scaleHeight(30);
        }
        contentBox.setLayoutX(contentBoxX);
        contentBox.setLayoutY(contentBoxY);
        root.getChildren().addAll(contentBox, backButton);
        Scene scene = new Scene(root, dimensions[0], dimensions[1]);
        scene.setOnKeyPressed(event -> {
            if (event.getCode() == javafx.scene.input.KeyCode.ENTER) {
                saveNameAndStartGame(nameField.getText());
                event.consume();
            }
        });
        startButton.setOnAction(e -> saveNameAndStartGame(nameField.getText()));
        FadeTransition fadeIn = new FadeTransition(Duration.millis(800), root);
        fadeIn.setFromValue(0);
        fadeIn.setToValue(1);
        fadeIn.play();
        stage.setScene(scene);
        applyFullscreen();
        Platform.runLater(() -> {
            nameField.requestFocus();
            nameField.selectAll();
        });
    }
    private void saveNameAndStartGame(String name) {
        String playerName = name.trim();
        if (playerName.isEmpty()) {
            playerName = "Anonymous";
        }
        playerName = playerName.replace("...", "");
        Settings.getInstance().setPlayerName(playerName);
        Settings.getInstance().saveSettings();
        if (startGameCallback != null) {
            startGameCallback.run();
        }
    }
    private String getPreviousPlayerName() {
        try {
            String savedName = Settings.getInstance().getPlayerName();
            if (savedName != null && !savedName.isEmpty()) {
                return savedName;
            }
            List<database.model.HighScore> highScores = database.DatabaseManager.getInstance().getTopHighScores(10);
            if (!highScores.isEmpty()) {
                return highScores.get(0).getPlayerName();
            }
        } catch (Exception e) {
        }
        return null;
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
            applyFullscreenLayout();
        }
    }
    private void applyFullscreenLayout() {
        Pane root = (Pane) stage.getScene().getRoot();
        VBox contentBox = null;
        Button backButton = null;
        for (javafx.scene.Node node : root.getChildren()) {
            if (node instanceof VBox) {
                contentBox = (VBox) node;
            } else if (node instanceof Button) {
                backButton = (Button) node;
            }
        }
        if (contentBox != null) {
            double contentWidth = 900; 
            contentBox.setMinWidth(contentWidth);
            contentBox.setMaxWidth(contentWidth);
            contentBox.setPrefWidth(contentWidth);
            double contentBoxX = 325; 
            double contentBoxY = 120; 
            contentBox.setLayoutX(contentBoxX);
            contentBox.setLayoutY(contentBoxY);
            for (javafx.scene.Node node : contentBox.getChildren()) {
                if (node instanceof Label) {
                    Label label = (Label) node;
                    label.setFont(Font.font(flappyFont.getFamily(), 50)); 
                    label.setMinWidth(800);
                    label.setPrefWidth(800);
                    label.setMaxWidth(800);
                } else if (node instanceof TextField) {
                    TextField textField = (TextField) node;
                    textField.setMinSize(500, 80);
                    textField.setPrefSize(500, 80);
                    textField.setMaxSize(500, 80);
                    textField.setStyle(
                        "-fx-background-color: #FFFFE0;" + 
                        "-fx-text-fill: #8B4513;" + 
                        "-fx-font-size: 40px;" +
                        "-fx-padding: 10;" +
                        "-fx-border-color: #8B4513;" +
                        "-fx-border-width: 3;" +
                        "-fx-border-radius: 5;" +
                        "-fx-background-radius: 5;"
                    );
                } else if (node instanceof Button) {
                    Button button = (Button) node;
                    button.setFont(Font.font(flappyFont.getFamily(), 36)); 
                    button.setMinSize(200, 100); 
                    button.setPrefSize(200, 100);
                    button.setMaxSize(200, 100);
                }
            }
        }
        if (backButton != null) {
            backButton.setLayoutX(50);
            backButton.setLayoutY(50);
            ImageView backView = (ImageView) backButton.getGraphic();
            if (backView != null) {
                backView.setFitWidth(80);
                backView.setPreserveRatio(true);
            }
        }
    }
}

