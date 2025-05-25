package flappybird;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.geometry.VPos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCombination;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.*;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.application.Platform;

public class OptionsMenu {
    private final Stage stage;
    private final Font customFont;
    private final Runnable returnCallback;
    private final boolean fromPauseMenu;

    public OptionsMenu(Stage stage) {
        this(stage, () -> new MainMenu(stage).show(), false);
    }
    
    public OptionsMenu(Stage stage, Runnable returnCallback, boolean fromPauseMenu) {
        this.stage = stage;
        this.returnCallback = returnCallback;
        this.fromPauseMenu = fromPauseMenu;
        Font font = Font.loadFont(getClass().getResourceAsStream("/fonts/flappyfont.ttf"), 18);
        this.customFont = font != null ? font : Font.font("Verdana", 18);
    }

    public void show() {
        Pane layeredRoot = new Pane();
        int[] dimensions = Settings.getInstance().getDimensions();
        ScaleHelper.updateDimensions(dimensions[0], dimensions[1]);
        layeredRoot.setPrefSize(dimensions[0], dimensions[1]);
        int randomSky;
        do {
            randomSky = (int) (Math.random() * 10) + 1;
        } while (randomSky == 4);
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
        Label titleLabel = new Label(fromPauseMenu ? "  Options" : "  Options");
        titleLabel.setFont(Font.font(customFont.getFamily(), ScaleHelper.scaleHeight(30)));
        titleLabel.setTextFill(Color.YELLOW);
        titleLabel.setLayoutX(ScaleHelper.scaleX(250));
        titleLabel.setLayoutY(ScaleHelper.scaleY(20));
        root.getChildren().add(titleLabel);
        layeredRoot.getChildren().add(root);
        Settings settings = Settings.getInstance();
        Slider hornSlider = createStyledSlider(settings.getHornVolume());
        Slider crashSlider = createStyledSlider(settings.getCrashVolume());
        Slider bgmSlider = createStyledSlider(settings.getBgmVolume());
        Label jumpKeyLabel = createStyledKeyLabel(settings.getJumpKey());
        Label resetKeyLabel = createStyledKeyLabel(settings.getResetKey());
        Label restartKeyLabel = createStyledKeyLabel(settings.getRestartKey());
        ToggleButton fullscreenToggle = createStyledToggleButton(settings.isFullscreen());
        fullscreenToggle.selectedProperty().addListener((obs, oldVal, newVal) -> {});
        jumpKeyLabel.setOnMouseClicked(e -> showKeyCaptureDialog("Jump", jumpKeyLabel));
        resetKeyLabel.setOnMouseClicked(e -> showKeyCaptureDialog("Reset", resetKeyLabel));
        restartKeyLabel.setOnMouseClicked(e -> showKeyCaptureDialog("Restart", restartKeyLabel));
        GridPane controls = new GridPane();
        controls.setPadding(new Insets(ScaleHelper.scaleHeight(40)));
        controls.setVgap(ScaleHelper.scaleHeight(20));
        controls.setHgap(ScaleHelper.scaleWidth(20));
        controls.setAlignment(Pos.CENTER_LEFT);
        addSliderRow(controls, 0, "CountDown", "Volume", hornSlider);
        addSliderRow(controls, 1, "Crash", "Volume", crashSlider);
        addSliderRow(controls, 2, "BGM", "Volume", bgmSlider);
        addInputRow(controls, 3, "Jump", "Key", jumpKeyLabel);
        addInputRow(controls, 4, "Reset", "Key", resetKeyLabel);
        addInputRow(controls, 5, "Restart", "Key", restartKeyLabel);
        addToggleRow(controls, 6, "Full", "Screen", fullscreenToggle);
        AnchorPane.setTopAnchor(controls, ScaleHelper.scaleY(30.0));
        AnchorPane.setLeftAnchor(controls, ScaleHelper.scaleX(150.0));
        root.getChildren().add(controls);
        Button tickButton = new Button("", new ImageView(loadImage("/resources/mainmenu/tick.png")));
        styleIconButton(tickButton);
        Button backButton = new Button("", new ImageView(loadImage("/resources/mainmenu/back.png")));
        styleIconButton(backButton);
        tickButton.setOnAction(e -> {
            settings.setHornVolume(hornSlider.getValue());
            settings.setCrashVolume(crashSlider.getValue());
            settings.setBgmVolume(bgmSlider.getValue());
            settings.setJumpKey(jumpKeyLabel.getText());
            settings.setResetKey(resetKeyLabel.getText());
            settings.setRestartKey(restartKeyLabel.getText());
            boolean oldFullscreen = settings.isFullscreen();
            settings.setFullscreen(fullscreenToggle.isSelected());
            settings.setAspectRatio("Default");
            settings.saveSettings();
            boolean fullscreenChanged = oldFullscreen != settings.isFullscreen();
            if (fullscreenChanged) {
                applyWindowSizeChanges();
                resetMainMenuLayoutStorage();
                return;
            }
            showPopup("Settings applied!");
        });
        backButton.setOnAction(e -> {
            boolean fullscreenChanged = Settings.getInstance().isFullscreen() != 
                                       (stage.isFullScreen() || stage.getWidth() == 1920);
            if (fullscreenChanged) {
                applyWindowSizeChanges();
            } else if (returnCallback != null) {
                returnCallback.run();
            } else {
                resetMainMenuLayoutStorage();
                new MainMenu(stage).show();
            }
        });
        AnchorPane.setLeftAnchor(tickButton, ScaleHelper.scaleX(365.0));
        AnchorPane.setTopAnchor(tickButton, ScaleHelper.scaleY(480.0));
        AnchorPane.setLeftAnchor(backButton, ScaleHelper.scaleX(25.0));
        AnchorPane.setTopAnchor(backButton, ScaleHelper.scaleY(25.0));
        root.getChildren().addAll(tickButton, backButton);
        stage.setScene(new Scene(layeredRoot, dimensions[0], dimensions[1]));
        if (Settings.getInstance().isFullscreen()) {
            applyFullscreenLayout();
        }
    }

