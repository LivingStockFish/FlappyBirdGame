package flappybird;

import database.DatabaseManager;
import database.model.HighScore;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.Stage;

import java.text.SimpleDateFormat;
import java.util.List;

public class HighScoresScreen {
    private final Stage stage;
    private final Font customFont;
    private final Runnable returnCallback;

    public HighScoresScreen(Stage stage) {
        this(stage, () -> new MainMenu(stage).show());
    }
    
    public HighScoresScreen(Stage stage, Runnable returnCallback) {
        this.stage = stage;
        this.returnCallback = returnCallback;
        Font font = Font.loadFont(getClass().getResourceAsStream("/fonts/flappyfont.ttf"), 18);
        this.customFont = font != null ? font : Font.font("Verdana", 18);
    }

    public void show() {
        Pane layeredRoot = new Pane();
        int[] dimensions = Settings.getInstance().getDimensions();
        ScaleHelper.updateDimensions(dimensions[0], dimensions[1]);
        layeredRoot.setPrefSize(dimensions[0], dimensions[1]);
        
        int randomSky = (int) (Math.random() * 10) + 1;
        String backgroundPath = "/resources/sky" + randomSky + ".png";
        Image bgImage = loadImage(backgroundPath);
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
        layeredRoot.getChildren().add(bgView);
        
        AnchorPane root = new AnchorPane();
        root.setPrefSize(dimensions[0], dimensions[1]);
        
        Label titleLabel = new Label("High Scores");
        titleLabel.setFont(Font.font(customFont.getFamily(), ScaleHelper.scaleHeight(36)));
        titleLabel.setTextFill(Color.YELLOW);
        titleLabel.setEffect(new DropShadow(ScaleHelper.scaleHeight(5), Color.BLACK));
        
        double titleX = ScaleHelper.scaleX(250);
        double titleY = ScaleHelper.scaleY(30);
        titleLabel.setLayoutX(titleX);
        titleLabel.setLayoutY(titleY);
        
        VBox scoresContainer = new VBox(ScaleHelper.scaleHeight(10));
        scoresContainer.setPadding(new Insets(ScaleHelper.scaleHeight(20)));
        scoresContainer.setStyle("-fx-background-color: rgba(0, 0, 0, 0.7); -fx-border-color: gold; -fx-border-width: 3;");
        
        HBox headerRow = createHeaderRow();
        scoresContainer.getChildren().add(headerRow);
        
        List<HighScore> highScores = DatabaseManager.getInstance().getTopHighScores(10);
        
        if (highScores.isEmpty()) {
            Label noScoresLabel = new Label("No high scores yet!");
            noScoresLabel.setFont(Font.font(customFont.getFamily(), ScaleHelper.scaleHeight(24)));
            noScoresLabel.setTextFill(Color.WHITE);
            noScoresLabel.setPadding(new Insets(ScaleHelper.scaleHeight(20)));
            scoresContainer.getChildren().add(noScoresLabel);
        } else {
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
            for (int i = 0; i < highScores.size(); i++) {
                HighScore score = highScores.get(i);
                HBox scoreRow = createScoreRow(
                    i + 1,
                    score.getPlayerName(),
                    score.getScore(),
                    score.getDateAchieved() != null ? dateFormat.format(score.getDateAchieved()) : "N/A"
                );
                scoresContainer.getChildren().add(scoreRow);
            }
        }
        
        ScrollPane scrollPane = new ScrollPane(scoresContainer);
        scrollPane.setFitToWidth(true);
        scrollPane.setStyle("-fx-background: transparent; -fx-background-color: transparent;");
        
        double scrollWidth = ScaleHelper.scaleWidth(600);
        double scrollHeight = ScaleHelper.scaleHeight(400);
        scrollPane.setPrefSize(scrollWidth, scrollHeight);
        
        double scrollX = ScaleHelper.scaleX(100);
        double scrollY = ScaleHelper.scaleY(80);
        scrollPane.setLayoutX(scrollX);
        scrollPane.setLayoutY(scrollY);
        
        Button backButton = new Button("", new ImageView(loadImage("/resources/mainmenu/back.png")));
        styleIconButton(backButton);
        backButton.setOnAction(e -> {
            if (returnCallback != null) {
                returnCallback.run();
            }
        });
        
        double backX = ScaleHelper.scaleX(25);
        double backY = ScaleHelper.scaleY(25);
        backButton.setLayoutX(backX);
        backButton.setLayoutY(backY);
        
        root.getChildren().addAll(titleLabel, scrollPane, backButton);
        layeredRoot.getChildren().add(root);
        
        Scene scene = new Scene(layeredRoot, dimensions[0], dimensions[1]);
        stage.setScene(scene);
        
        if (Settings.getInstance().isFullscreen()) {
            applyFullscreenLayout(titleLabel, scrollPane, backButton);
        }
    }
    
