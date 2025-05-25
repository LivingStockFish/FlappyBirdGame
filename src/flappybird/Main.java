package flappybird;

import javafx.application.Application;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCombination;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) {
        Settings settings = Settings.getInstance();
        primaryStage.initStyle(StageStyle.UNDECORATED);
        if (settings.isFullscreen()) {
            primaryStage.setFullScreenExitHint("");
            primaryStage.setFullScreenExitKeyCombination(KeyCombination.NO_MATCH);
            primaryStage.setWidth(1920);
            primaryStage.setHeight(1080);
            primaryStage.setFullScreen(true);
        }
        Image icon = new Image(getClass().getResource("/resources/mainmenu/logo.png").toExternalForm());
        primaryStage.getIcons().add(icon);
        int[] dimensions = settings.getDimensions();
        ScaleHelper.updateDimensions(dimensions[0], dimensions[1]);
        javafx.geometry.Rectangle2D screenBounds = javafx.stage.Screen.getPrimary().getVisualBounds();
        double centerX = screenBounds.getMinX() + screenBounds.getWidth() / 2;
        double centerY = screenBounds.getMinY() + screenBounds.getHeight() / 2;
        primaryStage.setWidth(dimensions[0]);
        primaryStage.setHeight(dimensions[1]);
        primaryStage.setX(centerX - dimensions[0] / 2);
        primaryStage.setY(centerY - dimensions[1] / 2);
        MainMenu menu = new MainMenu(primaryStage);
        menu.show();
        primaryStage.show();
        primaryStage.sizeToScene();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