    private void addSliderRow(GridPane grid, int row, String yellow, String red, Slider slider) {
        TextFlow label = new TextFlow(
            coloredText(yellow + " ", Color.YELLOW),
            coloredText(red + ":", Color.RED)
        );
        slider.setPrefWidth(ScaleHelper.scaleWidth(200));
        grid.add(label, 0, row);
        grid.add(slider, 1, row);
        GridPane.setValignment(label, VPos.CENTER);
        GridPane.setValignment(slider, VPos.CENTER);
    }

    private void addInputRow(GridPane grid, int row, String yellow, String red, Label lbl) {
        TextFlow label = new TextFlow(
            coloredText(yellow + " ", Color.YELLOW),
            coloredText(red + ":", Color.RED)
        );
        lbl.setMinWidth(ScaleHelper.scaleWidth(100));
        lbl.setAlignment(Pos.CENTER);
        lbl.setPadding(new Insets(ScaleHelper.scaleHeight(5)));
        double fontSize = customFont.getSize() * ScaleHelper.getUniformScale();
        Font scaledFont = Font.font(customFont.getFamily(), fontSize);
        lbl.setFont(scaledFont);
        GridPane.setMargin(lbl, new Insets(0, 0, 0, ScaleHelper.scaleWidth(45)));
        grid.add(label, 0, row);
        grid.add(lbl, 1, row);
        GridPane.setValignment(label, VPos.CENTER);
        GridPane.setValignment(lbl, VPos.CENTER);
    }
    
