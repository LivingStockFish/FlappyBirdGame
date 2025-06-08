package flappybird;
import javafx.scene.Group;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import java.util.Random;
import javafx.geometry.Bounds;
public class Pipe {
    private Group topPipe;
    private Group bottomPipe;
    private static final int REF_WIDTH = 80;
    private static final int REF_GAP = 200;
    private int width;
    private int gap;
    private int screenHeight;
    private static final Random random = new Random();
    private static final Image[][] PIPE_IMAGES = new Image[10][2];
    private Image pipeBodyImage;
    private Image pipeCapImage;
    private void loadCachedImages(int themeIndex) {
        if (PIPE_IMAGES[themeIndex][0] == null) {
            PIPE_IMAGES[themeIndex][0] = new Image(getClass().getResource("/resources/pipe_body_" + (themeIndex + 1) + ".png").toExternalForm());
            PIPE_IMAGES[themeIndex][1] = new Image(getClass().getResource("/resources/pipe_cap_" + (themeIndex + 1) + ".png").toExternalForm());
        }
        pipeBodyImage = PIPE_IMAGES[themeIndex][0];
        pipeCapImage = PIPE_IMAGES[themeIndex][1];
    }
    public Pipe(double startX, int themeIndex) {
        screenHeight = ScaleHelper.getCurrentHeight();
        if (Settings.getInstance().isFullscreen()) {
            width = 100;
            gap = 300;
            loadCachedImages(themeIndex);
            int fixedScreenHeight = 864;
            int topMargin = 100;
            int minGapStart = topMargin;
            int maxGapRange = fixedScreenHeight - gap - 200;
            int gapStart = minGapStart + random.nextInt(maxGapRange > 0 ? maxGapRange : 100);
            bottomPipe = createPipe(startX, gapStart + gap, fixedScreenHeight - (gapStart + gap), false);
            topPipe = createPipe(startX, 0, gapStart, true);
        } else {
            width = (int) ScaleHelper.scaleWidth(REF_WIDTH);
            gap = (int) ScaleHelper.scaleHeight(REF_GAP);
            loadCachedImages(themeIndex);
            int topMargin = (int) ScaleHelper.scaleHeight(60);
            int minGapStart = topMargin;
            int maxGapRange = screenHeight - gap - (int) ScaleHelper.scaleHeight(200);
            int gapStart = minGapStart + random.nextInt(maxGapRange > 0 ? maxGapRange : 100);
            bottomPipe = createPipe(startX, gapStart + gap, screenHeight - (gapStart + gap), false);
            topPipe = createPipe(startX, 0, gapStart, true);
        }
    }
    public Pipe(double startX, int themeIndex, Pipe templatePipe) {
        this.width = templatePipe.width;
        this.gap = templatePipe.gap;
        this.screenHeight = templatePipe.screenHeight;
        loadCachedImages(themeIndex);
        double topPipeY = templatePipe.topPipe.getLayoutY();
        double bottomPipeY = templatePipe.bottomPipe.getLayoutY();
        int topPipeHeight = (int) (templatePipe.topPipe.getBoundsInLocal().getHeight());
        int bottomPipeHeight = (int) (templatePipe.bottomPipe.getBoundsInLocal().getHeight());
        topPipe = createPipe(startX, topPipeY, topPipeHeight, true);
        bottomPipe = createPipe(startX, bottomPipeY, bottomPipeHeight, false);
    }
    private Group createPipe(double x, double y, int height, boolean isTopPipe) {
        Group pipeGroup = new Group();
        int bodyHeight;
        if (Settings.getInstance().isFullscreen()) {
            bodyHeight = 30;
        } else {
            bodyHeight = (int) ScaleHelper.scaleHeight(30);
        }
        int bodyCount = (int) Math.ceil((double)(height) / bodyHeight);
        for (int i = 0; i < bodyCount; i++) {
            ImageView body = new ImageView(pipeBodyImage);
            body.setFitWidth(width);
            body.setFitHeight(bodyHeight);
            if (isTopPipe) {
                body.setScaleY(-1);
                body.setLayoutY(i * bodyHeight);
                if (i == bodyCount - 1) break;
            } else {
                if (Settings.getInstance().isFullscreen()) {
                    body.setLayoutY(i * bodyHeight + 30);
                } else {
                    body.setLayoutY(i * bodyHeight + ScaleHelper.scaleHeight(30));
                }
            }
            pipeGroup.getChildren().add(body);
        }
        ImageView cap = new ImageView(pipeCapImage);
        cap.setFitWidth(width);
        if (Settings.getInstance().isFullscreen()) {
            cap.setFitHeight(30);
        } else {
            cap.setFitHeight(ScaleHelper.scaleHeight(30));
        }
        if (!isTopPipe) {
            cap.setLayoutY(0);
        } else {
            if (Settings.getInstance().isFullscreen()) {
                cap.setLayoutY(height - 30);
            } else {
                cap.setLayoutY(height - ScaleHelper.scaleHeight(30));
            }
        }
        pipeGroup.getChildren().add(cap);
        pipeGroup.setLayoutX(x);
        pipeGroup.setLayoutY(y);
        return pipeGroup;
    }
    public void update(double speed) {
        topPipe.setLayoutX(topPipe.getLayoutX() - speed);
        bottomPipe.setLayoutX(bottomPipe.getLayoutX() - speed);
    }
    public boolean isOffScreen() {
        return bottomPipe.getLayoutX() + width < -100;
    }
    public double getX() {
        return topPipe.getLayoutX();
    }
    private static double fullscreenShrinkMargin = 25;
    private static double windowedShrinkMargin = 19;
    public boolean collidesWith(Bird bird) {
        double pipeX = topPipe.getLayoutX();
        double birdX = bird.getX();
        if (pipeX > birdX + 100 || pipeX + width < birdX - 50) {
            return false;
        }
        Bounds birdBounds = bird.getView().getBoundsInParent();
        Bounds topPipeBounds = topPipe.getBoundsInParent();
        Bounds bottomPipeBounds = bottomPipe.getBoundsInParent();
        double shrinkMargin;
        if (Settings.getInstance().isFullscreen()) {
            shrinkMargin = fullscreenShrinkMargin;
        } else {
            shrinkMargin = ScaleHelper.scaleWidth(windowedShrinkMargin);
        }
        Bounds shrunkTopPipeBounds = new javafx.geometry.BoundingBox(
            topPipeBounds.getMinX() + shrinkMargin,
            topPipeBounds.getMinY() + shrinkMargin,
            topPipeBounds.getWidth() - 2 * shrinkMargin,
            topPipeBounds.getHeight() - 2 * shrinkMargin
        );
        Bounds shrunkBottomPipeBounds = new javafx.geometry.BoundingBox(
            bottomPipeBounds.getMinX() + shrinkMargin,
            bottomPipeBounds.getMinY() + shrinkMargin,
            bottomPipeBounds.getWidth() - 2 * shrinkMargin,
            bottomPipeBounds.getHeight() - 2 * shrinkMargin
        );
        return birdBounds.intersects(shrunkTopPipeBounds) || birdBounds.intersects(shrunkBottomPipeBounds);
    }
    public Group getTopPipe() {
        return topPipe;
    }
    public Group getBottomPipe() {
        return bottomPipe;
    }
    public void updateColor(int newThemeIndex) {
        loadCachedImages(newThemeIndex);
        for (javafx.scene.Node node : topPipe.getChildren()) {
            if (node instanceof ImageView) {
                ImageView imageView = (ImageView) node;
                if (imageView.getLayoutY() == 0 || 
                    (Settings.getInstance().isFullscreen() && imageView.getLayoutY() == topPipe.getBoundsInLocal().getHeight() - 30) ||
                    (!Settings.getInstance().isFullscreen() && imageView.getLayoutY() == topPipe.getBoundsInLocal().getHeight() - ScaleHelper.scaleHeight(30))) {
                    imageView.setImage(pipeCapImage);
                } else {
                    imageView.setImage(pipeBodyImage);
                }
            }
        }
        for (javafx.scene.Node node : bottomPipe.getChildren()) {
            if (node instanceof ImageView) {
                ImageView imageView = (ImageView) node;
                if (imageView.getLayoutY() == 0) {
                    imageView.setImage(pipeCapImage);
                } else {
                    imageView.setImage(pipeBodyImage);
                }
            }
        }
    }
}

