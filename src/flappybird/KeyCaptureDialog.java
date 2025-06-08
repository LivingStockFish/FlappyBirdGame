package flappybird;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
public class KeyCaptureDialog {
    public static void show(Stage stage, String action, Label target, Font customFont) {
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
        Label keyPressedLabel = new Label("Current: " + target.getText());
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
            s.getRoot().requestFocus();
        });
        d.show();
        final boolean[] keyProcessed = {false};
        s.addEventFilter(javafx.scene.input.KeyEvent.KEY_PRESSED, evt -> {
            if (keyProcessed[0]) return;
            evt.consume();
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
            if (!displayName.isEmpty()) {
                keyPressedLabel.setText("Selected: " + displayName);
                target.setText(displayName);
                new Thread(() -> {
                    try {
                        Thread.sleep(300);
                        Platform.runLater(() -> {
                            keyProcessed[0] = true;
                            d.close();
                        });
                    } catch (InterruptedException e) {
                    }
                }).start();
            }
        });
    }
}

