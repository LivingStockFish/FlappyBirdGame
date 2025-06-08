package flappybird;
import database.DatabaseManager;
import database.model.HighScore;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import java.text.SimpleDateFormat;
import java.util.List;
public class HighScoresScreen {
    private final Stage stage;
    private final Font customFont;
    private final Runnable returnCallback;
    private static boolean openedFromGameOver = false;
    public static void setOpenedFromGameOver(boolean value) {
        openedFromGameOver = value;
    }
    public static boolean isOpenedFromGameOver() {
        return openedFromGameOver;
    }
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
        if (Settings.getInstance().isFullscreen() || stage.isFullScreen()) {
            showInSeparateWindow(false);
        } else {
            showInSeparateWindow(true);
        }
    }
    public void showInSeparateWindow() {
        showInSeparateWindow(true);
    }
    private void showInSeparateWindow(boolean separateWindow) {
        Stage highScoresStage = new Stage();
        highScoresStage.setTitle("High Scores");
        double width, height;
        if (separateWindow) {
            highScoresStage.initStyle(StageStyle.UNDECORATED);
            highScoresStage.setResizable(false);
            if (!Settings.getInstance().isFullscreen() && !stage.isFullScreen()) {
                width = 800;  
                height = 601;  
            } else {
                width = 1280;  
                height = 720;
            }
            java.awt.Dimension screenSize = java.awt.Toolkit.getDefaultToolkit().getScreenSize();
            double screenWidth = screenSize.getWidth();
            double screenHeight = screenSize.getHeight();
            highScoresStage.setX((screenWidth - width) / 2);
            if (!Settings.getInstance().isFullscreen() && !stage.isFullScreen()) {
                highScoresStage.setY(((screenHeight - height) / 2)-25); 
            } else {
                highScoresStage.setY((screenHeight - height) / 2); 
            }
        } else if (Settings.getInstance().isFullscreen() || stage.isFullScreen()) {
            highScoresStage.initOwner(stage);
            highScoresStage.initModality(Modality.APPLICATION_MODAL);
            highScoresStage.initStyle(StageStyle.UNDECORATED);
            width = java.awt.Toolkit.getDefaultToolkit().getScreenSize().getWidth();
            height = 1080; 
            highScoresStage.setWidth(width);
            highScoresStage.setHeight(height);
            highScoresStage.setX(0);
            highScoresStage.setY(0);
        } else {
            highScoresStage.initOwner(stage);
            highScoresStage.initModality(Modality.APPLICATION_MODAL);
            highScoresStage.initStyle(StageStyle.UNDECORATED);
            int[] dimensions = Settings.getInstance().getDimensions();
            width = dimensions[0];  
            height = dimensions[1]; 
        }
        Pane layeredRoot = new Pane();
        layeredRoot.setPrefSize(width, height);
        Scene highScoresScene = new Scene(layeredRoot, width, height);
        highScoresScene.addEventFilter(javafx.scene.input.KeyEvent.KEY_PRESSED, event -> {
            if (event.getCode() == KeyCode.ESCAPE) {
                highScoresStage.close();
                if (!separateWindow && returnCallback != null) {
                    returnCallback.run();
                }
            }
            event.consume();
        });
        int randomSky = (int) (Math.random() * 10) + 1;
        String backgroundPath = "/resources/sky" + randomSky + ".png";
        Image bgImage = loadImage(backgroundPath);
        ImageView bgView = new ImageView(bgImage);
        double bgWidth, bgHeight;
        if (Settings.getInstance().isFullscreen()) {
            bgWidth = 7200;
            bgHeight = 1080;
        } else if (separateWindow && !Settings.getInstance().isFullscreen() && !stage.isFullScreen()) {
            bgWidth = 3100;  
            bgHeight = 605;  
            bgWidth = ScaleHelper.scaleWidth(bgWidth);
            bgHeight = ScaleHelper.scaleHeight(bgHeight);
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
        if (separateWindow && !Settings.getInstance().isFullscreen() && !stage.isFullScreen()) {
            bgView.setLayoutY(0); 
        }
        layeredRoot.getChildren().add(bgView);
        AnchorPane root = new AnchorPane();
        root.setPrefSize(width, height);
        Label titleLabel = new Label("High Scores");
        titleLabel.setFont(Font.font(customFont.getFamily(), ScaleHelper.scaleHeight(36)));
        titleLabel.setTextFill(Color.YELLOW);
        titleLabel.setEffect(new DropShadow(ScaleHelper.scaleHeight(5), Color.BLACK));
        double titleX, titleY;
        if (Settings.getInstance().isFullscreen() || highScoresStage.isFullScreen()) {
            titleLabel.setFont(Font.font(customFont.getFamily(), 72)); 
            titleX = (width / 2) - 400; 
            titleY = 30; 
        } else if (separateWindow) {
            titleLabel.setFont(Font.font(customFont.getFamily(), 48)); 
            if (!Settings.getInstance().isFullscreen() && !stage.isFullScreen()) {
                titleX = (width / 2) - 260; 
            } else {
                titleX = (width / 2) - 200; 
            }
            titleY = 20; 
        } else {
            int[] dimensions = Settings.getInstance().getDimensions();
            titleX = (dimensions[0] / 2) - 150;  
            titleY = 10;  
            titleLabel.setFont(Font.font(customFont.getFamily(), 36));
        }
        titleLabel.setLayoutX(titleX);
        titleLabel.setLayoutY(titleY);
        VBox scoresContainer = new VBox(ScaleHelper.scaleHeight(10));
        if (Settings.getInstance().isFullscreen() || highScoresStage.isFullScreen()) {
            scoresContainer.setPadding(new Insets(
                20,   
                70,   
                70,   
                20    
            ));
        } else if (separateWindow) {
            scoresContainer.setPadding(new Insets(
                20,   
                50,   
                50,   
                20    
            ));
        } else {
            scoresContainer.setPadding(new Insets(
                ScaleHelper.scaleHeight(20),  
                ScaleHelper.scaleHeight(30),  
                ScaleHelper.scaleHeight(40),  
                ScaleHelper.scaleHeight(20)   
            ));
        }
        scoresContainer.setStyle("-fx-background-color: transparent;");
        scoresContainer.setMinWidth(ScaleHelper.scaleWidth(780));
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
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
            SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm:ss");
            for (int i = 0; i < highScores.size(); i++) {
                HighScore score = highScores.get(i);
                String date = score.getDateAchieved() != null ? dateFormat.format(score.getDateAchieved()) : "N/A";
                String time = score.getDateAchieved() != null ? timeFormat.format(score.getDateAchieved()) : "N/A";
                HBox scoreRow = createScoreRow(
                    i + 1,
                    score.getPlayerName(),
                    score.getScore(),
                    date,
                    time
                );
                scoresContainer.getChildren().add(scoreRow);
            }
        }
        ScrollPane scrollPane = new ScrollPane(scoresContainer);
        scrollPane.setFitToWidth(false); 
        if (separateWindow && !Settings.getInstance().isFullscreen() && !stage.isFullScreen()) {
            scrollPane.setStyle(
                    "-fx-border-color: yellow; " +
                    "-fx-border-width: 2 2 1.5 2; " + 
                    "-fx-border-insets: 0 50 20 0; " + 
                    "-fx-background-color: rgba(0, 0, 0, 0.7); " +
                    "-fx-background: rgba(0, 0, 0, 0.7); " +
                    "-fx-background-insets: 0 50 20 0; " + 
                    "-fx-padding: 0;"); 
        } else {
            scrollPane.setStyle("-fx-border-color: yellow; -fx-border-width: 3; -fx-background-color: rgba(0, 0, 0, 0.7); -fx-background: rgba(0, 0, 0, 0.7);");
        }
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.ALWAYS);
        scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.ALWAYS);
        scrollPane.setPrefHeight(scoresContainer.getPrefHeight());
        scrollPane.setFitToHeight(false);
        highScoresStage.setOnShown(event -> {
            Platform.runLater(() -> {
                double containerHeight = scoresContainer.getHeight();
                scrollPane.setPrefHeight(containerHeight);
                scrollPane.lookup(".scroll-bar:vertical").setStyle(
                    "-fx-pref-height: " + containerHeight + "px; " +
                    "-fx-min-height: " + containerHeight + "px; " +
                    "-fx-max-height: " + containerHeight + "px;"
                );
            });
        });
        highScoresStage.heightProperty().addListener((obs, oldVal, newVal) -> {
            Platform.runLater(() -> {
                double containerHeight = scoresContainer.getHeight();
                scrollPane.setPrefHeight(containerHeight);
                scrollPane.lookup(".scroll-bar:vertical").setStyle(
                    "-fx-pref-height: " + containerHeight + "px; " +
                    "-fx-min-height: " + containerHeight + "px; " +
                    "-fx-max-height: " + containerHeight + "px;"
                );
            });
        });
        if (separateWindow && !Settings.getInstance().isFullscreen() && !stage.isFullScreen()) {
            scrollPane.setPadding(new Insets(0, 0, 0, 0)); 
            scoresContainer.setPadding(new Insets(
                ScaleHelper.scaleHeight(20),  
                ScaleHelper.scaleHeight(30),  
                ScaleHelper.scaleHeight(40),  
                ScaleHelper.scaleHeight(20)   
            ));
            highScoresStage.setOnShown(event -> {
                Platform.runLater(() -> {
                    scrollPane.lookup(".viewport").setStyle("-fx-padding: 0; -fx-background-insets: 0;");
                });
            });
        } else {
            scrollPane.setPadding(new Insets(0, 20, 20, 0));
        }
        String cssPath;
        if (Settings.getInstance().isFullscreen() || highScoresStage.isFullScreen() || separateWindow) {
            cssPath = getClass().getResource("/resources/styles/fullscreen-scrollbar.css").toExternalForm();
        } else {
            cssPath = getClass().getResource("/resources/styles/default-scrollbar.css").toExternalForm();
        }
        scrollPane.getStylesheets().add(cssPath);
        double scrollWidth, scrollHeight;
        if (separateWindow) {
            if (!Settings.getInstance().isFullscreen() && !stage.isFullScreen()) {
                scrollWidth = 1100;
                scrollHeight = 650; 
            } else {
                scrollWidth = 900;
                scrollHeight = 500;
            }
        } else {
            int[] dimensions = Settings.getInstance().getDimensions();
            scrollWidth = dimensions[0] - 50;   
            scrollHeight = dimensions[1] + 650 - 100; 
        }
        scrollPane.setPrefSize(scrollWidth, scrollHeight);
        scoresContainer.setMinWidth(ScaleHelper.scaleWidth(780));
        double scrollX, scrollY;
        if (Settings.getInstance().isFullscreen() || highScoresStage.isFullScreen()) {
            scrollWidth = 1200; 
            scrollHeight = 550; 
            scrollPane.setPrefSize(scrollWidth, scrollHeight);
            scoresContainer.setMinWidth(scrollWidth - 20); 
            scrollX = (width / 2) - (scrollWidth / 2); 
            scrollY = 140; 
        } else if (separateWindow) {
            if (!Settings.getInstance().isFullscreen() && !stage.isFullScreen()) {
                scrollWidth = 700;
                scrollHeight = 350; 
                scrollPane.setPrefSize(scrollWidth, scrollHeight);
                scoresContainer.setMinWidth(scrollWidth - 20);
                scrollX = (width / 2) - (scrollWidth / 2);
                scrollY = ((height / 2) - (scrollHeight / 2)) - 30; 
            } else {
                scrollWidth = 900;
                scrollHeight = 450;
                scrollPane.setPrefSize(scrollWidth, scrollHeight);
                scoresContainer.setMinWidth(scrollWidth - 20);
                scrollX = (width / 2) - (scrollWidth / 2);
                scrollY = 140; 
            }
        } else {
            int[] dimensions = Settings.getInstance().getDimensions();
            scrollX = (dimensions[0] - scrollWidth) / 2;  
            scrollY = 50;  
        }
        scrollPane.setLayoutX(scrollX);
        scrollPane.setLayoutY(scrollY);
        Button backButton = new Button("", new ImageView(loadImage("/resources/mainmenu/back.png")));
        if (Settings.getInstance().isFullscreen() || highScoresStage.isFullScreen()) {
            styleIconButton(backButton, 35); 
        } else if (separateWindow && !Settings.getInstance().isFullscreen() && !stage.isFullScreen()) {
            styleIconButton(backButton, 50); 
        } else if (separateWindow) {
            styleIconButton(backButton, 35);
        } else {
            styleIconButton(backButton);
        }
        backButton.setOnAction(e -> {
            if (isOpenedFromGameOver()) {
                setOpenedFromGameOver(false);
            }
            Game.resetAllHighScoresScreenFlags();
            highScoresStage.close();
            if (!separateWindow && returnCallback != null) {
                returnCallback.run();
            }
            if (highScoresStage.getOwner() != null) {
                Platform.runLater(() -> {
                    try {
                        Stage ownerStage = (Stage) highScoresStage.getOwner();
                        ownerStage.requestFocus();
                        if (ownerStage.getScene() != null) {
                            Scene scene = ownerStage.getScene();
                            scene.getRoot().requestFocus();
                            KeyCode dummyCode = KeyCode.F24; 
                            scene.getRoot().fireEvent(
                                new javafx.scene.input.KeyEvent(
                                    javafx.scene.input.KeyEvent.KEY_PRESSED, "", "", dummyCode, 
                                    false, false, false, false
                                )
                            );
                        }
                    } catch (Exception ex) {
                    }
                });
            }
        });
        double backX, backY;
        if (Settings.getInstance().isFullscreen() || highScoresStage.isFullScreen() || separateWindow) {
            backX = 20; 
            backY = 20; 
        } else {
            backX = ScaleHelper.scaleX(25);
            backY = ScaleHelper.scaleY(20);
        }
        backButton.setLayoutX(backX);
        backButton.setLayoutY(backY);
        root.getChildren().addAll(titleLabel, scrollPane, backButton);
        layeredRoot.getChildren().add(root);
        highScoresScene.setRoot(layeredRoot);
        highScoresStage.setScene(highScoresScene);
        highScoresScene.addEventFilter(javafx.scene.input.KeyEvent.KEY_PRESSED, event -> {
            if (event.getCode() == KeyCode.ESCAPE) {
                highScoresStage.close();
                if (returnCallback != null) {
                    returnCallback.run();
                }
            }
            if (event.getCode() == KeyCode.F4 && event.isAltDown()) {
                highScoresStage.close();
                if (!separateWindow && stage != null) {
                    javafx.application.Platform.runLater(() -> {
                        stage.close();
                    });
                }
                event.consume();
                return;
            }
            event.consume();
        });
        highScoresStage.setOnHidden(event -> {
            if (returnCallback != null && stage.isShowing()) {
                returnCallback.run();
            }
        });
        highScoresStage.showAndWait();
    }
    private HBox createHeaderRow() { 
         double spacing;
         if (Settings.getInstance().isFullscreen()) {
             spacing = -20; 
         } else {
             spacing = ScaleHelper.scaleWidth(15);
         }
         HBox row = new HBox(spacing);
        row.setAlignment(Pos.CENTER); 
        row.setPadding(new Insets(ScaleHelper.scaleHeight(10)));
        row.setStyle("-fx-border-color: yellow; -fx-border-width: 0 0 2 0;");
        if (Settings.getInstance().isFullscreen()) {
             row.setMinWidth(980); 
         } else {
             row.setMinWidth(ScaleHelper.scaleWidth(760));
         }
        Label rankHeader = createHeaderLabel("RANK");
        Label nameHeader = createHeaderLabel("PLAYER");
        Label scoreHeader = createHeaderLabel("SCORE");
        Label dateHeader = createHeaderLabel("DATE");
        Label timeHeader = createHeaderLabel("TIME");
        rankHeader.setPrefWidth(ScaleHelper.scaleWidth(80));
        nameHeader.setPrefWidth(ScaleHelper.scaleWidth(160)); 
        scoreHeader.setPrefWidth(ScaleHelper.scaleWidth(100));
        dateHeader.setPrefWidth(ScaleHelper.scaleWidth(120));
        timeHeader.setPrefWidth(ScaleHelper.scaleWidth(120));
        rankHeader.setPadding(new Insets(0, 2, 0, 2));
        nameHeader.setPadding(new Insets(0, 2, 0, 2));
        scoreHeader.setPadding(new Insets(0, 2, 0, 2));
        dateHeader.setPadding(new Insets(0, 2, 0, 2));
        timeHeader.setPadding(new Insets(0, 2, 0, 2));
        HBox rankBox = new HBox(rankHeader);
        HBox nameBox = new HBox(nameHeader);
        HBox scoreBox = new HBox(scoreHeader);
        HBox dateBox = new HBox(dateHeader);
        HBox timeBox = new HBox(timeHeader);
        rankBox.setAlignment(Pos.CENTER);
        nameBox.setAlignment(Pos.CENTER);
        scoreBox.setAlignment(Pos.CENTER);
        dateBox.setAlignment(Pos.CENTER);
        timeBox.setAlignment(Pos.CENTER);
        rankBox.setPrefWidth(ScaleHelper.scaleWidth(80));
        nameBox.setPrefWidth(ScaleHelper.scaleWidth(160)); 
        scoreBox.setPrefWidth(ScaleHelper.scaleWidth(100));
        dateBox.setPrefWidth(ScaleHelper.scaleWidth(120));
        timeBox.setPrefWidth(ScaleHelper.scaleWidth(120));
        row.getChildren().addAll(rankBox, nameBox, scoreBox, dateBox, timeBox);
        return row;
    }
    private Label createHeaderLabel(String text) {
        if (text != null) {
            text = text.replace("...", "");
        }
        Label label = new Label(text);
        if (Settings.getInstance().isFullscreen()) {
            label.setFont(Font.font(customFont.getFamily(), 22)); 
        } else {
            label.setFont(Font.font(customFont.getFamily(), ScaleHelper.scaleHeight(18)));
        }
        label.setTextFill(Color.GOLD);
        label.setTextOverrun(javafx.scene.control.OverrunStyle.CLIP);
        label.setMinWidth(javafx.scene.control.Control.USE_PREF_SIZE);
        label.setMaxWidth(Double.MAX_VALUE);
        label.setAlignment(Pos.CENTER);
        label.setWrapText(false);
        return label;
    }
    private HBox createScoreRow(int rank, String playerName, int score, String date, String time) {
        double spacing;
        if (Settings.getInstance().isFullscreen()) {
            spacing = -20; 
        } else {
            spacing = ScaleHelper.scaleWidth(15);
        }
        HBox row = new HBox(spacing);
        row.setAlignment(Pos.CENTER); 
        row.setPadding(new Insets(ScaleHelper.scaleHeight(10)));
        if (Settings.getInstance().isFullscreen()) {
            row.setMinWidth(980); 
        } else {
            row.setMinWidth(ScaleHelper.scaleWidth(760));
        }
        if (rank % 2 == 0) {
            row.setStyle("-fx-background-color: rgba(255, 255, 255, 0.1);");
        } else {
            row.setStyle("-fx-background-color: transparent;");
        }
        if (playerName != null) {
            playerName = playerName.replace("...", "");
        }
        if (date != null) {
            date = date.replace("...", "");
        }
        if (time != null) {
            time = time.replace("...", "");
        }
        Label rankLabel = createScoreLabel(String.valueOf(rank));
        Label nameLabel = createScoreLabel(playerName);
        Label scoreLabel = createScoreLabel(String.valueOf(score));
        Label dateLabel = createScoreLabel(date);
        Label timeLabel = createScoreLabel(time);
        rankLabel.setPrefWidth(ScaleHelper.scaleWidth(80));
        nameLabel.setPrefWidth(ScaleHelper.scaleWidth(160)); 
        scoreLabel.setPrefWidth(ScaleHelper.scaleWidth(100));
        dateLabel.setPrefWidth(ScaleHelper.scaleWidth(120));
        timeLabel.setPrefWidth(ScaleHelper.scaleWidth(120));
        rankLabel.setWrapText(false);
        nameLabel.setWrapText(false);
        scoreLabel.setWrapText(false);
        dateLabel.setWrapText(false);
        timeLabel.setWrapText(false);
        rankLabel.setTextOverrun(javafx.scene.control.OverrunStyle.CLIP);
        nameLabel.setTextOverrun(javafx.scene.control.OverrunStyle.CLIP);
        scoreLabel.setTextOverrun(javafx.scene.control.OverrunStyle.CLIP);
        dateLabel.setTextOverrun(javafx.scene.control.OverrunStyle.CLIP);
        timeLabel.setTextOverrun(javafx.scene.control.OverrunStyle.CLIP);
        javafx.scene.control.Tooltip nameTip = new javafx.scene.control.Tooltip(playerName);
        javafx.scene.control.Tooltip dateTip = new javafx.scene.control.Tooltip(date);
        javafx.scene.control.Tooltip timeTip = new javafx.scene.control.Tooltip(time);
        javafx.scene.control.Tooltip.install(nameLabel, nameTip);
        javafx.scene.control.Tooltip.install(dateLabel, dateTip);
        javafx.scene.control.Tooltip.install(timeLabel, timeTip);
        rankLabel.setPadding(new Insets(0, 2, 0, 2));
        nameLabel.setPadding(new Insets(0, 2, 0, 2));
        scoreLabel.setPadding(new Insets(0, 2, 0, 2));
        dateLabel.setPadding(new Insets(0, 2, 0, 2));
        timeLabel.setPadding(new Insets(0, 2, 0, 2));
        HBox rankBox = new HBox(rankLabel);
        HBox nameBox = new HBox(nameLabel);
        HBox scoreBox = new HBox(scoreLabel);
        HBox dateBox = new HBox(dateLabel);
        HBox timeBox = new HBox(timeLabel);
        rankBox.setAlignment(Pos.CENTER);
        nameBox.setAlignment(Pos.CENTER);
        scoreBox.setAlignment(Pos.CENTER);
        dateBox.setAlignment(Pos.CENTER);
        timeBox.setAlignment(Pos.CENTER);
        rankBox.setPrefWidth(ScaleHelper.scaleWidth(80));
        nameBox.setPrefWidth(ScaleHelper.scaleWidth(160)); 
        scoreBox.setPrefWidth(ScaleHelper.scaleWidth(100));
        dateBox.setPrefWidth(ScaleHelper.scaleWidth(120));
        timeBox.setPrefWidth(ScaleHelper.scaleWidth(120));
        row.getChildren().addAll(rankBox, nameBox, scoreBox, dateBox, timeBox);
        return row;
    }
    private Label createScoreLabel(String text) {
        if (text == null) {
            text = "";
        }
        text = text.replace("...", "");
        Label label = new Label(text);
        if (Settings.getInstance().isFullscreen()) {
            label.setFont(Font.font(customFont.getFamily(), 20)); 
        } else {
            label.setFont(Font.font(customFont.getFamily(), ScaleHelper.scaleHeight(16)));
        }
        label.setTextFill(Color.WHITE);
        label.setTextOverrun(javafx.scene.control.OverrunStyle.CLIP);
        label.setMinWidth(javafx.scene.control.Control.USE_PREF_SIZE);
        label.setMaxWidth(Double.MAX_VALUE);
        label.setWrapText(false);
        label.setAlignment(Pos.CENTER);
        return label;
    }
    private void styleIconButton(Button button) {
        styleIconButton(button, 60);
    }
    private void styleIconButton(Button button, int size) {
        ImageView view = (ImageView) button.getGraphic();
        view.setFitWidth(ScaleHelper.scaleWidth(size));
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
        titleLabel.setLayoutX(670); 
        titleLabel.setLayoutY(175);
        titleLabel.setFont(Font.font(customFont.getFamily(), 60));
        titleLabel.setEffect(new DropShadow(15, Color.BLACK));
        scrollPane.setLayoutX(250); 
        scrollPane.setLayoutY(275);
        scrollPane.setPrefSize(1415, 600); 
        backButton.setLayoutX(250);
        backButton.setLayoutY(170); 
        ImageView backGraphic = (ImageView) backButton.getGraphic();
        backGraphic.setFitWidth(80);
    }
    private Image loadImage(String path) {
        return new Image(getClass().getResource(path).toExternalForm());
    }
    private void findAndResetGameFlag(javafx.scene.Node node) {
        if (node.getScene() != null) {
            if (node.getScene().getWindow() instanceof Stage) {
                Stage stage = (Stage) node.getScene().getWindow();
                if (stage.getUserData() instanceof Game) {
                    Game game = (Game) stage.getUserData();
                    game.forceResetHighScoresScreenFlag();
                    return; 
                }
            }
            if (node.getScene().getUserData() instanceof Game) {
                Game game = (Game) node.getScene().getUserData();
                game.forceResetHighScoresScreenFlag();
                return; 
            }
        }
        if (node.getUserData() instanceof Game) {
            Game game = (Game) node.getUserData();
            game.forceResetHighScoresScreenFlag();
            return; 
        }
        Game.resetAllHighScoresScreenFlags();
    }
}