    private void addToggleRow(GridPane grid, int row, String yellow, String red, ToggleButton toggle) {
        TextFlow label = new TextFlow(
            coloredText(yellow + " ", Color.YELLOW),
            coloredText(red + ":", Color.RED)
        );
        GridPane.setMargin(toggle, new Insets(0, 0, 0, ScaleHelper.scaleWidth(45)));
        grid.add(label, 0, row);
        grid.add(toggle, 1, row);
        GridPane.setValignment(label, VPos.CENTER);
        GridPane.setValignment(toggle, VPos.CENTER);
    }

    private Slider createStyledSlider(double initialValue) {
        Slider slider = new Slider(0, 100, initialValue);
        slider.setPrefWidth(ScaleHelper.scaleWidth(200));
        slider.skinProperty().addListener((obs, oldSkin, newSkin) -> {
            if (newSkin != null) {
                slider.lookupAll(".track").forEach(track -> track.setStyle("-fx-background-color: brown;"));
                slider.lookupAll(".thumb").forEach(thumb -> thumb.setStyle("-fx-background-color: yellow;"));
            }
        });
        slider.setStyle("-fx-control-inner-background: brown; -fx-background-color: transparent;");
        return slider;
    }
    
    private ToggleButton createStyledToggleButton(boolean initialState) {
        ToggleButton toggle = new ToggleButton(initialState ? "Yes" : "No");
        toggle.setSelected(initialState);
        double toggleWidth = ScaleHelper.scaleWidth(100);
        double toggleHeight = ScaleHelper.scaleHeight(35);
        toggle.setPrefWidth(toggleWidth);
        toggle.setMinWidth(toggleWidth);
        toggle.setMaxWidth(toggleWidth);
        toggle.setPrefHeight(toggleHeight);
        toggle.setMinHeight(toggleHeight);
        toggle.setMaxHeight(toggleHeight);
        double scaledFontSize = (customFont.getSize() - 1) * ScaleHelper.getUniformScale();
        Font toggleFont = Font.font(customFont.getFamily(), scaledFontSize);
        toggle.setFont(toggleFont);
        String baseStyle = 
            "-fx-background-color: brown; " +
            "-fx-border-color: yellow; " +
            "-fx-border-width: 2; " +
            "-fx-text-fill: yellow; " +
            "-fx-font-family: '" + customFont.getFamily() + "'; " +
            "-fx-cursor: hand; " +
            "-fx-padding: 0 0 " + ScaleHelper.scaleHeight(4) + " 0; " +
            "-fx-alignment: CENTER;";
        toggle.setStyle(baseStyle);
        toggle.setOnMouseEntered(e -> toggle.setStyle(baseStyle));
        toggle.setOnMouseExited(e -> toggle.setStyle(baseStyle));
        toggle.setOnMousePressed(e -> toggle.setStyle(baseStyle));
        toggle.setOnMouseReleased(e -> toggle.setStyle(baseStyle));
        toggle.selectedProperty().addListener((obs, oldVal, newVal) -> {
            toggle.setText(newVal ? "Yes" : "No");
            toggle.setStyle(baseStyle);
        });
        return toggle;
    }

