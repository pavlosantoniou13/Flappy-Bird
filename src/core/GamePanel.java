package core;

import entities.Bird;
import entities.Pipe;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.Random;

public class GamePanel extends JPanel implements ActionListener, KeyListener {

    // New state constants
    private static final int MENU = 0;
    private static final int PLAYING = 1;
    private int gameState = MENU; // Start at the menu

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

        if (gameState == MENU) {
            g.setColor(Color.white);
            g.setFont(new Font("Arial", Font.BOLD, 40));
            g.drawString("FLAPPY BIRD", 50, 200);
            
            g.setFont(new Font("Arial", Font.PLAIN, 20));
            g.drawString("Press SPACE to Start", 80, 300);
            
            // Draw the bird just sitting there for the arcade look
            bird.draw(g);
        } 
        else {


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

    public void restartGame() {
        bird.y = birdY;               // Reset bird position
        bird.velocityY = 0;          // Stop any downward momentum
        bird.rotation = 0;           // Level the bird out
        pipes.clear();               // Remove all old pipes
        score = 0;                   // Reset the score
        gameOver = false;            // Flip the flag
        
        // Restart the timers
        gameLoop.start();
        placePipesTimer.start();
    }

   @Override
    public void actionPerformed(ActionEvent e) {
        if (gameState == PLAYING && !gameOver) {
            move();
        }
        repaint();
    }

    @Override public void keyTyped(KeyEvent e) {}

    @Override
    public void keyPressed(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_SPACE) {
            if (gameState == MENU) {
                gameState = PLAYING;
                placePipesTimer.start();
                bird.velocityY = -9;
            } 
            else if (gameState == PLAYING) {
                // Check if we are restarting or flapping
                if (gameOver) {
                    restartGame(); // Just reset everything and wait for the NEXT press to flap
                } else {
                    bird.velocityY = -9; // Only flap if the bird is actually alive
                }
            }
        }
    }
    
    @Override public void keyReleased(KeyEvent e) {}
}