    private HBox createHeaderRow() {
        HBox row = new HBox(ScaleHelper.scaleWidth(20));
        row.setAlignment(Pos.CENTER_LEFT);
        row.setPadding(new Insets(ScaleHelper.scaleHeight(10)));
        row.setStyle("-fx-border-color: gold; -fx-border-width: 0 0 2 0;");
        
        Label rankHeader = createHeaderLabel("Rank");
        Label nameHeader = createHeaderLabel("Player");
        Label scoreHeader = createHeaderLabel("Score");
        Label dateHeader = createHeaderLabel("Date");
        
        rankHeader.setPrefWidth(ScaleHelper.scaleWidth(60));
        nameHeader.setPrefWidth(ScaleHelper.scaleWidth(150));
        scoreHeader.setPrefWidth(ScaleHelper.scaleWidth(100));
        dateHeader.setPrefWidth(ScaleHelper.scaleWidth(200));
        
        row.getChildren().addAll(rankHeader, nameHeader, scoreHeader, dateHeader);
        return row;
    }
    
    private Label createHeaderLabel(String text) {
        Label label = new Label(text);
        label.setFont(Font.font(customFont.getFamily(), ScaleHelper.scaleHeight(20)));
        label.setTextFill(Color.GOLD);
        return label;
    }
    
    private HBox createScoreRow(int rank, String playerName, int score, String date) {
        HBox row = new HBox(ScaleHelper.scaleWidth(20));
        row.setAlignment(Pos.CENTER_LEFT);
        row.setPadding(new Insets(ScaleHelper.scaleHeight(10)));
        
        if (rank % 2 == 0) {
            row.setStyle("-fx-background-color: rgba(255, 255, 255, 0.1);");
        }
        
        Label rankLabel = createScoreLabel(String.valueOf(rank));
        Label nameLabel = createScoreLabel(playerName);
        Label scoreLabel = createScoreLabel(String.valueOf(score));
        Label dateLabel = createScoreLabel(date);
        
        rankLabel.setPrefWidth(ScaleHelper.scaleWidth(60));
        nameLabel.setPrefWidth(ScaleHelper.scaleWidth(150));
        scoreLabel.setPrefWidth(ScaleHelper.scaleWidth(100));
        dateLabel.setPrefWidth(ScaleHelper.scaleWidth(200));
        
        row.getChildren().addAll(rankLabel, nameLabel, scoreLabel, dateLabel);
        return row;
    }
    
    private Label createScoreLabel(String text) {
        Label label = new Label(text);
        label.setFont(Font.font(customFont.getFamily(), ScaleHelper.scaleHeight(18)));
        label.setTextFill(Color.WHITE);
        return label;
    }
    
    private void styleIconButton(Button button) {
        ImageView view = (ImageView) button.getGraphic();
        view.setFitWidth(ScaleHelper.scaleWidth(60));
        view.setPreserveRatio(true);
        button.setStyle("-fx-background-color: transparent;");
        
        double hoverScale = 1.1;
        button.setOnMouseEntered(e -> {
            button.setScaleX(hoverScale);
            button.setScaleY(hoverScale);
        });
        
        button.setOnMouseExited(e -> {
            button.setScaleX(1.0);
            button.setScaleY(1.0);
        });
    }
    
    private void applyFullscreenLayout(Label titleLabel, ScrollPane scrollPane, Button backButton) {
        titleLabel.setLayoutX(800);
        titleLabel.setLayoutY(100);
        titleLabel.setFont(Font.font(customFont.getFamily(), 60));
        titleLabel.setEffect(new DropShadow(15, Color.BLACK));
        
        scrollPane.setLayoutX(500);
        scrollPane.setLayoutY(200);
        scrollPane.setPrefSize(1000, 600);
        
        backButton.setLayoutX(100);
        backButton.setLayoutY(100);
        ImageView backGraphic = (ImageView) backButton.getGraphic();
        backGraphic.setFitWidth(80);
    }
    
    private Image loadImage(String path) {
        return new Image(getClass().getResource(path).toExternalForm());
    }
}