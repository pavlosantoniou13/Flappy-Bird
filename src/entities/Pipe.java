package entities;
import java.awt.*;

public class Pipe extends GameObject {
    public boolean passed = false;

    // This is the constructor that was missing!
    public Pipe(int x, int y, int width, int height, Image img) {
        super(x, y, width, height, img);
    }
}