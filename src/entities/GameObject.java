package entities;
import java.awt.*;

public abstract class GameObject {
    public int x, y, width, height;
    public Image img;

    public GameObject(int x, int y, int width, int height, Image img) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.img = img;
    }

    public void draw(Graphics g) {
        g.drawImage(img, x, y, width, height, null);
    }
}