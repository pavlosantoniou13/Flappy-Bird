package entities;
import java.awt.*;

public class Bird extends GameObject {
    public double rotation = 0;
    public int velocityY = 0;

    public Bird(int x, int y, int width, int height, Image img) {
        super(x, y, width, height, img);
    }

    @Override
    public void draw(Graphics g) {
        Graphics2D g2d = (Graphics2D) g;
        int centerX = x + width / 2;
        int centerY = y + height / 2;

        g2d.rotate(rotation, centerX, centerY);
        super.draw(g); // Draws the image
        g2d.rotate(-rotation, centerX, centerY);
    }
}