package flappybird;
import javafx.animation.FadeTransition;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCombination;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import javafx.util.Duration;
public class BirdSelector {
    private final Stage stage;
    private final Runnable startGameCallback;
    private MainMenu parentMenu;
    public BirdSelector(Stage stage, Runnable startGameCallback) {
        this.stage = stage;
        this.startGameCallback = startGameCallback;
    }
    public BirdSelector(Stage stage, Runnable startGameCallback, MainMenu parentMenu) {
        this.stage = stage;
        this.startGameCallback = startGameCallback;
        this.parentMenu = parentMenu;
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
        Image selectorTextImage = new Image(getClass().getResource("/resources/bird_selector_text.png").toExternalForm());
        ImageView selectorTextView = new ImageView(selectorTextImage);
        double scaledWidth = ScaleHelper.scaleWidth(400);
        selectorTextView.setFitWidth(scaledWidth);
        selectorTextView.setPreserveRatio(true);
        double centerX = ScaleHelper.scaleX(400);
        selectorTextView.setLayoutX(0);
        selectorTextView.setLayoutY(0);
        selectorTextView.setLayoutX(centerX - selectorTextView.getBoundsInLocal().getWidth()/2);
        selectorTextView.setLayoutY(ScaleHelper.scaleY(50));
        DropShadow dropShadow = new DropShadow();
        dropShadow.setOffsetX(ScaleHelper.scaleWidth(3));
        dropShadow.setOffsetY(ScaleHelper.scaleHeight(3));
        selectorTextView.setEffect(dropShadow);
        root.getChildren().add(selectorTextView);
        Button bird1Button = createBirdButton(1);
        Button bird2Button = createBirdButton(2);
        Button bird3Button = createBirdButton(3);
        double screenCenterX = ScaleHelper.scaleX(400);
        double buttonWidth = ScaleHelper.scaleWidth(150);
        double buttonSpacing = ScaleHelper.scaleWidth(60);
        double button2X = screenCenterX - buttonWidth/2;
        double button1X = button2X - buttonWidth - buttonSpacing;
        double button3X = button2X + buttonWidth + buttonSpacing;
        double buttonY = ScaleHelper.scaleY(230);
        bird1Button.setLayoutX(button1X);
        bird1Button.setLayoutY(buttonY);
        bird2Button.setLayoutX(button2X);
        bird2Button.setLayoutY(buttonY);
        bird3Button.setLayoutX(button3X);
        bird3Button.setLayoutY(buttonY);
        root.getChildren().addAll(bird1Button, bird2Button, bird3Button);
        addBackButton(root);
        Scene scene = new Scene(root, dimensions[0], dimensions[1]);
        FadeTransition fadeIn = new FadeTransition(Duration.millis(800), root);
        fadeIn.setFromValue(0);
        fadeIn.setToValue(1);
        fadeIn.play();
        stage.setScene(scene);
        applyFullscreen();
    }
    private Button createBirdButton(int birdNumber) {
        Image buttonImage = new Image(getClass().getResource("/resources/bird_select_" + birdNumber + ".png").toExternalForm());
        ImageView buttonView = new ImageView(buttonImage);
        buttonView.setFitWidth(ScaleHelper.scaleWidth(150));
        buttonView.setPreserveRatio(true);
        Button button = new Button();
        button.setGraphic(buttonView);
        styleButton(button);
        button.setOnAction(e -> {
            Settings.getInstance().setSelectedBird(birdNumber);
            Settings.getInstance().saveSettings();
            new PlayerNameScreen(stage, startGameCallback).show();
        });
        return button;
    }
    private void addBackButton(Pane root) {
        Image backImage = new Image(getClass().getResource("/resources/mainmenu/back.png").toExternalForm());
        ImageView backView = new ImageView(backImage);
        backView.setFitWidth(ScaleHelper.scaleWidth(50));
        backView.setPreserveRatio(true);
        Button backButton = new Button();
        backButton.setGraphic(backView);
        styleButton(backButton);
        backButton.setLayoutX(ScaleHelper.scaleX(20));
        backButton.setLayoutY(ScaleHelper.scaleY(20));
        backButton.setOnAction(e -> {
            if (parentMenu != null) {
                parentMenu.show();
            } else {
                new MainMenu(stage).show();
            }
        });
        root.getChildren().add(backButton);
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
        ImageView selectorTextView = null;
        Button bird1Button = null;
        Button bird2Button = null;
        Button bird3Button = null;
        Button backButton = null;
        for (javafx.scene.Node node : root.getChildren()) {
            if (node instanceof ImageView && !(node.getParent() instanceof Button)) {
                if (node != root.getChildren().get(0)) {
                    selectorTextView = (ImageView) node;
                }
            } else if (node instanceof Button) {
                Button button = (Button) node;
                if (button.getLayoutX() < 100) {
                    backButton = button;
                } else {
                    if (bird1Button == null) {
                        bird1Button = button;
                    } else if (bird2Button == null) {
                        bird2Button = button;
                    } else {
                        bird3Button = button;
                    }
                }
            }
        }
        if (selectorTextView != null) {
            selectorTextView.setFitWidth(500);
            double centerX = 1920 / 2 - 180;
            selectorTextView.setLayoutX(centerX - selectorTextView.getBoundsInLocal().getWidth()/2);
            selectorTextView.setLayoutY(50);
            DropShadow dropShadow = new DropShadow();
            dropShadow.setOffsetX(5);
            dropShadow.setOffsetY(5);
            dropShadow.setRadius(10);
            selectorTextView.setEffect(dropShadow);
        }
        if (bird1Button != null && bird2Button != null && bird3Button != null) {
            double screenCenterX = 1920 / 2 - 250;
            double buttonWidth = 180;
            double buttonSpacing = 175;
            double button2X = screenCenterX - buttonWidth/4 +10;
            double button1X = button2X - buttonWidth - buttonSpacing;
            double button3X = button2X + buttonWidth + buttonSpacing;
            double buttonY = 325;
            bird1Button.setLayoutX(button1X);
            bird1Button.setLayoutY(buttonY);
            bird2Button.setLayoutX(button2X);
            bird2Button.setLayoutY(buttonY);
            bird3Button.setLayoutX(button3X);
            bird3Button.setLayoutY(buttonY);
            for (Button button : new Button[]{bird1Button, bird2Button, bird3Button}) {
                ImageView buttonView = (ImageView) button.getGraphic();
                if (buttonView != null) {
                    buttonView.setFitWidth(180);
                    buttonView.setPreserveRatio(true);
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

