package flappybird;

public class ScaleHelper {
    private static final int REF_WIDTH = 800;
    private static final int REF_HEIGHT = 600;
    
    private static int currentWidth = REF_WIDTH;
    private static int currentHeight = REF_HEIGHT;
    
    private static double scaleX = 1.0;
    private static double scaleY = 1.0;
    
    private static double uniformScale = 1.0;
    
    private static double offsetX = 0.0;
    private static double offsetY = 0.0;
    
    public static void updateDimensions(int width, int height) {
        currentWidth = width;
        currentHeight = height;
        
        if (width >= 1920 || height >= 1080 || Settings.getInstance().isFullscreen()) {
            scaleX = 2.4;
            scaleY = 1.8;
            currentWidth = 1920;
            currentHeight = 1080;
        } else {
            scaleX = (double) width / REF_WIDTH;
            scaleY = (double) height / REF_HEIGHT;
        }
        
        offsetX = 0;
        offsetY = 0;
        
        uniformScale = Math.min(scaleX, scaleY);
        
    }
    
    public static double scaleX(double x) {
        return x * scaleX;
    }
    
    public static double scaleY(double y) {
        return y * scaleY;
    }
    
    public static double scaleWidth(double width) {
        return width * scaleX;
    }
    
    public static double scaleHeight(double height) {
        return height * scaleY;
    }
    
    public static int getCurrentWidth() {
        return currentWidth;
    }
    
    public static int getCurrentHeight() {
        return currentHeight;
    }
    
    public static double getScaleX() {
        return scaleX;
    }
    
    public static double getScaleY() {
        return scaleY;
    }
    
    public static double getUniformScale() {
        return uniformScale;
    }
    
    public static double getOffsetX() {
        return offsetX;
    }
    
    public static double getOffsetY() {
        return offsetY;
    }
    
    public static int getRefWidth() {
        return REF_WIDTH;
    }
    
    public static int getRefHeight() {
        return REF_HEIGHT;
    }
}
