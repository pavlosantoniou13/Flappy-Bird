package core;

import entities.Bird;
import entities.Pipe;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.Random;

public class GamePanel extends JPanel implements ActionListener, KeyListener {
    // Board dimensions from your original App.java
    int boardWidth = 360;
    int boardHeight = 640;

    // Images from your original FlappyBird.java
    Image backgroundImage;
    Image birdImage;
    Image topPipeImage;
    Image bottomPipeImage;

    // Bird and Pipe constants from your original FlappyBird.java
    int birdX = boardWidth/8;
    int birdY = boardHeight/2;
    int birdWidth = 34;
    int birdHeight = 24;

    int pipeX = boardWidth;
    int pipeY = 0;
    int pipeWidth = 64;
    int pipeHeight = 512;

    // Game logic variables
    Bird bird;
    int velocityX = -4; 
    int velocityY = 0;
    int gravity = 1;

    ArrayList<Pipe> pipes;
    Random random = new Random();

    Timer gameLoop;
    Timer placePipesTimer;

    boolean gameOver = false;
    double score = 0;

    public GamePanel() {
        setPreferredSize(new Dimension(boardWidth, boardHeight));
        setFocusable(true);
        addKeyListener(this);

        // Load images exactly as before
        backgroundImage = new ImageIcon(getClass().getResource("/flappybirdbg.png")).getImage();
        birdImage = new ImageIcon(getClass().getResource("/flappybird.png")).getImage();
        topPipeImage = new ImageIcon(getClass().getResource("/toppipe.png")).getImage();
        bottomPipeImage = new ImageIcon(getClass().getResource("/bottompipe.png")).getImage();

        // Initialize bird and pipes
        bird = new Bird(birdX, birdY, birdWidth, birdHeight, birdImage);
        pipes = new ArrayList<Pipe>();

        // Place pipes timer
        placePipesTimer = new Timer(1500, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                placePipes();
            }
        });
        placePipesTimer.start();

        // Game loop timer
        gameLoop = new Timer(1000/60, this);
        gameLoop.start();
    }

    public void placePipes() {
        // Your original random pipe logic
        int randomPipeY = (int) (pipeY - pipeHeight/4 - Math.random() * (pipeHeight/2));
        int openingSpace = boardHeight/4;

        Pipe topPipe = new Pipe(pipeX, randomPipeY, pipeWidth, pipeHeight, topPipeImage);
        pipes.add(topPipe);

        Pipe bottomPipe = new Pipe(pipeX, topPipe.y + pipeHeight + openingSpace, pipeWidth, pipeHeight, bottomPipeImage);
        pipes.add(bottomPipe);
    }

    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        draw(g); 
    }

    public void draw(Graphics g) {
        // Background
        g.drawImage(backgroundImage, 0, 0, boardWidth, boardHeight, null);

        // Bird drawing with rotation
        bird.draw(g); 

        // Pipes drawing
        for(int i = 0; i < pipes.size(); i++) {
            Pipe pipe = pipes.get(i);
            pipe.draw(g);
        }

        // Score display
        g.setColor(Color.black);
        g.setFont(new Font("Arial", Font.PLAIN, 32));
        if (gameOver) {
            g.drawString("Game Over: " + String.valueOf((int) score), 10, 35);
        } else {
            g.drawString(String.valueOf((int) score), 10, 35);
        }
    }

    public void move() {
        // Apply your original physics to the bird object
        bird.velocityY += gravity;
        bird.y += bird.velocityY;
        bird.y = Math.max(bird.y, 0);
        bird.rotation = Math.toRadians(Math.min(45, Math.max(-25, bird.velocityY * 4)));

        // Pipe movement and collision
        for(int i = 0; i < pipes.size(); i++) {
            Pipe pipe = pipes.get(i);
            pipe.x += velocityX;

            if(!pipe.passed && bird.x > pipe.x + pipe.width) {
                pipe.passed = true;
                score += 0.5;
            }

            if(collision(bird, pipe)) {
                gameOver = true;
            }
        }

        if(bird.y > boardHeight) {
            gameOver = true;
        }
    }

    // Your original collision math
    public boolean collision(Bird a, Pipe b) {
        return  a.x < b.x + b.width &&
                a.x + a.width > b.x &&
                a.y < b.y + b.height &&
                a.y + a.height > b.y;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        move();
        repaint();
        if (gameOver) {
            placePipesTimer.stop();
            gameLoop.stop();
        }
    }

    @Override public void keyTyped(KeyEvent e) {}

    @Override
    public void keyPressed(KeyEvent e) {
        if(e.getKeyCode() == KeyEvent.VK_SPACE) {
            bird.velocityY = -9; // Original flap strength
            if (gameOver) {
                // Restart logic
                bird.y = birdY;
                bird.velocityY = 0;
                pipes.clear();
                score = 0;
                gameOver = false;
                gameLoop.start();
                placePipesTimer.start();
                bird.rotation = 0;
            }
        }
    }

    @Override public void keyReleased(KeyEvent e) {}
}