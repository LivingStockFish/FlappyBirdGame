package flappybird;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public class Bird {
    private double y;
    private double x;
    private double velocity = 0;
    private ImageView view;
    
    private double gravity;
    private double jumpStrength;
    
    private static final double DEFAULT_GRAVITY = 0.45;
    private static final double DEFAULT_JUMP_STRENGTH = -7.0;
    
    private static final double FULLSCREEN_GRAVITY = 0.45;
    private static final double FULLSCREEN_JUMP_STRENGTH = -7.2;
    
    private static final double REF_X = 100;
    private static final double REF_Y = 300;

    public Bird() {
        int selectedBird = Settings.getInstance().getSelectedBird();
        
        String birdImagePath = "/resources/bird" + selectedBird + ".png";
        this.view = new ImageView(new Image(getClass().getResource(birdImagePath).toExternalForm()));
        
        if (Settings.getInstance().isFullscreen()) {
            this.view.setFitHeight(90);
            this.view.setFitWidth(90);
            
            this.x = 250;
            this.y = 300;
            
            this.gravity = FULLSCREEN_GRAVITY;
            this.jumpStrength = FULLSCREEN_JUMP_STRENGTH;
        } else {
            this.view.setFitHeight(ScaleHelper.scaleHeight(50));
            this.view.setFitWidth(ScaleHelper.scaleWidth(50));
            
            this.x = ScaleHelper.scaleX(REF_X);
            this.y = ScaleHelper.scaleY(REF_Y);
            
            this.gravity = DEFAULT_GRAVITY;
            this.jumpStrength = DEFAULT_JUMP_STRENGTH;
        }
        
        view.setLayoutX(x);
        view.setLayoutY(y);
    }

    private double lastRenderedY = 0;
    private static final double RENDER_THRESHOLD = 0.5;
    
    public void update() {
        if (Settings.getInstance().isFullscreen()) {
            velocity += gravity * 1.15;
        } else {
            velocity += gravity * 1.1;
        }
        
        y += velocity;

        if (Math.abs(y - lastRenderedY) > RENDER_THRESHOLD) {
            view.setLayoutY(y);
            lastRenderedY = y;
        }
    }

    public void jump() {
        velocity = jumpStrength;
    }

    public ImageView getView() {
        return view;
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }
    
    public void reset() {
        velocity = -2.0;
        
        if (Settings.getInstance().isFullscreen()) {
            this.y = 300;
            
            this.gravity = FULLSCREEN_GRAVITY;
            this.jumpStrength = FULLSCREEN_JUMP_STRENGTH;
        } else {
            this.y = ScaleHelper.scaleY(REF_Y);
            
            this.gravity = DEFAULT_GRAVITY;
            this.jumpStrength = DEFAULT_JUMP_STRENGTH;
        }
        
        view.setLayoutY(y);
        lastRenderedY = y;
    }
}
