package snake;

import javax.swing.JPanel;
import javax.swing.plaf.DimensionUIResource;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.util.Random;

public class GamePanel extends JPanel implements ActionListener {
    
    static final int SCREEN_WIDTH = 600;
    static final int SCREEN_HEIGHT = 600;
    static final int UNIT_SIZE = 25;
    static final int GAME_UNITS = (SCREEN_WIDTH * SCREEN_HEIGHT)/UNIT_SIZE;
    static final int DELAY = 75; // Higher the number slower the game
    final int x[] = new int[GAME_UNITS]; // Snake isn't going to be bigger than the game itself
    final int y[] = new int[GAME_UNITS];
    int bodyParts = 6; // begin with 6 body parts of the snake
    int appleEaten = 0;
    int appleX;
    int appleY;
    char direction = 'R'; // Snake will begin by going to the right
    boolean running = false;
    Timer timer;
    boolean gameOn = true;
    Random random;

    GamePanel() {
        random = new Random();
        this.setPreferredSize(new DimensionUIResource(SCREEN_WIDTH, SCREEN_HEIGHT));
        this.setBackground(Color.black);
        this.setFocusable(true);
        MyKeyAdapter mka = new MyKeyAdapter();
        this.addKeyListener(mka);
        startGame();
    }

    public void startGame() {
        newApple();
        running = true;
        timer = new Timer(DELAY,this);
        timer.start();
    }

    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        draw(g);
    }

    public void draw(Graphics g) {

        if (running) {
            for (int i = 0; i < SCREEN_HEIGHT/UNIT_SIZE; i++) {
                g.drawLine(i * UNIT_SIZE, 0, i * UNIT_SIZE, SCREEN_HEIGHT);
                g.drawLine(0, i * UNIT_SIZE, SCREEN_WIDTH, i * UNIT_SIZE);
            }
            Color appleColor = new Color(255, 153, 51);
            g.setColor(appleColor);
            g.fillOval(appleX, appleY, UNIT_SIZE, UNIT_SIZE);
    
            for (int i = 0; i < bodyParts; i++) {
                if (i == 0) {
                    Color headColor = new Color(102, 204, 255);
                    g.setColor(headColor);
                    g.fillRect(x[i], y[i], UNIT_SIZE, UNIT_SIZE);
                }
                else {
                    Color bodyColor = new Color(51, 153, 255);
                    g.setColor(bodyColor);
                    g.fillRect(x[i], y[i], UNIT_SIZE, UNIT_SIZE);
                }
            }

            // Score and Snake Length
            g.setColor(Color.red);
            g.setFont(new Font("Int Free", Font.ITALIC, 20));
            FontMetrics metrics = getFontMetrics(g.getFont());
            g.drawString("Points - " + appleEaten, (SCREEN_WIDTH - metrics.stringWidth("Points - " + appleEaten))/2, g.getFont().getSize());
        
            if (gameOn == false) {
                pause(g);
            } 
        }
        else {
            gameOver(g);
        }
    }

    public void newApple() {
        appleX = random.nextInt((int)(SCREEN_WIDTH/UNIT_SIZE)) * UNIT_SIZE;
        appleY = random.nextInt((int)(SCREEN_HEIGHT/UNIT_SIZE)) * UNIT_SIZE;
    }

    public void move() {

        for (int i = bodyParts; i > 0; i--) {
            x[i] = x[i-1];
            y[i] = y[i-1];
        }

        switch (direction) {
            case 'U':
                y[0] = y[0] - UNIT_SIZE;
                break;

            case 'D':
                y[0] = y[0] + UNIT_SIZE;
                break;

            case 'L':
                x[0] = x[0] - UNIT_SIZE;
                break;

            case 'R':
                x[0] = x[0] + UNIT_SIZE;
                break;
        }
    }

    public void checkApple() {
        if ((x[0] == appleX) && (y[0] == appleY)) {
            bodyParts++;
            appleEaten++;
            newApple();
        }
    }

    public void checkCollisions() {

        // This checks if head collides with body
        for (int i = bodyParts; i > 0; i--) {
            if ((x[0] == x[i]) && (y[0] == y[i])) {
                running = false;
            } 
        }

        // Check if head touches left border
        if (x[0] < 0) {
            running = false;
        }

        // Check if head touches right border
        if (x[0] > SCREEN_WIDTH) {
            running = false;
        }

        // Check if head touches top border
        if (y[0] < 0) {
            running = false;
        }

        if (y[0] > SCREEN_HEIGHT) {
            running = false;
        }

        if (running == false) {
            timer.stop();
        }
    }

    public void pause(Graphics g) {
        timer.stop();
        Color pauseColor = new Color(51, 204, 204);
        g.setColor(pauseColor);
        g.setFont(new Font("Int Free", Font.BOLD, 50));
        FontMetrics metrics = getFontMetrics(g.getFont());
        g.drawString("Paused", (SCREEN_WIDTH - metrics.stringWidth("Paused"))/2, (SCREEN_HEIGHT/2));
    }

    public void resume() {
        gameOn = true;
        timer.start();
    }

    public void gameOver(Graphics g) {
        // Game Over text
        g.setColor(Color.red);
        g.setFont(new Font("Int Free", Font.BOLD, 75));
        FontMetrics metrics = getFontMetrics(g.getFont());
        g.drawString("Game Over", (SCREEN_WIDTH - metrics.stringWidth("Game Over"))/2, (SCREEN_HEIGHT/2));
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (running) {
            move();
            checkApple();
            checkCollisions();
        }
        repaint();
    }

    public class MyKeyAdapter extends KeyAdapter {
        @Override
        public void keyPressed(KeyEvent e) {
            switch(e.getKeyCode()) {
                case KeyEvent.VK_LEFT:
                    if (direction != 'R') {
                        direction = 'L';
                    }
                    break;

                case KeyEvent.VK_RIGHT:
                    if (direction != 'L') {
                        direction = 'R';
                    }
                    break;

                case KeyEvent.VK_UP:
                    if (direction != 'D') {
                        direction = 'U';
                    }
                    break;

                case KeyEvent.VK_DOWN:
                    if (direction != 'U') {
                        direction = 'D';
                    }
                    break;

                case KeyEvent.VK_SPACE:
                    if (gameOn) {
                        gameOn = false;
                    }
                    else {
                        resume();
                    }
            }
        }
    }
}
