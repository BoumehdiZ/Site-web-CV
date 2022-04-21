// 
// Decompiled by Procyon v0.5.36
// 

package snake;

import java.awt.Font;
import java.awt.Graphics;
import java.util.Iterator;
import java.awt.Color;
import java.util.ArrayDeque;
import java.awt.Component;
import java.awt.Container;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import javax.swing.JFrame;
import java.util.Random;
import java.awt.Point;
import java.util.Deque;
import javax.swing.JPanel;

public class Snake extends JPanel
{
    private final int WIDTH = 50;
    private Deque<SnakePart> snake;
    private int offset;
    private int newDirection;
    private int continuerMenuP;
    private int continuerGameOver;
    private int menuPrincipale;
    private Point apple;
    private Random rand;
    private boolean isGrowing;
    private boolean gamelost;
    
    public static void main(final String[] args) {
        final JFrame frame = new JFrame("Snake");
        final Snake snake = new Snake();
        frame.addKeyListener(new KeyListener() {
            @Override
            public void keyTyped(final KeyEvent e) {
            }
            
            @Override
            public void keyPressed(final KeyEvent e) {
                snake.onKeyPressed(e.getKeyCode());
                System.out.println(e.getKeyCode());
            }
            
            @Override
            public void keyReleased(final KeyEvent e) {
            }
        });
        frame.setContentPane(snake);
        frame.setSize(650, 650);
        frame.setResizable(false);
        frame.setLocationRelativeTo(null);
        frame.setDefaultCloseOperation(3);
        frame.setVisible(true);
    }
    
    public Snake() {
        this.snake = new ArrayDeque<SnakePart>();
        this.offset = 0;
        this.newDirection = 39;
        this.continuerMenuP = 0;
        this.continuerGameOver = 0;
        this.menuPrincipale = 1;
        this.apple = new Point(0, 0);
        this.rand = new Random();
        this.isGrowing = false;
        this.gamelost = false;
        this.createApple();
        this.snake.add(new SnakePart(0, 0, 39));
        this.setBackground(Color.WHITE);
        new Thread(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    Snake.this.repaint();
                    try {
                        Thread.sleep(16L);
                    }
                    catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();
    }
    
    public void createApple() {
        boolean positionAvaliable;
        do {
            this.apple.x = this.rand.nextInt(12);
            this.apple.y = this.rand.nextInt(12);
            positionAvaliable = true;
            for (final SnakePart p : this.snake) {
                if (p.x == this.apple.x && p.y == this.apple.y) {
                    positionAvaliable = false;
                    break;
                }
            }
        } while (!positionAvaliable);
    }
    
    @Override
    protected void paintComponent(final Graphics g) {
        super.paintComponent(g);
        if (this.menuPrincipale == 1) {
            g.setColor(Color.black);
            g.setFont(new Font("Arial", 90, 90));
            g.drawString("SNAKE", 325 - g.getFontMetrics().stringWidth("SNAKE") / 2, 162);
            g.setFont(new Font("Arial", 40, 40));
            g.drawString("THE GAME", 325 - g.getFontMetrics().stringWidth("THE GAME") / 2, 216);
            g.setFont(new Font("Arial", 20, 20));
            g.drawString("Appuyez sur Entr\u00e9e pour commencer", 325 - g.getFontMetrics().stringWidth("Appuyez sur Entr\u00e9e pour commencer") / 2, 325);
            if (this.continuerMenuP == 10) {
                this.menuPrincipale = 0;
                this.continuerMenuP = 0;
            }
        }
        else {
            SnakePart head = null;
            if (this.gamelost) {
                g.setColor(Color.black);
                g.setFont(new Font("Arial", 90, 90));
                g.drawString("GAME OVER", 325 - g.getFontMetrics().stringWidth("GAME OVER") / 2, 325);
                if (this.continuerGameOver == 10) {
                    this.gamelost = false;
                    this.menuPrincipale = 1;
                    this.continuerGameOver = 0;
                    head = this.snake.getFirst();
                    for (final SnakePart p : this.snake) {
                        if (p != head) {
                            this.snake.remove(p);
                        }
                    }
                    head.x = 0;
                    head.y = 0;
                    head.direction = 39;
                }
                return;
            }
            this.offset += 5;
            if (this.offset == 50) {
                this.offset = 0;
                try {
                    head = (SnakePart)this.snake.getFirst().clone();
                    head.move();
                    head.direction = this.newDirection;
                    this.snake.addFirst(head);
                    if (head.x == this.apple.x && head.y == this.apple.y) {
                        this.isGrowing = true;
                        this.createApple();
                    }
                    if (!this.isGrowing) {
                        this.snake.pollLast();
                    }
                    else {
                        this.isGrowing = false;
                    }
                }
                catch (CloneNotSupportedException e) {
                    e.printStackTrace();
                }
            }
            g.setColor(Color.red);
            g.fillOval(this.apple.x * 50 + 12, this.apple.y * 50 + 12, 25, 25);
            g.setColor(Color.DARK_GRAY);
            for (final SnakePart p : this.snake) {
                if (this.offset == 0 && p != head && p.x == head.x && p.y == head.y) {
                    this.gamelost = true;
                }
                if (p.direction == 37 || p.direction == 39) {
                    g.fillRect(p.x * 50 + ((p.direction == 37) ? (-this.offset) : this.offset), p.y * 50, 50, 50);
                }
                else {
                    g.fillRect(p.x * 50, p.y * 50 + ((p.direction == 38) ? (-this.offset) : this.offset), 50, 50);
                }
            }
            g.setColor(Color.BLUE);
            g.drawString("Score : " + (this.snake.size() - 1), 10, 20);
        }
    }
    
    public void onKeyPressed(final int keyCode) {
        if (keyCode >= 37 && keyCode <= 40 && Math.abs(keyCode - this.newDirection) != 2) {
            this.newDirection = keyCode;
        }
        if (keyCode == 10 && !this.gamelost) {
            this.continuerMenuP = keyCode;
        }
        if (keyCode == 10 && this.gamelost) {
            this.continuerGameOver = keyCode;
        }
    }
    
    class SnakePart
    {
        public int x;
        public int y;
        public int direction;
        
        public SnakePart(final int x, final int y, final int direction) {
            this.x = x;
            this.y = y;
            this.direction = direction;
        }
        
        public void move() {
            if (this.direction == 37 || this.direction == 39) {
                this.x += ((this.direction == 37) ? -1 : 1);
                if (this.x > 12) {
                    this.x = 0;
                }
                else if (this.x < 0) {
                    this.x = 13;
                }
            }
            else {
                this.y += ((this.direction == 38) ? -1 : 1);
                if (this.y > 12) {
                    this.y = 0;
                }
                else if (this.y < 0) {
                    this.y = 13;
                }
            }
        }
        
        @Override
        protected Object clone() throws CloneNotSupportedException {
            return new SnakePart(this.x, this.y, this.direction);
        }
    }
}