    private Text coloredText(String text, Color color) {
        Text t = new Text(text);
        t.setFill(color);
        double fontSize = customFont.getSize() * ScaleHelper.getUniformScale();
        Font scaledFont = Font.font(customFont.getFamily(), fontSize);
        t.setFont(scaledFont);
        t.setTextAlignment(javafx.scene.text.TextAlignment.LEFT);
        return t;
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

    private Label createStyledKeyLabel(String keyText) {
        Label label = new Label(keyText);
        label.setStyle("-fx-background-color: brown; -fx-border-color: yellow;");
        label.setPadding(new Insets(ScaleHelper.scaleHeight(5)));
        double fontSize = customFont.getSize() * ScaleHelper.getUniformScale();
        Font scaledFont = Font.font(customFont.getFamily(), fontSize);
        label.setFont(scaledFont);
        label.setMinWidth(ScaleHelper.scaleWidth(100));
        label.setAlignment(Pos.CENTER);
        label.setTextFill(Color.YELLOW);
        return label;
    }

    private void showKeyCaptureDialog(String action, Label target) {
        Stage d = new Stage();
        d.initStyle(StageStyle.UNDECORATED);
        String promptText = Settings.getInstance().isFullscreen() ? 
                            "press a key . . ." : 
                            "Press a key for " + action;
        Label prompt = new Label(promptText);
        prompt.setTextFill(Color.YELLOW);
        double fontSize = customFont.getSize() * ScaleHelper.getUniformScale();
        Font scaledFont = Font.font(customFont.getFamily(), fontSize);
        prompt.setFont(scaledFont);
        

        Label keyPressedLabel = new Label("Waiting for key press...");
        keyPressedLabel.setTextFill(Color.WHITE);
        keyPressedLabel.setFont(scaledFont);
        
        Button closeButton = new Button("Cancel");
        closeButton.setFont(scaledFont);
        closeButton.setStyle(
            "-fx-background-color: yellow;" +
            "-fx-text-fill: red;" +
            "-fx-font-size: " + ScaleHelper.scaleHeight(14) + "px;" +
            "-fx-font-weight: bold;"
        );
        closeButton.setOnAction(e -> d.close());
        closeButton.setDefaultButton(false);
        closeButton.setCancelButton(false);
        
        VBox b = new VBox(ScaleHelper.scaleHeight(15), prompt, keyPressedLabel, closeButton);
        b.setAlignment(Pos.CENTER);
        b.setPadding(new Insets(ScaleHelper.scaleHeight(20)));
        b.setStyle("-fx-background-color: brown; -fx-border-color: yellow; -fx-border-width: 3;");
        
        Scene s = new Scene(b, ScaleHelper.scaleWidth(300), ScaleHelper.scaleHeight(150));
        d.setScene(s);
        d.initOwner(stage);
        d.initModality(Modality.APPLICATION_MODAL);
        
        double dialogWidth = ScaleHelper.scaleWidth(300);
        double dialogHeight = ScaleHelper.scaleHeight(150);
        d.setX(stage.getX() + (stage.getWidth() - dialogWidth) / 2);
        d.setY(stage.getY() + (stage.getHeight() - dialogHeight) / 2);
        

        Platform.runLater(() -> {
            closeButton.setDefaultButton(false);
            closeButton.setCancelButton(false);
        });
        
        d.show();
        
        final boolean[] keyProcessed = {false};
        
        s.setOnKeyPressed(evt -> {
            if (keyProcessed[0]) return;
            
            KeyCode code = evt.getCode();
            String displayName = "";
            

            switch (code) {
                case ENTER:
                    displayName = "Enter";
                    break;
                case SPACE:
                    displayName = "Space";
                    break;
                case ESCAPE:
                    d.close();
                    return;
                case UP:
                    displayName = "Up";
                    break;
                case DOWN:
                    displayName = "Down";
                    break;
                case LEFT:
                    displayName = "Left";
                    break;
                case RIGHT:
                    displayName = "Right";
                    break;
                case TAB:
                    displayName = "Tab";
                    break;
                case SHIFT:
                    displayName = "Shift";
                    break;
                case CONTROL:
                    displayName = "Ctrl";
                    break;
                case ALT:
                    displayName = "Alt";
                    break;
                default:

                    if (code != KeyCode.UNDEFINED) {
                        displayName = code.getName();
                    }
                    break;
            }
            

            keyPressedLabel.setText("Key pressed: " + displayName);
            

            if (!displayName.isEmpty()) {

                

                target.setText(displayName);
                

                new Thread(() -> {
                    try {
                        Thread.sleep(300);
                        Platform.runLater(() -> {
                            keyProcessed[0] = true;
                            d.close();
                        });
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }).start();
            }
        });
    }

    private void showPopup(String messageText) {
        Stage popup = new Stage();
        popup.initOwner(stage);
        popup.initModality(Modality.APPLICATION_MODAL);
        popup.initStyle(StageStyle.UNDECORATED);
        VBox content = new VBox(ScaleHelper.scaleHeight(20));
        content.setAlignment(Pos.CENTER);
        content.setPadding(new Insets(ScaleHelper.scaleHeight(20)));
        content.setStyle("-fx-background-color: brown; -fx-border-color: yellow; -fx-border-width: 3;");
        Label message = new Label(messageText);
        message.setTextFill(Color.YELLOW);
        double fontSize = customFont.getSize() * ScaleHelper.getUniformScale();
        Font scaledFont = Font.font(customFont.getFamily(), fontSize);
        message.setFont(scaledFont);
        Button okButton = new Button("OK");
        okButton.setFont(scaledFont);
        okButton.setStyle(
            "-fx-background-color: yellow;" +
            "-fx-text-fill: red;" +
            "-fx-font-size: " + ScaleHelper.scaleHeight(16) + "px;" +
            "-fx-font-weight: bold;"
        );
        okButton.setOnAction(ev -> popup.close());
        content.getChildren().addAll(message, okButton);
        Scene scene = new Scene(content);
        popup.setScene(scene);
        popup.setOnShown(e -> {
            popup.setX(stage.getX() + (stage.getWidth() - popup.getWidth()) / 2);
            popup.setY(stage.getY() + (stage.getHeight() - popup.getHeight()) / 2);
        });
        popup.showAndWait();
    }

    private void applyFullscreenLayout() {
        Pane layeredRoot = (Pane) stage.getScene().getRoot();
        AnchorPane contentRoot = null;
        Label titleLabel = null;
        GridPane controls = null;
        Button tickButton = null;
        Button backButton = null;
        for (javafx.scene.Node node : layeredRoot.getChildren()) {
            if (node instanceof AnchorPane) {
                contentRoot = (AnchorPane) node;
                break;
            }
        }
        if (contentRoot == null) return;
        for (javafx.scene.Node node : contentRoot.getChildren()) {
            if (node instanceof Label) {
                titleLabel = (Label) node;
            } else if (node instanceof GridPane) {
                controls = (GridPane) node;
            } else if (node instanceof Button) {
                ImageView graphic = (ImageView) ((Button) node).getGraphic();
                if (graphic != null && graphic.getImage() != null) {
                    String url = graphic.getImage().getUrl();
                    if (url != null) {
                        if (url.contains("tick")) {
                            tickButton = (Button) node;
                        } else if (url.contains("back")) {
                            backButton = (Button) node;
                        }
                    }
                }
            }
        }
        double screenWidth = 1920;
        double leftOffset = 250;
        if (titleLabel != null) {
            titleLabel.setLayoutX((screenWidth / 2) - leftOffset);
            titleLabel.setLayoutY(150);
            titleLabel.setFont(Font.font(customFont.getFamily(), 45));
            javafx.scene.effect.DropShadow dropShadow = new javafx.scene.effect.DropShadow();
            dropShadow.setColor(Color.BROWN);
            dropShadow.setRadius(5.0);
            dropShadow.setOffsetX(3.0);
            dropShadow.setOffsetY(3.0);
            titleLabel.setEffect(dropShadow);
        }
        if (controls != null) {
            AnchorPane.setLeftAnchor(controls, (screenWidth / 2 - 300) - leftOffset);
            AnchorPane.setTopAnchor(controls, 180.0);
            controls.setVgap(30);
            controls.setHgap(40);
            for (javafx.scene.Node node : controls.getChildren()) {
                if (node instanceof Slider) {
                    ((Slider) node).setPrefWidth(300);
                }
            }
        }
        if (tickButton != null) {
            AnchorPane.setRightAnchor(tickButton, null);
            AnchorPane.setBottomAnchor(tickButton, 150.0);
            AnchorPane.setLeftAnchor(tickButton, (screenWidth / 2 + 185) - leftOffset);
            AnchorPane.setTopAnchor(tickButton, null);
            ImageView tickGraphic = (ImageView) tickButton.getGraphic();
            if (tickGraphic != null) {
                tickGraphic.setFitWidth(100);
                tickGraphic.setPreserveRatio(true);
            }
        }
        if (backButton != null) {
            AnchorPane.setLeftAnchor(backButton, null);
            AnchorPane.setRightAnchor(backButton, 1600.0);
            AnchorPane.setTopAnchor(backButton, 140.0);
            ImageView backGraphic = (ImageView) backButton.getGraphic();
            if (backGraphic != null) {
                backGraphic.setFitWidth(75);
                backGraphic.setPreserveRatio(true);
            }
        }
    }
    
    private void applyWindowSizeChanges() {
        Settings settings = Settings.getInstance();
        int[] dimensions = settings.getDimensions();
        ScaleHelper.updateDimensions(dimensions[0], dimensions[1]);
        double centerX = stage.getX() + stage.getWidth() / 2;
        double centerY = stage.getY() + stage.getHeight() / 2;
        stage.setWidth(dimensions[0]);
        stage.setHeight(dimensions[1]);
        stage.setX(centerX - dimensions[0] / 2);
        stage.setY(centerY - dimensions[1] / 2);
        ensureWindowVisible();
        if (settings.isFullscreen()) {
            stage.setFullScreenExitHint("");
            stage.setFullScreenExitKeyCombination(KeyCombination.NO_MATCH);
            stage.setWidth(1920);
            stage.setHeight(1080);
            resetMainMenuLayoutStorage();
            stage.setFullScreen(true);
        } else if (stage.isFullScreen()) {
            stage.setFullScreen(false);
            resetMainMenuLayoutStorage();
        }
        Platform.runLater(() -> {
            OptionsMenu newMenu = new OptionsMenu(stage, returnCallback, fromPauseMenu);
            newMenu.show();
            if (settings.isFullscreen()) {
                newMenu.applyFullscreenLayout();
            }
            newMenu.showPopup("Settings applied!");
        });
        return;
    }
    
    private void ensureWindowVisible() {
        javafx.geometry.Rectangle2D screenBounds = javafx.stage.Screen.getPrimary().getVisualBounds();
        if (stage.getX() < screenBounds.getMinX()) {
            stage.setX(screenBounds.getMinX());
        }
        if (stage.getY() < screenBounds.getMinY()) {
            stage.setY(screenBounds.getMinY());
        }
        if (stage.getX() + stage.getWidth() > screenBounds.getMaxX()) {
            stage.setX(screenBounds.getMaxX() - stage.getWidth());
        }
        if (stage.getY() + stage.getHeight() > screenBounds.getMaxY()) {
            stage.setY(screenBounds.getMaxY() - stage.getHeight());
        }
        if (stage.getWidth() > screenBounds.getWidth() || 
            stage.getHeight() > screenBounds.getHeight()) {
            stage.centerOnScreen();
        }
    }
    
    private void resetMainMenuLayoutStorage() {
        try {
            java.lang.reflect.Field layoutStoredField = MainMenu.class.getDeclaredField("layoutStored");
            layoutStoredField.setAccessible(true);
            layoutStoredField.set(null, false);
            java.lang.reflect.Field comingFromFullscreenChangeField = 
                MainMenu.class.getDeclaredField("comingFromFullscreenChange");
            comingFromFullscreenChangeField.setAccessible(true);
            comingFromFullscreenChangeField.set(null, true);
            java.lang.reflect.Field[] fields = MainMenu.class.getDeclaredFields();
            for (java.lang.reflect.Field field : fields) {
                if (field.getName().startsWith("stored") && field.getType() == double.class) {
                    field.setAccessible(true);
                    field.setDouble(null, -1);
                }
            }
        } catch (Exception e) {
        }
    }
    
    private Image loadImage(String path) {
        try {
            return new Image(getClass().getResource(path).toExternalForm());
        } catch (Exception e) {
            return null;
        }
    }
}
