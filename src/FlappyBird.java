import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.util.ArrayList;
import java.util.Random;

public class FlappyBird extends JPanel implements ActionListener, KeyListener {

    // Window size
    int boardWidth = 360;
    int boardHeight = 640;

    // Images
    Image backgroundImage;
    Image birdImage;
    Image topPipeImage;
    Image bottomPipeImage;

    // Bird
    int birdX = boardWidth / 8;
    int birdY = boardHeight / 2;
    int birdWidth = 34;
    int birdHeight = 24;

    class Bird {
        int x = birdX;
        int y = birdY;
        int width = birdWidth;
        int height = birdHeight;

        Image image;

        Bird(Image image) {
            this.image = image;
        }
    }

    // Pipes
    int pipeX = boardWidth;
    int pipeY = 0;
    int pipeWidth = 64;
    int pipeHeight = 512;

    class Pipe {
        int x = pipeX;
        int y = pipeY;
        int width = pipeWidth;
        int height = pipeHeight;
        Image image;

        boolean passed = false;

        Pipe (Image image) {
            this.image = image;
        }
    }

    // Game logic
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

    FlappyBird() {

        // Set size of window
        setPreferredSize(new Dimension(boardWidth, boardHeight));

        // Check for keyboard inputs
        setFocusable(true);
        addKeyListener(this);

        // Load images
        backgroundImage = new ImageIcon(getClass().getResource("./images/flappybirdbg.png")).getImage();
        birdImage = new ImageIcon(getClass().getResource("./images/flappybird.png")).getImage();
        topPipeImage = new ImageIcon(getClass().getResource("./images/toppipe.png")).getImage();
        bottomPipeImage = new ImageIcon(getClass().getResource("./images/bottompipe.png")).getImage();

        // Create bird
        bird = new Bird(birdImage);

        // Create pipes list
        pipes = new ArrayList<Pipe>();

        // Game timer, loop 60 times a second
        gameLoop = new Timer(1000/60, this);
        gameLoop.start();

        // Place pipes timer, loop every 1.5 seconds
        placePipesTimer = new Timer(Math.max(1500-25*(int)score,1000), new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                placePipes();
            }
        });
        placePipesTimer.start();

    }

    public void placePipes() {

        int randomPipeY = (int) (pipeY - pipeHeight/4 - Math.random()*(pipeHeight/2));
        int openingSpace = boardHeight/4;

        Pipe topPipe = new Pipe(topPipeImage);
        topPipe.y = randomPipeY;
        pipes.add(topPipe);

        Pipe bottomPipe = new Pipe(bottomPipeImage);
        bottomPipe.y = topPipe.y + pipeHeight + openingSpace;
        pipes.add(bottomPipe);
    }

    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        draw(g);
    }

    public void draw(Graphics g) {

        // Draw background
        g.drawImage(backgroundImage, 0, 0, boardWidth, boardHeight, null);

        // Draw bird
        g.drawImage(birdImage, bird.x, bird.y, bird.width, bird.height, null);

        // Draw pipes
        for(int i = 0; i < pipes.size(); i++) {
            Pipe pipe = pipes.get(i);
            g.drawImage(pipe.image, pipe.x, pipe.y, pipe.width, pipe.height, null);
        }

        // Show score
        g.setColor(Color.white);
        g.setFont(new Font("Arial", Font.PLAIN, 32));
        if(gameOver) {
            g.drawString("Game Over: " + String.valueOf((int) score), 10, 35);
        }
        else {
            g.drawString(String.valueOf((int) score), 10, 35);
        }
    }

    public void move() {

        // Bird
        velocityY += gravity;
        bird.y += velocityY;
        bird.y = Math.max(bird.y, 0); 

        // Pipes
        for(int i = 0; i < pipes.size(); i++) {
            Pipe pipe = pipes.get(i);
            pipe.x += velocityX;

            if(!pipe.passed && bird.x > pipe.x + pipe.width) {
                pipe.passed = true;
                score += 0.5; // Because there are two pipes so when we pass them both we get one point
            }

            // If bird collides end the game
            if(collision(bird, pipe)) {
                gameOver = true;
            }
        }

        // If bird falls off the map
        if(bird.y > boardHeight) {
            gameOver = true;
        }
    }

    public boolean collision(Bird a, Pipe b) {
        return a.x < b.x + b.width &&   // Bird's top left corner doesn't reach pipe's top right corner
               a.x + a.width > b.x &&   // Bird's top right corner passes pipe's top left corner
               a.y < b.y + b.height &&  // Bird's top left corner doesn't reach pipe's bottom left corner
               a.y + a.height > b.y;    // Bird's bottom left corner passes pipe's top left corner
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

    @Override
    public void keyPressed(KeyEvent e) {

        // Move up when we press space
        if(e.getKeyCode() == KeyEvent.VK_SPACE) {
            velocityY = -9;

            // If we press space and game is over
            if (gameOver) {

                // Restart the game by resetting the conditions
                bird.y = birdY;
                velocityY = 0;
                pipes.clear();
                score = 0;
                gameOver = false;
                gameLoop.start();
                placePipesTimer.start();
            }
        }
    }

    @Override
    public void keyTyped(KeyEvent e) {
        
    }

    @Override
    public void keyReleased(KeyEvent e) {
        
    }
}
